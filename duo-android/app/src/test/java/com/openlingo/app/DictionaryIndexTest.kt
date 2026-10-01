package com.openlingo.app

import com.openlingo.app.data.local.curriculum.AdvancedCurriculumData
import com.openlingo.app.data.local.curriculum.B1CurriculumData
import com.openlingo.app.data.local.curriculum.ExpandedCurriculumData
import com.openlingo.app.data.local.curriculum.JapaneseN4CurriculumData
import com.openlingo.app.data.local.curriculum.UnitPayload
import com.openlingo.app.data.local.entities.ChallengeEntity
import com.openlingo.app.data.local.entities.ChallengeOptionEntity
import com.openlingo.app.data.local.entities.VocabScheduleEntity
import com.openlingo.app.data.local.models.ChallengeType
import com.openlingo.app.dictionary.Dictionary
import com.openlingo.app.dictionary.DictionaryIndex
import com.openlingo.app.ui.UnitVocabularyIndex
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * WI-11. The offline dictionary promises an authoritative answer, so what is
 * worth locking is not "does it find something" but "does it refuse to find
 * something it should not".
 *
 * Every assertion here is on behaviour a learner can observe: which words open
 * a sheet, which stay shut, and what the sheet says. The four failure modes the
 * feature can plausibly ship with are each pinned by a test that fails if the
 * corresponding guard is removed:
 *
 *  - a distractor defined as vocabulary (`a distractor is not vocabulary`);
 *  - a word in many challenges producing a wall of duplicates
 *    (`a word taught by several challenges is one entry`);
 *  - a guess instead of a refusal (`an unknown word opens nothing`);
 *  - a story line resolving to the whole sentence
 *    (`a tap inside a sentence finds the word, never the sentence`).
 */
class DictionaryIndexTest {

    private val payloads: List<UnitPayload> =
        ExpandedCurriculumData.spanishExpandedUnits +
            ExpandedCurriculumData.japaneseExpandedUnits +
            AdvancedCurriculumData.spanishAdvancedUnits +
            AdvancedCurriculumData.japaneseAdvancedUnits +
            B1CurriculumData.spanishA2Units +
            B1CurriculumData.spanishB1Units +
            B1CurriculumData.japaneseN4Units +
            JapaneseN4CurriculumData.japaneseN4ExtensionUnits

    private val challenges = payloads.flatMap { it.challenges }
    private val options = payloads.flatMap { it.options }

    private val dictionary: Dictionary = DictionaryIndex.build(challenges, options, emptyList())

    // --- a word the corpus teaches resolves ---------------------------------

    /**
     * `What is 'Station' in Katakana?` is a completed definition the corpus
     * already contains, so the entry has to carry it rather than merely the
     * script.
     */
    @Test
    fun `a word the curriculum teaches resolves with the meaning the prompt already states`() {
        val station = dictionary.lookup("駅")
        assertNotNull("駅 is the correct answer of challenge 2031", station)
        assertEquals("Station", station!!.gloss)
    }

    @Test
    fun `lookup ignores case and surrounding whitespace the way the corpus writes it`() {
        assertNotNull(dictionary.lookup("  UN CAFÉ, POR FAVOR  "))
    }

    // --- a distractor is not vocabulary -------------------------------------

    /**
     * The discipline `UnitVocabularyIndex` already applies, re-pinned where the
     * stakes are higher: a lookup is read as a definition, so indexing the
     * wrong answer to "Which one means 'A coffee, please'?" would tell the
     * learner that `Una cerveza fría` is a thing the app teaches.
     */
    @Test
    fun `a distractor is not vocabulary`() {
        val distractors = options.filter { !it.correct }.map { DictionaryIndex.key(it.text) }.toSet()
        // Compared by key, not by the raw string: the corpus writes `Café` as
        // a taught answer and `café` as a distractor in another challenge, and
        // those are one word — a case-sensitive comparison would call the
        // distractor untaught and then fail to explain why it resolves.
        val taught = dictionary.entries.map { DictionaryIndex.key(it.term) }.toSet()
        assertTrue("control missing", dictionary.lookup("Un café, por favor") != null)

        val neverTaught = distractors.filterNot { it in taught }
        assertTrue("corpus offers no distractor-only word to test", neverTaught.size > 50)
        neverTaught.forEach { assertNull("distractor indexed as vocabulary: $it", dictionary.lookup(it)) }
    }

    // --- an unknown word refuses --------------------------------------------

    @Test
    fun `an unknown word opens nothing rather than a wrong guess`() {
        assertNull(dictionary.lookup("Kwyjibo"))
        assertNull(dictionary.lookup("habloz"))
        assertNull(dictionary.lookup(""))
    }

    @Test
    fun `a tap on a word nobody teaches opens nothing`() {
        val line = "Zorbulax flimflam wuggetum"
        for (offset in line.indices) {
            assertNull("offset $offset resolved", dictionary.resolveTap(line, offset))
        }
    }

    @Test
    fun `an index with nothing loaded declines every lookup`() {
        assertNull(Dictionary.EMPTY.lookup("hola"))
        assertNull(Dictionary.EMPTY.resolveTap("hola", 0))
        assertEquals(0, Dictionary.EMPTY.size)
    }

    // --- one word, one entry -------------------------------------------------

    @Test
    fun `a word taught by several challenges is one entry`() {
        // `La cuenta` is the correct answer of both 1019 and 1021.
        val lessons = options.filter { it.text.trim().equals("La cuenta", ignoreCase = true) }
        assertTrue("corpus changed: fewer than two challenges teach it", lessons.size >= 2)
        assertTrue(lessons.all { it.correct })

        val matching = dictionary.entries.filter { DictionaryIndex.key(it.term) == "la cuenta" }
        assertEquals(1, matching.size)
    }

    @Test
    fun `the index holds exactly the distinct headwords its sources offer`() {
        val taught = options.filter { it.correct }
            .map { DictionaryIndex.key(it.text) }
            .filter { it.isNotEmpty() }
            .toSet()
        assertEquals(taught.size, dictionary.size)
    }

    // --- Japanese shows script and reading -----------------------------------

    @Test
    fun `a japanese entry carries the script and the reading the source gives it`() {
        val station = dictionary.lookup("駅")!!
        assertEquals("駅", station.term)
        assertEquals("Eki", station.romaji)
    }

    /**
     * The reading is the one piece of information a Japanese learner cannot get
     * from the kanji alone, so an entry that had script but no reading would
     * be the failure the romaji toggle exists to prevent.
     */
    @Test
    fun `every japanese headword the source gives a reading for keeps that reading`() {
        val withReading = options.filter { it.correct && it.romaji != null && it.text.isJapanese() }
        assertTrue("corpus has no japanese readings to test", withReading.size > 50)
        // A headword can be the answer of several challenges, each with its own
        // reading; the first one the build meets is the one it keeps, so the
        // expectation is computed in the build's own order.
        val optionsByChallenge = options.groupBy { it.challengeId }
        val firstReading = LinkedHashMap<String, String>()
        challenges.forEach { challenge ->
            optionsByChallenge[challenge.id].orEmpty().forEach { option ->
                if (option.correct && option.romaji != null && option.text.isJapanese()) {
                    firstReading.putIfAbsent(DictionaryIndex.key(option.text), option.romaji)
                }
            }
        }
        firstReading.forEach { (term, romaji) ->
            val entry = dictionary.lookup(term)
            assertNotNull("$term is not indexed", entry)
            assertEquals("reading lost for $term", romaji, entry!!.romaji)
        }
    }

    // --- the honest empty case ----------------------------------------------

    /**
     * Word-bank tiles routinely have no English gloss of their own, because the
     * prompt glosses the sentence they assemble rather than the tile. The entry
     * still has to open, with the sentence it belongs to.
     */
    @Test
    fun `a word with no gloss and no word class still opens and shows what is known`() {
        val conjunction = dictionary.lookup("y")
        assertNotNull("`y` is a correct tile of challenge 1018", conjunction)
        assertNull(conjunction!!.gloss)
        assertNull(conjunction.partOfSpeech)
        assertEquals("Agua y pan", conjunction.example)
        assertEquals("Water and bread", conjunction.exampleTranslation)
    }

    /**
     * Found on device: long-pressing `De nada` in a unit's vocabulary list opened a
     * sheet reading `De nada` / `You`. The prompt is `What is 'You're welcome'?`
     * and the apostrophe in `You're` was being taken for the closing quote, so a
     * learner was shown the wrong meaning for a word rather than no meaning.
     *
     * The other two shapes matter as much as the failing one. A gloss with no
     * apostrophe in it must come back exactly as before, and a prompt that quotes
     * something other than a meaning must still be read as nothing at all — a
     * wider quote is no reason to start inventing glosses.
     */
    @Test
    fun `an apostrophe inside a gloss is not the end of the gloss`() {
        val index = DictionaryIndex.build(
            listOf(
                challenge(1, ChallengeType.SELECT, "What is 'You're welcome'?"),
                challenge(2, ChallengeType.SELECT, "How do you say 'I don't understand'?"),
                challenge(3, ChallengeType.SELECT, "Which one means 'Station'?"),
                challenge(4, ChallengeType.SELECT, "Which present form of hablar goes with 'yo'?"),
            ),
            listOf(
                option(10, 1, "De nada", correct = true),
                option(11, 2, "わかりません", correct = true),
                option(12, 3, "Estación", correct = true),
                option(13, 4, "hablo", correct = true),
            ),
            emptyList(),
        )

        assertEquals("You're welcome", index.lookup("De nada")!!.gloss)
        assertEquals("I don't understand", index.lookup("わかりません")!!.gloss)
        assertEquals("Station", index.lookup("Estación")!!.gloss)
        assertNull("'yo' is a slot, not a meaning", index.lookup("hablo")!!.gloss)
    }

    /**
     * The whole corpus, both languages, every challenge that states a meaning: the
     * English the prompt quotes is read as far as the quote that closes it, and not
     * one character short of it. The span is computed here from the first and last
     * apostrophe of the prompt rather than from the rule under test, so this is an
     * independent reading of the same data and not a restatement of the parser.
     *
     * Twenty-three prompts in the corpus carry an apostrophe inside the English —
     * `You're welcome`, `I don't understand`, `Let's go to the park` — and every one
     * of them was cut at the contraction.
     */
    @Test
    fun `no gloss in the corpus is cut short at an apostrophe`() {
        val optionsByChallenge = options.groupBy { it.challengeId }
        val cut = mutableListOf<String>()
        var checked = 0

        for (challenge in challenges) {
            val question = challenge.question
            val open = question.indexOf('\'')
            val close = question.lastIndexOf('\'')
            if (open < 0 || close <= open) continue
            val span = question.substring(open + 1, close).trim()
            if (span.isEmpty()) continue
            val own = optionsByChallenge[challenge.id].orEmpty()
            if (own.none { it.correct }) continue

            // One challenge at a time, so a word taught twice cannot borrow the
            // gloss of its neighbour and hide a truncation here.
            val entry = DictionaryIndex.build(listOf(challenge), own, emptyList())
            if (challenge.type == ChallengeType.WORD_BANK) {
                // A word bank translates the sentence its tiles spell out, and that
                // translation is the sentence's, shown under IN A SENTENCE.
                own.filter { it.correct }.forEach { tile ->
                    val translation = entry.lookup(tile.text)?.exampleTranslation ?: return@forEach
                    checked++
                    if (translation != span) cut += "${challenge.id} / ${tile.text}: '$translation'"
                }
            } else {
                val correct = own.filter { it.correct }
                if (correct.size != 1) continue
                val gloss = entry.lookup(correct.single().text)?.gloss ?: continue
                checked++
                if (gloss != span) cut += "${challenge.id}: '$gloss' of \"$question\""
            }
        }

        assertTrue("no prompt quoted any English, so the check proved nothing", checked > 100)
        assertEquals("glosses cut short at an apostrophe: $cut", emptyList<String>(), cut)
    }

    /**
     * A prompt that quotes a *slot* rather than a meaning must not be read as
     * one. `Which present form of hablar goes with 'yo'?` quotes `yo`, and
     * turning that into a definition of `hablo` would be a confident lie.
     */
    @Test
    fun `a prompt that quotes a slot does not become a gloss`() {
        val form = dictionary.lookup("hablo")
        assertNotNull("`hablo` is the correct answer of challenge 30001", form)
        assertNull("'yo' is a slot, not a meaning", form!!.gloss)
    }

    @Test
    fun `a conjugation answer is the one word class the corpus can support`() {
        // Same shape as above, but this prompt really does state the meaning.
        val potential = dictionary.lookup("話せます")
        assertNotNull("`話せます` is the correct answer of challenge 30025", potential)
        assertEquals("I can speak", potential!!.gloss)
        assertEquals("verb", potential.partOfSpeech)
    }

    /**
     * The one word-class inference the index makes is "a conjugation drill
     * answers with a verb form". That is only safe while it is true of every
     * CONJUGATE item in the corpus, so it is checked rather than assumed: a
     * non-verb answer here means the sheet would be labelling a noun or an
     * adjective, and the rule has to go.
     */
    @Test
    fun `every conjugation answer in the corpus really is a verb form`() {
        val conjugations = challenges.filter { it.type == ChallengeType.CONJUGATE }
        assertTrue("corpus has no conjugation drills to test", conjugations.size >= 10)
        conjugations.forEach { drill ->
            val answers = options.filter { it.challengeId == drill.id && it.correct }
            assertTrue("conjugation ${drill.id} has no answer", answers.isNotEmpty())
            answers.forEach { answer ->
                assertEquals(
                    "${answer.text} (challenge ${drill.id}) is not a verb form",
                    "verb",
                    dictionary.lookup(answer.text)!!.partOfSpeech,
                )
            }
        }
    }

    // --- a tap resolves to a word, not a sentence ---------------------------

    @Test
    fun `a tap inside a story line finds the word, never the sentence`() {
        val (line, word) = firstTaughtWordInAStoryLine()
        val hit = dictionary.resolveTap(line, line.indexOf(word))

        assertNotNull("$word is taught and sits in \"$line\"", hit)
        assertEquals(word, hit!!.entry.term)
        assertEquals(word, line.substring(hit.start, hit.end))
        assertTrue(
            "a tap must not resolve to the whole line: $line",
            hit.end - hit.start < line.length,
        )
    }

    @Test
    fun `every story line in the corpus yields words or nothing, never a line`() {
        val stories = challenges.filter { it.type == ChallengeType.STORY }
        assertTrue("corpus has no story prompts to test", stories.isNotEmpty())
        stories.forEach { story ->
            story.question.split('\n')
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .forEach { line ->
                    for (offset in line.indices) {
                        val hit = dictionary.resolveTap(line, offset)
                            ?: continue
                        assertTrue(
                            "story tap returned the whole line: $line",
                            hit.end - hit.start < line.length,
                        )
                        val resolved = line.substring(hit.start, hit.end)
                        assertTrue(
                            "story tap returned text that is not the entry: $resolved",
                            resolved.isNotBlank(),
                        )
                    }
                }
        }
    }

    /**
     * A multi-word scheduled phrase is reachable from inside a sentence, and
     * the single word inside it still wins when it is itself taught. Step 1
     * before step 3 is the only thing separating these two answers.
     */
    @Test
    fun `a scheduled phrase is reached from a sentence but a taught word inside it wins first`() {
        val withPhrases = DictionaryIndex.build(
            emptyList(),
            emptyList(),
            listOf(
                scheduled("es:buenos_dias", "Buenos días", "Good morning"),
                scheduled("es:hola", "Hola", "Hello"),
            ),
        )
        val sentence = "Buenos días, hola"
        val phraseHit = withPhrases.resolveTap(sentence, sentence.indexOf("Buenos"))
        assertNotNull("a scheduled phrase is a headword in its own right", phraseHit)
        assertEquals("buenos días", DictionaryIndex.key(phraseHit!!.entry.term))
        assertEquals("Buenos días", sentence.substring(phraseHit.start, phraseHit.end))

        val wordHit = withPhrases.resolveTap(sentence, sentence.indexOf("hola"))
        assertEquals("hola", DictionaryIndex.key(wordHit!!.entry.term))
        assertEquals("hola", sentence.substring(wordHit.start, wordHit.end))
    }

    // --- Japanese, where the words are not separated ------------------------

    /**
     * Japanese is written without spaces, so a whole sentence arrives as one
     * token and the only way to find the word under the tap is containment.
     *
     * The sentence is the real one the corpus assembles from the correct tiles
     * of challenge 30030 (`田中さんは` + `先生です`), joined without the space
     * the app renders between chips. `先生です` and `です` are both taught, and
     * the longer one has to win — a tap on the shared characters is ambiguous
     * and the resolver resolves it toward the more specific answer.
     */
    @Test
    fun `a tap inside an unspaced japanese sentence finds the taught word within it`() {
        val sentence = "田中さんは先生です"
        val start = sentence.indexOf("先生です")
        assertTrue("corpus changed: the sentence no longer holds the headword", start > 0)
        assertNotNull("先生です is a correct tile of 30030", dictionary.lookup("先生です"))
        assertNotNull("です is a correct tile of 2044", dictionary.lookup("です"))

        val hit = dictionary.resolveTap(sentence, start)
        assertNotNull("a taught word sits inside the unspaced run", hit)
        assertEquals("先生です", hit!!.entry.term)
        assertEquals("先生です", sentence.substring(hit.start, hit.end))
        assertTrue("a tap must not resolve to the whole run", hit.end - hit.start < sentence.length)
    }

    /**
     * The Latin counterpart must not do the same thing: `hablo` inside
     * `hablamos` is a different word, and a tap that returned it would be
     * wrong with total confidence. Containment is therefore restricted to
     * runs that actually contain CJK.
     */
    @Test
    fun `a latin word is never matched as a fragment of a longer word`() {
        val index = DictionaryIndex.build(
            listOf(challenge(1, ChallengeType.SELECT, "Which one means 'I speak'?")),
            listOf(option(10, 1, "hablo", correct = true)),
            emptyList(),
        )
        assertEquals("hablo", index.lookup("hablo")!!.term)
        assertNull("hablo is not inside hablamos", index.resolveTap("hablamos", 0))
        assertNull("hablo is not inside hablamos", index.resolveTap("hablamos", 5))
    }

    /**
     * A word inside a taught phrase wins the tap, and that is the design, not a
     * gap: `cuenta` is itself a headword, so a tap on either word of
     * `La cuenta` answers with the word, and only a tap landing on neither
     * widens out to the phrase. The phrase is reached from the vocabulary list,
     * which looks a scheduled word up by name rather than by position.
     */
    @Test
    fun `a word inside a taught phrase answers for itself rather than the phrase`() {
        val phrase = dictionary.lookup("La cuenta")!!
        assertNotNull("`La cuenta` is the correct answer of 1019 and 1021", phrase)
        assertNotNull("`cuenta` is taught on its own", dictionary.lookup("cuenta"))

        val onSecondWord = dictionary.resolveTap(phrase.term, phrase.term.indexOf("cuenta"))!!
        assertEquals("cuenta", DictionaryIndex.key(onSecondWord.entry.term))
        assertEquals("cuenta", phrase.term.substring(onSecondWord.start, onSecondWord.end))
    }

    /**
     * The per-unit vocabulary list renders the term itself, so a tap there has
     * to resolve the whole phrase even though the same tap inside a sentence
     * must resolve the single word. Found on device: tapping `Buenos días` in
     * Unit 1's vocabulary list opened a sheet for `buenos` saying it had no
     * gloss, directly above the `Good morning` the row was already printing.
     */
    @Test
    fun `resolving a term as a term takes the whole phrase, not the word inside it`() {
        val phrase = dictionary.lookup("La cuenta")!!
        assertNotNull("`cuenta` is taught on its own", dictionary.lookup("cuenta"))

        // Any offset inside the phrase, including the one on `cuenta` that the
        // sentence resolver prefers, resolves to the phrase on a term surface.
        for (offset in phrase.term.indices) {
            val hit = dictionary.resolveTerm(phrase.term, offset)
            assertNotNull("offset $offset of \"${phrase.term}\" resolved", hit)
            assertEquals(
                "offset $offset returned a fragment",
                "la cuenta",
                DictionaryIndex.key(hit!!.entry.term),
            )
            assertEquals(0, hit.start)
            assertEquals(phrase.term.length, hit.end)
        }
    }

    /**
     * The other half of the same contract: a string that is not itself a
     * headword must still behave exactly as it does inside a sentence, so this
     * cannot become a lookup that swallows the surrounding text.
     */
    @Test
    fun `resolving a term falls back to the sentence resolver when the string is not a headword`() {
        val sentence = "La cuenta está lista"
        for (offset in sentence.indices) {
            val asTerm = dictionary.resolveTerm(sentence, offset)
            val asSentence = dictionary.resolveTap(sentence, offset)
            assertEquals(
                "offset $offset resolved differently as a term",
                asSentence?.entry?.let { DictionaryIndex.key(it.term) },
                asTerm?.entry?.let { DictionaryIndex.key(it.term) },
            )
        }
        // And the tap on a word inside it is still the word, not the phrase.
        val onCuenta = dictionary.resolveTerm(sentence, sentence.indexOf("cuenta"))!!
        assertEquals("cuenta", DictionaryIndex.key(onCuenta.entry.term))
    }

    @Test
    fun `an empty index resolves no term either`() {
        assertNull(Dictionary.EMPTY.resolveTerm("Buenos días", 0))
        assertNull(Dictionary.EMPTY.resolveTerm("", 0))
    }

    // --- invariants ---------------------------------------------------------

    /**
     * Every single-word headword has to be reachable by tapping it, because
     * that is the only way a learner meets most of them: a one-token term can
     * only be shadowed by a longer phrase, and the resolver always tries the
     * word itself first.
     *
     * Multi-word headwords are deliberately excluded — see the phrase test
     * above for why a tap on `La cuenta` answers `cuenta` — and they are
     * covered instead by the vocabulary-list test, which looks them up by name.
     */
    @Test
    fun `every single-word headword is reachable by tapping it`() {
        val single = dictionary.entries.filter { DictionaryIndex.tokenize(it.term).size == 1 }
        single.forEach { entry ->
            val reached = entry.term.indices.any { offset ->
                val hit = dictionary.resolveTap(entry.term, offset)
                hit != null && DictionaryIndex.key(hit.entry.term) == DictionaryIndex.key(entry.term)
            }
            assertTrue("no tap on \"${entry.term}\" reaches it", reached)
        }
    }

    @Test
    fun `every clip the index points at ships in the bundle`() {
        val available = File("src/main/assets/audio").walkTopDown()
            .filter { it.isFile }
            .map { it.name }
            .toSet()
        val referenced = dictionary.entries.mapNotNull { it.audioSrc }
        assertTrue("no audio referenced at all", referenced.isNotEmpty())
        referenced.forEach { src ->
            assertTrue("missing audio asset for $src", src.substringAfterLast("/") in available)
        }
    }

    /**
     * The unit vocabulary list and the dictionary must not disagree about what
     * makes two written forms the same word, or a word can be in the list the
     * unit shows and absent from the sheet the learner taps.
     */
    @Test
    fun `the index agrees with the unit vocabulary list on what makes the same word`() {
        val samples = listOf("Café", "  UN CAFÉ, POR FAVOR ", "Buenos días", "駅", "Hello", "")
        samples.forEach { term ->
            assertEquals(term, UnitVocabularyIndex.key(term), DictionaryIndex.key(term))
        }
    }


    /**
     * The first word of the first story line that the index actually knows.
     *
     * Derived from the corpus rather than hard-coded so the test keeps testing
     * a real story line after the curriculum changes, and fails loudly (rather
     * than passing on an empty loop) if no story line holds a taught word any
     * more.
     */
    private fun firstTaughtWordInAStoryLine(): Pair<String, String> {
        val stories = challenges.filter { it.type == ChallengeType.STORY }
        assertTrue("corpus has no story prompts to test", stories.isNotEmpty())
        stories.forEach { story ->
            story.question.split('\n')
                .map { it.trim() }
                .filter { it.length > 12 }
                .forEach { line ->
                    DictionaryIndex.tokenize(line).forEach { range ->
                        val word = line.substring(range.first, range.last + 1)
                        if (dictionary.lookup(word) != null) return line to word
                    }
                }
        }
        throw AssertionError("no story line in the corpus contains a taught word")
    }
    // --- fixtures -----------------------------------------------------------

    private fun challenge(id: Int, type: ChallengeType, question: String) =
        ChallengeEntity(id = id, lessonId = 1, type = type, question = question, orderIndex = 0)

    private fun option(id: Int, challengeId: Int, text: String, correct: Boolean) =
        ChallengeOptionEntity(id = id, challengeId = challengeId, text = text, correct = correct)

    private fun scheduled(id: String, foreign: String, translation: String) = VocabScheduleEntity(
        id = id, language = "es", foreign = foreign, translation = translation, category = "Test",
    )

    private fun String.isJapanese(): Boolean = any { it.code in 0x3040..0x30FF || it.code in 0x4E00..0x9FFF }
}

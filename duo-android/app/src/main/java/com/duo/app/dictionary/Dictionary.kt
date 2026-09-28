package com.duo.app.dictionary

import com.duo.app.data.local.entities.ChallengeEntity
import com.duo.app.data.local.entities.ChallengeOptionEntity
import com.duo.app.data.local.entities.VocabScheduleEntity
import com.duo.app.data.local.models.ChallengeType

/**
 * One headword in the offline dictionary, carrying only what the app's own data
 * actually says about it.
 *
 * Every field except [term] is nullable and stays null when the corpus has
 * nothing to say. That is deliberate: the sheet is presented as authoritative,
 * so an invented part of speech or a guessed gloss is worse than an absent one.
 * Older challenges and word-bank tiles routinely carry no English gloss, and
 * those entries still open — they show the example sentence and the audio,
 * which is more than the learner had before tapping.
 */
data class DictionaryEntry(
    /** The target-language surface form, as the corpus writes it. */
    val term: String,
    /** Japanese reading, or null for languages that do not use one. */
    val romaji: String? = null,
    /** English meaning, when the corpus pairs this word with one. */
    val gloss: String? = null,
    /** Word class, or null when the corpus does not name one. See [DictionaryIndex]. */
    val partOfSpeech: String? = null,
    /** A target-language sentence showing the word in use, when the corpus has one. */
    val example: String? = null,
    /** English translation of [example], when the corpus states one. */
    val exampleTranslation: String? = null,
    /** A bundled clip, or null. Never a URL and never a generated path. */
    val audioSrc: String? = null,
    /** The topic the word is filed under in the FSRS schedule, when it is scheduled. */
    val category: String? = null,
)

/**
 * What the definition sheet says when the app has no English for a word.
 *
 * The sheet is presented as authoritative, so this line has to be true in every
 * place it can appear. It used to add that the word is "one you assemble rather
 * than translate", which is invented rationale: on a match-pairs board both sides
 * of every pair are `correct = true` — the board is built by pairing all-correct
 * options — so every one of the fourteen boards was told a falsehood about its
 * own tiles. The only reason there is no gloss is the one the data gives, which
 * is that the corpus pairs this word with no English, so that is all it says.
 */
const val NO_GLOSS_NOTICE = "No English gloss for this word in the app yet."

/**
 * A successful tap: which entry was found, and the exact character range of
 * the text that produced it. The range is what lets the caller underline the
 * word it resolved to rather than the whole line.
 */
data class LookupHit(
    val entry: DictionaryEntry,
    val start: Int,
    val end: Int,
)

/**
 * An immutable, offline headword index over the content the app already ships.
 *
 * No network, no asset, no column: the headwords are the target-language
 * answers the curriculum teaches, and the glosses are the English the same
 * challenge already pairs them with. See [DictionaryIndex] for how.
 */
class Dictionary internal constructor(
    private val byKey: Map<String, DictionaryEntry>,
) {

    val size: Int get() = byKey.size

    val entries: Collection<DictionaryEntry> get() = byKey.values

    /**
     * Exact headword lookup, case- and whitespace-insensitive.
     *
     * Returns null for anything not taught. A dictionary that guesses is worse
     * than one that declines: the learner cannot tell a real definition from an
     * invented one, so "no entry" has to be an honest, reachable answer.
     */
    fun lookup(term: String): DictionaryEntry? = byKey[DictionaryIndex.key(term)]

    /**
     * Resolves a tap at [offset] in [text] to a single entry.
     *
     * A question, a story or a vocabulary line is one multi-word string and a
     * tap lands inside it, so deciding "what did they tap" is the whole
     * problem. The resolution order is fixed and always prefers the most
     * specific answer:
     *
     *  1. the tapped word itself, punctuation trimmed (`¿Dónde?` -> `dónde`);
     *  2. for a run with no spaces — Japanese — the longest taught headword
     *     inside that run, so a tap inside `ちょっと待ってください` finds `待つ`
     *     instead of nothing;
     *  3. otherwise the shortest run of up to [DictionaryIndex.MAX_SPAN_WORDS]
     *     adjacent words that is itself a taught headword, which is how a
     *     multi-word scheduled phrase like `Buenos días` is reached from a
     *     story line.
     *
     * Step 3 is what keeps a tap from returning the sentence: a story line is
     * longer than the window, and a whole line is never a headword. Step 1
     * coming first is what keeps it from returning a phrase when the learner
     * tapped the one word inside it. Every branch is exact containment of a
     * headword that exists, so no branch can invent a match.
     */
    fun resolveTap(text: String, offset: Int): LookupHit? {
        if (text.isEmpty()) return null
        val tokens = DictionaryIndex.tokenize(text)
        if (tokens.isEmpty()) return null
        val at = DictionaryIndex.nearestToken(tokens, offset) ?: return null
        val range = tokens[at]

        lookup(text.substring(range.first, range.last + 1))?.let {
            return LookupHit(it, range.first, range.last + 1)
        }

        // Japanese writes its sentence-final 。 as punctuation, so a taught term
        // like `公園に行きましょう。` is one token plus a full stop, and the tap
        // on the run stops one character short of the key. Retrying the run
        // extended over the punctuation that follows reaches it. The lookup is
        // still exact containment of a headword that exists, so this branch
        // cannot invent a match any more than step 1 can.
        var tail = range.last + 1
        while (tail < text.length && !text[tail].isLetterOrDigit()) tail++
        if (tail > range.last + 1) {
            lookup(text.substring(range.first, tail))?.let {
                return LookupHit(it, range.first, tail)
            }
        }

        DictionaryIndex.containedHeadword(byKey, text, range, offset)?.let { return it }

        for (width in 2..DictionaryIndex.MAX_SPAN_WORDS) {
            for (start in (at - width + 1)..at) {
                if (start < 0 || start + width > tokens.size) continue
                val from = tokens[start].first
                val to = tokens[start + width - 1].last + 1
                lookup(text.substring(from, to))?.let { return LookupHit(it, from, to) }
            }
        }
        return null
    }

    /**
     * Resolves a tap on a string that is *itself* a headword, falling back to
     * [resolveTap] when it is not.
     *
     * [resolveTap] deliberately prefers the single tapped word over the phrase
     * around it, because on a question or a story line that is the right answer:
     * the line is context, and the word is what the learner asked about. On the
     * per-unit vocabulary list the relationship is inverted — the row's text is
     * the term, the row is what the tap is aimed at, and the words inside it are
     * not separately taught. Resolving `Buenos días` to `buenos` there opened a
     * sheet that said the word had no gloss, forty pixels above the `Good
     * morning` the row was already printing.
     *
     * So the whole string is tried first here, and only a string that is not
     * itself a headword goes to [resolveTap]. No branch can invent a match: the
     * first is an exact lookup and the second is the sentence resolver.
     */
    fun resolveTerm(text: String, offset: Int): LookupHit? =
        lookup(text)?.let { LookupHit(it, 0, text.length) } ?: resolveTap(text, offset)

    companion object {
        /** Nothing loaded yet. Every lookup on it declines, so no sheet can open. */
        val EMPTY = Dictionary(emptyMap())
    }
}

/**
 * WI-11: builds [Dictionary] from the curriculum and the FSRS vocabulary table.
 *
 * **Where the data comes from.** Two places, both already on the device: the
 * `challenges` / `challenge_options` tables the app seeds from its curriculum,
 * and the `vocab_schedule` table the Practice tab already reviews. No dictionary
 * asset is bundled and no column is added, so this costs the database nothing
 * at version 14.
 *
 * **What becomes a headword.** The `correct` option of a challenge, and the
 * `foreign` term of a scheduled word. Only correct options, for the same reason
 * [com.duo.app.ui.UnitVocabularyIndex] buckets only correct options: the
 * Spanish seed offers `gracias` as the wrong answer to "Sí por favor", so
 * indexing every option would define, authoritatively, a word the unit is
 * teaching the learner to reject. A distractor is not vocabulary.
 *
 * **What becomes the gloss.** The English the challenge already pairs with its
 * answer — `Which one means 'A coffee, please'?` is a finished definition
 * sitting in the prompt, and reading it back is not authoring a new one. The
 * read is deliberately narrow ([GLOSS_ANCHORS]) and fires only when the
 * challenge has exactly one correct answer, so a prompt that quotes a *slot*
 * rather than a meaning — `Which present form of hablar goes with 'yo'?` — is
 * read as nothing instead of being turned into a false gloss of `hablo`.
 *
 * **What becomes the part of speech.** A `CONJUGATE` challenge is by
 * construction a verb drill, so its correct answer is a verb form. Nothing
 * else in the corpus names a word class, so every other entry's part of speech
 * is null and the sheet omits the row. Inferring a noun from an `UNRELATED`
 * distractor, or a topic from a unit title, would be a guess wearing data's
 * clothes.
 */
object DictionaryIndex {

    /**
     * How many adjacent words a tap may span when the word itself is unknown.
     *
     * Four covers the longest scheduled phrase in the corpus
     * (`Un café, por favor`) and is far short of any question or story line, so
     * widening this can never turn a tap into a sentence lookup.
     */
    const val MAX_SPAN_WORDS = 4

    /**
     * The only question shapes whose quoted English is read as a definition.
     *
     * Each is a shape where the curriculum itself states "this English, that
     * target text". The shapes deliberately absent are the ones that quote
     * something *other* than a meaning: `goes with 'yo'`,
     * `fits 'Ellos ___ con ella ayer'`, `What does 山 (yama) mean?` (which quotes
     * a reading, not a definition), and `Assemble: 'Water and bread'` (which
     * glosses the assembled sentence, not any single tile).
     */
    private val GLOSS_ANCHORS = listOf(
        "means '",
        "Which one of these is '",
        "How do you say '",
        "How do you ask '",
        "Translate: '",
        "What is '",
        "Which kanji is '",
    )

    /** How good a source of an example sentence is; higher wins. */
    private const val EXAMPLE_NONE = 0
    private const val EXAMPLE_SENTENCE_LINE = 1
    private const val EXAMPLE_ASSEMBLED = 2

    fun build(
        challenges: List<ChallengeEntity>,
        options: List<ChallengeOptionEntity>,
        vocab: List<VocabScheduleEntity>,
    ): Dictionary {
        if (challenges.isEmpty() && vocab.isEmpty()) return Dictionary.EMPTY

        val optionsByChallenge = options.groupBy { it.challengeId }
        val pending = LinkedHashMap<String, Pending>()

        if (challenges.isEmpty() && options.isEmpty() && vocab.isEmpty()) return Dictionary.EMPTY

        for (challenge in challenges) {
            val correct = optionsByChallenge[challenge.id].orEmpty().filter { it.correct }
            if (correct.isEmpty()) continue

            // A quoted English can only be the gloss of one thing when there is
            // exactly one thing for it to gloss, and never when the thing is a
            // word bank: `Translate: 'Hello, good morning'` is the translation
            // of the sentence the tiles assemble, not of any one tile. The
            // one-correct-answer rule happens to exclude every word bank in
            // the corpus today, but that is a property of the content, not a
            // property of the rule — so the type check states it outright and
            // the count is only the second line of defence.
            val promptGloss = if (challenge.type == ChallengeType.WORD_BANK) {
                null
            } else {
                quotedGloss(challenge.question)?.takeIf { correct.size == 1 }
            }

            val assembled = if (challenge.type == ChallengeType.WORD_BANK) {
                correct.joinToString(" ") { it.text.trim() } to assembleGloss(challenge.question)
            } else {
                null
            }

            for (option in correct) {
                val term = option.text.trim()
                if (term.isEmpty()) continue
                val entry = pending.getOrPut(key(term)) { Pending(term) }

                option.romaji?.let { if (entry.romaji == null) entry.romaji = it }
                promptGloss?.let { if (entry.gloss == null) entry.gloss = it }
                option.audioSrc?.let { if (entry.audioSrc == null) entry.audioSrc = it }
                if (challenge.type == ChallengeType.CONJUGATE && entry.partOfSpeech == null) {
                    entry.partOfSpeech = "verb"
                }
                if (assembled != null) {
                    entry.offer(EXAMPLE_ASSEMBLED, assembled.first, assembled.second)
                } else {
                    sentenceLineContaining(challenge.question, term)
                        ?.let { line -> entry.offer(EXAMPLE_SENTENCE_LINE, line, null) }
                }
            }
        }

        // The FSRS table is the one place the app states a gloss outright, so it
        // wins over a gloss read back out of a prompt.
        for (word in vocab) {
            val term = word.foreign.trim()
            if (term.isEmpty()) continue
            val entry = pending.getOrPut(key(term)) { Pending(term) }
            if (!word.romaji.isNullOrBlank()) entry.romaji = word.romaji
            if (!word.translation.isNullOrBlank()) entry.gloss = word.translation
            if (!word.audioSrc.isNullOrBlank()) entry.audioSrc = word.audioSrc
            if (word.category.isNotBlank()) entry.category = word.category
        }

        return Dictionary(pending.mapValues { (_, value) -> value.toEntry() })
    }

    /**
     * Comparison key for an authored term.
     *
     * Identical to [com.duo.app.ui.UnitVocabularyIndex.key] on purpose — the
     * unit vocabulary list and the dictionary must not disagree about whether
     * `Café` and `café` are the same word, and neither is allowed a layering
     * dependency on the other. `DictionaryIndexTest` pins them together. Not
     * accent-folded: whether `café` and `cafe` are one word is a content
     * decision, not a matcher's to make.
     */
    fun key(term: String): String = term.trim().lowercase()

    private class Pending(val term: String) {
        var romaji: String? = null
        var gloss: String? = null
        var partOfSpeech: String? = null
        var audioSrc: String? = null
        var category: String? = null
        private var example: String? = null
        private var exampleTranslation: String? = null
        private var exampleRank = EXAMPLE_NONE

        /**
         * Keeps the best example seen for this headword: the sentence the word
         * is assembled into beats a line that merely contains it, and within a
         * rank the first one wins, so the result does not depend on map
         * iteration order.
         */
        fun offer(rank: Int, text: String, translation: String?) {
            if (rank < exampleRank) return
            exampleRank = rank
            example = text
            exampleTranslation = translation
        }

        fun toEntry() = DictionaryEntry(
            term = term,
            romaji = romaji,
            gloss = gloss,
            partOfSpeech = partOfSpeech,
            example = example,
            exampleTranslation = exampleTranslation,
            audioSrc = audioSrc,
            category = category,
        )
    }

    /**
     * The English a prompt quotes as the meaning of its answer, or null when
     * the prompt quotes something else.
     */
    private fun quotedGloss(question: String): String? {
        val anchor = GLOSS_ANCHORS
            .map { it to question.indexOf(it) }
            .filter { it.second >= 0 }
            .minByOrNull { it.second }
            ?: return null
        return betweenQuotes(question, anchor.second + anchor.first.length - 1)
    }

    /**
     * The English an `Assemble: '...'` prompt gives for the sentence its correct
     * tiles spell out. Trailing prose after the closing quote — `— the polite
     * copula` — is not part of the translation.
     */
    private fun assembleGloss(question: String): String? {
        val at = question.indexOf("Assemble: '")
        if (at < 0) return null
        return betweenQuotes(question, at + "Assemble: ".length)
    }

    private fun betweenQuotes(text: String, quoteStart: Int): String? {
        if (quoteStart >= text.length || text[quoteStart] != '\'') return null
        // An English gloss can contain an apostrophe of its own — `You're welcome`,
        // `don't`, `Let's` — and stopping at the first quote after the anchor cut
        // every one of them at the contraction, so `De nada` opened a sheet that
        // read `You`. The closing quote is the *last* apostrophe in the remainder
        // that is not an elision inside a word, and an apostrophe inside a word is
        // the one followed by a letter. Every prompt the corpus writes quotes
        // exactly one span, so the true end of that span is unambiguous, and a
        // gloss with no apostrophe in it lands on the very same quote as before.
        var close = -1
        for (index in text.length - 1 downTo quoteStart + 1) {
            if (text[index] != '\'') continue
            if (index + 1 < text.length && text[index + 1].isLetter()) continue
            close = index
            break
        }
        if (close < 0) return null
        return text.substring(quoteStart + 1, close).trim().takeIf { it.isNotEmpty() }
    }

    /**
     * The first line of a challenge's own text that contains [term].
     *
     * FILL_BLANK, ASSIST and STORY prompts are target-language text, and a
     * STORY prompt is a whole dialogue — so this is where a real example
     * sentence comes from, one line at a time rather than the entire story.
     */
    private fun sentenceLineContaining(question: String, term: String): String? =
        question.split('\n')
            .map { it.trim() }
            .firstOrNull { line -> line.isNotEmpty() && line.contains(term, ignoreCase = true) }

    /**
     * Character ranges of the runs of letters and digits in [text].
     *
     * Everything else — spaces, punctuation, Japanese brackets — separates. A
     * Japanese run therefore comes back as one long token, which is exactly
     * why [containedHeadword] exists.
     */
    internal fun tokenize(text: String): List<IntRange> {
        val ranges = ArrayList<IntRange>()
        var start = -1
        text.forEachIndexed { index, c ->
            if (c.isLetterOrDigit()) {
                if (start < 0) start = index
            } else if (start >= 0) {
                ranges.add(start..index - 1)
                start = -1
            }
        }
        if (start >= 0) ranges.add(start..text.length - 1)
        return ranges
    }

    /** Index of the token containing [offset], or the nearest one to it. */
    internal fun nearestToken(tokens: List<IntRange>, offset: Int): Int? {
        if (tokens.isEmpty()) return null
        tokens.indexOfFirst { offset in it }.let { if (it >= 0) return it }
        var best = 0
        var bestDistance = Int.MAX_VALUE
        tokens.forEachIndexed { index, range ->
            val distance = if (offset < range.first) range.first - offset else offset - range.last
            if (distance < bestDistance) {
                bestDistance = distance
                best = index
            }
        }
        return best
    }

    /**
     * The longest taught headword inside [range] that a tap at [offset] falls
     * within.
     *
     * Applied only to a range that contains a CJK character. Japanese is
     * written without spaces, so `ちょっと待ってください` arrives as one token and
     * the headword inside it can only be found by containment. Latin is
     * space-delimited, and running this over a Latin word would happily match
     * `hablo` inside `hablamos` — two different words, and exactly the kind of
     * confident wrong answer this feature must not produce.
     */
    internal fun containedHeadword(
        byKey: Map<String, DictionaryEntry>,
        text: String,
        range: IntRange,
        offset: Int,
    ): LookupHit? {
        val raw = text.substring(range.first, range.last + 1)
        if (raw.none { it.isCjk() }) return null
        var best: LookupHit? = null
        for ((term, entry) in byKey) {
            if (term.length >= raw.length) continue
            var from = 0
            while (from <= raw.length - term.length) {
                val at = raw.indexOf(term, from, ignoreCase = true)
                if (at < 0) break
                val start = range.first + at
                val end = start + term.length
                if (offset >= start && offset < end) {
                    val hit = best
                    if (hit == null || term.length > hit.end - hit.start) {
                        best = LookupHit(entry, start, end)
                    }
                }
                from = at + 1
            }
        }
        return best
    }

    private fun Char.isCjk(): Boolean =
        code in 0x3040..0x30FF || code in 0x3400..0x4DBF || code in 0x4E00..0x9FFF ||
            code in 0xF900..0xFAFF || code in 0xFF66..0xFF9F
}

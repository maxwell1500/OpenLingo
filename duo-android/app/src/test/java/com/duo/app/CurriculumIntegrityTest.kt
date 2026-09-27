package com.duo.app

import com.duo.app.data.local.character.KanaRepository
import com.duo.app.data.local.curriculum.AdvancedCurriculumData
import com.duo.app.data.local.curriculum.B1CurriculumData
import com.duo.app.data.local.curriculum.ExpandedCurriculumData
import com.duo.app.data.local.curriculum.JapaneseN4CurriculumData
import com.duo.app.data.local.curriculum.UnitPayload
import com.duo.app.data.local.models.ChallengeType
import com.duo.app.grammar.AnswerGrader
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pure-JVM integrity tests over the bundled curriculum payloads.
 *
 * These catch the class of bug that previously only surfaced on-device
 * (wrong IDs, dangling foreign keys, missing audio files) at build time.
 */
class CurriculumIntegrityTest {

    private val allPayloads: List<UnitPayload> =
        ExpandedCurriculumData.spanishExpandedUnits +
            ExpandedCurriculumData.japaneseExpandedUnits +
            AdvancedCurriculumData.spanishAdvancedUnits +
            AdvancedCurriculumData.japaneseAdvancedUnits +
            B1CurriculumData.spanishA2Units +
            B1CurriculumData.spanishB1Units +
            B1CurriculumData.japaneseN4Units +
            JapaneseN4CurriculumData.japaneseN4ExtensionUnits

    /** The fixed errorTag vocabulary, mirroring the KDoc on ChallengeOptionEntity.errorTag. */
    private val errorTagVocabulary = setOf(
        "WRONG_TENSE",
        "WRONG_PERSON",
        "WRONG_FORM",
        "WRONG_COPULA",
        "WRONG_CLASSIFIER",
        "WRONG_REGISTER",
        "UNRELATED",
    )

    /**
     * Which errorTags are honest for each grammaticalFocus the corpus ships.
     *
     * A focus missing from this map fails the test below, so new content has to
     * declare the tags its distractors may carry instead of inheriting whatever
     * happens to fit. Several focuses deliberately allow more than one tag: a
     * verb paradigm can be wrong in person or in tense (es.preterito.regular),
     * and ja.potential can be answered with a plain form (WRONG_FORM) or with
     * the past potential (WRONG_TENSE), because the ます form really does
     * inflect for the past.
     */
    private val honestTagsByFocus = mapOf(
        // Spanish verbs: right frame, wrong ending - person, number or tense.
        "es.imperfecto" to setOf("WRONG_TENSE", "WRONG_PERSON"),
        "es.present_person" to setOf("WRONG_PERSON", "WRONG_TENSE"),
        "es.preterito.regular" to setOf("WRONG_TENSE", "WRONG_PERSON"),
        "es.tener_present" to setOf("WRONG_PERSON"),
        // Irregular and stem-changing preterites: right tense, wrong person or
        // number, or a form the regular preterite would have produced. The
        // infinitive and the present are a frame error, so WRONG_FORM is honest
        // where a distractor leaves a dictionary form in the slot.
        "es.preterito.irregular" to setOf("WRONG_TENSE", "WRONG_PERSON", "WRONG_FORM"),
        "es.preterito.stem_changing" to setOf("WRONG_TENSE", "WRONG_PERSON", "WRONG_FORM"),
        // The past perfect: a plain preterite or present perfect is a tense error,
        // an -ndo form or a wrongly-built participle is a form error, and another
        // subject is a person error.
        "es.past_perfect" to setOf("WRONG_TENSE", "WRONG_FORM", "WRONG_PERSON"),
        // The conditional. Regular and irregular share the same two honest errors:
        // the future of the same verb is a tense error, another person is a person
        // error, and an infinitive left in the slot is a form error.
        "es.conditional.regular" to setOf("WRONG_TENSE", "WRONG_PERSON", "WRONG_FORM"),
        "es.conditional.irregular" to setOf("WRONG_TENSE", "WRONG_PERSON", "WRONG_FORM"),
        // The polite periphrasis is a register choice first (quiero vs me
        // gustaría) and a form choice second (me gusta, the infinitive).
        "es.conditional.periphrasis" to setOf("WRONG_REGISTER", "WRONG_FORM", "WRONG_TENSE"),
        // Connectives: a distractor is a connective that cannot carry the relation
        // the sentence states — a different purpose/cause/result/concession in the
        // same slot — which is the form the sentence calls for, not a different word.
        "es.connectives" to setOf("WRONG_FORM"),
        // ser vs estar: a usage choice, plus agreement for the distractors that get that right.
        "es.ser_estar" to setOf("WRONG_COPULA", "WRONG_PERSON"),
        "es.ser_present" to setOf("WRONG_COPULA", "WRONG_PERSON"),
        // Japanese counters: the numeral is right in every distractor.
        "ja.counter_classifier" to setOf("WRONG_CLASSIFIER"),
        "ja.counter_people" to setOf("WRONG_CLASSIFIER"),
        "ja.counter_time" to setOf("WRONG_CLASSIFIER"),
        // Japanese verbs inflect for form, not tense: only ます/ました and です/でした carry one.
        "ja.potential" to setOf("WRONG_FORM", "WRONG_TENSE"),
        "ja.potential_nominal" to setOf("WRONG_FORM"),
        "ja.te_form" to setOf("WRONG_FORM", "WRONG_TENSE"),
        "ja.request_polite" to setOf("WRONG_FORM", "WRONG_REGISTER", "WRONG_TENSE"),
        "ja.polite_verb" to setOf("WRONG_REGISTER", "WRONG_FORM", "WRONG_TENSE"),
        "ja.polite_register" to setOf("WRONG_REGISTER"),
        "ja.copula_polite" to setOf("WRONG_REGISTER", "WRONG_TENSE"),
        // Japanese units 11-12. A verb slot here can miss on the ending
        // (WRONG_FORM), on the register it was inflected in (WRONG_REGISTER —
        // 買った against 買いました is the whole point of the plain/polite
        // pair) or on whether it inflects for the past at all (WRONG_TENSE —
        // 行きます against 行きました), so all three are honest per focus.
        "ja.past_polite" to setOf("WRONG_TENSE", "WRONG_FORM", "WRONG_REGISTER"),
        "ja.plain_vs_polite" to setOf("WRONG_REGISTER", "WRONG_TENSE", "WRONG_FORM"),
        "ja.negative" to setOf("WRONG_FORM", "WRONG_REGISTER", "WRONG_TENSE"),
        // The adjective classes: a wrong ending is a form error, and dropping
        // the past off 寒かった is a tense error. Neither has a register to get
        // wrong — 寒いです and 寒かった are the same word in two of its own forms.
        "ja.i_adjective" to setOf("WRONG_FORM", "WRONG_TENSE"),
        "ja.na_adjective" to setOf("WRONG_FORM", "WRONG_TENSE", "WRONG_REGISTER"),
        "ja.ability_polite" to setOf("WRONG_FORM", "WRONG_REGISTER", "WRONG_TENSE"),
        "ja.think" to setOf("WRONG_FORM", "WRONG_REGISTER", "WRONG_TENSE"),
        "ja.giving_receiving" to setOf("WRONG_FORM", "WRONG_TENSE", "WRONG_REGISTER"),
    )

    @Test
    fun `all entity ids are globally unique`() {
        val unitIds = allPayloads.map { it.unit.id }
        val lessonIds = allPayloads.flatMap { it.lessons }.map { it.id }
        val challengeIds = allPayloads.flatMap { it.challenges }.map { it.id }
        val optionIds = allPayloads.flatMap { it.options }.map { it.id }

        assertEquals(unitIds.size, unitIds.toSet().size)
        assertEquals(lessonIds.size, lessonIds.toSet().size)
        assertEquals(challengeIds.size, challengeIds.toSet().size)
        assertEquals(optionIds.size, optionIds.toSet().size)
    }

    @Test
    fun `foreign keys resolve within the payload set`() {
        val unitIds = allPayloads.map { it.unit.id }.toSet()
        val lessonIds = allPayloads.flatMap { it.lessons }.map { it.id }.toSet()
        val challengeIds = allPayloads.flatMap { it.challenges }.map { it.id }.toSet()

        allPayloads.forEach { payload ->
            payload.lessons.forEach { lesson ->
                assertEquals(
                    "lesson ${lesson.id} must belong to its own unit ${payload.unit.id}",
                    payload.unit.id,
                    lesson.unitId,
                )
                assertTrue("unit ${lesson.unitId} must exist", lesson.unitId in unitIds)
            }
            payload.challenges.forEach { challenge ->
                assertTrue(
                    "challenge ${challenge.id} references missing lesson ${challenge.lessonId}",
                    challenge.lessonId in lessonIds,
                )
            }
            payload.options.forEach { option ->
                assertTrue(
                    "option ${option.id} references missing challenge ${option.challengeId}",
                    option.challengeId in challengeIds,
                )
            }
        }
    }

    @Test
    fun `order indexes are contiguous within each unit and lesson`() {
        allPayloads.forEach { payload ->
            val lessonOrder = payload.lessons.map { it.orderIndex }.sorted()
            assertEquals(
                "unit ${payload.unit.id} lessons must be 0-based contiguous",
                (0 until payload.lessons.size).toList(),
                lessonOrder,
            )
            payload.lessons.forEach { lesson ->
                val lessonChallenges = payload.challenges.filter { it.lessonId == lesson.id }
                val challengeOrder = lessonChallenges.map { it.orderIndex }.sorted()
                assertEquals(
                    "lesson ${lesson.id} challenges must be 0-based contiguous",
                    (0 until lessonChallenges.size).toList(),
                    challengeOrder,
                )
            }
        }
    }

    @Test
    fun `every challenge is answerable and choice challenges have distractors`() {
        val optionsByChallenge = allPayloads.flatMap { it.options }.groupBy { it.challengeId }
        allPayloads.flatMap { it.challenges }.forEach { challenge ->
            val options = optionsByChallenge[challenge.id].orEmpty()
            assertTrue("challenge ${challenge.id} has no options", options.isNotEmpty())
            assertTrue(
                "challenge ${challenge.id} has no correct option",
                options.any { it.correct },
            )
            if (challenge.type != ChallengeType.LISTEN) {
                assertTrue(
                    "${challenge.type.rawValue} challenge ${challenge.id} needs at least 2 options",
                    options.size >= 2,
                )
            }
        }
    }

    @Test
    fun `every referenced audio file exists in assets`() {
        val audioDir = File("src/main/assets/audio")
        val available = audioDir.walkTopDown()
            .filter { it.isFile }
            .map { it.name }
            .toSet()
        val referenced = allPayloads.flatMap { it.challenges }.mapNotNull { it.audioSrc } +
            allPayloads.flatMap { it.options }.mapNotNull { it.audioSrc }
        assertTrue("no audio referenced at all", referenced.isNotEmpty())
        referenced.forEach { src ->
            val file = src.substringAfterLast("/")
            assertTrue("missing audio asset for $src", file in available)
        }
    }

    @Test
    fun `kana syllabaries are complete and unique`() {
        val hiragana = KanaRepository.hiraganaList
        val katakana = KanaRepository.katakanaList

        assertEquals(46, hiragana.size)
        assertEquals(46, katakana.size)
        assertEquals(46, hiragana.map { it.character }.toSet().size)
        assertEquals(46, katakana.map { it.character }.toSet().size)
        (hiragana + katakana).forEach {
            assertTrue("blank romaji for ${it.character}", it.romaji.isNotBlank())
            assertTrue("no strokes for ${it.character}", it.strokes.isNotEmpty())
        }
    }

    @Test
    fun `every challenge type in the corpus is a known ChallengeType`() {
        val unknown = allPayloads.flatMap { it.challenges }
            .map { it.type.rawValue }
            .distinct()
            .filter { raw ->
                try {
                    ChallengeType.fromRaw(raw)
                    false
                } catch (e: IllegalArgumentException) {
                    true
                }
            }
        assertEquals("challenge types not in the ChallengeType vocabulary: $unknown", emptyList<String>(), unknown)
    }

    @Test
    fun `grammar-tagged challenges explain why at least one distractor is wrong`() {
        val optionsByChallenge = allPayloads.flatMap { it.options }.groupBy { it.challengeId }
        allPayloads.flatMap { it.challenges }
            .filter { it.grammaticalFocus != null }
            .forEach { challenge ->
                val focus = challenge.grammaticalFocus
                val tags = optionsByChallenge[challenge.id].orEmpty()
                    .filterNot { it.correct }
                    .mapNotNull { it.errorTag }
                val explained = tags.any { it != "UNRELATED" }
                assertTrue(
                    "challenge ${challenge.id} is tagged with grammaticalFocus '$focus' but no " +
                        "incorrect option carries a specific errorTag; option errorTags were " +
                        "${optionsByChallenge[challenge.id].orEmpty().map { it.errorTag }} " +
                        "(tag one distractor WRONG_TENSE, WRONG_PERSON, WRONG_FORM, WRONG_COPULA, " +
                        "WRONG_CLASSIFIER or WRONG_REGISTER so the learner sees why it is wrong)",
                    explained,
                )
            }
    }

    @Test
    fun `error tags stay inside the fixed vocabulary and never mark a correct option`() {
        val vocabulary = errorTagVocabulary
        val allOptions = allPayloads.flatMap { it.options }

        val unknown = allOptions.mapNotNull { it.errorTag }.distinct().filter { it !in vocabulary }
        assertEquals(
            "errorTag values outside the fixed vocabulary: $unknown",
            emptyList<String>(),
            unknown,
        )

        val mislabelled = allOptions.filter { it.correct && it.errorTag != null }.map { it.id }
        assertEquals(
            "options that are the correct answer but carry an errorTag: $mislabelled",
            emptyList<Int>(),
            mislabelled,
        )
    }

    @Test
    fun `a grammar-tagged challenge never keeps an unrelated distractor`() {
        val optionsByChallenge = allPayloads.flatMap { it.options }.groupBy { it.challengeId }
        allPayloads.flatMap { it.challenges }
            .filter { it.grammaticalFocus != null }
            .forEach { challenge ->
                val unrelated = optionsByChallenge[challenge.id].orEmpty()
                    .filter { it.errorTag == "UNRELATED" }
                    .map { it.id }
                assertTrue(
                    "challenge ${challenge.id} is tagged with grammaticalFocus " +
                        "'${challenge.grammaticalFocus}' but still offers UNRELATED distractors " +
                        "$unrelated; a grammar item must be passable only by a grammatical error",
                    unrelated.isEmpty(),
                )
            }
    }

    /**
     * The in-context hint is written from these strings, so a tag that names the
     * wrong category teaches the learner something false - a learner who answers
     * 話します instead of 話せます has picked the wrong verb form, and must never be
     * told they got the tense wrong when Japanese has no tense inflection. This is
     * the guard against that class of defect: every tag is checked against the
     * tags its own grammaticalFocus declares honest.
     */
    @Test
    fun `an error tag is compatible with the grammatical focus of its challenge`() {
        val optionsByChallenge = allPayloads.flatMap { it.options }.groupBy { it.challengeId }
        val unmappedFocuses = mutableListOf<String>()
        val dishonestTags = mutableListOf<String>()
        allPayloads.flatMap { it.challenges }
            .filter { it.grammaticalFocus != null }
            .forEach { challenge ->
                val focus = challenge.grammaticalFocus!!
                val honest = honestTagsByFocus[focus]
                if (honest == null) {
                    unmappedFocuses += "challenge ${challenge.id} uses grammaticalFocus '$focus', " +
                        "which honestTagsByFocus does not cover; add it with the tags its " +
                        "distractors may honestly carry"
                } else {
                    optionsByChallenge[challenge.id].orEmpty()
                        .mapNotNull { option -> option.errorTag?.let { option to it } }
                        .filterNot { (_, tag) -> tag in honest }
                        .forEach { (option, tag) ->
                            dishonestTags += "option ${option.id} ('${option.text}') on challenge " +
                                "${challenge.id} ($focus) is tagged $tag; the honest tags for " +
                                "$focus are ${honest.sorted()}"
                        }
                }
            }
        assertEquals(
            "grammaticalFocus values with no declared set of honest error tags: $unmappedFocuses",
            emptyList<String>(),
            unmappedFocuses,
        )
        assertEquals(
            "error tags that contradict the grammatical focus of their challenge",
            emptyList<String>(),
            dishonestTags,
        )
    }

    /**
     * `acceptedAnswers` is the key a typed `FILL_BLANK` is graded against, so
     * every entry must be a form this item's own `grammaticalFocus` licenses.
     *
     * A form the corpus teaches as the answer to a *different* focus is exactly
     * what the item's rule text is written to rule out. Challenge 30007 asked
     * for the 3rd person preterite ("Mi amigo ___ el billete ayer", focus
     * `es.preterito.regular`) and its rule text names `compra` as the present —
     * but the key also accepted `compro`, which challenge 1040 teaches as the
     * `es.present_person` answer, so the item graded a first person present as
     * the 3rd person preterite.
     */
    @Test
    fun `a fill blank accepts no form taught as another focus's answer`() {
        val optionsByChallenge = allPayloads.flatMap { it.options }.groupBy { it.challengeId }
        val correctByText = allPayloads.flatMap { it.challenges }
            .filter { it.grammaticalFocus != null }
            .flatMap { challenge ->
                optionsByChallenge[challenge.id].orEmpty()
                    .filter { it.correct }
                    .map { option ->
                        Triple(option.text, challenge.grammaticalFocus!!, challenge.id)
                    }
            }

        val clashes = mutableListOf<String>()
        allPayloads.flatMap { it.challenges }
            .filter { it.type == ChallengeType.FILL_BLANK }
            .forEach { challenge ->
                val focus = challenge.grammaticalFocus ?: return@forEach
                AnswerGrader.acceptedVariants(challenge.acceptedAnswers).forEach { accepted ->
                    correctByText
                        .filter { (text, otherFocus, ownerId) ->
                            text == accepted && ownerId != challenge.id && otherFocus != focus
                        }
                        .forEach { (_, otherFocus, ownerId) ->
                            clashes += "challenge ${challenge.id} ($focus) accepts '$accepted', " +
                                "which challenge $ownerId teaches as its $otherFocus answer"
                        }
                }
            }
        assertEquals(
            "fill blanks accepting a form the corpus assigns to another grammatical focus: $clashes",
            emptyList<String>(),
            clashes,
        )
    }

    /**
     * A held-out item must ask for a form the rest of the corpus does not
     * already hand the learner. When a key entry is shared with another
     * `FILL_BLANK` whose own answer differs, the shared string belongs to that
     * other item's verb, and the held-out item stops testing anything: a
     * learner who learned the taught item alone still passes.
     *
     * Challenge 31011 (held out) teaches the te-form of 言う and its rule text
     * says the answer is 言って — yet the key also accepted `いって`, which
     * challenge 30024 already accepts as its answer, the te-form of the
     * unrelated verb 行く.
     */
    @Test
    fun `a fill blank accepts no form another fill blank already owns`() {
        val optionsByChallenge = allPayloads.flatMap { it.options }.groupBy { it.challengeId }
        val items = allPayloads.flatMap { it.challenges }
            .filter { it.type == ChallengeType.FILL_BLANK }
            .map { challenge ->
                Triple(
                    challenge,
                    optionsByChallenge[challenge.id].orEmpty().firstOrNull { it.correct }?.text,
                    AnswerGrader.acceptedVariants(challenge.acceptedAnswers),
                )
            }

        val clashes = mutableListOf<String>()
        items.forEach { (challenge, answer, accepted) ->
            accepted.forEach { variant ->
                items
                    .filter { it.first.id != challenge.id && it.second != answer && variant in it.third }
                    .forEach { (other, otherAnswer, _) ->
                        clashes += "challenge ${challenge.id} (answer '$answer') accepts " +
                            "'$variant', which challenge ${other.id} already accepts for its " +
                            "own answer '$otherAnswer'; the held-out pool must not reuse a form " +
                            "the lesson path already teaches"
                    }
            }
        }
        assertEquals(
            "fill blanks sharing an answer key entry with a differently-answered item: $clashes",
            emptyList<String>(),
            clashes,
        )
    }

    /**
     * `ことができます` is 「〜こと が できます」: `こと` nominalises whatever
     * sits in front of it, so the blank in a 「〜___ことができます。」 scaffold
     * holds a whole verb. Every Japanese verb ends in a kana — an ichidan verb
     * in る, a godan verb in the u-row of its ます stem — and a する-verb keeps
     * its する in writing, because こと is itself the する→す nominaliser.
     *
     * Challenge 60025 asked 「車を___ことができます。」 and keyed 運転, so the
     * learner composed 車を運転ことができます: a noun in a verb slot, and not
     * the sentence the item's own clip speaks. Nothing else here could see it —
     * the grader matches strings, so a key that composes into nothing is a key
     * that passes every other assertion in this file.
     *
     * The test is "ends in a kana that can end a verb" rather than "ends in
     * する", because a godan ます stem (書く, keyed by 30029) and an ichidan る
     * form (読める, keyed by 31013) fill the same slot and have to stay legal.
     * The bare する-verb stem is the one form that cannot end a verb, and it is
     * the whole defect, so the assertion stops there.
     */
    @Test
    fun `a fill blank before ことができます keys a verb, not a bare suru stem`() {
        val verbFinalKana = "うくぐすつぬぶむる"

        val malformed = mutableListOf<String>()
        allPayloads.flatMap { it.challenges }
            .filter { it.type == ChallengeType.FILL_BLANK }
            .filter { it.question.contains("ことができます") }
            .forEach { challenge ->
                AnswerGrader.acceptedVariants(challenge.acceptedAnswers).forEach { accepted ->
                    if (accepted.isNotEmpty() && accepted.takeLast(1) !in verbFinalKana) {
                        malformed += "challenge ${challenge.id} ('${challenge.question}') accepts " +
                            "'$accepted', which does not end in a verb: こと can only nominalise a " +
                            "verb, and a する-verb keeps its する"
                    }
                }
            }
        assertEquals(
            "fill blanks keying a form that cannot stand as a verb before ことができます: $malformed",
            emptyList<String>(),
            malformed,
        )
    }

    /**
     * The rule text is the only place a held-out item states which forms are
     * wrong, so it has to name every form the option set tags as an error, and
     * the answer key must stay clear of all of them. Together the two say the
     * key never contains a form the rule text calls incorrect.
     */
    @Test
    fun `held-out rule text names every wrong form and the answer key names none`() {
        val optionsByChallenge = allPayloads.flatMap { it.options }.groupBy { it.challengeId }
        val unexplained = mutableListOf<String>()
        val contradictions = mutableListOf<String>()
        allPayloads.flatMap { it.challenges }
            .filter { it.heldOut }
            .forEach { challenge ->
                val rule = challenge.ruleText.orEmpty()
                val wrong = optionsByChallenge[challenge.id].orEmpty()
                    .filterNot { it.correct }
                    .filter { it.errorTag != null }
                    .map { it.text }
                wrong.filterNot { it in rule }.forEach { text ->
                    unexplained += "challenge ${challenge.id} offers '$text' as a wrong form " +
                        "but its rule text never names it, so the learner is shown a form " +
                        "with no stated reason"
                }
                AnswerGrader.acceptedVariants(challenge.acceptedAnswers)
                    .filter { it in wrong }
                    .forEach { text ->
                        contradictions += "challenge ${challenge.id} accepts '$text' as an " +
                            "answer while its own option set tags that same form as an error"
                    }
            }
        assertEquals(
            "held-out wrong forms the rule text never explains: $unexplained",
            emptyList<String>(),
            unexplained,
        )
        assertEquals(
            "held-out answer keys containing a form the item tags as wrong: $contradictions",
            emptyList<String>(),
            contradictions,
        )
    }

    /**
     * A typed held-out item is ungradeable without a key: `AnswerGrader` matches
     * only against `acceptedAnswers`, so a missing or partly blank one leaves
     * the learner with no correct string to type. The blank-segment case matters
     * because the grader silently drops it, leaving the authored key and the
     * graded key quietly different.
     */
    @Test
    fun `every held-out fill blank carries a complete answer key`() {
        val malformed = allPayloads.flatMap { it.challenges }
            .filter { it.heldOut && it.type == ChallengeType.FILL_BLANK }
            .mapNotNull { challenge ->
                val raw = challenge.acceptedAnswers
                val entries = raw?.split('|').orEmpty()
                when {
                    raw.isNullOrBlank() ->
                        "challenge ${challenge.id} is a held-out FILL_BLANK with no " +
                            "acceptedAnswers; nothing it can be graded against"
                    entries.any { it.isBlank() } ->
                        "challenge ${challenge.id} has a blank entry in '$raw'; AnswerGrader " +
                            "drops it, so the authored key and the graded key disagree"
                    else -> null
                }
            }
        assertEquals(
            "held-out fill blanks with a missing or partly blank answer key: $malformed",
            emptyList<String>(),
            malformed,
        )
    }

    /**
     * SPEC line 418 requires the pool to be real curriculum-quality content. A
     * held-out row is what the learner is graded on, so an empty question, a
     * missing focus or a missing rule is a placeholder shipped into the one
     * mechanic whose whole job is to test transfer.
     */
    @Test
    fun `held-out items carry the content they are graded on`() {
        val placeholders = allPayloads.flatMap { it.challenges }
            .filter { it.heldOut }
            .mapNotNull { challenge ->
                val missing = listOfNotNull(
                    "question".takeIf { challenge.question.isNullOrBlank() },
                    "grammaticalFocus".takeIf { challenge.grammaticalFocus.isNullOrBlank() },
                    "ruleText".takeIf { challenge.ruleText.isNullOrBlank() },
                )
                missing.takeIf { it.isNotEmpty() }?.let {
                    "challenge ${challenge.id} is held-out with no ${it.joinToString(" and ")}"
                }
            }
        assertEquals(
            "held-out items missing the content they are graded on: $placeholders",
            emptyList<String>(),
            placeholders,
        )
    }

    /**
     * `LessonDao` filters `heldOut = 0` out of `getChallengesForLesson` and
     * `getChallengesForUnits`, and `getHeldOutChallengesForUnits` is the only
     * query that selects them, scoped to the unit ids `getUnitIdsForLevel`
     * returns. A held-out item outside those units is therefore filtered out of
     * every lesson and selected by no checkpoint: content nothing can reach.
     * Symmetrically, a lesson that held nothing but held-out items would vanish
     * from the path entirely and stop being completable.
     */
    @Test
    fun `held-out items are reachable by a checkpoint and leave their lesson intact`() {
        val checkpointUnits = (10..31).toSet() + setOf(40, 41)
        val unitOfLesson = allPayloads
            .flatMap { payload -> payload.lessons.map { it.id to payload.unit.id } }
            .toMap()
        val heldOut = allPayloads.flatMap { it.challenges }.filter { it.heldOut }

        val unreachable = heldOut
            .filter { unitOfLesson[it.lessonId] !in checkpointUnits }
            .map { challenge ->
                "challenge ${challenge.id} is held-out in unit ${unitOfLesson[challenge.lessonId]}, " +
                    "which no checkpoint level selects; it is filtered out of every lesson and " +
                    "reachable by no checkpoint"
            }
        assertEquals(
            "held-out items no checkpoint can reach: $unreachable",
            emptyList<String>(),
            unreachable,
        )

        val emptied = heldOut.map { it.lessonId }.toSet().filter { lessonId ->
            allPayloads.flatMap { it.challenges }.none { it.lessonId == lessonId && !it.heldOut }
        }
        assertEquals(
            "lessons left with no taught challenge once their held-out items are filtered out: $emptied",
            emptyList<Int>(),
            emptied.sorted(),
        )
    }

    /**
     * `startCheckpoint` returns early only when both pools are empty, so an
     * empty held-out pool does not fail loudly — it silently serves taught
     * items and the checkpoint stops testing anything unseen. The UI offers
     * A1/A2/B1 for Spanish and N5/N4 for Japanese, and those levels map to
     * units 10-17, 18/19, 30/31 and 20-27, 28/29/40/41, so every one of those pools
     * has to be populated.
     */
    @Test
    fun `every checkpoint level's held-out pool is populated`() {
        val unitOfLesson = allPayloads
            .flatMap { payload -> payload.lessons.map { it.id to payload.unit.id } }
            .toMap()
        val heldOutUnits = allPayloads.flatMap { it.challenges }
            .filter { it.heldOut }
            .mapNotNull { unitOfLesson[it.lessonId] }
            .toSet()

        val empty = mapOf(
            "A2" to listOf(18, 19),
            "B1" to listOf(30, 31),
            "N4" to listOf(28, 29, 40, 41),
        )
            .filterValues { units -> units.none { it in heldOutUnits } }
            .keys
        assertEquals(
            "checkpoint levels whose units hold no held-out items, so the checkpoint falls " +
                "back to taught ones: $empty",
            emptySet<String>(),
            empty,
        )
    }
}

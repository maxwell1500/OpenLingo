package com.duo.app

import com.duo.app.data.local.character.KanaRepository
import com.duo.app.data.local.curriculum.AdvancedCurriculumData
import com.duo.app.data.local.curriculum.B1CurriculumData
import com.duo.app.data.local.curriculum.ExpandedCurriculumData
import com.duo.app.data.local.curriculum.UnitPayload
import com.duo.app.data.local.models.ChallengeType
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
            B1CurriculumData.japaneseN4Units

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
}

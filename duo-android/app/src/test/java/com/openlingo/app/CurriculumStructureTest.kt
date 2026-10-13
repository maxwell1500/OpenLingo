package com.openlingo.app

import com.openlingo.app.data.local.character.KanaRepository
import com.openlingo.app.data.local.curriculum.UnitPayload
import com.openlingo.app.data.local.models.ChallengeType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Structural integrity: unique entity ids, resolvable foreign keys, contiguous
 * order indexes, complete kana syllabaries and well-paced lessons.
 */
class CurriculumStructureTest {

    private val allPayloads: List<UnitPayload> = Corpus.allPayloads

    private companion object {
        /** A lesson shorter than this is a stub, not a lesson. */
        const val MIN_ON_PATH_CHALLENGES = 3

        /** Past this a lesson stops being a session and becomes a marathon. */
        const val MAX_ON_PATH_CHALLENGES = 30
    }

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
            // INTRO is a single-term discovery card: `IntroChallengeCard` renders
            // `options.firstOrNull()` and never composes the option list, so a
            // decoy would be authoring a distractor that cannot be shown. LISTEN
            // is exempt for the same reason — the clip is the stimulus.
            if (challenge.type != ChallengeType.LISTEN && challenge.type != ChallengeType.INTRO) {
                assertTrue(
                    "${challenge.type.rawValue} challenge ${challenge.id} needs at least 2 options",
                    options.size >= 2,
                )
            }
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
    fun `no japanese text repeats a kanji instead of using the iteration mark`() {
        fun kanjiRuns(field: String, where: String): List<String> {
            val hits = mutableListOf<String>()
            field.forEachIndexed { i, c ->
                if (c.code in 0x4E00..0x9FFF && i + 1 < field.length &&
                    field[i + 1] == c
                ) {
                    hits += "$where: $field (doubled ${c} at $i)"
                }
            }
            return hits
        }

        val doubled = allPayloads.flatMap { payload ->
            val fromOptions = payload.options.flatMap {
                kanjiRuns(it.text, "option ${it.id}")
            }
            val fromChallenges = payload.challenges.flatMap { challenge ->
                val at = "challenge ${challenge.id}"
                kanjiRuns(challenge.question, "$at question") +
                    (challenge.ruleText?.let { kanjiRuns(it, "$at ruleText") }
                        ?: emptyList()) +
                    // acceptedAnswers is a |-separated list of ALTERNATIVES, so
                    // only each alternative is scanned: a repeat across the
                    // separator is two spellings, not one word typed twice.
                    (challenge.acceptedAnswers
                        ?.split("|")
                        ?.flatMap { kanjiRuns(it, "$at answer") }
                        ?: emptyList())
            }
            fromOptions + fromChallenges
        }
        assertEquals(
            "kanji doubled instead of written with 々, so a learner reads the " +
                "typo and is graded against it",
            emptyList<String>(),
            doubled,
        )
    }

    @Test
    fun `a word bank's correct tiles spell its sentence in ascending id order`() {
        val optionsByChallenge = allPayloads.flatMap { it.options }.groupBy { it.challengeId }
        val outOfOrder = mutableListOf<String>()
        allPayloads.flatMap { it.challenges }
            .filter { it.type == ChallengeType.WORD_BANK }
            .forEach { challenge ->
                val correct = optionsByChallenge[challenge.id].orEmpty().filter { it.correct }
                val authoredOrder = correct.map { it.id }
                val gradedOrder = correct.sortedBy { it.id }.map { it.id }
                if (authoredOrder != gradedOrder) {
                    outOfOrder += "challenge ${challenge.id} ('${challenge.question}'): authored " +
                        "${authoredOrder.joinToString()} but graded in ${gradedOrder.joinToString()}"
                }
            }
        assertEquals(
            "WORD_BANK tiles must be authored in the ascending id order the grader uses: $outOfOrder",
            emptyList<String>(),
            outOfOrder,
        )
    }

    @Test
    fun `on-path lesson pacing falls within pedagogical bounds`() {
        val outOfBounds = mutableListOf<String>()
        allPayloads.forEach { payload ->
            payload.lessons.forEach { lesson ->
                val onPath = payload.challenges.count { it.lessonId == lesson.id && !it.heldOut }
                if (onPath < MIN_ON_PATH_CHALLENGES || onPath > MAX_ON_PATH_CHALLENGES) {
                    outOfBounds += "lesson ${lesson.id} ('${lesson.title}') in unit ${payload.unit.id} " +
                        "has $onPath on-path challenges, outside " +
                        "$MIN_ON_PATH_CHALLENGES..$MAX_ON_PATH_CHALLENGES"
                }
            }
        }
        assertEquals(
            "lessons whose on-path length is a stub or a marathon: $outOfBounds",
            emptyList<String>(),
            outOfBounds,
        )
    }

}

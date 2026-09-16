package com.duo.app

import com.duo.app.data.local.character.KanaRepository
import com.duo.app.data.local.curriculum.AdvancedCurriculumData
import com.duo.app.data.local.curriculum.B1CurriculumData
import com.duo.app.data.local.curriculum.ExpandedCurriculumData
import com.duo.app.data.local.curriculum.UnitPayload
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
            B1CurriculumData.spanishB1Units +
            B1CurriculumData.japaneseB1Units

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
            if (challenge.type != "LISTEN") {
                assertTrue(
                    "${challenge.type} challenge ${challenge.id} needs at least 2 options",
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
}

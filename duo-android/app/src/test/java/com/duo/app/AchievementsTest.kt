package com.duo.app

import com.duo.app.ui.evaluateAchievements
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Achievement thresholds unlock exactly at their documented totals
 * and stay locked below them.
 */
class AchievementsTest {

    private fun unlockedIds(
        points: Int = 0,
        streak: Int = 1,
        kana: Int = 0,
        challenges: Int = 0,
        lessons: Int = 0,
        isJapanese: Boolean = true,
    ): Set<String> = evaluateAchievements(points, streak, kana, challenges, lessons, isJapanese)
        .filter { it.second }
        .map { it.first.id }
        .toSet()

    @Test
    fun `fresh guest has nothing unlocked`() {
        assertTrue(unlockedIds().isEmpty())
    }

    @Test
    fun `first activity unlocks starter achievements`() {
        val ids = unlockedIds(challenges = 1, lessons = 1, kana = 1)
        assertTrue(ids.contains("first-steps"))
        assertTrue(ids.contains("lesson-done"))
        assertTrue(ids.contains("kana-start"))
    }

    @Test
    fun `xp streak and kana thresholds unlock at exact totals`() {
        assertTrue(unlockedIds(points = 99).none { it == "century" })
        assertTrue(unlockedIds(points = 100).contains("century"))
        assertTrue(unlockedIds(points = 999).none { it == "scholar" })
        assertTrue(unlockedIds(points = 1000).contains("scholar"))

        assertTrue(unlockedIds(streak = 2).none { it == "warming-up" })
        assertTrue(unlockedIds(streak = 3).contains("warming-up"))
        assertTrue(unlockedIds(streak = 6).none { it == "unstoppable" })
        assertTrue(unlockedIds(streak = 7).contains("unstoppable"))

        assertTrue(unlockedIds(kana = 9).none { it == "kana-ten" })
        assertTrue(unlockedIds(kana = 10).contains("kana-ten"))
        assertTrue(unlockedIds(kana = 45).none { it == "kana-master" })
        assertTrue(unlockedIds(kana = 46).contains("kana-master"))
    }

    @Test
    fun `catalog holds nine achievements on the Japanese course`() {
        assertEquals(9, evaluateAchievements(0, 1, 0, 0, 0, isJapanese = true).size)
    }

    /**
     * Kana mastery is Japanese-course content. A Spanish learner cannot trace a
     * kana, so a full kana count must still leave them with no kana shelf — and
     * no "Syllabary master" trophy they could never earn.
     */
    @Test
    fun `kana achievements are Japanese only`() {
        val spanish = evaluateAchievements(0, 1, 46, 0, 0, isJapanese = false)
        assertTrue(
            "the Spanish shelf offers a kana achievement",
            spanish.none { it.first.id.startsWith("kana-") },
        )
        assertEquals(6, spanish.size)
        assertTrue(unlockedIds(kana = 46, isJapanese = false).none { it.startsWith("kana-") })
    }
}

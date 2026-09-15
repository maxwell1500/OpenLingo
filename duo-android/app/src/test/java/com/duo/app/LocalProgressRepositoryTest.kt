package com.duo.app

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.duo.app.data.local.DuoDatabase
import com.duo.app.data.repository.AnswerResult
import com.duo.app.data.repository.LocalProgressRepository
import java.time.LocalDate
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Repository integration tests against an in-memory Room database.
 *
 * Locks the content contract (16 units / 30 lessons / 94 challenges) and the
 * hearts / XP / streak / mistake write paths that previously regressed
 * silently until manual on-device checks.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LocalProgressRepositoryTest {

    private lateinit var db: DuoDatabase
    private lateinit var repository: LocalProgressRepository

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            DuoDatabase::class.java,
        ).allowMainThreadQueries().build()
        repository = LocalProgressRepository(db)
    }

    @After
    fun tearDown() {
        db.close()
    }

    private suspend fun lessonChallengeCount(lessonId: Int): Int =
        db.lessonDao().getChallengesForLesson(lessonId).size

    @Test
    fun `seed locks the content contract and is idempotent`() = runTest {
        repository.initializeIfNeeded()

        val spanishUnits = db.courseDao().getUnitsForCourseDirect(1)
        val japaneseUnits = db.courseDao().getUnitsForCourseDirect(2)
        assertEquals(8, spanishUnits.size)
        assertEquals(8, japaneseUnits.size)
        assertEquals((0 until 8).toList(), spanishUnits.map { it.orderIndex })
        assertEquals((0 until 8).toList(), japaneseUnits.map { it.orderIndex })

        var lessons = 0
        var challenges = 0
        (spanishUnits + japaneseUnits).forEach { unit ->
            db.courseDao().getLessonsForUnit(unit.id).test {
                val unitLessons = awaitItem()
                lessons += unitLessons.size
                unitLessons.forEach { lesson ->
                    challenges += lessonChallengeCount(lesson.id)
                }
            }
        }
        assertEquals(30, lessons)
        assertEquals(98, challenges)

        // Re-running the seed must not duplicate or drop rows.
        repository.initializeIfNeeded()
        assertEquals(8, db.courseDao().getUnitsForCourseDirect(1).size)
        assertEquals(8, db.courseDao().getUnitsForCourseDirect(2).size)
    }
    @Test
    fun `sound and haptics toggles persist`() = runTest {
        repository.initializeIfNeeded()

        repository.setSoundEnabled(false)
        repository.setHapticsEnabled(false)

        val user = db.userProgressDao().getUserProgressDirect("guest_local")
        assertEquals(false, user?.soundEnabled)
        assertEquals(false, user?.hapticsEnabled)

        repository.setSoundEnabled(true)
        assertEquals(true, db.userProgressDao().getUserProgressDirect("guest_local")?.soundEnabled)
    }

    @Test
    fun `reset wipes progress but keeps curriculum`() = runTest {
        repository.initializeIfNeeded()
        repository.submitAnswer(challengeId = 2001, isCorrect = false)
        repository.submitAnswer(challengeId = 1001, isCorrect = true)
        repository.markCharacterMastered("あ", "HIRAGANA")

        repository.resetAllProgress()

        val user = db.userProgressDao().getUserProgressDirect("guest_local")
        assertEquals(0, user?.points)
        assertEquals(5, user?.hearts)
        assertEquals(1, user?.streak)
        assertTrue(
            db.challengeProgressDao().getCompletedChallengeIdsDirect("guest_local").isEmpty(),
        )
        repository.getMistakes().test { assertTrue(awaitItem().isEmpty()) }
        repository.getCharacterMastery().test { assertTrue(awaitItem().isEmpty()) }
        // Curriculum seeds survive the wipe.
        assertEquals(8, db.courseDao().getUnitsForCourseDirect(1).size)
    }
    @Test
    fun `lesson count starts at zero and completes a full lesson`() = runTest {
        repository.initializeIfNeeded()
        assertEquals(0, repository.getCompletedLessonCount())

        // Completing every challenge of lesson 100 marks exactly one lesson done.
        val lessonIds = db.lessonDao().getChallengesForLesson(100).map { it.id }
        assertTrue(lessonIds.isNotEmpty())
        lessonIds.forEach { repository.submitAnswer(challengeId = it, isCorrect = true) }
        assertEquals(1, repository.getCompletedLessonCount())
    }

    @Test
    fun `correct answers accrue quest XP for today`() = runTest {
        repository.initializeIfNeeded()
        repository.getTodayXp().test {
            assertEquals(0, awaitItem())
        }

        repository.submitAnswer(challengeId = 2001, isCorrect = true)
        repository.submitAnswer(challengeId = 2002, isCorrect = true)
        repository.getTodayXp().test {
            assertEquals(20, awaitItem())
        }
    }

    @Test
    fun `missed day stashes the broken streak for repair`() = runTest {
        repository.initializeIfNeeded()
        val userDao = db.userProgressDao()
        userDao.updateStreak("guest_local", 7, LocalDate.now().minusDays(3).toString())

        repository.refreshDailyState()

        val user = userDao.getUserProgressDirect("guest_local")
        assertEquals(1, user?.streak)
        assertEquals(7, user?.brokenStreak)
    }

    @Test
    fun `next completion after a break repairs the streak`() = runTest {
        repository.initializeIfNeeded()
        val userDao = db.userProgressDao()
        userDao.updateStreak("guest_local", 7, LocalDate.now().minusDays(3).toString())
        repository.refreshDailyState()

        val result = repository.submitAnswer(challengeId = 2001, isCorrect = true)

        assertTrue((result as AnswerResult.Correct).streakRepaired)
        val user = userDao.getUserProgressDirect("guest_local")
        assertEquals(8, user?.streak)
        assertEquals(0, user?.brokenStreak)
    }

    @Test
    fun `perfect bonus banks five XP including quest credit`() = runTest {
        repository.initializeIfNeeded()

        assertEquals(5, repository.awardPerfectBonus())
        assertEquals(5, db.userProgressDao().getUserProgressDirect("guest_local")?.points)
        repository.getTodayXp().test {
            assertEquals(5, awaitItem())
        }
    }

    @Test
    fun `backup export and import round-trips state correctly`() = runTest {
        repository.initializeIfNeeded()
        repository.submitAnswer(challengeId = 2001, isCorrect = true)
        repository.submitAnswer(challengeId = 2002, isCorrect = false)
        repository.markCharacterMastered("あ", "HIRAGANA")

        val json = repository.exportBackupJson()
        assertTrue(json.contains("\"points\": 10"))
        assertTrue(json.contains("2001"))
        assertTrue(json.contains("2002"))
        assertTrue(json.contains("\"character\": \"あ\""))

        // Wipe progress to test restoration
        repository.resetAllProgress()
        assertEquals(0, db.userProgressDao().getUserProgressDirect("guest_local")?.points)
        assertTrue(db.challengeProgressDao().getCompletedChallengeIdsDirect("guest_local").isEmpty())

        // Import and verify restoration
        val result = repository.importBackupJson(json)
        assertTrue(result.isSuccess)
        assertEquals(10, db.userProgressDao().getUserProgressDirect("guest_local")?.points)
        assertEquals(4, db.userProgressDao().getUserProgressDirect("guest_local")?.hearts)
        assertTrue(db.challengeProgressDao().getCompletedChallengeIdsDirect("guest_local").contains(2001))
        repository.getMistakes().test {
            val mistakes = awaitItem()
            assertTrue(mistakes.any { it.challengeId == 2002 })
        }
        repository.getCharacterMastery().test {
            val mastery = awaitItem()
            assertTrue(mastery.any { it.character == "あ" })
        }
    }

    @Test
    fun `correct answer banks XP and completes the challenge`() = runTest {
        repository.initializeIfNeeded()

        val result = repository.submitAnswer(challengeId = 2001, isCorrect = true)

        assertTrue(result is AnswerResult.Correct)
        assertEquals(10, (result as AnswerResult.Correct).pointsGained)
        assertEquals(10, db.userProgressDao().getUserProgressDirect("guest_local")?.points)
        assertTrue(
            db.challengeProgressDao()
                .getCompletedChallengeIdsDirect("guest_local").contains(2001),
        )
    }

    @Test
    fun `wrong answer loses a heart and records an SRS mistake`() = runTest {
        repository.initializeIfNeeded()

        val result = repository.submitAnswer(challengeId = 2001, isCorrect = false)

        assertTrue(result is AnswerResult.Incorrect)
        assertEquals(4, (result as AnswerResult.Incorrect).remainingHearts)
        assertEquals(4, db.userProgressDao().getUserProgressDirect("guest_local")?.hearts)
        repository.getMistakes().test {
            val mistakes = awaitItem()
            assertTrue(mistakes.any { it.challengeId == 2001 })
        }
    }

    @Test
    fun `correct answer after a miss clears the mistake`() = runTest {
        repository.initializeIfNeeded()
        repository.submitAnswer(challengeId = 2001, isCorrect = false)

        repository.submitAnswer(challengeId = 2001, isCorrect = true)

        repository.getMistakes().test {
            assertTrue(awaitItem().none { it.challengeId == 2001 })
        }
        assertEquals(10, db.userProgressDao().getUserProgressDirect("guest_local")?.points)
    }

    @Test
    fun `hearts floor at zero and refill restores five`() = runTest {
        repository.initializeIfNeeded()

        repeat(7) { repository.submitAnswer(challengeId = 2001, isCorrect = false) }
        assertEquals(0, db.userProgressDao().getUserProgressDirect("guest_local")?.hearts)

        repository.refillHearts()
        assertEquals(5, db.userProgressDao().getUserProgressDirect("guest_local")?.hearts)
    }

    @Test
    fun `missed day resets streak to one and refills hearts`() = runTest {
        repository.initializeIfNeeded()
        val userDao = db.userProgressDao()
        userDao.updateStreak("guest_local", 9, LocalDate.now().minusDays(2).toString())
        userDao.updateHearts("guest_local", 2)

        repository.refreshDailyState()

        val user = userDao.getUserProgressDirect("guest_local")
        assertEquals(1, user?.streak)
        assertEquals(5, user?.hearts)
    }

    @Test
    fun `character mastery round-trips with attempt counting`() = runTest {
        repository.initializeIfNeeded()

        repository.markCharacterMastered("あ", "HIRAGANA")
        repository.markCharacterMastered("あ", "HIRAGANA")

        repository.getCharacterMastery().test {
            val mastery = awaitItem()
            assertTrue(mastery.any { it.character == "あ" && it.attempts == 2 })
        }
    }
}

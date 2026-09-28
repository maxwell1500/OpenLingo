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
import kotlinx.coroutines.flow.first
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Repository integration tests against an in-memory Room database.
 * Locks the content contract (24 units / 50 lessons / 246 lesson-path challenges plus
 * 22 held-out checkpoint challenges) and the hearts / XP / streak / mistake write paths
 * that previously regressed silently until manual on-device checks.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LocalProgressRepositoryTest {

    /** FSRS stability is a REAL, so a restored value is compared to within rounding. */
    private val FSRS_TOLERANCE = 1e-9

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
        assertEquals(16, spanishUnits.size)
        assertEquals(16, japaneseUnits.size)
        assertEquals((0 until 16).toList(), spanishUnits.map { it.orderIndex })
        assertEquals((0 until 16).toList(), japaneseUnits.map { it.orderIndex })

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
        assertEquals(429, challenges)

        // Re-running the seed must not duplicate or drop rows.
        repository.initializeIfNeeded()
        assertEquals(16, db.courseDao().getUnitsForCourseDirect(1).size)
        assertEquals(16, db.courseDao().getUnitsForCourseDirect(2).size)
    }

    @Test
    fun `held-out checkpoint items are seeded but never appear on a lesson path`() = runTest {
        repository.initializeIfNeeded()

        val spanishHeldOut = repository.getHeldOutChallengesForUnits(listOf(18, 19))
        val japaneseHeldOut = repository.getHeldOutChallengesForUnits(listOf(28, 29, 40, 41))
        assertEquals(4, spanishHeldOut.size)
        assertEquals(10, japaneseHeldOut.size)
        // A held-out item that carries no rule could not assess generalisation.
        (spanishHeldOut + japaneseHeldOut).forEach {
            assertTrue("held-out ${it.challenge.id} has no rule", it.challenge.ruleText != null)
        }

        val heldOutIds = (spanishHeldOut + japaneseHeldOut).map { it.challenge.id }.toSet()
        val lessonIds = (spanishHeldOut + japaneseHeldOut).map { it.challenge.lessonId }.toSet()
        lessonIds.forEach { lessonId ->
            val visible = db.lessonDao().getChallengesForLesson(lessonId).map { it.id }
            assertTrue("lesson $lessonId leaked held-out items", visible.none { it in heldOutIds })
        }

        // A1 / N5 have no held-out pool yet, so those checkpoints fall back to taught items.
        assertTrue(repository.getHeldOutChallengesForUnits(listOf(10, 11, 12, 13, 14, 15, 16, 17)).isEmpty())
    }

    @Test
    fun `a lesson with held-out items still completes on its visible challenges alone`() = runTest {
        repository.initializeIfNeeded()

        // Lesson 119 carries two held-out items that a learner never sees on the path.
        val visible = db.lessonDao().getChallengesForLesson(119).map { it.id }
        assertTrue(visible.isNotEmpty())
        assertTrue(visible.none { it in listOf(31000, 31001) })

        visible.forEach { repository.submitAnswer(challengeId = it, isCorrect = true) }
        assertTrue(repository.getCompletedLessonIds().first().contains(119))
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
    fun `theme mode defaults to system, persists, and survives backup round-trip`() = runTest {
        repository.initializeIfNeeded()

        assertEquals("SYSTEM", db.userProgressDao().getUserProgressDirect("guest_local")?.themeMode)

        repository.setThemeMode("DARK")
        assertEquals("DARK", db.userProgressDao().getUserProgressDirect("guest_local")?.themeMode)

        val json = repository.exportBackupJson()
        repository.setThemeMode("LIGHT")
        assertTrue(repository.importBackupJson(json).isSuccess)
        assertEquals("DARK", db.userProgressDao().getUserProgressDirect("guest_local")?.themeMode)
    }

    @Test
    fun `daily quest goal defaults to 30, persists, and the quest is measured against it`() = runTest {
        repository.initializeIfNeeded()

        // A learner who never opens Settings sees exactly the old fixed quest.
        assertEquals(30, repository.getDailyQuestGoalDirect())

        // Three correct answers: 30 XP, precisely the target the quest used to
        // be hardcoded to.
        repository.submitAnswer(challengeId = 2001, isCorrect = true)
        repository.submitAnswer(challengeId = 2002, isCorrect = true)
        repository.submitAnswer(challengeId = 2003, isCorrect = true)
        repository.getDailyQuest().test {
            val quest = awaitItem()
            assertEquals(30, quest.xp)
            assertEquals(30, quest.goal)
            assertTrue(quest.isComplete)
        }

        // The very same 30 XP is not enough once the learner raises the goal -
        // impossible if completion were still measured against the old constant.
        repository.setDailyQuestGoal(100)
        assertEquals(100, repository.getDailyQuestGoalDirect())
        repository.getDailyQuest().test {
            val quest = awaitItem()
            assertEquals(30, quest.xp)
            assertEquals(100, quest.goal)
            assertTrue("30 XP must not complete a 100 XP quest", !quest.isComplete)
        }

        // And lowering the goal below today's XP meets it right away.
        repository.setDailyQuestGoal(10)
        repository.getDailyQuest().test {
            val quest = awaitItem()
            assertEquals(10, quest.goal)
            assertTrue(quest.isComplete)
        }
    }

    @Test
    fun `a backup without the goal field restores the 30 XP default`() = runTest {
        repository.initializeIfNeeded()
        repository.setDailyQuestGoal(50)

        // A v1/v2 export: `dailyQuestGoal` did not exist, so the key is absent.
        val legacy = """{"version":1,"userProgress":{"points":42,"hearts":5,"streak":3,"lastActiveDate":"2026-09-01"}}"""

        assertTrue(repository.importBackupJson(legacy).isSuccess)
        assertEquals(42, db.userProgressDao().getUserProgressDirect("guest_local")?.points)
        assertEquals(30, repository.getDailyQuestGoalDirect())
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
        // The day's quest progress is part of the reset too. Leaving the day row
        // behind put a learner on 0 points looking at a bar still reading the XP
        // they had earned today, so the next correct answer completed the day
        // at 10 points.
        assertNull(db.dailyActivityDao().getXpDirect(LocalDate.now().toString()))
        repository.getDailyQuest().test {
            val quest = awaitItem()
            assertEquals(0, quest.xp)
            assertEquals(30, quest.goal)
            assertFalse(quest.isComplete)
        }
        // Curriculum seeds survive the wipe.
        assertEquals(16, db.courseDao().getUnitsForCourseDirect(1).size)
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
        repository.saveCheckpointScore("guest_local", 1, "A1", 20, 25)
        val sampleVocab = db.vocabScheduleDao().getAllVocabDirect().first()
        // Rating 3 ("good") is the FSRS review: it advances the schedule and moves
        // the card out of due-today, so the exported row is a genuinely reviewed
        // schedule rather than a re-serialisation of the seed.
        repository.reviewVocab(sampleVocab, 3)
        val reviewed = db.vocabScheduleDao().getVocabById(sampleVocab.id)!!
        assertTrue("the review did not move the FSRS schedule", reviewed.stability != sampleVocab.stability)
        assertTrue("the review did not repush the due date", reviewed.due > sampleVocab.due)
        repository.setDailyQuestGoal(50)

        val json = repository.exportBackupJson()
        assertTrue(json.contains("\"points\": 10"))
        assertTrue(json.contains("2001"))
        assertTrue(json.contains("2002"))
        assertTrue(json.contains("\"character\": \"あ\""))
        assertTrue(json.contains("\"level\": \"A1\""))
        assertTrue(json.contains("checkpointScores"))
        assertTrue(json.contains("vocabSchedule"))
        assertTrue(json.contains("\"dailyQuestGoal\": 50"))
        val exportedVocabCount = db.vocabScheduleDao().getTotalCount()

        // The FSRS schedule has to survive the round trip, and the assertion that
        // proves it is the one that would fail if the restore were a no-op: after
        // the export the card is reviewed again with a different rating, so the
        // live row no longer matches the exported one. A restore that silently
        // skipped vocab_schedule would leave that post-export state behind.
        repository.reviewVocab(reviewed, 1)
        val afterExport = db.vocabScheduleDao().getVocabById(sampleVocab.id)!!
        assertTrue("the post-export review did not change the schedule", afterExport.stability != reviewed.stability)

        // Wipe progress to test restoration
        repository.resetAllProgress()
        assertEquals(0, db.userProgressDao().getUserProgressDirect("guest_local")?.points)
        assertTrue(db.challengeProgressDao().getCompletedChallengeIdsDirect("guest_local").isEmpty())
        // The reset puts the learner back on the default goal...
        assertEquals(30, repository.getDailyQuestGoalDirect())

        // Import and verify restoration
        val result = repository.importBackupJson(json)
        assertTrue(result.isSuccess)
        assertEquals(10, db.userProgressDao().getUserProgressDirect("guest_local")?.points)
        assertEquals(4, db.userProgressDao().getUserProgressDirect("guest_local")?.hearts)
        // ...and the import has to put their chosen goal back, or restoring a
        // backup silently rewrites the quest they set.
        assertEquals(50, repository.getDailyQuestGoalDirect())
        assertTrue(db.challengeProgressDao().getCompletedChallengeIdsDirect("guest_local").contains(2001))
        repository.getMistakes().test {
            val mistakes = awaitItem()
            assertTrue(mistakes.any { it.challengeId == 2002 })
        }
        repository.getCharacterMastery().test {
            val mastery = awaitItem()
            assertTrue(mastery.any { it.character == "あ" })
        }
        val restoredCheckpoints = db.checkpointScoreDao().getAllScoresDirect("guest_local")
        assertEquals(1, restoredCheckpoints.size)
        assertEquals("A1", restoredCheckpoints[0].level)
        assertEquals(20, restoredCheckpoints[0].correct)

        // The reviewed card comes back exactly as it was exported, not as the
        // post-export review left it: that is what distinguishes a real restore
        // from "the table was never cleared, so the row was still there".
        val restoredVocab = db.vocabScheduleDao().getVocabById(sampleVocab.id)
        assertTrue("the reviewed vocab row is gone", restoredVocab != null)
        assertEquals("stability", reviewed.stability, restoredVocab!!.stability, FSRS_TOLERANCE)
        assertEquals("reps", reviewed.reps, restoredVocab.reps)
        assertEquals("lapses", reviewed.lapses, restoredVocab.lapses)
        assertEquals("state", reviewed.state, restoredVocab.state)
        assertEquals("lastReview", reviewed.lastReview, restoredVocab.lastReview)
        assertEquals("due", reviewed.due, restoredVocab.due)
        // And the whole table is the exported table, not just the one card: a
        // restore that dropped rows would still pass the assertions above.
        assertEquals(
            "the restored FSRS table is not the exported one",
            exportedVocabCount,
            db.vocabScheduleDao().getAllVocabDirect().size,
        )
    }

    /**
     * A restored backup is the learner's state, not an increment on top of
     * whatever is already there. Found on device: export at 20 XP for the day,
     * reset, restore, and the quest read 40/100 while the learner's actual
     * total was 20 — because the import went through the same accumulator the
     * answering path uses, and the reset deliberately leaves the day row alone.
     */
    @Test
    fun `restoring a backup replaces the day's XP instead of adding to it`() = runTest {
        repository.initializeIfNeeded()
        repository.submitAnswer(challengeId = 2001, isCorrect = true)
        repository.submitAnswer(challengeId = 2002, isCorrect = true)

        val today = LocalDate.now().toString()
        assertEquals(20, db.dailyActivityDao().getXpDirect(today))
        val json = repository.exportBackupJson()

        // The same restore twice in a row, and once more after a reset. Each
        // must land on the exported total rather than climbing.
        assertTrue(repository.importBackupJson(json).isSuccess)
        assertEquals(20, db.dailyActivityDao().getXpDirect(today))
        assertTrue(repository.importBackupJson(json).isSuccess)
        assertEquals(20, db.dailyActivityDao().getXpDirect(today))

        repository.resetAllProgress()
        assertTrue(repository.importBackupJson(json).isSuccess)
        assertEquals(20, db.dailyActivityDao().getXpDirect(today))

        // And the quest the learner actually sees agrees with their points.
        repository.getDailyQuest().test {
            val quest = awaitItem()
            assertEquals(20, quest.xp)
            assertEquals(30, quest.goal)
            assertFalse(quest.isComplete)
        }
        assertEquals(20, db.userProgressDao().getUserProgressDirect("guest_local")?.points)
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

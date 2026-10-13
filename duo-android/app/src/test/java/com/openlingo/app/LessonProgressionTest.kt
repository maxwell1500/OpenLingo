package com.openlingo.app

import androidx.test.core.app.ApplicationProvider
import com.openlingo.app.data.local.OpenLingoDatabase
import com.openlingo.app.data.local.entities.ChallengeEntity
import com.openlingo.app.data.local.entities.ChallengeOptionEntity
import com.openlingo.app.data.local.entities.CourseEntity
import com.openlingo.app.data.local.entities.LessonEntity
import com.openlingo.app.data.local.entities.UnitEntity
import com.openlingo.app.data.local.models.ChallengeType
import com.openlingo.app.data.repository.LocalProgressRepository
import com.openlingo.app.ui.ActiveScreen
import com.openlingo.app.ui.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Lesson progression, driven through the real ViewModel over the real Room database.
 *
 * Two Phase 1B behaviours live here, both invisible to the answer checks themselves:
 *
 * 1. An `INTRO` card is presentation, not assessment. It must clear without a write:
 *    no heart, no XP, no completion, no mistake — and it must not leave a phantom
 *    retry behind that stops the lesson from ending.
 * 2. A miss in an ordinary lesson used to be thrown away with the lesson. It is now
 *    replayed as a heart-free "Review what you missed" loop before the lesson can
 *    finish; a miss inside that loop costs no heart and is replayed again, so the
 *    lesson ends only once the learner clears it.
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = PlacementTestApplication::class)
class LessonProgressionTest {

    private lateinit var app: OpenLingoApplication
    private lateinit var db: OpenLingoDatabase
    private lateinit var repository: LocalProgressRepository
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() = runBlocking<Unit> {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        app = ApplicationProvider.getApplicationContext()
        db = app.database
        repository = app.repository
        // The database file outlives a test method, and the previous test may have
        // replaced the corpus; every test starts from an empty one.
        withContext(Dispatchers.IO) { db.clearAllTables() }
        repository.initializeIfNeeded()
        repository.setSoundEnabled(false)
        repository.setHapticsEnabled(false)
        viewModel = MainViewModel(app)
        // Loads the profile the ViewModel's sound/haptics reads use, so no test
        // reaches the audio player or the vibrator.
        viewModel.userProgress.first { it != null }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ---------------------------------------------------------------------- INTRO

    @Test
    fun `an INTRO card advances without grading and without costing a heart`() = runTest {
        seedLesson(9001 to ChallengeType.INTRO, 9002 to ChallengeType.SELECT)
        val first = startLesson()
        assertEquals(ChallengeType.INTRO, first.currentChallenge.challenge.type)
        val heartsBefore = repository.getUserProgressDirect()!!.hearts

        // An INTRO card has no answer to check: the ViewModel clears it with no write.
        viewModel.checkAnswer()
        val cleared = viewModel.activeScreen.value as ActiveScreen.Exercise
        assertNotNull("an INTRO card must become continuable", cleared.feedback)

        assertEquals(
            "an INTRO card cost a heart",
            heartsBefore,
            repository.getUserProgressDirect()!!.hearts,
        )
        assertEquals("an INTRO card paid XP", 0, repository.getUserProgressDirect()!!.points)
        assertTrue(
            "an INTRO card was recorded as a mistake",
            repository.getMistakeChallenges(1).isEmpty(),
        )
        assertTrue(
            "an INTRO card was marked complete",
            repository.getCompletedChallengeIds().first().isEmpty(),
        )

        // It advances cleanly to the next challenge, which starts ungraded.
        viewModel.nextChallengeOrFinish()
        val second = viewModel.activeScreen.value as ActiveScreen.Exercise
        assertEquals(1, second.challengeIndex)
        assertEquals(ChallengeType.SELECT, second.currentChallenge.challenge.type)
        assertNull("the next challenge started pre-graded", second.feedback)

        // The card left no phantom retry behind: the lesson finishes on its own.
        answerCurrent(wrong = false)
        viewModel.nextChallengeOrFinish()
        val finished = viewModel.activeScreen.first { it is ActiveScreen.LessonComplete }
        assertTrue(
            "the INTRO card queued a retry round: ${viewModel.activeScreen.value}",
            finished is ActiveScreen.LessonComplete,
        )
    }

    // ----------------------------------------------------------- mistake recovery

    @Test
    fun `a lesson with a miss replays it instead of finishing`() = runTest {
        seedLesson(9101 to ChallengeType.SELECT, 9102 to ChallengeType.SELECT)
        startLesson()

        answerCurrent(wrong = true)
        viewModel.nextChallengeOrFinish()
        assertEquals(1, (viewModel.activeScreen.value as ActiveScreen.Exercise).challengeIndex)

        // Clear the last challenge of the first pass: the lesson must not finish,
        // because the first pass left a miss behind.
        answerCurrent(wrong = false)
        viewModel.nextChallengeOrFinish()

        val review = viewModel.activeScreen.value as? ActiveScreen.Exercise
            ?: error("the lesson finished instead of reviewing its miss: ${viewModel.activeScreen.value}")
        assertEquals(
            "the retry round must replay the missed challenge",
            9101,
            review.currentChallenge.challenge.id,
        )
        assertEquals(0, review.challengeIndex)
        assertEquals("the retry round holds only the missed challenge", 1, review.totalChallenges)
        assertNull("the retry round started pre-graded", review.feedback)
    }

    @Test
    fun `clearing the retry round finishes the lesson`() = runTest {
        seedLesson(9201 to ChallengeType.SELECT, 9202 to ChallengeType.SELECT)
        startLesson()
        answerCurrent(wrong = true)
        viewModel.nextChallengeOrFinish()
        answerCurrent(wrong = false)
        viewModel.nextChallengeOrFinish()
        assertEquals(
            9201,
            (viewModel.activeScreen.value as ActiveScreen.Exercise).currentChallenge.challenge.id,
        )

        // Clear the retry: the queue drains and the lesson is allowed to finish.
        answerCurrent(wrong = false)
        viewModel.nextChallengeOrFinish()
        viewModel.activeScreen.first { it is ActiveScreen.LessonComplete }

        assertTrue(
            "the recovered challenge must be marked complete",
            repository.getCompletedChallengeIds().first().contains(9201),
        )
    }

    @Test
    fun `a miss during the retry round costs no heart and is replayed`() = runTest {
        seedLesson(9301 to ChallengeType.SELECT, 9302 to ChallengeType.SELECT)
        startLesson()

        // The first pass is a normal lesson: its miss does cost a heart.
        answerCurrent(wrong = true)
        viewModel.nextChallengeOrFinish()
        answerCurrent(wrong = false)
        viewModel.nextChallengeOrFinish()
        assertEquals(
            9301,
            (viewModel.activeScreen.value as ActiveScreen.Exercise).currentChallenge.challenge.id,
        )
        val heartsBefore = repository.getUserProgressDirect()!!.hearts
        assertEquals(
            "the first pass must have cost exactly one heart",
            LocalProgressRepository.MAX_HEARTS - 1,
            heartsBefore,
        )

        // The recovery round is heart-free, however many times it is missed...
        answerCurrent(wrong = true)
        assertEquals(
            "a miss in the retry round cost a heart",
            heartsBefore,
            repository.getUserProgressDirect()!!.hearts,
        )

        // ...and the miss is replayed rather than dropped: the loop ends only once
        // the learner clears it.
        viewModel.nextChallengeOrFinish()
        val again = viewModel.activeScreen.value as? ActiveScreen.Exercise
            ?: error("the loop ended with a miss outstanding: ${viewModel.activeScreen.value}")
        assertEquals(9301, again.currentChallenge.challenge.id)

        answerCurrent(wrong = false)
        viewModel.nextChallengeOrFinish()
        val finished = viewModel.activeScreen.first { it is ActiveScreen.LessonComplete }
        assertTrue(
            "clearing the loop must finish the lesson: ${viewModel.activeScreen.value}",
            finished is ActiveScreen.LessonComplete,
        )
    }

    @Test
    fun `clearing mistake recovery loop grants heart refund`() = runTest {
        seedLesson(9401 to ChallengeType.SELECT, 9402 to ChallengeType.SELECT)
        startLesson()

        // First pass: miss the first challenge (costs a heart)
        answerCurrent(wrong = true)
        viewModel.nextChallengeOrFinish()
        answerCurrent(wrong = false)
        viewModel.nextChallengeOrFinish()

        // Now in the recovery round
        val review = viewModel.activeScreen.value as? ActiveScreen.Exercise
            ?: error("expected recovery round")
        assertEquals(9401, review.currentChallenge.challenge.id)

        val heartsBefore = repository.getUserProgressDirect()!!.hearts
        assertEquals(
            "the first pass must have cost exactly one heart",
            LocalProgressRepository.MAX_HEARTS - 1,
            heartsBefore,
        )

        // Clear the recovery round
        answerCurrent(wrong = false)
        viewModel.nextChallengeOrFinish()
        viewModel.activeScreen.first { it is ActiveScreen.LessonComplete }

        // The heart refund is granted
        assertEquals(
            "clearing the recovery loop must refund one heart",
            LocalProgressRepository.MAX_HEARTS,
            repository.getUserProgressDirect()!!.hearts,
        )
    }

    // --------------------------------------------------------------------- helpers

    /**
     * A Spanish course with exactly one lesson and the given challenges, so a lesson's
     * progression is fully determined by the test. SELECT challenges get a right and a
     * wrong option; an INTRO card gets none, because it presents rather than asks.
     */
    private suspend fun seedLesson(vararg challenges: Pair<Int, ChallengeType>) =
        withContext(Dispatchers.IO) {
            db.clearAllTables()
            db.courseDao().insertCourses(listOf(CourseEntity(id = 1, title = "Spanish", imageSrc = "🇪🇸")))
            db.courseDao().insertUnits(
                listOf(UnitEntity(id = 10, courseId = 1, title = "Unit 1: A1", description = "", orderIndex = 0)),
            )
            db.courseDao().insertLessons(
                listOf(LessonEntity(id = 101, unitId = 10, title = "Lesson: basics", orderIndex = 0)),
            )
            db.lessonDao().insertChallenges(
                challenges.mapIndexed { index, (id, type) ->
                    ChallengeEntity(id = id, lessonId = 101, type = type, question = "Q$id", orderIndex = index)
                },
            )
            db.lessonDao().insertOptions(
                challenges.filter { (_, type) -> type != ChallengeType.INTRO }.flatMap { (id, _) ->
                    listOf(
                        ChallengeOptionEntity(id = id * 10 + 1, challengeId = id, text = "right", correct = true),
                        ChallengeOptionEntity(id = id * 10 + 2, challengeId = id, text = "wrong", correct = false),
                    )
                },
            )
            repository.resetAllProgress()
            repository.setSoundEnabled(false)
            repository.setHapticsEnabled(false)
        }

    /** Opens the seeded lesson, waiting for the profile so sound stays off. */
    private suspend fun startLesson(): ActiveScreen.Exercise {
        viewModel.userProgress.first { it?.soundEnabled == false }
        viewModel.startLesson(101)
        return viewModel.activeScreen.first { it is ActiveScreen.Exercise } as ActiveScreen.Exercise
    }

    /** Answers the current challenge right or wrong and waits for its feedback. */
    private suspend fun answerCurrent(wrong: Boolean) {
        val exercise = viewModel.activeScreen.value as? ActiveScreen.Exercise
            ?: error("no exercise is on screen")
        val option = exercise.currentChallenge.options.first { it.correct != wrong }
        viewModel.selectOption(option.id)
        viewModel.checkAnswer()
        // The graded screen is what enables CONTINUE, so the drive waits for it
        // exactly as a learner would — and by then the answer's writes have landed.
        viewModel.activeScreen.first { (it as? ActiveScreen.Exercise)?.feedback != null }
    }
}

package com.openlingo.app

import androidx.test.core.app.ApplicationProvider
import com.openlingo.app.data.local.OpenLingoDatabase
import com.openlingo.app.data.local.entities.ChallengeEntity
import com.openlingo.app.data.local.entities.ChallengeOptionEntity
import com.openlingo.app.data.local.entities.CourseEntity
import com.openlingo.app.data.local.entities.LessonEntity
import com.openlingo.app.data.local.entities.UnitEntity
import com.openlingo.app.data.local.models.ChallengeType
import com.openlingo.app.data.repository.AnswerResult
import com.openlingo.app.data.repository.ChallengeWithOptions
import com.openlingo.app.data.repository.LocalProgressRepository
import com.openlingo.app.ui.ActiveScreen
import com.openlingo.app.ui.MainViewModel
import com.openlingo.app.ui.MatchPairs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * The app without its `onCreate` seeding and without its alarms: every test here
 * owns the corpus it runs against, and a background seed would race the one the
 * test writes.
 */
class PlacementTestApplication : OpenLingoApplication() {
    override fun onCreate() {
        // Deliberately nothing.
    }
}

/**
 * The placement run's own invariants, driven through the real ViewModel over the
 * real Room database.
 *
 * Four defects lived in this flow, all of them invisible to the answer checks:
 *
 * 1. The score fast-forward was course-blind, so a Japanese run wrote Spanish
 *    unit ids — unlocking nothing for the learner and completing 49 Spanish
 *    challenges.
 * 2. A match-pairs board grades itself when its last pair clears, and it
 *    submitted that answer without running the checkpoint tally, so every board
 *    in a placement run scored zero.
 * 3. `exitExercise` left the checkpoint flag set, so the next ordinary lesson
 *    ended in the checkpoint branch and its CONTINUE granted a placement unlock
 *    for a test that was never finished.
 * 4. That same board submitted its answer outside practice, so placement paid it
 *    10 XP, advanced the daily quest and completed the challenge — for one
 *    exercise type only.
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = PlacementTestApplication::class)
class PlacementFlowTest {

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
        // The database file can outlive a test method, and the previous test may
        // have replaced the corpus; every test starts from an empty one.
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


    // ------------------------------------------------------------------ finding 1

    @Test
    fun `a japanese placement run unlocks japanese units and no spanish ones`() = runTest {
        switchTo(courseId = 2)
        val spanishChallenges = challengeIdsOf(courseId = 1)

        startPlacement()
        viewModel.applyPlacementScore(correct = 20, total = 25) // 80%: the N4 tier
        viewModel.activeScreen.first { it is ActiveScreen.LessonMap }

        val completed = repository.getCompletedChallengeIds().first().toSet()
        val n5Challenges = repository.getChallengesForUnits((20..27).toList())
            .map { it.challenge.id }
            .toSet()
        assertEquals("the N4 tier must complete all of N5", n5Challenges, completed)
        assertTrue(
            "a japanese run completed spanish challenges: ${completed intersect spanishChallenges}",
            (completed intersect spanishChallenges).isEmpty(),
        )

        // The partial tier is course-correct too: mid-N5 completes 20-23 only.
        repository.resetAllProgress()
        repository.setSoundEnabled(false)
        repository.setHapticsEnabled(false)
        switchTo(courseId = 2)

        startPlacement()
        viewModel.applyPlacementScore(correct = 13, total = 25) // 52%: mid-N5
        viewModel.activeScreen.first { it is ActiveScreen.LessonMap }

        val midN5Challenges = repository.getChallengesForUnits((20..23).toList())
            .map { it.challenge.id }
            .toSet()
        assertEquals(
            "the mid-N5 tier must complete units 20-23",
            midN5Challenges,
            repository.getCompletedChallengeIds().first().toSet(),
        )

        // A fast-forward whose units match nothing must fail loudly: a silent no-op
        // would look like a successful unlock and leave the learner where they were.
        val staleUnit = runCatching { repository.completeChallengesUpToUnit(listOf(9999)) }.exceptionOrNull()
        assertTrue("a fast-forward over a stale unit must throw, was $staleUnit", staleUnit is IllegalStateException)
        val noUnits = runCatching { repository.completeChallengesUpToUnit(emptyList()) }.exceptionOrNull()
        assertTrue("a fast-forward with no units must throw, was $noUnits", noUnits is IllegalStateException)
    }

    // ------------------------------------------------------------------ finding 2

    @Test
    fun `a completed match-pairs board scores in a placement run`() = runTest {
        seedOneMatchPairsBoard()
        switchTo(courseId = 2)

        val exercise = startPlacement()
        assertEquals(ChallengeType.MATCH_PAIRS, exercise.currentChallenge.challenge.type)
        clearBoard(exercise)
        awaitAnswerLanded()

        viewModel.nextChallengeOrFinish()
        val result = viewModel.activeScreen.first { it is ActiveScreen.CheckpointResult }
            as ActiveScreen.CheckpointResult

        assertEquals("the board's answer did not count", 1, result.correct)
        assertEquals(1, result.total)
    }

    // ------------------------------------------------------------------ finding 3

    @Test
    fun `an abandoned placement run cannot turn the next lesson into a checkpoint`() = runTest {
        switchTo(courseId = 2)

        // (1) Leave a checkpoint with the X, then take an ordinary lesson.
        viewModel.startCheckpoint("N5")
        viewModel.activeScreen.first { it is ActiveScreen.Exercise }
        viewModel.exitExercise()
        viewModel.startLesson(lessonId = 401)
        viewModel.activeScreen.first { it is ActiveScreen.Exercise }
        driveToTheEnd()
        assertTrue(
            "a lesson after an abandoned checkpoint ended as ${viewModel.activeScreen.value}",
            viewModel.activeScreen.value is ActiveScreen.LessonComplete,
        )

        // (2) Starting a lesson directly clears the checkpoint state too.
        viewModel.startCheckpoint("N5")
        viewModel.activeScreen.first { it is ActiveScreen.Exercise }
        viewModel.startLesson(lessonId = 402)
        viewModel.activeScreen.first { it is ActiveScreen.Exercise }
        driveToTheEnd()
        assertTrue(
            "a lesson started inside a checkpoint ended as ${viewModel.activeScreen.value}",
            viewModel.activeScreen.value is ActiveScreen.LessonComplete,
        )

        // (3) A practice session started after a checkpoint is not scored as one.
        repository.queueMistakes(listOf(62029))
        viewModel.startCheckpoint("N5")
        viewModel.activeScreen.first { it is ActiveScreen.Exercise }
        viewModel.startMistakePractice()
        viewModel.activeScreen.first { it is ActiveScreen.Exercise }
        driveToTheEnd()
        assertEquals(
            "a practice session ended as a checkpoint",
            ActiveScreen.LessonMap,
            viewModel.activeScreen.value,
        )
    }

    // ------------------------------------------------------------------ finding 4

    @Test
    fun `a placement answer is practice and pays nothing`() = runTest {
        seedOneMatchPairsBoard()
        switchTo(courseId = 2)

        val exercise = startPlacement()
        val boardId = exercise.currentChallenge.challenge.id
        clearBoard(exercise)
        awaitAnswerLanded()

        assertEquals("placement paid xp", 0, repository.getUserProgressDirect()!!.points)
        assertEquals("placement advanced the daily quest", 0, repository.getTodayXp().first())
        assertTrue(
            "placement completed the challenge",
            repository.getCompletedChallengeIds().first().isEmpty(),
        )

        // Control: the same answer outside a placement run does write all three, so
        // the reads above are looking at a path that really does record answers.
        val earned = repository.submitAnswer(boardId, isCorrect = true, isPractice = false)
        assertTrue("the control answer was not graded correct", earned is AnswerResult.Correct)
        assertEquals(
            LocalProgressRepository.POINTS_PER_CHALLENGE,
            repository.getUserProgressDirect()!!.points,
        )
    }

    // -------------------------------------------------------------------- the ramp

    @Test
    fun `the spanish placement run ramps from A1 to B1`() = runTest {
        startPlacement()
        val bands = driveRecordingBands(bandOfChallenge(courseId = 1))
        assertEquals(
            "the Spanish run must ramp 12 A1 + 8 A2 + 5 B1, was $bands",
            List(12) { "A1" } + List(8) { "A2" } + List(5) { "B1" },
            bands,
        )
    }

    @Test
    fun `the japanese placement run ramps from N5 to N4`() = runTest {
        switchTo(courseId = 2)
        startPlacement()
        val bands = driveRecordingBands(bandOfChallenge(courseId = 2))
        assertEquals(
            "the Japanese run must ramp 14 N5 + 11 N4, was $bands",
            List(14) { "N5" } + List(11) { "N4" },
            bands,
        )
    }

    @Test
    fun `a learner correct only on the beginner band starts from zero`() = runTest {
        // The ramp puts 12 A1 challenges in the 25-challenge run; a learner who
        // answers only those scores 12/25 = 48%, under the 50% partial tier.
        startPlacement()
        viewModel.applyPlacementScore(correct = 12, total = 25)
        viewModel.activeScreen.first { it is ActiveScreen.LessonMap }
        val completed = repository.getCompletedChallengeIds().first()
        assertTrue("a 48% placement run must fast-forward nothing, completed $completed", completed.isEmpty())
    }

    @Test
    fun `a perfect spanish placement run fast-forwards the advanced unlock`() = runTest {
        val a1Challenges = repository.getChallengesForUnits(listOf(10, 11, 12, 13, 14, 15, 16, 17))
            .map { it.challenge.id }.toSet()
        startPlacement()
        viewModel.applyPlacementScore(correct = 25, total = 25)
        viewModel.activeScreen.first { it is ActiveScreen.LessonMap }
        val completed = repository.getCompletedChallengeIds().first().toSet()
        assertEquals("a perfect run must complete all of A1", a1Challenges, completed intersect a1Challenges)
    }

    // --------------------------------------------------------------------- helpers

    private suspend fun switchTo(courseId: Int) {
        repository.switchCourse(courseId)
        viewModel.userProgress.first { it?.activeCourseId == courseId }
    }

    private suspend fun challengeIdsOf(courseId: Int): Set<Int> {
        val unitIds = repository.getUnitsWithLessonsForCourse(courseId).first().map { it.unit.id }
        return repository.getChallengesForUnits(unitIds).map { it.challenge.id }.toSet()
    }

    private suspend fun startPlacement(): ActiveScreen.Exercise {
        viewModel.startPlacementTest()
        return viewModel.activeScreen.first { it is ActiveScreen.Exercise } as ActiveScreen.Exercise
    }

    /** Taps every pair of the board the exercise is showing. */
    private fun clearBoard(exercise: ActiveScreen.Exercise) {
        MatchPairs.pairings(exercise.currentChallenge.options).forEach { (first, second) ->
            viewModel.selectPairTile(first)
            viewModel.selectPairTile(second)
        }
    }

    /**
     * Answers every challenge of the running session correctly, waiting for each
     * answer before advancing, and stops when the session ends.
     *
     * The session's last answer ends it asynchronously (lesson complete,
     * checkpoint result or the practice tab), so the loop waits for that screen
     * rather than spinning on the challenge it just answered.
     */
    private suspend fun driveToTheEnd() {
        var answered = 0
        while (true) {
            val screen = viewModel.activeScreen.value as? ActiveScreen.Exercise ?: return
            answerCurrentCorrectly()
            viewModel.nextChallengeOrFinish()
            check(++answered < 200) { "the session did not end after $answered answers" }
            val next = viewModel.activeScreen.value
            if (next is ActiveScreen.Exercise && next.challengeIndex == screen.challengeIndex) {
                viewModel.activeScreen.first { it !is ActiveScreen.Exercise }
                return
            }
        }
    }

    private suspend fun answerCurrentCorrectly() {
        val exercise = viewModel.activeScreen.value as ActiveScreen.Exercise
        val challenge = exercise.currentChallenge.challenge
        val options = exercise.currentChallenge.options
        when (challenge.type) {
            ChallengeType.WORD_BANK -> options.filter { it.correct }.sortedBy { it.id }
                .forEach { viewModel.selectWordTile(it.id) }
            ChallengeType.FILL_BLANK -> viewModel.updateTypedAnswer(options.first { it.correct }.text)
            ChallengeType.MATCH_PAIRS -> {
                clearBoard(exercise)
                // The board submits its own answer in the ViewModel's scope; wait for
                // that write so no test ends, or advances, with it still in flight.
                awaitAnswerLanded()
            }
            else -> viewModel.selectOption(options.first { it.correct }.id)
        }
        if (challenge.type != ChallengeType.MATCH_PAIRS) {
            viewModel.checkAnswer()
            // The graded screen is what enables CONTINUE, so the drive waits for it
            // exactly as a learner would.
            viewModel.activeScreen.first { (it as? ActiveScreen.Exercise)?.feedback != null }
        }
    }

    /**
     * Waits for the answer's write path to land. `submitAnswer` records the
     * per-type stats row before any completion or XP write, so observing that row
     * and then giving the IO pool a moment is what lets the assertions above mean
     * something: the writes placement must not make would follow it.
     *
     * Runs on IO so the timeout and the grace are real time, not the test's
     * virtual clock — the writes being awaited are on the IO pool either way.
     */
    private suspend fun awaitAnswerLanded() = withContext(Dispatchers.IO) {
        withTimeout(5_000) {
            repository.getExerciseTypeStats().first { stats -> stats.any { it.attempts > 0 } }
        }
        delay(200)
    }

    /**
     * A corpus of exactly one match-pairs board on the Japanese course. The
     * placement sampler takes 25 of the course's challenges, so a corpus of one
     * challenge is what makes a placement session deterministic.
     */
    private suspend fun seedOneMatchPairsBoard() = withContext(Dispatchers.IO) {
        db.clearAllTables()
        db.courseDao().insertCourses(listOf(CourseEntity(id = 2, title = "Japanese", imageSrc = "🇯🇵")))
        db.courseDao().insertUnits(
            listOf(UnitEntity(id = 20, courseId = 2, title = "Unit 1: N5", description = "", orderIndex = 0)),
        )
        db.courseDao().insertLessons(
            listOf(LessonEntity(id = 401, unitId = 20, title = "Lesson: passive and causative", orderIndex = 0)),
        )
        db.lessonDao().insertChallenges(
            listOf(
                ChallengeEntity(
                    id = 62029,
                    lessonId = 401,
                    type = ChallengeType.MATCH_PAIRS,
                    question = "Match the passive with the causative of the same verb",
                    orderIndex = 0,
                ),
            ),
        )
        db.lessonDao().insertOptions(
            listOf(
                "休まれる" to 6300581,
                "休ませる" to 6300582,
                "待たれる" to 6300583,
                "待たせる" to 6300584,
                "飲まれる" to 6300585,
                "飲ませる" to 6300586,
                "書かれる" to 6300587,
                "書かせる" to 6300588,
            ).map { (text, id) ->
                ChallengeOptionEntity(id = id, challengeId = 62029, text = text, correct = true)
            },
        )
        repository.resetAllProgress()
        repository.setSoundEnabled(false)
        repository.setHapticsEnabled(false)
    }

    /**
     * Maps a challenge to the band its unit belongs to, using the same level→unit
     * map the placement sampler draws from.
     */
    private suspend fun bandOfChallenge(courseId: Int): (ChallengeWithOptions) -> String {
        val units = repository.getUnitsWithLessonsForCourse(courseId).first()
        val lessonToBand = units.flatMap { u ->
            u.lessons.map { it.id to bandForUnit(courseId, u.unit.id) }
        }.toMap()
        return { challenge -> lessonToBand[challenge.challenge.lessonId] ?: "?" }
    }

    private fun bandForUnit(courseId: Int, unitId: Int): String = when {
        courseId == 1 && unitId in 10..17 -> "A1"
        courseId == 1 && unitId in 18..19 -> "A2"
        courseId == 1 && unitId in 30..37 -> "B1"
        courseId == 2 && unitId in 20..27 -> "N5"
        courseId == 2 -> "N4"
        else -> "?"
    }

    /**
     * Answers every challenge of the running session correctly, recording the band
     * of each challenge as it is shown, and stops when the session ends.
     */
    private suspend fun driveRecordingBands(bandOf: (ChallengeWithOptions) -> String): List<String> {
        val bands = mutableListOf<String>()
        var answered = 0
        while (true) {
            val screen = viewModel.activeScreen.value as? ActiveScreen.Exercise ?: return bands
            bands.add(bandOf(screen.currentChallenge))
            answerCurrentCorrectly()
            viewModel.nextChallengeOrFinish()
            check(++answered < 200) { "the session did not end after $answered answers" }
            val next = viewModel.activeScreen.value
            if (next is ActiveScreen.Exercise && next.challengeIndex == screen.challengeIndex) {
                viewModel.activeScreen.first { it !is ActiveScreen.Exercise }
                return bands
            }
        }
    }
}

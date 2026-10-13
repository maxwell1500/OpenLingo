package com.openlingo.app.data.repository

import com.openlingo.app.data.local.OpenLingoDatabase
import com.openlingo.app.data.local.entities.ChallengeEntity
import com.openlingo.app.data.local.entities.ChallengeOptionEntity
import com.openlingo.app.data.local.entities.ChallengeProgressEntity
import com.openlingo.app.data.local.entities.CourseEntity
import com.openlingo.app.data.local.entities.LessonEntity
import com.openlingo.app.data.local.entities.UnitEntity
import com.openlingo.app.data.local.entities.UserProgressEntity
import com.openlingo.app.data.local.entities.UnitWithLessons
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import com.openlingo.app.data.local.models.BackupJson
import com.openlingo.app.data.local.models.CharacterMasteryBackup
import com.openlingo.app.data.local.models.DailyActivityBackup
import com.openlingo.app.data.local.models.MistakeBackup
import com.openlingo.app.data.local.models.OpenLingoBackup
import com.openlingo.app.data.local.models.UserProgressBackup
import com.openlingo.app.data.local.models.CheckpointScoreBackup
import com.openlingo.app.data.local.models.VocabScheduleBackup
import com.openlingo.app.data.local.models.ChallengeType
import com.openlingo.app.data.local.entities.CharacterMasteryEntity
import com.openlingo.app.data.local.entities.MistakeEntity
import com.openlingo.app.data.local.entities.CheckpointScoreEntity
import com.openlingo.app.data.local.entities.VocabScheduleEntity
import com.openlingo.app.data.local.entities.ExerciseTypeStatsEntity
import com.openlingo.app.data.fsrs.FsrsScheduler
import com.openlingo.app.dictionary.Dictionary
import com.openlingo.app.dictionary.DictionaryIndex

data class ChallengeWithOptions(
    val challenge: ChallengeEntity,
    val options: List<ChallengeOptionEntity>,
    val isCompleted: Boolean,
)

/**
 * WI-13: the daily quest, measured against the learner's own goal rather than
 * a fixed constant. [isComplete] is the single definition of "quest met", used
 * by the lesson-map card, the home-screen widget, and the tests alike.
 */
data class DailyQuest(
    val xp: Int,
    val goal: Int,
) {
    val isComplete: Boolean get() = xp >= goal
}

sealed interface AnswerResult {
    data class Correct(val pointsGained: Int, val totalPoints: Int, val hearts: Int, val streakRepaired: Boolean = false) : AnswerResult
    data class Incorrect(val remainingHearts: Int) : AnswerResult
}

class LocalProgressRepository(private val database: OpenLingoDatabase) {

    companion object {
        const val GUEST_USER_ID = "guest_local"
        const val MAX_HEARTS = 5
        const val POINTS_PER_CHALLENGE = 10
        /**
         * The daily XP target a learner gets before choosing one: the value the
         * quest was hardcoded to before WI-13. Falls back to this only when no
         * profile row exists yet.
         */
        const val DAILY_QUEST_XP = 30
        /** The steps a learner may pick their daily XP goal from. */
        val DAILY_QUEST_XP_OPTIONS = listOf(10, 20, 30, 50, 100)
        const val PERFECT_BONUS = 5
    }

    private val courseDao = database.courseDao()
    private val lessonDao = database.lessonDao()
    private val userProgressDao = database.userProgressDao()
    private val challengeProgressDao = database.challengeProgressDao()
    private val characterMasteryDao = database.characterMasteryDao()
    private val mistakeDao = database.mistakeDao()
    private val dailyActivityDao = database.dailyActivityDao()
    private val checkpointScoreDao = database.checkpointScoreDao()
    private val exerciseTypeStatsDao = database.exerciseTypeStatsDao()
    private val vocabScheduleDao = database.vocabScheduleDao()
    /**
     * Initializes the local database: seeds courses and ensures a guest user profile exists.
     */
    suspend fun initializeIfNeeded() = withContext(Dispatchers.IO) {
        // 1. Ensure Guest user exists in user_progress
        val existingUser = userProgressDao.getUserProgressDirect(GUEST_USER_ID)
        if (existingUser == null) {
            userProgressDao.upsertUserProgress(
                UserProgressEntity(
                    userId = GUEST_USER_ID,
                    userName = "Guest Learner",
                    userImageSrc = "/mascot.svg",
                    activeCourseId = 1, // Default to Spanish
                    hearts = MAX_HEARTS,
                    points = 0,
                    streak = 1,
                    lastActiveDate = java.time.LocalDate.now().toString(),
                )
            )
        } else {
            refreshDailyState(existingUser.userId)
        }


        // 2. Seed Spanish & Japanese courses and expanded A1 units
        val sampleUnit = courseDao.getUnitsForCourseDirect(1)
        if (sampleUnit.size < 8) {
            seedSpanishCourse()
            seedJapaneseCourse()
        }
        // Insertions are REPLACE-on-conflict: always seed curricula so newly
        // added challenges/units roll out without requiring a full wipe.
        seedExpandedCurricula()
        seedAdvancedCurricula()
        seedB1Curricula()
        seedInitialVocabSchedule()
    }
    /**
     * Day rollover: reset streak when a full day was missed, refill hearts every
     * new day. Safe to call from app start and from the midnight alarm.
     */
    suspend fun refreshDailyState(userId: String = GUEST_USER_ID) = withContext(Dispatchers.IO) {
        val existingUser = userProgressDao.getUserProgressDirect(userId) ?: return@withContext
        // Maintain streak based on date
        val today = java.time.LocalDate.now()
        val lastActive = runCatching { java.time.LocalDate.parse(existingUser.lastActiveDate) }.getOrNull()
        if (lastActive != null) {
            val daysBetween = java.time.temporal.ChronoUnit.DAYS.between(lastActive, today)
            if (daysBetween > 1) {
                // Missed a full day: stash the broken streak for repair, reset to 1.
                // A newer break overwrites an unrepaired older one.
                userProgressDao.setBrokenStreak(existingUser.userId, existingUser.streak)
                userProgressDao.updateStreak(existingUser.userId, 1, today.toString())
            }
            if (daysBetween >= 1) {
                // New day: hearts refill to full (daily reset)
                userProgressDao.updateHearts(existingUser.userId, MAX_HEARTS)
            }
        }
    }

    fun getUserProgress(): Flow<UserProgressEntity?> =
        userProgressDao.getUserProgress(GUEST_USER_ID).flowOn(Dispatchers.IO)

    fun getAllCourses(): Flow<List<CourseEntity>> =
        courseDao.getAllCourses().flowOn(Dispatchers.IO)

    suspend fun getUserProgressDirect(): UserProgressEntity? = withContext(Dispatchers.IO) {
        userProgressDao.getUserProgressDirect(GUEST_USER_ID)
    }

    fun getUnitsForCourse(courseId: Int): Flow<List<UnitEntity>> =
        courseDao.getUnitsForCourse(courseId).flowOn(Dispatchers.IO)

    /**
     * Lessons where every challenge is completed by the guest user.
     * One-shot aggregation for the Profile tab; cheap on local SQLite.
     */
    suspend fun getCompletedLessonCount(): Int = withContext(Dispatchers.IO) {
        challengeProgressDao.getCompletedLessonIdsDirect(GUEST_USER_ID).size
    }

    fun getCompletedLessonIds(): Flow<List<Int>> =
        challengeProgressDao.getCompletedLessonIds(GUEST_USER_ID).flowOn(Dispatchers.IO)

    fun getUnitsWithLessonsForCourse(courseId: Int): Flow<List<UnitWithLessons>> =
        courseDao.getUnitsWithLessonsForCourse(courseId).flowOn(Dispatchers.IO)

    fun getLessonsForUnit(unitId: Int): Flow<List<LessonEntity>> =
        courseDao.getLessonsForUnit(unitId).flowOn(Dispatchers.IO)

    fun getCompletedChallengeIds(): Flow<List<Int>> =
        challengeProgressDao.getCompletedChallengeIds(GUEST_USER_ID).flowOn(Dispatchers.IO)

    suspend fun getChallengesForLesson(lessonId: Int): List<ChallengeWithOptions> =
        withContext(Dispatchers.IO) {
            val challenges = lessonDao.getChallengesForLesson(lessonId)
            challenges.map { challenge ->
                val options = lessonDao.getOptionsForChallenge(challenge.id)
                val isCompleted = challengeProgressDao.isChallengeCompleted(GUEST_USER_ID, challenge.id)
                ChallengeWithOptions(
                    challenge = challenge,
                    options = options,
                    isCompleted = isCompleted,
                )
            }
        }

    /**
     * The challenges to re-ask in practice, limited to [courseId]'s mistakes.
     *
     * Reading the whole table here launched Japanese prompts into a Spanish
     * practice session (and the reverse); the course is resolved through the
     * mistake's lesson → unit, exactly as the review list does.
     */
    suspend fun getMistakeChallenges(courseId: Int): List<ChallengeWithOptions> =
        withContext(Dispatchers.IO) {
            val mistakes = mistakeDao.getMistakesForCourseDirect(courseId)
            val challengeIds = mistakes.map { it.challengeId }.distinct()
            if (challengeIds.isEmpty()) return@withContext emptyList()
            val challenges = lessonDao.getChallengesByIds(challengeIds)
            val statsMap = exerciseTypeStatsDao.getAllStatsDirect().associateBy { it.type }
            // Sort weakest type first (lowest accuracy percent), then by challenge order
            val sortedChallenges = challenges.sortedBy { challenge ->
                statsMap[challenge.type.rawValue]?.accuracyPercent ?: 100
            }
            sortedChallenges.map { challenge ->
                val options = lessonDao.getOptionsForChallenge(challenge.id)
                val isCompleted = challengeProgressDao.isChallengeCompleted(GUEST_USER_ID, challenge.id)
                ChallengeWithOptions(
                    challenge = challenge,
                    options = options,
                    isCompleted = isCompleted,
                )
            }
        }

    /** Clears the active course's review queue, not every course's. */
    suspend fun clearMistakes(courseId: Int) = withContext(Dispatchers.IO) {
        mistakeDao.clearMistakesForCourse(courseId)
    }

    suspend fun switchCourse(courseId: Int) = withContext(Dispatchers.IO) {
        userProgressDao.setActiveCourse(GUEST_USER_ID, courseId)
    }

    suspend fun setShowRomaji(showRomaji: Boolean) = withContext(Dispatchers.IO) {
        val current = userProgressDao.getUserProgressDirect(GUEST_USER_ID)
        val userId = current?.userId ?: GUEST_USER_ID
        userProgressDao.setShowRomaji(userId, showRomaji)
    }

    /**
     * Writes the learner's single completion row for one challenge.
     *
     * The row id is auto-generated, so Room's REPLACE on `markChallengeCompleted`
     * never fires for a re-answer and every call appended another row. Duplicate
     * (userId, challengeId) rows inflate the GROUP BY count behind
     * `getCompletedLessonIds`, which un-completes a finished lesson and keeps the
     * next one locked. Delete this learner's row for the challenge, then insert.
     */
    private suspend fun markChallengeCompletedOnce(challengeId: Int) {
        challengeProgressDao.clearChallengeFor(GUEST_USER_ID, challengeId)
        challengeProgressDao.markChallengeCompleted(
            ChallengeProgressEntity(
                userId = GUEST_USER_ID,
                challengeId = challengeId,
                completed = true,
                synced = false,
            )
        )
    }

    suspend fun submitAnswer(challengeId: Int, isCorrect: Boolean, isPractice: Boolean = false): AnswerResult =
        withContext(Dispatchers.IO) {
            val user = userProgressDao.getUserProgressDirect(GUEST_USER_ID)
                ?: UserProgressEntity(userId = GUEST_USER_ID, activeCourseId = 1, hearts = MAX_HEARTS, points = 0)

            // Track accuracy per exercise type
            lessonDao.getChallengeById(challengeId)?.let { challenge ->
                val typeName = challenge.type.rawValue
                val currentStats = exerciseTypeStatsDao.getStatsForType(typeName)
                val newAttempts = (currentStats?.attempts ?: 0) + 1
                val newCorrect = (currentStats?.correct ?: 0) + if (isCorrect) 1 else 0
                exerciseTypeStatsDao.upsertStats(
                    ExerciseTypeStatsEntity(
                        type = typeName,
                        attempts = newAttempts,
                        correct = newCorrect,
                    )
                )
            }

            if (isCorrect) {
                if (!isPractice) {
                    markChallengeCompletedOnce(challengeId)
                }
                // Answered right: clear any pending mistake for this challenge.
                mistakeDao.clearMistake(challengeId)
                val today = java.time.LocalDate.now().toString()
                var streakRepaired = false
                if (!isPractice) {
                    userProgressDao.addPoints(GUEST_USER_ID, POINTS_PER_CHALLENGE)
                    dailyActivityDao.addXp(today, POINTS_PER_CHALLENGE)
                    val newPoints = user.points + POINTS_PER_CHALLENGE
                    if (user.lastActiveDate != today) {
                        val lastActive = runCatching { java.time.LocalDate.parse(user.lastActiveDate) }.getOrNull()
                        val consecutive = lastActive?.plusDays(1)?.toString() == today
                        userProgressDao.updateStreak(
                            GUEST_USER_ID,
                            if (consecutive) user.streak + 1 else 1,
                            today,
                        )
                    }
                    if (user.brokenStreak > 0) {
                        userProgressDao.updateStreak(GUEST_USER_ID, user.brokenStreak + 1, today)
                        userProgressDao.setBrokenStreak(GUEST_USER_ID, 0)
                        streakRepaired = true
                    }
                    AnswerResult.Correct(
                        pointsGained = POINTS_PER_CHALLENGE,
                        totalPoints = newPoints,
                        hearts = user.hearts,
                        streakRepaired = streakRepaired,
                    )
                } else {
                    AnswerResult.Correct(
                        pointsGained = 0,
                        totalPoints = user.points,
                        hearts = user.hearts,
                        streakRepaired = false,
                    )
                }
            } else {
                if (!isPractice) {
                    val challenge = lessonDao.getChallengeById(challengeId)
                    if (challenge?.type == ChallengeType.LISTEN) {
                        // LISTEN mistakes are audio mis-hears, not knowledge gaps.
                        // Record the mistake for review but do NOT deduct hearts.
                        mistakeDao.upsertMistake(
                            com.openlingo.app.data.local.entities.MistakeEntity(
                                challengeId = challengeId,
                                lessonId = challenge.lessonId,
                            )
                        )
                        AnswerResult.Incorrect(remainingHearts = user.hearts)
                    } else {
                        val newHearts = (user.hearts - 1).coerceAtLeast(0)
                        userProgressDao.updateHearts(GUEST_USER_ID, newHearts)
                        mistakeDao.upsertMistake(
                            com.openlingo.app.data.local.entities.MistakeEntity(
                                challengeId = challengeId,
                                lessonId = challenge?.lessonId ?: 0,
                            )
                        )
                        AnswerResult.Incorrect(remainingHearts = newHearts)
                    }
                } else {
                    // Practice mode: still record the miss so it stays in the queue,
                    // but no heart loss.
                    lessonDao.getChallengeById(challengeId)?.let { challenge ->
                        mistakeDao.upsertMistake(
                            com.openlingo.app.data.local.entities.MistakeEntity(
                                challengeId = challengeId,
                                lessonId = challenge.lessonId,
                            )
                        )
                    }
                    AnswerResult.Incorrect(remainingHearts = user.hearts)
                }
            }
        }

    /** The learner's own daily XP goal; [DAILY_QUEST_XP] until they pick one. */
    fun getDailyQuestGoal(): Flow<Int> =
        getUserProgress()
            .map { it?.dailyQuestGoal ?: DAILY_QUEST_XP }
            .flowOn(Dispatchers.IO)

    /** The learner's own daily XP goal, read once. */
    suspend fun getDailyQuestGoalDirect(): Int = withContext(Dispatchers.IO) {
        userProgressDao.getUserProgressDirect(GUEST_USER_ID)?.dailyQuestGoal ?: DAILY_QUEST_XP
    }

    /** Today's XP against the learner's own goal. */
    fun getDailyQuest(): Flow<DailyQuest> =
        combine(getTodayXp(), getDailyQuestGoal()) { xp, goal ->
            DailyQuest(xp = xp, goal = goal)
        }

    /** Today's quest, read once; backs the home-screen widget. */
    suspend fun getDailyQuestDirect(): DailyQuest = withContext(Dispatchers.IO) {
        DailyQuest(
            xp = dailyActivityDao.getXpDirect(java.time.LocalDate.now().toString()) ?: 0,
            goal = getDailyQuestGoalDirect(),
        )
    }

    suspend fun setDailyQuestGoal(goal: Int) = withContext(Dispatchers.IO) {
        require(goal in DAILY_QUEST_XP_OPTIONS) { "daily goal must be one of $DAILY_QUEST_XP_OPTIONS, was $goal" }
        userProgressDao.setDailyQuestGoal(GUEST_USER_ID, goal)
    }

    /** XP earned today (local date); backs the daily quest card. */
    fun getTodayXp(): Flow<Int> =
        dailyActivityDao.getXp(java.time.LocalDate.now().toString())
            .map { it ?: 0 }
            .flowOn(Dispatchers.IO)

    /** Flawless-lesson bonus; call once when a lesson finishes with zero misses. */
    suspend fun awardPerfectBonus(): Int = withContext(Dispatchers.IO) {
        userProgressDao.addPoints(GUEST_USER_ID, PERFECT_BONUS)
        dailyActivityDao.addXp(java.time.LocalDate.now().toString(), PERFECT_BONUS)
        PERFECT_BONUS
    }

    suspend fun refillHearts() = withContext(Dispatchers.IO) {
        userProgressDao.updateHearts(GUEST_USER_ID, MAX_HEARTS)
    }
    suspend fun setSoundEnabled(enabled: Boolean) = withContext(Dispatchers.IO) {
        userProgressDao.setSoundEnabled(GUEST_USER_ID, enabled)
    }

    suspend fun setHapticsEnabled(enabled: Boolean) = withContext(Dispatchers.IO) {
        userProgressDao.setHapticsEnabled(GUEST_USER_ID, enabled)
    }

    suspend fun setThemeAccent(accent: String) = withContext(Dispatchers.IO) {
        userProgressDao.setThemeAccent(GUEST_USER_ID, accent)
    }

    suspend fun setThemeMode(mode: String) = withContext(Dispatchers.IO) {
        userProgressDao.setThemeMode(GUEST_USER_ID, mode)
    }

    suspend fun setOnboardingSeen() = withContext(Dispatchers.IO) {
        userProgressDao.setOnboardingSeen(GUEST_USER_ID, true)
    }

    /**
     * Fast-tracks the learner by marking every challenge of [unitIds] complete.
     * Used by the placement test, which derives those units from the course's own
     * levels — Spanish A1 is 10-17, Japanese N5 is 20-27.
     *
     * Taking the units rather than a tier id is what the old shape got wrong: it
     * hardcoded the Spanish lists for both courses, so a Japanese run unlocked
     * nothing for the learner and marked 49 Spanish challenges complete instead,
     * and it also named units 1 and 2, which no course has.
     *
     * Throws rather than writing nothing when the units match no challenge: a
     * silent no-op, or a write against the wrong course, is a corrupted save that
     * nothing downstream can see.
     */
    suspend fun completeChallengesUpToUnit(unitIds: List<Int>) = withContext(Dispatchers.IO) {
        check(unitIds.isNotEmpty()) { "placement fast-forward has no units" }
        val challenges = lessonDao.getChallengesForUnits(unitIds)
        check(challenges.isNotEmpty()) { "placement fast-forward matched no unit (units $unitIds)" }
        for (c in challenges) {
            markChallengeCompletedOnce(c.id)
        }
        userProgressDao.addPoints(GUEST_USER_ID, challenges.size * POINTS_PER_CHALLENGE)
    }

    /**
     * Full local reset: wipes completions, mistakes, kana mastery and the day's
     * quest progress, then restores a fresh guest profile. Curriculum seeds are
     * preserved.
     *
     * The daily-activity wipe is part of the reset, not an oversight: the reset
     * dialog promises that XP goes back to zero, and leaving the day row behind
     * put a learner on 0 points looking at a quest reading 20/30 — so one
     * correct answer later the bar claimed the day was complete at 10 points.
     */
    suspend fun resetAllProgress() = withContext(Dispatchers.IO) {
        challengeProgressDao.clearProgressForUser(GUEST_USER_ID)
        mistakeDao.clearAllMistakes()
        characterMasteryDao.clearAllMastery()
        dailyActivityDao.clearAllActivity()
        userProgressDao.upsertUserProgress(
            UserProgressEntity(
                userId = GUEST_USER_ID,
                userName = "Guest Learner",
                userImageSrc = "/mascot.svg",
                activeCourseId = 1,
                hearts = MAX_HEARTS,
                points = 0,
                streak = 1,
                lastActiveDate = java.time.LocalDate.now().toString(),
                themeAccent = "TEAL",
                themeMode = "SYSTEM",
                dailyQuestGoal = DAILY_QUEST_XP,
            )
        )
    }
    /**
     * Exports complete offline user progress to a structured JSON string.
     */
    suspend fun exportBackupJson(): String = withContext(Dispatchers.IO) {
        val user = userProgressDao.getUserProgressDirect(GUEST_USER_ID)
        val userBackup = user?.let {
            UserProgressBackup(
                points = it.points,
                hearts = it.hearts,
                streak = it.streak,
                lastActiveDate = it.lastActiveDate,
                showRomaji = it.showRomaji,
                soundEnabled = it.soundEnabled,
                hapticsEnabled = it.hapticsEnabled,
                themeAccent = it.themeAccent,
                themeMode = it.themeMode,
                dailyQuestGoal = it.dailyQuestGoal,
            )
        }
        val completed = challengeProgressDao.getCompletedChallengeIdsDirect(GUEST_USER_ID)
        val mastery = characterMasteryDao.getAllMasteryDirect().map {
            CharacterMasteryBackup(it.character, it.script, it.attempts, it.masteredAt)
        }
        val mistakes = mistakeDao.getAllMistakesDirect().map {
            MistakeBackup(it.challengeId, it.lessonId, it.timestamp)
        }
        val daily = dailyActivityDao.getAllDailyActivityDirect().map {
            DailyActivityBackup(it.date, it.xp)
        }
        val checkpoints = checkpointScoreDao.getAllScoresDirect(GUEST_USER_ID).map {
            CheckpointScoreBackup(it.courseId, it.level, it.correct, it.total, it.timestamp)
        }
        val vocab = vocabScheduleDao.getAllVocabDirect().map {
            VocabScheduleBackup(
                id = it.id,
                language = it.language,
                foreign = it.foreign,
                romaji = it.romaji,
                translation = it.translation,
                audioSrc = it.audioSrc,
                category = it.category,
                difficulty = it.difficulty,
                stability = it.stability,
                reps = it.reps,
                lapses = it.lapses,
                state = it.state,
                lastReview = it.lastReview,
                due = it.due,
            )
        }
        val backup = OpenLingoBackup(
            userProgress = userBackup,
            completedChallengeIds = completed,
            characterMastery = mastery,
            mistakes = mistakes,
            dailyActivity = daily,
            checkpointScores = checkpoints,
            vocabSchedule = vocab,
        )
        BackupJson.format.encodeToString(OpenLingoBackup.serializer(), backup)
    }

    /**
     * Restores offline progress from a valid OpenLingo backup JSON string.
     */
    suspend fun importBackupJson(jsonString: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val backup = BackupJson.format.decodeFromString(OpenLingoBackup.serializer(), jsonString)

            backup.userProgress?.let { u ->
                val current = userProgressDao.getUserProgressDirect(GUEST_USER_ID)
                userProgressDao.upsertUserProgress(
                    UserProgressEntity(
                        userId = GUEST_USER_ID,
                        userName = current?.userName ?: "Guest Learner",
                        activeCourseId = current?.activeCourseId ?: 1,
                        hearts = u.hearts,
                        points = u.points,
                        streak = u.streak,
                        lastActiveDate = u.lastActiveDate,
                        showRomaji = u.showRomaji,
                        soundEnabled = u.soundEnabled,
                        hapticsEnabled = u.hapticsEnabled,
                        themeAccent = u.themeAccent,
                        themeMode = u.themeMode,
                        dailyQuestGoal = u.dailyQuestGoal,
                    )
                )
            }

            challengeProgressDao.clearProgressForUser(GUEST_USER_ID)
            backup.completedChallengeIds.forEach { id ->
                markChallengeCompletedOnce(id)
            }

            characterMasteryDao.clearAllMastery()
            backup.characterMastery.forEach { m ->
                characterMasteryDao.upsertMastery(
                    CharacterMasteryEntity(
                        character = m.character,
                        script = m.script,
                        attempts = m.attempts,
                        masteredAt = m.masteredAt,
                    )
                )
            }

            mistakeDao.clearAllMistakes()
            backup.mistakes.forEach { mist ->
                mistakeDao.upsertMistake(
                    MistakeEntity(
                        challengeId = mist.challengeId,
                        lessonId = mist.lessonId,
                        timestamp = mist.timestamp,
                    )
                )
            }

            // An import restores an absolute total, not a delta. `addXp` is the
            // answering path's accumulator, so restoring through it added the
            // exported XP on top of the row that was already there: a learner
            // who reset and then restored ended up with double the day's XP and
            // a quest reading higher than the points they actually have. The
            // table is replaced wholesale, like mistakes, mastery and
            // checkpoints above it.
            dailyActivityDao.clearAllActivity()
            backup.dailyActivity.forEach { d ->
                dailyActivityDao.putDay(
                    com.openlingo.app.data.local.entities.DailyActivityEntity(d.date, d.xp),
                )
            }

            checkpointScoreDao.clearAllScoresForUser(GUEST_USER_ID)
            backup.checkpointScores.forEach { cp ->
                checkpointScoreDao.insert(
                    com.openlingo.app.data.local.entities.CheckpointScoreEntity(
                        userId = GUEST_USER_ID,
                        courseId = cp.courseId,
                        level = cp.level,
                        correct = cp.correct,
                        total = cp.total,
                        timestamp = cp.timestamp,
                    )
                )
            }

            backup.vocabSchedule.forEach { vs ->
                vocabScheduleDao.update(
                    VocabScheduleEntity(
                        id = vs.id,
                        language = vs.language,
                        foreign = vs.foreign,
                        romaji = vs.romaji,
                        translation = vs.translation,
                        audioSrc = vs.audioSrc,
                        category = vs.category,
                        difficulty = vs.difficulty,
                        stability = vs.stability,
                        reps = vs.reps,
                        lapses = vs.lapses,
                        state = vs.state,
                        lastReview = vs.lastReview,
                        due = vs.due,
                    )
                )
            }
        }
    }

    fun getCharacterMastery(): Flow<List<com.openlingo.app.data.local.entities.CharacterMasteryEntity>> =
        characterMasteryDao.getAllMastery().flowOn(Dispatchers.IO)

    suspend fun markCharacterMastered(
        character: String,
        script: String,
    ) = withContext(Dispatchers.IO) {
        val existing = characterMasteryDao.getMasteryDirect(character)
        characterMasteryDao.upsertMastery(
            com.openlingo.app.data.local.entities.CharacterMasteryEntity(
                character = character,
                script = script,
                attempts = (existing?.attempts ?: 0) + 1,
                masteredAt = System.currentTimeMillis(),
            )
        )
    }

    /**
     * Refunds [amount] hearts, capped at [MAX_HEARTS]. Used when the learner
     * completes the mistake recovery loop — the heart lost on the original miss
     * is given back as a reward for clearing the review.
     */
    suspend fun refundHeart(amount: Int = 1) = withContext(Dispatchers.IO) {
        val user = userProgressDao.getUserProgressDirect(GUEST_USER_ID) ?: return@withContext
        val newHearts = (user.hearts + amount).coerceAtMost(MAX_HEARTS)
        userProgressDao.updateHearts(GUEST_USER_ID, newHearts)
    }

    /**
     * Awards character mastery for the kana taught in a lesson.
     * Lesson 200 covers the first five hiragana vowels; lesson 201 covers
     * the か and さ rows.
     */
    suspend fun recordKanaMasteryForLesson(lessonId: Int) = withContext(Dispatchers.IO) {
        when (lessonId) {
            200 -> listOf("あ", "い", "う", "え", "お")
            201 -> listOf("か", "き", "く", "け", "こ", "さ", "し", "す", "せ", "そ")
            else -> return@withContext
        }.forEach { char ->
            characterMasteryDao.upsertMastery(
                CharacterMasteryEntity(character = char, script = "HIRAGANA")
            )
        }
    }

    fun getMistakes(courseId: Int): Flow<List<com.openlingo.app.data.local.entities.MistakeEntry>> =
        mistakeDao.getMistakeEntries(courseId).flowOn(Dispatchers.IO)

    fun getExerciseTypeStats(): Flow<List<ExerciseTypeStatsEntity>> =
        exerciseTypeStatsDao.getAllStats().flowOn(Dispatchers.IO)

    fun getMistakeCount(courseId: Int): Flow<Int> =
        mistakeDao.getMistakeCount(courseId).flowOn(Dispatchers.IO)

    private suspend fun seedSpanishCourse() {
        courseDao.insertCourses(
            listOf(
                CourseEntity(id = 1, title = "Spanish", imageSrc = "🇪🇸")
            )
        )
        courseDao.insertUnits(
            listOf(
                UnitEntity(
                    id = 10,
                    courseId = 1,
                    title = "Unit 1: Spanish Essentials",
                    description = "Learn foundational Spanish greetings, nouns, and phrases",
                    orderIndex = 0,
                ),
                UnitEntity(
                    id = 11,
                    courseId = 1,
                    title = "Unit 2: People & Family",
                    description = "Talk about family members, people, and simple descriptions",
                    orderIndex = 1,
                ),
            )
        )
        courseDao.insertLessons(
            listOf(
                LessonEntity(id = 100, unitId = 10, title = "Lesson 1: Greetings & Essentials", orderIndex = 0),
                LessonEntity(id = 101, unitId = 10, title = "Lesson 2: Common & Polite Phrases", orderIndex = 1),
                LessonEntity(id = 102, unitId = 11, title = "Lesson 3: People & Family", orderIndex = 0),
                LessonEntity(id = 103, unitId = 11, title = "Lesson 4: Introductions & Identity", orderIndex = 1),
            )
        )

        // Challenges for Lesson 100 (Unit 1): Greetings & Essentials
        // INTRO -> Recognition -> Production -> LISTEN
        lessonDao.insertChallenges(
            listOf(
                // An INTRO card's question is the ENGLISH it introduces; the
                // word itself lives on the option (below). Filing the Spanish
                // word into `question` made the card show its own word as its
                // own meaning line, and made DictionaryIndex file "De nada" as
                // its own gloss — a tautology that beat the honest
                // "You're welcome" taught later in the same unit.
                ChallengeEntity(id = 1001, lessonId = 100, type = ChallengeType.INTRO, question = "Hello", romaji = "ˈola", audioSrc = "asset:///audio/es/hola.ogg", orderIndex = 0),
                ChallengeEntity(id = 1002, lessonId = 100, type = ChallengeType.INTRO, question = "Good morning", romaji = "bˈuenos dˈias", audioSrc = "asset:///audio/es/buenos_dias.ogg", orderIndex = 1),
                ChallengeEntity(id = 1003, lessonId = 100, type = ChallengeType.INTRO, question = "Goodbye", orderIndex = 2),
                ChallengeEntity(id = 1004, lessonId = 100, type = ChallengeType.SELECT, question = "Which one means 'Hello'?", orderIndex = 3),
                ChallengeEntity(id = 1005, lessonId = 100, type = ChallengeType.SELECT, question = "Which phrase means 'Good morning'?", orderIndex = 4),
                ChallengeEntity(id = 1006, lessonId = 100, type = ChallengeType.ASSIST, question = "___ días", orderIndex = 5),
                ChallengeEntity(id = 1007, lessonId = 100, type = ChallengeType.WORD_BANK, question = "Translate: 'Hello, good morning'", orderIndex = 6),
                ChallengeEntity(id = 1008, lessonId = 100, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/es/buenos_dias.ogg", orderIndex = 7),
            )
        )
        lessonDao.insertOptions(
            listOf(
                // INTRO cards: one option each with text + romaji
                ChallengeOptionEntity(id = 1000100, challengeId = 1001, text = "¡Hola!", romaji = "ˈola", correct = true, audioSrc = "asset:///audio/es/hola.ogg"),
                ChallengeOptionEntity(id = 1000101, challengeId = 1002, text = "Buenos días", romaji = "bˈuenos dˈias", correct = true, audioSrc = "asset:///audio/es/buenos_dias.ogg"),
                ChallengeOptionEntity(id = 1000102, challengeId = 1003, text = "Adiós", correct = true),

                // SELECT 1004: "Which one means 'Hello'?"
                ChallengeOptionEntity(id = 1000103, challengeId = 1004, text = "¡Hola!", romaji = "ˈola", correct = true),
                ChallengeOptionEntity(id = 1000104, challengeId = 1004, text = "Buenos días", romaji = "bˈuenos dˈias", correct = false),
                ChallengeOptionEntity(id = 1000105, challengeId = 1004, text = "Adiós", correct = false),

                // SELECT 1005: "Which phrase means 'Good morning'?"
                ChallengeOptionEntity(id = 1000106, challengeId = 1005, text = "Buenos días", romaji = "bˈuenos dˈias", correct = true),
                ChallengeOptionEntity(id = 1000107, challengeId = 1005, text = "Adiós", correct = false),
                ChallengeOptionEntity(id = 1000108, challengeId = 1005, text = "¡Hola!", romaji = "ˈola", correct = false),

                // ASSIST 1006: "___ días"
                ChallengeOptionEntity(id = 1000109, challengeId = 1006, text = "Buenos", correct = true),
                ChallengeOptionEntity(id = 1000110, challengeId = 1006, text = "Adiós", correct = false),
                ChallengeOptionEntity(id = 1000111, challengeId = 1006, text = "Hola", correct = false),

                // WORD_BANK 1007: "¡Hola! buenos días"
                ChallengeOptionEntity(id = 1000112, challengeId = 1007, text = "¡Hola!", correct = true),
                ChallengeOptionEntity(id = 1000113, challengeId = 1007, text = "buenos", correct = true),
                ChallengeOptionEntity(id = 1000114, challengeId = 1007, text = "días", correct = true),
                ChallengeOptionEntity(id = 1000115, challengeId = 1007, text = "adiós", correct = false),

                // LISTEN 1008
                ChallengeOptionEntity(id = 1000116, challengeId = 1008, text = "Buenos días", romaji = "bˈuenos dˈias", correct = true),
                ChallengeOptionEntity(id = 1000117, challengeId = 1008, text = "Buenas noches", correct = false),
                ChallengeOptionEntity(id = 1000118, challengeId = 1008, text = "¡Hola!", romaji = "ˈola", correct = false),
            )
        )

        // Challenges for Lesson 101 (Unit 1): Common & Polite Phrases
        lessonDao.insertChallenges(
            listOf(
                ChallengeEntity(id = 1009, lessonId = 101, type = ChallengeType.INTRO, question = "Please", romaji = "por fabhˈor", audioSrc = "asset:///audio/es/por_favor.ogg", orderIndex = 0),
                ChallengeEntity(id = 1010, lessonId = 101, type = ChallengeType.INTRO, question = "Thank you", romaji = "grˈasias", audioSrc = "asset:///audio/es/gracias.ogg", orderIndex = 1),
                ChallengeEntity(id = 1011, lessonId = 101, type = ChallengeType.INTRO, question = "You're welcome", romaji = "de nˈadha", audioSrc = "asset:///audio/es/de_nada.ogg", orderIndex = 2),
                ChallengeEntity(id = 1012, lessonId = 101, type = ChallengeType.INTRO, question = "Yes", orderIndex = 3),
                ChallengeEntity(id = 1013, lessonId = 101, type = ChallengeType.SELECT, question = "How do you say 'Thank you'?", audioSrc = "asset:///audio/es/gracias.ogg", orderIndex = 4),
                ChallengeEntity(id = 1014, lessonId = 101, type = ChallengeType.SELECT, question = "What is 'You're welcome'?", audioSrc = "asset:///audio/es/de_nada.ogg", orderIndex = 5),
                ChallengeEntity(id = 1015, lessonId = 101, type = ChallengeType.ASSIST, question = "Sí, por ___", orderIndex = 6),
                ChallengeEntity(id = 100010, lessonId = 101, type = ChallengeType.WORD_BANK, question = "Translate: 'Yes, please'", orderIndex = 7),
                ChallengeEntity(id = 100011, lessonId = 101, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/es/por_favor.ogg", orderIndex = 8),
            )
        )
        lessonDao.insertOptions(
            listOf(
                // INTRO cards
                ChallengeOptionEntity(id = 1000119, challengeId = 1009, text = "Por favor", romaji = "por fabhˈor", correct = true, audioSrc = "asset:///audio/es/por_favor.ogg"),
                ChallengeOptionEntity(id = 1000120, challengeId = 1010, text = "Gracias", romaji = "grˈasias", correct = true, audioSrc = "asset:///audio/es/gracias.ogg"),
                ChallengeOptionEntity(id = 1000121, challengeId = 1011, text = "De nada", romaji = "de nˈadha", correct = true, audioSrc = "asset:///audio/es/de_nada.ogg"),
                ChallengeOptionEntity(id = 1000122, challengeId = 1012, text = "Sí", correct = true),

                // SELECT 1013: "How do you say 'Thank you'?"
                ChallengeOptionEntity(id = 1000123, challengeId = 1013, text = "Muchas gracias", romaji = "mˈuchas grˈasias", correct = true),
                ChallengeOptionEntity(id = 1000124, challengeId = 1013, text = "De nada", romaji = "de nˈadha", correct = false),
                ChallengeOptionEntity(id = 1000125, challengeId = 1013, text = "Por favor", romaji = "por fabhˈor", correct = false),

                // SELECT 1014: "What is 'You're welcome'?"
                ChallengeOptionEntity(id = 1000126, challengeId = 1014, text = "De nada", romaji = "de nˈadha", correct = true),
                ChallengeOptionEntity(id = 1000127, challengeId = 1014, text = "Gracias", romaji = "grˈasias", correct = false),
                ChallengeOptionEntity(id = 1000128, challengeId = 1014, text = "Por favor", romaji = "por fabhˈor", correct = false),

                // ASSIST 1015: "Sí, por ___"
                ChallengeOptionEntity(id = 1000129, challengeId = 1015, text = "favor", correct = true),
                ChallengeOptionEntity(id = 1000130, challengeId = 1015, text = "gracias", correct = false),
                ChallengeOptionEntity(id = 1000131, challengeId = 1015, text = "nada", correct = false),

                // WORD_BANK 100010: "Sí por favor"
                ChallengeOptionEntity(id = 1000132, challengeId = 100010, text = "Sí", correct = true),
                ChallengeOptionEntity(id = 1000133, challengeId = 100010, text = "por", correct = true),
                ChallengeOptionEntity(id = 1000134, challengeId = 100010, text = "favor", correct = true),
                ChallengeOptionEntity(id = 1000135, challengeId = 100010, text = "gracias", correct = false),
                ChallengeOptionEntity(id = 1000136, challengeId = 100010, text = "de", correct = false),

                // LISTEN 100011
                ChallengeOptionEntity(id = 1000137, challengeId = 100011, text = "Por favor", romaji = "por fabhˈor", correct = true),
                ChallengeOptionEntity(id = 1000138, challengeId = 100011, text = "Gracias", romaji = "grˈasias", correct = false),
                ChallengeOptionEntity(id = 1000139, challengeId = 100011, text = "De nada", romaji = "de nˈadha", correct = false),
            )
        )

        // Challenges for Lesson 102 (Unit 2): People & Family — La mujer PROMOTED
        lessonDao.insertChallenges(
            listOf(
                ChallengeEntity(id = 100012, lessonId = 102, type = ChallengeType.INTRO, question = "The man", romaji = "el ˈombre", orderIndex = 0),
                ChallengeEntity(id = 100013, lessonId = 102, type = ChallengeType.INTRO, question = "The woman", romaji = "la mujˈer", orderIndex = 1),
                ChallengeEntity(id = 100014, lessonId = 102, type = ChallengeType.INTRO, question = "The father", romaji = "el pˈadhre", audioSrc = "asset:///audio/es/el_padre.ogg", orderIndex = 2),
                ChallengeEntity(id = 100015, lessonId = 102, type = ChallengeType.INTRO, question = "The mother", romaji = "la mˈadhre", audioSrc = "asset:///audio/es/la_madre.ogg", orderIndex = 3),
                ChallengeEntity(id = 100016, lessonId = 102, type = ChallengeType.INTRO, question = "The family", romaji = "la famˈilia", audioSrc = "asset:///audio/es/la_familia.ogg", orderIndex = 4),
                ChallengeEntity(id = 100017, lessonId = 102, type = ChallengeType.SELECT, question = "Translate: 'The father'", orderIndex = 5),
                ChallengeEntity(id = 100018, lessonId = 102, type = ChallengeType.SELECT, question = "Translate: 'The mother'", orderIndex = 6),
                ChallengeEntity(id = 100019, lessonId = 102, type = ChallengeType.SELECT, question = "Translate: 'The woman'", orderIndex = 7),
                ChallengeEntity(id = 100020, lessonId = 102, type = ChallengeType.MATCH_PAIRS, question = "Match each person to their meaning", orderIndex = 8),
                ChallengeEntity(id = 100021, lessonId = 102, type = ChallengeType.WORD_BANK, question = "Translate: 'The father and the mother'", orderIndex = 9),
                ChallengeEntity(id = 100022, lessonId = 102, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/es/la_familia.ogg", orderIndex = 10),
            )
        )
        lessonDao.insertOptions(
            listOf(
                // INTRO cards
                ChallengeOptionEntity(id = 1000140, challengeId = 100012, text = "El hombre", romaji = "el ˈombre", correct = true),
                ChallengeOptionEntity(id = 1000141, challengeId = 100013, text = "La mujer", romaji = "la mujˈer", correct = true),
                ChallengeOptionEntity(id = 1000142, challengeId = 100014, text = "El padre", romaji = "el pˈadhre", correct = true, audioSrc = "asset:///audio/es/el_padre.ogg"),
                ChallengeOptionEntity(id = 1000143, challengeId = 100015, text = "La madre", romaji = "la mˈadhre", correct = true, audioSrc = "asset:///audio/es/la_madre.ogg"),
                ChallengeOptionEntity(id = 1000144, challengeId = 100016, text = "La familia", romaji = "la famˈilia", correct = true, audioSrc = "asset:///audio/es/la_familia.ogg"),

                // SELECT 100017: "Translate: 'The father'"
                ChallengeOptionEntity(id = 1000145, challengeId = 100017, text = "El padre", romaji = "el pˈadhre", correct = true),
                ChallengeOptionEntity(id = 1000146, challengeId = 100017, text = "La madre", romaji = "la mˈadhre", correct = false),
                ChallengeOptionEntity(id = 1000147, challengeId = 100017, text = "El hombre", romaji = "el ˈombre", correct = false),

                // SELECT 100018: "Translate: 'The mother'"
                ChallengeOptionEntity(id = 1000148, challengeId = 100018, text = "La madre", romaji = "la mˈadhre", correct = true),
                ChallengeOptionEntity(id = 1000149, challengeId = 100018, text = "La mujer", romaji = "la mujˈer", correct = false),
                ChallengeOptionEntity(id = 1000150, challengeId = 100018, text = "El padre", romaji = "el pˈadhre", correct = false),

                // SELECT 100019: "Translate: 'The woman'"
                ChallengeOptionEntity(id = 1000151, challengeId = 100019, text = "La mujer", romaji = "la mujˈer", correct = true),
                ChallengeOptionEntity(id = 1000152, challengeId = 100019, text = "El hombre", romaji = "el ˈombre", correct = false),
                ChallengeOptionEntity(id = 1000153, challengeId = 100019, text = "La madre", romaji = "la mˈadhre", correct = false),

                // MATCH_PAIRS 100020: {El hombre: The man, La mujer: The woman, El padre: The father, La madre: The mother}
                ChallengeOptionEntity(id = 1000154, challengeId = 100020, text = "El hombre", romaji = "el ˈombre", correct = true),
                ChallengeOptionEntity(id = 1000155, challengeId = 100020, text = "The man", correct = true),
                ChallengeOptionEntity(id = 1000156, challengeId = 100020, text = "La mujer", romaji = "la mujˈer", correct = true),
                ChallengeOptionEntity(id = 1000157, challengeId = 100020, text = "The woman", correct = true),
                ChallengeOptionEntity(id = 1000158, challengeId = 100020, text = "El padre", romaji = "el pˈadhre", correct = true),
                ChallengeOptionEntity(id = 1000159, challengeId = 100020, text = "The father", correct = true),
                ChallengeOptionEntity(id = 1000160, challengeId = 100020, text = "La madre", romaji = "la mˈadhre", correct = true),
                ChallengeOptionEntity(id = 1000161, challengeId = 100020, text = "The mother", correct = true),

                // WORD_BANK 100021: "El padre y la madre"
                ChallengeOptionEntity(id = 1000162, challengeId = 100021, text = "El", correct = true),
                ChallengeOptionEntity(id = 1000163, challengeId = 100021, text = "padre", correct = true),
                ChallengeOptionEntity(id = 1000164, challengeId = 100021, text = "y", correct = true),
                ChallengeOptionEntity(id = 1000165, challengeId = 100021, text = "la", correct = true),
                ChallengeOptionEntity(id = 1000166, challengeId = 100021, text = "madre", correct = true),
                ChallengeOptionEntity(id = 1000167, challengeId = 100021, text = "mujer", correct = false),

                // LISTEN 100022
                ChallengeOptionEntity(id = 1000168, challengeId = 100022, text = "La familia", romaji = "la famˈilia", correct = true),
                ChallengeOptionEntity(id = 1000169, challengeId = 100022, text = "El padre", romaji = "el pˈadhre", correct = false),
                ChallengeOptionEntity(id = 1000170, challengeId = 100022, text = "La madre", romaji = "la mˈadhre", correct = false),
            )
        )

        // Challenges for Lesson 103 (Unit 2): Introductions & Identity (ser)
        lessonDao.insertChallenges(
            listOf(
                ChallengeEntity(id = 100023, lessonId = 103, type = ChallengeType.INTRO, question = "Nice to meet you", romaji = "mˈucho gˈusto", audioSrc = "asset:///audio/es/mucho_gusto.ogg", orderIndex = 0),
                ChallengeEntity(id = 100024, lessonId = 103, type = ChallengeType.INTRO, question = "I am", grammaticalFocus = "es.ser_present", ruleText = "ser (to be, identity): yo soy = I am", orderIndex = 1),
                ChallengeEntity(id = 100025, lessonId = 103, type = ChallengeType.INTRO, question = "A boy", romaji = "un nˈiño", orderIndex = 2),
                ChallengeEntity(id = 100026, lessonId = 103, type = ChallengeType.INTRO, question = "A girl", romaji = "ˈuna nˈiña", orderIndex = 3),
                ChallengeEntity(id = 100027, lessonId = 103, type = ChallengeType.SELECT, question = "How do you say 'Nice to meet you'?", orderIndex = 4),
                ChallengeEntity(id = 100028, lessonId = 103, type = ChallengeType.ASSIST, question = "Yo ___ un niño", grammaticalFocus = "es.ser_present", ruleText = "ser (to be, identity): yo soy = I am. Use soy with yo.", orderIndex = 5),
                ChallengeEntity(id = 100029, lessonId = 103, type = ChallengeType.WORD_BANK, question = "Assemble: 'I am a boy'", grammaticalFocus = "es.ser_present", ruleText = "ser (to be, identity): yo soy = I am. Use soy with yo.", orderIndex = 6),
                ChallengeEntity(id = 100030, lessonId = 103, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/es/yo_soy_un_nino.ogg", grammaticalFocus = "es.ser_present", ruleText = "ser (to be, identity): yo soy = I am. Use soy with yo.", orderIndex = 7),
            )
        )
        lessonDao.insertOptions(
            listOf(
                // INTRO cards
                ChallengeOptionEntity(id = 1000171, challengeId = 100023, text = "¡Mucho gusto!", romaji = "mˈucho gˈusto", correct = true, audioSrc = "asset:///audio/es/mucho_gusto.ogg"),
                ChallengeOptionEntity(id = 1000172, challengeId = 100024, text = "Yo soy", correct = true),
                ChallengeOptionEntity(id = 1000173, challengeId = 100025, text = "Un niño", romaji = "un nˈiño", correct = true),
                ChallengeOptionEntity(id = 1000174, challengeId = 100026, text = "Una niña", romaji = "ˈuna nˈiña", correct = true),

                // SELECT 100027: "How do you say 'Nice to meet you'?"
                ChallengeOptionEntity(id = 1000175, challengeId = 100027, text = "¡Mucho gusto!", romaji = "mˈucho gˈusto", correct = true),
                ChallengeOptionEntity(id = 1000176, challengeId = 100027, text = "Buenos días", romaji = "bˈuenos dˈias", correct = false),
                ChallengeOptionEntity(id = 1000177, challengeId = 100027, text = "Adiós", correct = false),

                // ASSIST 100028: "Yo ___ un niño"
                ChallengeOptionEntity(id = 1000178, challengeId = 100028, text = "soy", correct = true),
                ChallengeOptionEntity(id = 1000179, challengeId = 100028, text = "es", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 1000180, challengeId = 100028, text = "son", correct = false, errorTag = "WRONG_PERSON"),

                // WORD_BANK 100029: "Yo soy un niño"
                ChallengeOptionEntity(id = 1000181, challengeId = 100029, text = "Yo", correct = true),
                ChallengeOptionEntity(id = 1000182, challengeId = 100029, text = "soy", correct = true),
                ChallengeOptionEntity(id = 1000183, challengeId = 100029, text = "un", correct = true),
                ChallengeOptionEntity(id = 1000184, challengeId = 100029, text = "niño", correct = true),
                ChallengeOptionEntity(id = 1000185, challengeId = 100029, text = "son", correct = false, errorTag = "WRONG_PERSON"),
                ChallengeOptionEntity(id = 1000186, challengeId = 100029, text = "estoy", correct = false, errorTag = "WRONG_COPULA"),

                // LISTEN 100030
                ChallengeOptionEntity(id = 1000187, challengeId = 100030, text = "Yo soy un niño", romaji = "yˈo sˈoi un nˈiño", correct = true),
                ChallengeOptionEntity(id = 1000188, challengeId = 100030, text = "Yo estoy un niño", correct = false, errorTag = "WRONG_COPULA"),
                ChallengeOptionEntity(id = 1000189, challengeId = 100030, text = "Tú eres un niño", correct = false),
            )
        )
    }

    private suspend fun seedJapaneseCourse() {
        courseDao.insertCourses(
            listOf(
                CourseEntity(id = 2, title = "Japanese", imageSrc = "🇯🇵")
            )
        )
        courseDao.insertUnits(
            listOf(
                UnitEntity(
                    id = 20,
                    courseId = 2,
                    title = "Unit 1: Hiragana & Greetings",
                    description = "Learn essential Japanese sounds, daily greetings, and numbers",
                    orderIndex = 0,
                ),
                UnitEntity(
                    id = 21,
                    courseId = 2,
                    title = "Unit 2: Daily Life & Food",
                    description = "Common phrases for dining, polite interactions, and refreshments",
                    orderIndex = 1,
                ),
            )
        )
        courseDao.insertLessons(
            listOf(
                LessonEntity(id = 200, unitId = 20, title = "Lesson 1: Vowels あ–お", orderIndex = 0),
                LessonEntity(id = 201, unitId = 20, title = "Lesson 2: Consonants か & さ Rows", orderIndex = 1),
                LessonEntity(id = 20001, unitId = 20, title = "Lesson 3: First Words", orderIndex = 2),
                LessonEntity(id = 20002, unitId = 20, title = "Lesson 4: Daily Greetings", orderIndex = 3),
                LessonEntity(id = 20003, unitId = 20, title = "Lesson 5: Numbers 1–3", orderIndex = 4),
                LessonEntity(id = 202, unitId = 21, title = "Lesson 3: Eating & Drinking", orderIndex = 0),
                LessonEntity(id = 203, unitId = 21, title = "Lesson 4: Politeness & Gratitude", orderIndex = 1),
            )
        )

        // Lesson 200 (Unit 1): the vowel row, introduced before it is tested. The
        // five INTRO cards come first; every option in the recognition, matching
        // and listening rounds is one of those five kana, so nothing is asked cold.
        lessonDao.insertChallenges(
            listOf(
                ChallengeEntity(id = 2001, lessonId = 200, type = ChallengeType.INTRO, question = "あ", orderIndex = 0),
                ChallengeEntity(id = 2002, lessonId = 200, type = ChallengeType.INTRO, question = "い", orderIndex = 1),
                ChallengeEntity(id = 20000, lessonId = 200, type = ChallengeType.INTRO, question = "う", orderIndex = 2),
                ChallengeEntity(id = 20001, lessonId = 200, type = ChallengeType.INTRO, question = "え", orderIndex = 3),
                ChallengeEntity(id = 20002, lessonId = 200, type = ChallengeType.INTRO, question = "お", orderIndex = 4),
                ChallengeEntity(id = 20003, lessonId = 200, type = ChallengeType.SELECT, question = "Which kana is 'a'?", orderIndex = 5),
                ChallengeEntity(id = 20004, lessonId = 200, type = ChallengeType.SELECT, question = "Which kana is 'u'?", orderIndex = 6),
                ChallengeEntity(id = 20005, lessonId = 200, type = ChallengeType.MATCH_PAIRS, question = "Match each kana to its sound", orderIndex = 7),
                ChallengeEntity(id = 20006, lessonId = 200, type = ChallengeType.LISTEN, question = "Tap what you hear", orderIndex = 8),
            )
        )
        lessonDao.insertOptions(
            listOf(
                ChallengeOptionEntity(id = 200000, challengeId = 2001, text = "あ", romaji = "a", correct = true),
                ChallengeOptionEntity(id = 200001, challengeId = 2002, text = "い", romaji = "i", correct = true),
                ChallengeOptionEntity(id = 200002, challengeId = 20000, text = "う", romaji = "u", correct = true),
                ChallengeOptionEntity(id = 200003, challengeId = 20001, text = "え", romaji = "e", correct = true),
                ChallengeOptionEntity(id = 200004, challengeId = 20002, text = "お", romaji = "o", correct = true),

                ChallengeOptionEntity(id = 200005, challengeId = 20003, text = "あ", romaji = "a", correct = true),
                ChallengeOptionEntity(id = 200006, challengeId = 20003, text = "い", romaji = "i", correct = false),
                ChallengeOptionEntity(id = 200007, challengeId = 20003, text = "う", romaji = "u", correct = false),

                ChallengeOptionEntity(id = 200008, challengeId = 20004, text = "う", romaji = "u", correct = true),
                ChallengeOptionEntity(id = 200009, challengeId = 20004, text = "え", romaji = "e", correct = false),
                ChallengeOptionEntity(id = 200010, challengeId = 20004, text = "お", romaji = "o", correct = false),

                // MATCH_PAIRS reads consecutive pairs in ascending option-id order.
                ChallengeOptionEntity(id = 200011, challengeId = 20005, text = "あ", romaji = "a", correct = true),
                ChallengeOptionEntity(id = 200012, challengeId = 20005, text = "a", correct = true),
                ChallengeOptionEntity(id = 200013, challengeId = 20005, text = "い", romaji = "i", correct = true),
                ChallengeOptionEntity(id = 200014, challengeId = 20005, text = "i", correct = true),
                ChallengeOptionEntity(id = 200015, challengeId = 20005, text = "う", romaji = "u", correct = true),
                ChallengeOptionEntity(id = 200016, challengeId = 20005, text = "u", correct = true),
                ChallengeOptionEntity(id = 200017, challengeId = 20005, text = "え", romaji = "e", correct = true),
                ChallengeOptionEntity(id = 200018, challengeId = 20005, text = "e", correct = true),
                ChallengeOptionEntity(id = 200019, challengeId = 20005, text = "お", romaji = "o", correct = true),
                ChallengeOptionEntity(id = 200020, challengeId = 20005, text = "o", correct = true),

                ChallengeOptionEntity(id = 200021, challengeId = 20006, text = "あ", romaji = "a", correct = true),
                ChallengeOptionEntity(id = 200022, challengeId = 20006, text = "い", romaji = "i", correct = false),
                ChallengeOptionEntity(id = 200023, challengeId = 20006, text = "う", romaji = "u", correct = false),
            )
        )

        // Lesson 201 (Unit 1): the か and さ rows, introduced then recognised.
        lessonDao.insertChallenges(
            listOf(
                ChallengeEntity(id = 20007, lessonId = 201, type = ChallengeType.INTRO, question = "か", orderIndex = 0),
                ChallengeEntity(id = 20008, lessonId = 201, type = ChallengeType.INTRO, question = "き", orderIndex = 1),
                ChallengeEntity(id = 20009, lessonId = 201, type = ChallengeType.INTRO, question = "く", orderIndex = 2),
                ChallengeEntity(id = 20010, lessonId = 201, type = ChallengeType.INTRO, question = "け", orderIndex = 3),
                ChallengeEntity(id = 20011, lessonId = 201, type = ChallengeType.INTRO, question = "こ", orderIndex = 4),
                ChallengeEntity(id = 20012, lessonId = 201, type = ChallengeType.INTRO, question = "さ", orderIndex = 5),
                ChallengeEntity(id = 20013, lessonId = 201, type = ChallengeType.INTRO, question = "し", orderIndex = 6),
                ChallengeEntity(id = 20014, lessonId = 201, type = ChallengeType.INTRO, question = "す", orderIndex = 7),
                ChallengeEntity(id = 20015, lessonId = 201, type = ChallengeType.INTRO, question = "せ", orderIndex = 8),
                ChallengeEntity(id = 20016, lessonId = 201, type = ChallengeType.INTRO, question = "そ", orderIndex = 9),
                ChallengeEntity(id = 20017, lessonId = 201, type = ChallengeType.SELECT, question = "Which kana is 'ka'?", orderIndex = 10),
                ChallengeEntity(id = 20018, lessonId = 201, type = ChallengeType.SELECT, question = "Which kana is 'so'?", orderIndex = 11),
                ChallengeEntity(id = 20019, lessonId = 201, type = ChallengeType.MATCH_PAIRS, question = "Match each kana to its sound", orderIndex = 12),
                ChallengeEntity(id = 20020, lessonId = 201, type = ChallengeType.LISTEN, question = "Tap what you hear", orderIndex = 13),
            )
        )
        lessonDao.insertOptions(
            listOf(
                ChallengeOptionEntity(id = 200024, challengeId = 20007, text = "か", romaji = "ka", correct = true),
                ChallengeOptionEntity(id = 200025, challengeId = 20008, text = "き", romaji = "ki", correct = true),
                ChallengeOptionEntity(id = 200026, challengeId = 20009, text = "く", romaji = "ku", correct = true),
                ChallengeOptionEntity(id = 200027, challengeId = 20010, text = "け", romaji = "ke", correct = true),
                ChallengeOptionEntity(id = 200028, challengeId = 20011, text = "こ", romaji = "ko", correct = true),
                ChallengeOptionEntity(id = 200029, challengeId = 20012, text = "さ", romaji = "sa", correct = true),
                ChallengeOptionEntity(id = 200030, challengeId = 20013, text = "し", romaji = "shi", correct = true),
                ChallengeOptionEntity(id = 200031, challengeId = 20014, text = "す", romaji = "su", correct = true),
                ChallengeOptionEntity(id = 200032, challengeId = 20015, text = "せ", romaji = "se", correct = true),
                ChallengeOptionEntity(id = 200033, challengeId = 20016, text = "そ", romaji = "so", correct = true),

                ChallengeOptionEntity(id = 200034, challengeId = 20017, text = "か", romaji = "ka", correct = true),
                ChallengeOptionEntity(id = 200035, challengeId = 20017, text = "き", romaji = "ki", correct = false),
                ChallengeOptionEntity(id = 200036, challengeId = 20017, text = "く", romaji = "ku", correct = false),

                ChallengeOptionEntity(id = 200037, challengeId = 20018, text = "そ", romaji = "so", correct = true),
                ChallengeOptionEntity(id = 200038, challengeId = 20018, text = "さ", romaji = "sa", correct = false),
                ChallengeOptionEntity(id = 200039, challengeId = 20018, text = "し", romaji = "shi", correct = false),

                // MATCH_PAIRS reads consecutive pairs in ascending option-id order.
                ChallengeOptionEntity(id = 200040, challengeId = 20019, text = "か", romaji = "ka", correct = true),
                ChallengeOptionEntity(id = 200041, challengeId = 20019, text = "ka", correct = true),
                ChallengeOptionEntity(id = 200042, challengeId = 20019, text = "き", romaji = "ki", correct = true),
                ChallengeOptionEntity(id = 200043, challengeId = 20019, text = "ki", correct = true),
                ChallengeOptionEntity(id = 200044, challengeId = 20019, text = "く", romaji = "ku", correct = true),
                ChallengeOptionEntity(id = 200045, challengeId = 20019, text = "ku", correct = true),
                ChallengeOptionEntity(id = 200046, challengeId = 20019, text = "け", romaji = "ke", correct = true),
                ChallengeOptionEntity(id = 200047, challengeId = 20019, text = "ke", correct = true),
                ChallengeOptionEntity(id = 200048, challengeId = 20019, text = "こ", romaji = "ko", correct = true),
                ChallengeOptionEntity(id = 200049, challengeId = 20019, text = "ko", correct = true),

                ChallengeOptionEntity(id = 200050, challengeId = 20020, text = "か", romaji = "ka", correct = true),
                ChallengeOptionEntity(id = 200051, challengeId = 20020, text = "き", romaji = "ki", correct = false),
                ChallengeOptionEntity(id = 200052, challengeId = 20020, text = "さ", romaji = "sa", correct = false),
            )
        )

        // Lesson 20001 (Unit 1): first words assembled only from the kana above.
        lessonDao.insertChallenges(
            listOf(
                ChallengeEntity(id = 20021, lessonId = 20001, type = ChallengeType.INTRO, question = "あい — love", orderIndex = 0),
                ChallengeEntity(id = 20022, lessonId = 20001, type = ChallengeType.INTRO, question = "いえ — house", orderIndex = 1),
                ChallengeEntity(id = 20023, lessonId = 20001, type = ChallengeType.INTRO, question = "うえ — up / above", orderIndex = 2),
                ChallengeEntity(id = 20024, lessonId = 20001, type = ChallengeType.INTRO, question = "あお — blue", orderIndex = 3),
                ChallengeEntity(id = 20025, lessonId = 20001, type = ChallengeType.INTRO, question = "あさ — morning", orderIndex = 4),
                ChallengeEntity(id = 20026, lessonId = 20001, type = ChallengeType.SELECT, question = "Which word means 'love'?", orderIndex = 5),
                ChallengeEntity(id = 20027, lessonId = 20001, type = ChallengeType.SELECT, question = "Which word means 'morning'?", orderIndex = 6),
                ChallengeEntity(id = 20028, lessonId = 20001, type = ChallengeType.WORD_BANK, question = "Assemble: 'house'", orderIndex = 7),
                ChallengeEntity(id = 20029, lessonId = 20001, type = ChallengeType.LISTEN, question = "Tap what you hear", orderIndex = 8),
            )
        )
        lessonDao.insertOptions(
            listOf(
                ChallengeOptionEntity(id = 200053, challengeId = 20021, text = "あい", romaji = "ai", correct = true),
                ChallengeOptionEntity(id = 200054, challengeId = 20022, text = "いえ", romaji = "ie", correct = true),
                ChallengeOptionEntity(id = 200055, challengeId = 20023, text = "うえ", romaji = "ue", correct = true),
                ChallengeOptionEntity(id = 200056, challengeId = 20024, text = "あお", romaji = "ao", correct = true),
                ChallengeOptionEntity(id = 200057, challengeId = 20025, text = "あさ", romaji = "asa", correct = true),

                ChallengeOptionEntity(id = 200058, challengeId = 20026, text = "あい", romaji = "ai", correct = true),
                ChallengeOptionEntity(id = 200059, challengeId = 20026, text = "いえ", romaji = "ie", correct = false),
                ChallengeOptionEntity(id = 200060, challengeId = 20026, text = "あお", romaji = "ao", correct = false),

                ChallengeOptionEntity(id = 200061, challengeId = 20027, text = "あさ", romaji = "asa", correct = true),
                ChallengeOptionEntity(id = 200062, challengeId = 20027, text = "うえ", romaji = "ue", correct = false),
                ChallengeOptionEntity(id = 200063, challengeId = 20027, text = "あい", romaji = "ai", correct = false),

                // Word Bank for 20028: い + え ("house"); the decoys are taught kana.
                ChallengeOptionEntity(id = 200064, challengeId = 20028, text = "い", romaji = "i", correct = true),
                ChallengeOptionEntity(id = 200065, challengeId = 20028, text = "え", romaji = "e", correct = true),
                ChallengeOptionEntity(id = 200066, challengeId = 20028, text = "あ", romaji = "a", correct = false),
                ChallengeOptionEntity(id = 200067, challengeId = 20028, text = "う", romaji = "u", correct = false),

                ChallengeOptionEntity(id = 200068, challengeId = 20029, text = "あい", romaji = "ai", correct = true),
                ChallengeOptionEntity(id = 200069, challengeId = 20029, text = "あお", romaji = "ao", correct = false),
                ChallengeOptionEntity(id = 200070, challengeId = 20029, text = "あさ", romaji = "asa", correct = false),
            )
        )

        // Lesson 20002 (Unit 1): daily greetings — INTRO cards first, then
        // recognition, a cloze, an assembly and a listening round.
        lessonDao.insertChallenges(
            listOf(
                ChallengeEntity(id = 20030, lessonId = 20002, type = ChallengeType.INTRO, question = "こんにちは — Hello", audioSrc = "asset:///audio/ja/konnichiwa.ogg", orderIndex = 0),
                ChallengeEntity(id = 20031, lessonId = 20002, type = ChallengeType.INTRO, question = "ありがとう — Thank you", audioSrc = "asset:///audio/ja/arigatou.ogg", orderIndex = 1),
                ChallengeEntity(id = 20032, lessonId = 20002, type = ChallengeType.INTRO, question = "はい — Yes", audioSrc = "asset:///audio/ja/hai.ogg", orderIndex = 2),
                ChallengeEntity(id = 20033, lessonId = 20002, type = ChallengeType.SELECT, question = "Which one means 'Hello'?", audioSrc = "asset:///audio/ja/konnichiwa.ogg", orderIndex = 3),
                ChallengeEntity(id = 20034, lessonId = 20002, type = ChallengeType.SELECT, question = "How do you say 'Thank you'?", audioSrc = "asset:///audio/ja/arigatou.ogg", orderIndex = 4),
                ChallengeEntity(id = 20035, lessonId = 20002, type = ChallengeType.ASSIST, question = "Complete the greeting: 'Thank you' = ___", orderIndex = 5),
                ChallengeEntity(id = 20036, lessonId = 20002, type = ChallengeType.WORD_BANK, question = "Assemble: 'Yes, thank you'", orderIndex = 6),
                ChallengeEntity(id = 20037, lessonId = 20002, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/ja/konnichiwa.ogg", orderIndex = 7),
            )
        )
        lessonDao.insertOptions(
            listOf(
                ChallengeOptionEntity(id = 200071, challengeId = 20030, text = "こんにちは", romaji = "Konnichiwa", correct = true, audioSrc = "asset:///audio/ja/konnichiwa.ogg"),
                ChallengeOptionEntity(id = 200072, challengeId = 20031, text = "ありがとう", romaji = "Arigatou", correct = true, audioSrc = "asset:///audio/ja/arigatou.ogg"),
                ChallengeOptionEntity(id = 200073, challengeId = 20032, text = "はい", romaji = "Hai", correct = true, audioSrc = "asset:///audio/ja/hai.ogg"),

                ChallengeOptionEntity(id = 200074, challengeId = 20033, text = "こんにちは", romaji = "Konnichiwa", correct = true, audioSrc = "asset:///audio/ja/konnichiwa.ogg"),
                ChallengeOptionEntity(id = 200075, challengeId = 20033, text = "ありがとう", romaji = "Arigatou", correct = false),
                ChallengeOptionEntity(id = 200076, challengeId = 20033, text = "はい", romaji = "Hai", correct = false),

                ChallengeOptionEntity(id = 200077, challengeId = 20034, text = "ありがとう", romaji = "Arigatou", correct = true, audioSrc = "asset:///audio/ja/arigatou.ogg"),
                ChallengeOptionEntity(id = 200078, challengeId = 20034, text = "はい", romaji = "Hai", correct = false),
                ChallengeOptionEntity(id = 200079, challengeId = 20034, text = "こんにちは", romaji = "Konnichiwa", correct = false),

                ChallengeOptionEntity(id = 200080, challengeId = 20035, text = "ありがとう", romaji = "Arigatou", correct = true),
                ChallengeOptionEntity(id = 200081, challengeId = 20035, text = "こんにちは", romaji = "Konnichiwa", correct = false),
                ChallengeOptionEntity(id = 200082, challengeId = 20035, text = "はい", romaji = "Hai", correct = false),

                // Word Bank for 20036: はい + ありがとう ("Yes, thank you").
                ChallengeOptionEntity(id = 200083, challengeId = 20036, text = "はい", romaji = "hai", correct = true),
                ChallengeOptionEntity(id = 200084, challengeId = 20036, text = "ありがとう", romaji = "arigatou", correct = true),
                ChallengeOptionEntity(id = 200085, challengeId = 20036, text = "こんにちは", romaji = "konnichiwa", correct = false),
                ChallengeOptionEntity(id = 200086, challengeId = 20036, text = "あい", romaji = "ai", correct = false),

                ChallengeOptionEntity(id = 200087, challengeId = 20037, text = "こんにちは", romaji = "Konnichiwa", correct = true),
                ChallengeOptionEntity(id = 200088, challengeId = 20037, text = "ありがとう", romaji = "Arigatou", correct = false),
                ChallengeOptionEntity(id = 200089, challengeId = 20037, text = "はい", romaji = "Hai", correct = false),
            )
        )

        // Lesson 20003 (Unit 1): numbers 1-3 — the kana are introduced with the
        // kanji mapped to them, and each number is only ever tested against the
        // two other taught numbers. よん and ご never appear.
        lessonDao.insertChallenges(
            listOf(
                ChallengeEntity(id = 20038, lessonId = 20003, type = ChallengeType.INTRO, question = "いち (一) — One (1)", orderIndex = 0),
                ChallengeEntity(id = 20039, lessonId = 20003, type = ChallengeType.INTRO, question = "に (二) — Two (2)", orderIndex = 1),
                ChallengeEntity(id = 20040, lessonId = 20003, type = ChallengeType.INTRO, question = "さん (三) — Three (3)", orderIndex = 2),
                ChallengeEntity(id = 20041, lessonId = 20003, type = ChallengeType.SELECT, question = "Which kanji is 'One (1)'?", orderIndex = 3),
                ChallengeEntity(id = 20042, lessonId = 20003, type = ChallengeType.SELECT, question = "Which kanji is 'Two (2)'?", orderIndex = 4),
                ChallengeEntity(id = 20043, lessonId = 20003, type = ChallengeType.SELECT, question = "Which kanji is 'Three (3)'?", orderIndex = 5),
                ChallengeEntity(id = 20044, lessonId = 20003, type = ChallengeType.WORD_BANK, question = "Assemble: 'One, two, three'", orderIndex = 6),
                ChallengeEntity(id = 20045, lessonId = 20003, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/ja/ichi_ni_san.ogg", orderIndex = 7),
            )
        )
        lessonDao.insertOptions(
            listOf(
                ChallengeOptionEntity(id = 200090, challengeId = 20038, text = "いち", romaji = "ichi", correct = true),
                ChallengeOptionEntity(id = 200091, challengeId = 20039, text = "に", romaji = "ni", correct = true),
                ChallengeOptionEntity(id = 200092, challengeId = 20040, text = "さん", romaji = "san", correct = true),

                ChallengeOptionEntity(id = 200093, challengeId = 20041, text = "一", romaji = "ichi", correct = true),
                ChallengeOptionEntity(id = 200094, challengeId = 20041, text = "二", romaji = "ni", correct = false),
                ChallengeOptionEntity(id = 200095, challengeId = 20041, text = "三", romaji = "san", correct = false),

                ChallengeOptionEntity(id = 200096, challengeId = 20042, text = "二", romaji = "ni", correct = true),
                ChallengeOptionEntity(id = 200097, challengeId = 20042, text = "一", romaji = "ichi", correct = false),
                ChallengeOptionEntity(id = 200098, challengeId = 20042, text = "三", romaji = "san", correct = false),

                ChallengeOptionEntity(id = 200099, challengeId = 20043, text = "三", romaji = "san", correct = true),
                ChallengeOptionEntity(id = 200100, challengeId = 20043, text = "一", romaji = "ichi", correct = false),
                ChallengeOptionEntity(id = 200101, challengeId = 20043, text = "二", romaji = "ni", correct = false),

                // Word Bank for 20044: いち + に + さん; decoys are taught words.
                ChallengeOptionEntity(id = 200102, challengeId = 20044, text = "いち", romaji = "ichi", correct = true),
                ChallengeOptionEntity(id = 200103, challengeId = 20044, text = "に", romaji = "ni", correct = true),
                ChallengeOptionEntity(id = 200104, challengeId = 20044, text = "さん", romaji = "san", correct = true),
                ChallengeOptionEntity(id = 200105, challengeId = 20044, text = "あい", romaji = "ai", correct = false),
                ChallengeOptionEntity(id = 200106, challengeId = 20044, text = "あさ", romaji = "asa", correct = false),

                ChallengeOptionEntity(id = 200107, challengeId = 20045, text = "いち に さん", romaji = "ichi ni san", correct = true),
                ChallengeOptionEntity(id = 200108, challengeId = 20045, text = "いち に あさ", romaji = "ichi ni asa", correct = false),
                ChallengeOptionEntity(id = 200109, challengeId = 20045, text = "あい に さん", romaji = "ai ni san", correct = false),
            )
        )

        // Challenges for Lesson 202 (Unit 2: Eating & Drinking)
        lessonDao.insertChallenges(
            listOf(
                ChallengeEntity(id = 2009, lessonId = 202, type = ChallengeType.SELECT, question = "What is 'Water' in Japanese?", audioSrc = "asset:///audio/ja/mizu.ogg", orderIndex = 0),
                ChallengeEntity(id = 2010, lessonId = 202, type = ChallengeType.SELECT, question = "What is 'Green Tea'?", audioSrc = "asset:///audio/ja/ocha.ogg", orderIndex = 1),
                ChallengeEntity(id = 2011, lessonId = 202, type = ChallengeType.WORD_BANK, question = "Assemble: 'Water, please'", orderIndex = 2),
                ChallengeEntity(id = 2012, lessonId = 202, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/ja/itadakimasu.ogg", orderIndex = 3),
            )
        )
        lessonDao.insertOptions(
            listOf(
                ChallengeOptionEntity(id = 20029, challengeId = 2009, text = "お水", romaji = "Mizu", correct = true, audioSrc = "asset:///audio/ja/mizu.ogg"),
                ChallengeOptionEntity(id = 20030, challengeId = 2009, text = "お茶", romaji = "Ocha", correct = false, audioSrc = "asset:///audio/ja/ocha.ogg"),
                ChallengeOptionEntity(id = 20031, challengeId = 2009, text = "ごはん", romaji = "Gohan", correct = false),

                ChallengeOptionEntity(id = 20032, challengeId = 2010, text = "お茶", romaji = "Ocha", correct = true, audioSrc = "asset:///audio/ja/ocha.ogg"),
                ChallengeOptionEntity(id = 20033, challengeId = 2010, text = "お水", romaji = "Mizu", correct = false, audioSrc = "asset:///audio/ja/mizu.ogg"),
                ChallengeOptionEntity(id = 20034, challengeId = 2010, text = "パン", romaji = "Pan", correct = false),

                // Word Bank for 2011: "お水 を ください"
                ChallengeOptionEntity(id = 20035, challengeId = 2011, text = "お水", romaji = "omizu", correct = true, audioSrc = "asset:///audio/ja/mizu.ogg"),
                ChallengeOptionEntity(id = 20036, challengeId = 2011, text = "を", romaji = "o", correct = true),
                ChallengeOptionEntity(id = 20037, challengeId = 2011, text = "ください", romaji = "kudasai", correct = true),
                ChallengeOptionEntity(id = 20038, challengeId = 2011, text = "お茶", romaji = "ocha", correct = false),
                ChallengeOptionEntity(id = 20039, challengeId = 2011, text = "はい", romaji = "hai", correct = false),

                ChallengeOptionEntity(id = 20040, challengeId = 2012, text = "いただきます", romaji = "Itadakimasu", correct = true),
                ChallengeOptionEntity(id = 20041, challengeId = 2012, text = "いただきません", romaji = "Itadakimasen", correct = false),
                ChallengeOptionEntity(id = 20042, challengeId = 2012, text = "いだきます", romaji = "Idakimasu", correct = false),
            )
        )

        // Challenges for Lesson 203 (Unit 2: Politeness & Gratitude)
        lessonDao.insertChallenges(
            listOf(
                ChallengeEntity(id = 2013, lessonId = 203, type = ChallengeType.SELECT, question = "How do you say 'Yes' in Japanese?", audioSrc = "asset:///audio/ja/hai.ogg", orderIndex = 0),
                ChallengeEntity(id = 2014, lessonId = 203, type = ChallengeType.SELECT, question = "How do you say 'Excuse me / Sorry'?", audioSrc = "asset:///audio/ja/sumimasen.ogg", orderIndex = 1),
                ChallengeEntity(
                    id = 2015, lessonId = 203, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'Yes, thank you'",
                    orderIndex = 2,
                    grammaticalFocus = "ja.polite_register",
                    ruleText = "ございます is the polite form of あります. Use ある with friends and family, ございます in polite speech.\nSo the polite thank-you is ありがとうございます, never あります.",
                ),
                ChallengeEntity(id = 2016, lessonId = 203, type = ChallengeType.LISTEN, question = "Tap what you hear", audioSrc = "asset:///audio/ja/sumimasen.ogg", orderIndex = 3),
            )
        )
        lessonDao.insertOptions(
            listOf(
                ChallengeOptionEntity(id = 20043, challengeId = 2013, text = "はい", romaji = "Hai", correct = true, audioSrc = "asset:///audio/ja/hai.ogg"),
                ChallengeOptionEntity(id = 20044, challengeId = 2013, text = "いいえ", romaji = "Iie", correct = false),
                ChallengeOptionEntity(id = 20045, challengeId = 2013, text = "さようなら", romaji = "Sayounara", correct = false),

                ChallengeOptionEntity(id = 20046, challengeId = 2014, text = "すみません", romaji = "Sumimasen", correct = true, audioSrc = "asset:///audio/ja/sumimasen.ogg"),
                ChallengeOptionEntity(id = 20047, challengeId = 2014, text = "ありがとう", romaji = "Arigatou", correct = false, audioSrc = "asset:///audio/ja/arigatou.ogg"),
                ChallengeOptionEntity(id = 20048, challengeId = 2014, text = "おはよう", romaji = "Ohayou", correct = false),

                // Word Bank for 2015: "はい ありがとう ございます"
                ChallengeOptionEntity(id = 20049, challengeId = 2015, text = "はい", romaji = "hai", correct = true, audioSrc = "asset:///audio/ja/hai.ogg"),
                ChallengeOptionEntity(id = 20050, challengeId = 2015, text = "ありがとう", romaji = "arigatou", correct = true, audioSrc = "asset:///audio/ja/arigatou.ogg"),
                ChallengeOptionEntity(id = 20051, challengeId = 2015, text = "ございます", romaji = "gozaimasu", correct = true),
                ChallengeOptionEntity(id = 20052, challengeId = 2015, text = "あります", romaji = "arimasu", correct = false, errorTag = "WRONG_REGISTER"),

                ChallengeOptionEntity(id = 20053, challengeId = 2015, text = "すみません", romaji = "sumimasen", correct = false),

                ChallengeOptionEntity(id = 20054, challengeId = 2016, text = "すみません", romaji = "Sumimasen", correct = true),
                ChallengeOptionEntity(id = 20055, challengeId = 2016, text = "すいせん", romaji = "Suinsen", correct = false),
                ChallengeOptionEntity(id = 20056, challengeId = 2016, text = "みません", romaji = "Mimasen", correct = false),
            )
        )
    }

    private suspend fun seedExpandedCurricula() {
        val spanishUnits = com.openlingo.app.data.local.curriculum.ExpandedCurriculumData.spanishExpandedUnits
        val japaneseUnits = com.openlingo.app.data.local.curriculum.ExpandedCurriculumData.japaneseExpandedUnits

        for (payload in (spanishUnits + japaneseUnits)) {
            courseDao.insertUnits(listOf(payload.unit))
            courseDao.insertLessons(payload.lessons)
            lessonDao.insertChallenges(payload.challenges)
            lessonDao.insertOptions(payload.options)
        }
    }

    private suspend fun seedAdvancedCurricula() {
        val spanishUnits = com.openlingo.app.data.local.curriculum.AdvancedCurriculumData.spanishAdvancedUnits
        val japaneseUnits = com.openlingo.app.data.local.curriculum.AdvancedCurriculumData.japaneseAdvancedUnits

        for (payload in (spanishUnits + japaneseUnits)) {
            courseDao.insertUnits(listOf(payload.unit))
            courseDao.insertLessons(payload.lessons)
            lessonDao.insertChallenges(payload.challenges)
            lessonDao.insertOptions(payload.options)
        }
    }

    private suspend fun seedB1Curricula() {
        val spanishUnits = com.openlingo.app.data.local.curriculum.B1CurriculumData.spanishA2Units +
            com.openlingo.app.data.local.curriculum.B1CurriculumData.spanishB1Units
        val japaneseUnits = com.openlingo.app.data.local.curriculum.B1CurriculumData.japaneseN4Units +
            com.openlingo.app.data.local.curriculum.JapaneseN4CurriculumData.japaneseN4ExtensionUnits

        for (payload in (spanishUnits + japaneseUnits)) {
            courseDao.insertUnits(listOf(payload.unit))
            courseDao.insertLessons(payload.lessons)
            lessonDao.insertChallenges(payload.challenges)
            lessonDao.insertOptions(payload.options)
        }
    }

    /**
     * Retrieves all challenges for the given unit IDs, with options loaded.
     */
    suspend fun getChallengesForUnits(unitIds: List<Int>): List<ChallengeWithOptions> =
        withContext(Dispatchers.IO) {
            val challenges = lessonDao.getChallengesForUnits(unitIds)
            challenges.map { challenge ->
                ChallengeWithOptions(
                    challenge = challenge,
                    options = lessonDao.getOptionsForChallenge(challenge.id),
                    isCompleted = false,
                )
            }
        }

    /**
     * Retrieves the held-out challenges for the given unit IDs, with options loaded.
     *
     * These rows are seeded with the rest of the curriculum but are excluded from
     * [getChallengesForLesson] and [getChallengesForUnits], so a checkpoint can
     * assess a grammar structure the learner was taught without re-testing the exact
     * sentences they memorised.
     */
    suspend fun getHeldOutChallengesForUnits(unitIds: List<Int>): List<ChallengeWithOptions> =
        withContext(Dispatchers.IO) {
            lessonDao.getHeldOutChallengesForUnits(unitIds).map { challenge ->
                ChallengeWithOptions(
                    challenge = challenge,
                    options = lessonDao.getOptionsForChallenge(challenge.id),
                    isCompleted = false,
                )
            }
        }

    /**
     * Saves a checkpoint assessment score.
     */
    suspend fun saveCheckpointScore(
        userId: String,
        courseId: Int,
        level: String,
        correct: Int,
        total: Int,
    ) = withContext(Dispatchers.IO) {
        checkpointScoreDao.insert(
            CheckpointScoreEntity(
                userId = userId,
                courseId = courseId,
                level = level,
                correct = correct,
                total = total,
            )
        )
    }

    /**
     * Queues missed challenge IDs into the mistakes table for focused practice.
     */
    suspend fun queueMistakes(missedChallengeIds: List<Int>) =
        withContext(Dispatchers.IO) {
            for (challengeId in missedChallengeIds) {
                val lessonId = lessonDao.getChallengeById(challengeId)?.lessonId ?: -1
                mistakeDao.upsertMistake(
                    MistakeEntity(
                        challengeId = challengeId,
                        lessonId = lessonId,
                        timestamp = System.currentTimeMillis(),
                    )
                )
            }
        }

    private suspend fun seedInitialVocabSchedule() {
        if (vocabScheduleDao.getTotalCount() > 0) return
        val initialItems = listOf(
            VocabScheduleEntity("es:hola", "es", "Hola", null, "Hello", "asset:///audio/es/hola.ogg", "Spanish Essentials"),
            VocabScheduleEntity("es:buenos_dias", "es", "Buenos días", null, "Good morning", "asset:///audio/es/buenos_dias.ogg", "Spanish Essentials"),
            VocabScheduleEntity("es:gracias", "es", "Gracias", null, "Thank you", "asset:///audio/es/gracias.ogg", "Spanish Essentials"),
            VocabScheduleEntity("es:un_cafe", "es", "Un café, por favor", null, "A coffee, please", "asset:///audio/es/un_cafe_por_favor.ogg", "Food & Dining"),
            VocabScheduleEntity("es:la_cuenta", "es", "La cuenta, por favor", null, "The bill, please", "asset:///audio/es/la_cuenta.ogg", "Food & Dining"),
            VocabScheduleEntity("es:yo_hablo", "es", "Yo hablo español", null, "I speak Spanish", "asset:///audio/es/yo_hablo_espanol.ogg", "Action Verbs"),
            VocabScheduleEntity("ja:konnichiwa", "ja", "こんにちは", "Konnichiwa", "Hello / Good day", "asset:///audio/ja/konnichiwa.ogg", "Japanese Greetings"),
            VocabScheduleEntity("ja:ohayou", "ja", "おはようございます", "Ohayou gozaimasu", "Good morning", "asset:///audio/ja/ohayou.ogg", "Japanese Greetings"),
            VocabScheduleEntity("ja:mizu", "ja", "お水", "Mizu", "Water", "asset:///audio/ja/mizu.ogg", "Food & Refreshments"),
            VocabScheduleEntity("ja:koohii", "ja", "コーヒー", "Koohii", "Coffee", "asset:///audio/ja/koohii.ogg", "Katakana Loanwords"),
            VocabScheduleEntity("ja:pan", "ja", "パン", "Pan", "Bread", "asset:///audio/ja/pan.ogg", "Katakana Loanwords"),
            VocabScheduleEntity("ja:tabemasu", "ja", "たべます", "Tabemasu", "To eat", "asset:///audio/ja/tabemasu.ogg", "Daily Verbs"),
            VocabScheduleEntity("ja:nomimasu", "ja", "のみます", "Nomimasu", "To drink", "asset:///audio/ja/nomimasu.ogg", "Daily Verbs"),
            VocabScheduleEntity("ja:sumimasen", "ja", "すみません", "Sumimasen", "Excuse me / Sorry", "asset:///audio/ja/sumimasen.ogg", "Polite Expressions"),
        )
        vocabScheduleDao.insertAll(initialItems)
    }

    fun getDueVocab(language: String): Flow<List<VocabScheduleEntity>> {
        return vocabScheduleDao.getDueVocabForLanguage(language)
    }

    fun getDueVocabCount(language: String): Flow<Int> {
        return vocabScheduleDao.getDueCountForLanguage(language)
    }

    fun getAllVocab(language: String): Flow<List<VocabScheduleEntity>> {
        return vocabScheduleDao.getVocabForLanguage(language)
    }

    /**
     * WI-11: builds the offline dictionary from the tables that are already
     * here — every seeded challenge with its options, plus the FSRS vocabulary
     * the Practice tab reviews.
     *
     * Three flat reads, once, off the main thread. Nothing here writes, so a
     * lookup can never touch the mistakes table, the challenge-progress table
     * or the exercise-type stats: reading a definition is study, not an
     * attempt.
     */
    suspend fun loadDictionary(): Dictionary = withContext(Dispatchers.IO) {
        DictionaryIndex.build(
            challenges = lessonDao.getAllChallenges(),
            options = lessonDao.getAllOptions(),
            vocab = vocabScheduleDao.getAllVocabDirect(),
        )
    }

    suspend fun reviewVocab(item: VocabScheduleEntity, rating: Int) = withContext(Dispatchers.IO) {
        val updated = FsrsScheduler.schedule(item, rating)
        vocabScheduleDao.update(updated)
    }
}

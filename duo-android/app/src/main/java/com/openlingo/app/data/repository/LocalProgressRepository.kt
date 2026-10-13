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
        for (payload in com.openlingo.app.data.local.curriculum.BaseCurriculumData.spanishBaseUnits) {
            courseDao.insertUnits(listOf(payload.unit))
            courseDao.insertLessons(payload.lessons)
            lessonDao.insertChallenges(payload.challenges)
            lessonDao.insertOptions(payload.options)
        }
    }

    private suspend fun seedJapaneseCourse() {
        courseDao.insertCourses(
            listOf(
                CourseEntity(id = 2, title = "Japanese", imageSrc = "🇯🇵")
            )
        )
        for (payload in com.openlingo.app.data.local.curriculum.BaseCurriculumData.japaneseBaseUnits) {
            courseDao.insertUnits(listOf(payload.unit))
            courseDao.insertLessons(payload.lessons)
            lessonDao.insertChallenges(payload.challenges)
            lessonDao.insertOptions(payload.options)
        }
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

package com.duo.app.data.repository

import com.duo.app.data.local.DuoDatabase
import com.duo.app.data.local.entities.ChallengeEntity
import com.duo.app.data.local.entities.ChallengeOptionEntity
import com.duo.app.data.local.entities.ChallengeProgressEntity
import com.duo.app.data.local.entities.CourseEntity
import com.duo.app.data.local.entities.LessonEntity
import com.duo.app.data.local.entities.UnitEntity
import com.duo.app.data.local.entities.UserProgressEntity
import com.duo.app.data.local.entities.UnitWithLessons
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import com.duo.app.data.local.models.BackupJson
import com.duo.app.data.local.models.CharacterMasteryBackup
import com.duo.app.data.local.models.DailyActivityBackup
import com.duo.app.data.local.models.MistakeBackup
import com.duo.app.data.local.models.OpenLingoBackup
import com.duo.app.data.local.models.UserProgressBackup
import com.duo.app.data.local.entities.CharacterMasteryEntity
import com.duo.app.data.local.entities.MistakeEntity

data class ChallengeWithOptions(
    val challenge: ChallengeEntity,
    val options: List<ChallengeOptionEntity>,
    val isCompleted: Boolean,
)

sealed interface AnswerResult {
    data class Correct(val pointsGained: Int, val totalPoints: Int, val hearts: Int, val streakRepaired: Boolean = false) : AnswerResult
    data class Incorrect(val remainingHearts: Int) : AnswerResult
}

class LocalProgressRepository(private val database: DuoDatabase) {

    companion object {
        const val GUEST_USER_ID = "guest_local"
        const val MAX_HEARTS = 5
        const val POINTS_PER_CHALLENGE = 10
        const val DAILY_QUEST_XP = 30
        const val PERFECT_BONUS = 5
    }

    private val courseDao = database.courseDao()
    private val lessonDao = database.lessonDao()
    private val userProgressDao = database.userProgressDao()
    private val challengeProgressDao = database.challengeProgressDao()
    private val characterMasteryDao = database.characterMasteryDao()
    private val mistakeDao = database.mistakeDao()
    private val dailyActivityDao = database.dailyActivityDao()
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
                // New day: hearts refill to full (Duolingo-style daily refill)
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

    suspend fun getMistakeChallenges(): List<ChallengeWithOptions> =
        withContext(Dispatchers.IO) {
            val mistakes = mistakeDao.getAllMistakesDirect()
            val challengeIds = mistakes.map { it.challengeId }.distinct()
            if (challengeIds.isEmpty()) return@withContext emptyList()
            val challenges = lessonDao.getChallengesByIds(challengeIds)
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

    suspend fun switchCourse(courseId: Int) = withContext(Dispatchers.IO) {
        userProgressDao.setActiveCourse(GUEST_USER_ID, courseId)
    }

    suspend fun setShowRomaji(showRomaji: Boolean) = withContext(Dispatchers.IO) {
        val current = userProgressDao.getUserProgressDirect(GUEST_USER_ID)
        val userId = current?.userId ?: GUEST_USER_ID
        userProgressDao.setShowRomaji(userId, showRomaji)
    }

    suspend fun submitAnswer(challengeId: Int, isCorrect: Boolean, isPractice: Boolean = false): AnswerResult =
        withContext(Dispatchers.IO) {
            val user = userProgressDao.getUserProgressDirect(GUEST_USER_ID)
                ?: UserProgressEntity(userId = GUEST_USER_ID, activeCourseId = 1, hearts = MAX_HEARTS, points = 0)

            if (isCorrect) {
                if (!isPractice) {
                    challengeProgressDao.markChallengeCompleted(
                        ChallengeProgressEntity(
                            userId = GUEST_USER_ID,
                            challengeId = challengeId,
                            completed = true,
                            synced = false,
                        )
                    )
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
                    val newHearts = (user.hearts - 1).coerceAtLeast(0)
                    userProgressDao.updateHearts(GUEST_USER_ID, newHearts)
                    lessonDao.getChallengeById(challengeId)?.let { challenge ->
                        mistakeDao.upsertMistake(
                            com.duo.app.data.local.entities.MistakeEntity(
                                challengeId = challengeId,
                                lessonId = challenge.lessonId,
                            )
                        )
                    }
                    AnswerResult.Incorrect(remainingHearts = newHearts)
                } else {
                    // Practice mode: still record the miss so it stays in the queue,
                    // but no heart loss.
                    lessonDao.getChallengeById(challengeId)?.let { challenge ->
                        mistakeDao.upsertMistake(
                            com.duo.app.data.local.entities.MistakeEntity(
                                challengeId = challengeId,
                                lessonId = challenge.lessonId,
                            )
                        )
                    }
                    AnswerResult.Incorrect(remainingHearts = user.hearts)
                }
            }
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
     * Full local reset: wipes completions, mistakes, and kana mastery, then
     * restores a fresh guest profile. Curriculum seeds are preserved.
     */
    suspend fun resetAllProgress() = withContext(Dispatchers.IO) {
        challengeProgressDao.clearProgressForUser(GUEST_USER_ID)
        mistakeDao.clearAllMistakes()
        characterMasteryDao.clearAllMastery()
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
        val backup = OpenLingoBackup(
            userProgress = userBackup,
            completedChallengeIds = completed,
            characterMastery = mastery,
            mistakes = mistakes,
            dailyActivity = daily,
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
                    )
                )
            }

            challengeProgressDao.clearProgressForUser(GUEST_USER_ID)
            backup.completedChallengeIds.forEach { id ->
                challengeProgressDao.markChallengeCompleted(
                    ChallengeProgressEntity(
                        userId = GUEST_USER_ID,
                        challengeId = id,
                        completed = true,
                        synced = false,
                    )
                )
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

            backup.dailyActivity.forEach { d ->
                dailyActivityDao.addXp(d.date, d.xp)
            }
        }
    }

    fun getCharacterMastery(): Flow<List<com.duo.app.data.local.entities.CharacterMasteryEntity>> =
        characterMasteryDao.getAllMastery().flowOn(Dispatchers.IO)

    suspend fun markCharacterMastered(
        character: String,
        script: String,
    ) = withContext(Dispatchers.IO) {
        val existing = characterMasteryDao.getMasteryDirect(character)
        characterMasteryDao.upsertMastery(
            com.duo.app.data.local.entities.CharacterMasteryEntity(
                character = character,
                script = script,
                attempts = (existing?.attempts ?: 0) + 1,
                masteredAt = System.currentTimeMillis(),
            )
        )
    }

    fun getMistakes(): Flow<List<com.duo.app.data.local.entities.MistakeEntry>> =
        mistakeDao.getMistakeEntries().flowOn(Dispatchers.IO)

    fun getMistakeCount(): Flow<Int> =
        mistakeDao.getMistakeCount().flowOn(Dispatchers.IO)

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
                LessonEntity(id = 100, unitId = 10, title = "Lesson 1: Greetings & Nouns", orderIndex = 0),
                LessonEntity(id = 101, unitId = 10, title = "Lesson 2: Common Phrases", orderIndex = 1),
                LessonEntity(id = 102, unitId = 11, title = "Lesson 3: Family Members", orderIndex = 0),
                LessonEntity(id = 103, unitId = 11, title = "Lesson 4: Introductions", orderIndex = 1),
            )
        )

        // Challenges for Lesson 100 (Unit 1)
        lessonDao.insertChallenges(
            listOf(
                ChallengeEntity(id = 1001, lessonId = 100, type = "SELECT", question = "Which one of these is 'The man'?", orderIndex = 0),
                ChallengeEntity(id = 1002, lessonId = 100, type = "ASSIST", question = "Translate: 'Good morning'", audioSrc = "asset:///audio/es/buenos_dias.ogg", orderIndex = 1),
                ChallengeEntity(id = 1003, lessonId = 100, type = "WORD_BANK", question = "Translate: 'Hello, good morning'", orderIndex = 2),
                ChallengeEntity(id = 1004, lessonId = 100, type = "LISTEN", question = "Tap what you hear", audioSrc = "asset:///audio/es/buenos_dias.ogg", orderIndex = 3),
            )
        )
        lessonDao.insertOptions(
            listOf(
                ChallengeOptionEntity(id = 10001, challengeId = 1001, text = "El hombre", correct = true, audioSrc = "asset:///audio/es/el_padre.ogg"),
                ChallengeOptionEntity(id = 10002, challengeId = 1001, text = "La mujer", correct = false, audioSrc = "asset:///audio/es/la_madre.ogg"),
                ChallengeOptionEntity(id = 10003, challengeId = 1001, text = "La manzana", correct = false),

                ChallengeOptionEntity(id = 10004, challengeId = 1002, text = "Buenos días", correct = true, audioSrc = "asset:///audio/es/buenos_dias.ogg"),
                ChallengeOptionEntity(id = 10005, challengeId = 1002, text = "Buenas noches", correct = false),
                ChallengeOptionEntity(id = 10006, challengeId = 1002, text = "Hola", correct = false, audioSrc = "asset:///audio/es/hola.ogg"),

                // Word Bank for 1003: "¡Hola! buenos días"
                ChallengeOptionEntity(id = 10007, challengeId = 1003, text = "¡Hola!", correct = true),
                ChallengeOptionEntity(id = 10008, challengeId = 1003, text = "buenos", correct = true),
                ChallengeOptionEntity(id = 10009, challengeId = 1003, text = "días", correct = true),
                ChallengeOptionEntity(id = 10010, challengeId = 1003, text = "adiós", correct = false),
                ChallengeOptionEntity(id = 10011, challengeId = 1003, text = "noche", correct = false),

                ChallengeOptionEntity(id = 10012, challengeId = 1004, text = "Good morning", correct = true),
                ChallengeOptionEntity(id = 10013, challengeId = 1004, text = "Good night", correct = false),
                ChallengeOptionEntity(id = 10014, challengeId = 1004, text = "Goodbye", correct = false),
            )
        )

        // Challenges for Lesson 101 (Unit 1)
        lessonDao.insertChallenges(
            listOf(
                ChallengeEntity(id = 1005, lessonId = 101, type = "SELECT", question = "How do you say 'Thank you'?", audioSrc = "asset:///audio/es/gracias.ogg", orderIndex = 0),
                ChallengeEntity(id = 1006, lessonId = 101, type = "WORD_BANK", question = "Translate: 'Yes, please'", orderIndex = 1),
                ChallengeEntity(id = 1007, lessonId = 101, type = "LISTEN", question = "Tap what you hear", audioSrc = "asset:///audio/es/por_favor.ogg", orderIndex = 2),
                ChallengeEntity(id = 1008, lessonId = 101, type = "SELECT", question = "What is 'You're welcome'?", audioSrc = "asset:///audio/es/de_nada.ogg", orderIndex = 3),
            )
        )
        lessonDao.insertOptions(
            listOf(
                ChallengeOptionEntity(id = 10015, challengeId = 1005, text = "Muchas gracias", correct = true, audioSrc = "asset:///audio/es/gracias.ogg"),
                ChallengeOptionEntity(id = 10016, challengeId = 1005, text = "De nada", correct = false, audioSrc = "asset:///audio/es/de_nada.ogg"),
                ChallengeOptionEntity(id = 10017, challengeId = 1005, text = "Por favor", correct = false, audioSrc = "asset:///audio/es/por_favor.ogg"),

                // Word Bank for 1006: "Sí por favor"
                ChallengeOptionEntity(id = 10018, challengeId = 1006, text = "Sí", correct = true),
                ChallengeOptionEntity(id = 10019, challengeId = 1006, text = "por", correct = true),
                ChallengeOptionEntity(id = 10020, challengeId = 1006, text = "favor", correct = true),
                ChallengeOptionEntity(id = 10021, challengeId = 1006, text = "no", correct = false),
                ChallengeOptionEntity(id = 10022, challengeId = 1006, text = "gracias", correct = false),

                ChallengeOptionEntity(id = 10023, challengeId = 1007, text = "Please", correct = true),
                ChallengeOptionEntity(id = 10024, challengeId = 1007, text = "Thank you", correct = false),
                ChallengeOptionEntity(id = 10025, challengeId = 1007, text = "You're welcome", correct = false),

                ChallengeOptionEntity(id = 10026, challengeId = 1008, text = "De nada", correct = true, audioSrc = "asset:///audio/es/de_nada.ogg"),
                ChallengeOptionEntity(id = 10027, challengeId = 1008, text = "Perdón", correct = false),
                ChallengeOptionEntity(id = 10028, challengeId = 1008, text = "Hasta luego", correct = false),
            )
        )

        // Challenges for Lesson 102 (Unit 2: Family)
        lessonDao.insertChallenges(
            listOf(
                ChallengeEntity(id = 1009, lessonId = 102, type = "SELECT", question = "Translate: 'The father'", audioSrc = "asset:///audio/es/el_padre.ogg", orderIndex = 0),
                ChallengeEntity(id = 1010, lessonId = 102, type = "SELECT", question = "Translate: 'The mother'", audioSrc = "asset:///audio/es/la_madre.ogg", orderIndex = 1),
                ChallengeEntity(id = 1011, lessonId = 102, type = "WORD_BANK", question = "Translate: 'The father and the mother'", orderIndex = 2),
                ChallengeEntity(id = 1012, lessonId = 102, type = "LISTEN", question = "Tap what you hear", audioSrc = "asset:///audio/es/la_familia.ogg", orderIndex = 3),
            )
        )
        lessonDao.insertOptions(
            listOf(
                ChallengeOptionEntity(id = 10029, challengeId = 1009, text = "El padre", correct = true, audioSrc = "asset:///audio/es/el_padre.ogg"),
                ChallengeOptionEntity(id = 10030, challengeId = 1009, text = "La madre", correct = false, audioSrc = "asset:///audio/es/la_madre.ogg"),
                ChallengeOptionEntity(id = 10031, challengeId = 1009, text = "El hermano", correct = false),

                ChallengeOptionEntity(id = 10032, challengeId = 1010, text = "La madre", correct = true, audioSrc = "asset:///audio/es/la_madre.ogg"),
                ChallengeOptionEntity(id = 10033, challengeId = 1010, text = "La hermana", correct = false),
                ChallengeOptionEntity(id = 10034, challengeId = 1010, text = "La niña", correct = false),

                // Word Bank for 1011: "El padre y la madre"
                ChallengeOptionEntity(id = 10035, challengeId = 1011, text = "El", correct = true),
                ChallengeOptionEntity(id = 10036, challengeId = 1011, text = "padre", correct = true),
                ChallengeOptionEntity(id = 10037, challengeId = 1011, text = "y", correct = true),
                ChallengeOptionEntity(id = 10038, challengeId = 1011, text = "la", correct = true),
                ChallengeOptionEntity(id = 10039, challengeId = 1011, text = "madre", correct = true),
                ChallengeOptionEntity(id = 10040, challengeId = 1011, text = "hijo", correct = false),

                ChallengeOptionEntity(id = 10041, challengeId = 1012, text = "The family", correct = true),
                ChallengeOptionEntity(id = 10042, challengeId = 1012, text = "The house", correct = false),
                ChallengeOptionEntity(id = 10043, challengeId = 1012, text = "The city", correct = false),
            )
        )

        // Challenges for Lesson 103 (Unit 2: Introductions)
        lessonDao.insertChallenges(
            listOf(
                ChallengeEntity(id = 1013, lessonId = 103, type = "SELECT", question = "How do you say 'Nice to meet you'?", audioSrc = "asset:///audio/es/mucho_gusto.ogg", orderIndex = 0),
                ChallengeEntity(id = 1014, lessonId = 103, type = "WORD_BANK", question = "Translate: 'I am a boy'", orderIndex = 1),
                ChallengeEntity(id = 1015, lessonId = 103, type = "LISTEN", question = "Tap what you hear", audioSrc = "asset:///audio/es/yo_soy_un_nino.ogg", orderIndex = 2),
            )
        )
        lessonDao.insertOptions(
            listOf(
                ChallengeOptionEntity(id = 10044, challengeId = 1013, text = "¡Mucho gusto!", correct = true, audioSrc = "asset:///audio/es/mucho_gusto.ogg"),
                ChallengeOptionEntity(id = 10045, challengeId = 1013, text = "Hasta luego", correct = false),
                ChallengeOptionEntity(id = 10046, challengeId = 1013, text = "Buenos días", correct = false, audioSrc = "asset:///audio/es/buenos_dias.ogg"),

                // Word Bank for 1014: "Yo soy un niño"
                ChallengeOptionEntity(id = 10047, challengeId = 1014, text = "Yo", correct = true),
                ChallengeOptionEntity(id = 10048, challengeId = 1014, text = "soy", correct = true),
                ChallengeOptionEntity(id = 10049, challengeId = 1014, text = "un", correct = true),
                ChallengeOptionEntity(id = 10050, challengeId = 1014, text = "niño", correct = true),
                ChallengeOptionEntity(id = 10051, challengeId = 1014, text = "chica", correct = false),
                ChallengeOptionEntity(id = 10052, challengeId = 1014, text = "una", correct = false),

                ChallengeOptionEntity(id = 10053, challengeId = 1015, text = "I am a boy", correct = true),
                ChallengeOptionEntity(id = 10054, challengeId = 1015, text = "I am a girl", correct = false),
                ChallengeOptionEntity(id = 10055, challengeId = 1015, text = "I am a student", correct = false),
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
                    description = "Learn essential Japanese sounds and daily greetings",
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
                LessonEntity(id = 200, unitId = 20, title = "Lesson 1: Greetings", orderIndex = 0),
                LessonEntity(id = 201, unitId = 20, title = "Lesson 2: Numbers 1-3", orderIndex = 1),
                LessonEntity(id = 202, unitId = 21, title = "Lesson 3: Eating & Drinking", orderIndex = 0),
                LessonEntity(id = 203, unitId = 21, title = "Lesson 4: Politeness & Gratitude", orderIndex = 1),
            )
        )

        // Challenges for Lesson 200 (Unit 1)
        lessonDao.insertChallenges(
            listOf(
                ChallengeEntity(id = 2001, lessonId = 200, type = "SELECT", question = "Which one means 'Hello / Good day'?", audioSrc = "asset:///audio/ja/konnichiwa.ogg", orderIndex = 0),
                ChallengeEntity(id = 2002, lessonId = 200, type = "ASSIST", question = "Which phrase means 'Good morning'?", audioSrc = "asset:///audio/ja/ohayou.ogg", orderIndex = 1),
                ChallengeEntity(id = 2003, lessonId = 200, type = "WORD_BANK", question = "Assemble: 'Thank you very much'", orderIndex = 2),
                ChallengeEntity(id = 2004, lessonId = 200, type = "LISTEN", question = "Tap what you hear", audioSrc = "asset:///audio/ja/konnichiwa.ogg", orderIndex = 3),
            )
        )
        lessonDao.insertOptions(
            listOf(
                ChallengeOptionEntity(id = 20001, challengeId = 2001, text = "こんにちは", romaji = "Konnichiwa", correct = true, audioSrc = "asset:///audio/ja/konnichiwa.ogg"),
                ChallengeOptionEntity(id = 20002, challengeId = 2001, text = "さようなら", romaji = "Sayounara", correct = false),
                ChallengeOptionEntity(id = 20003, challengeId = 2001, text = "ありがとう", romaji = "Arigatou", correct = false, audioSrc = "asset:///audio/ja/arigatou.ogg"),

                ChallengeOptionEntity(id = 20004, challengeId = 2002, text = "おはようございます", romaji = "Ohayou gozaimasu", correct = true, audioSrc = "asset:///audio/ja/ohayou.ogg"),
                ChallengeOptionEntity(id = 20005, challengeId = 2002, text = "こんばんは", romaji = "Konbanwa", correct = false),
                ChallengeOptionEntity(id = 20006, challengeId = 2002, text = "はい", romaji = "Hai", correct = false, audioSrc = "asset:///audio/ja/hai.ogg"),

                // Word Bank for 2003: "どうも ありがとう ございます"
                ChallengeOptionEntity(id = 20007, challengeId = 2003, text = "どうも", romaji = "doumo", correct = true),
                ChallengeOptionEntity(id = 20008, challengeId = 2003, text = "ありがとう", romaji = "arigatou", correct = true, audioSrc = "asset:///audio/ja/arigatou.ogg"),
                ChallengeOptionEntity(id = 20009, challengeId = 2003, text = "ございます", romaji = "gozaimasu", correct = true),
                ChallengeOptionEntity(id = 20010, challengeId = 2003, text = "いいえ", romaji = "iie", correct = false),
                ChallengeOptionEntity(id = 20011, challengeId = 2003, text = "こんにちは", romaji = "konnichiwa", correct = false),

                ChallengeOptionEntity(id = 20012, challengeId = 2004, text = "Hello / Good day", correct = true),
                ChallengeOptionEntity(id = 20013, challengeId = 2004, text = "Goodbye", correct = false),
                ChallengeOptionEntity(id = 20014, challengeId = 2004, text = "Excuse me", correct = false),
            )
        )

        // Challenges for Lesson 201 (Unit 1: Numbers)
        lessonDao.insertChallenges(
            listOf(
                ChallengeEntity(id = 2005, lessonId = 201, type = "SELECT", question = "Which kanji is 'One (1)'?", orderIndex = 0),
                ChallengeEntity(id = 2006, lessonId = 201, type = "SELECT", question = "Which kanji is 'Two (2)'?", orderIndex = 1),
                ChallengeEntity(id = 2007, lessonId = 201, type = "WORD_BANK", question = "Assemble: 'One, two, three'", orderIndex = 2),
                ChallengeEntity(id = 2008, lessonId = 201, type = "LISTEN", question = "Tap what you hear", audioSrc = "asset:///audio/ja/ichi_ni_san.ogg", orderIndex = 3),
            )
        )
        lessonDao.insertOptions(
            listOf(
                ChallengeOptionEntity(id = 20015, challengeId = 2005, text = "一", romaji = "Ichi", correct = true),
                ChallengeOptionEntity(id = 20016, challengeId = 2005, text = "二", romaji = "Ni", correct = false),
                ChallengeOptionEntity(id = 20017, challengeId = 2005, text = "三", romaji = "San", correct = false),

                ChallengeOptionEntity(id = 20018, challengeId = 2006, text = "二", romaji = "Ni", correct = true),
                ChallengeOptionEntity(id = 20019, challengeId = 2006, text = "一", romaji = "Ichi", correct = false),
                ChallengeOptionEntity(id = 20020, challengeId = 2006, text = "四", romaji = "Shi", correct = false),

                // Word Bank for 2007: "いち に さん"
                ChallengeOptionEntity(id = 20021, challengeId = 2007, text = "いち", romaji = "ichi", correct = true),
                ChallengeOptionEntity(id = 20022, challengeId = 2007, text = "に", romaji = "ni", correct = true),
                ChallengeOptionEntity(id = 20023, challengeId = 2007, text = "さん", romaji = "san", correct = true),
                ChallengeOptionEntity(id = 20024, challengeId = 2007, text = "よん", romaji = "yon", correct = false),
                ChallengeOptionEntity(id = 20025, challengeId = 2007, text = "ご", romaji = "go", correct = false),

                ChallengeOptionEntity(id = 20026, challengeId = 2008, text = "One, two, three", correct = true),
                ChallengeOptionEntity(id = 20027, challengeId = 2008, text = "Four, five, six", correct = false),
                ChallengeOptionEntity(id = 20028, challengeId = 2008, text = "Seven, eight, nine", correct = false),
            )
        )

        // Challenges for Lesson 202 (Unit 2: Eating & Drinking)
        lessonDao.insertChallenges(
            listOf(
                ChallengeEntity(id = 2009, lessonId = 202, type = "SELECT", question = "What is 'Water' in Japanese?", audioSrc = "asset:///audio/ja/mizu.ogg", orderIndex = 0),
                ChallengeEntity(id = 2010, lessonId = 202, type = "SELECT", question = "What is 'Green Tea'?", audioSrc = "asset:///audio/ja/ocha.ogg", orderIndex = 1),
                ChallengeEntity(id = 2011, lessonId = 202, type = "WORD_BANK", question = "Assemble: 'Water, please'", orderIndex = 2),
                ChallengeEntity(id = 2012, lessonId = 202, type = "LISTEN", question = "Tap what you hear", audioSrc = "asset:///audio/ja/itadakimasu.ogg", orderIndex = 3),
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

                ChallengeOptionEntity(id = 20040, challengeId = 2012, text = "Thank you for the food (Bon appetit)", correct = true),
                ChallengeOptionEntity(id = 20041, challengeId = 2012, text = "Excuse me", correct = false),
                ChallengeOptionEntity(id = 20042, challengeId = 2012, text = "Good morning", correct = false),
            )
        )

        // Challenges for Lesson 203 (Unit 2: Politeness & Gratitude)
        lessonDao.insertChallenges(
            listOf(
                ChallengeEntity(id = 2013, lessonId = 203, type = "SELECT", question = "How do you say 'Yes' in Japanese?", audioSrc = "asset:///audio/ja/hai.ogg", orderIndex = 0),
                ChallengeEntity(id = 2014, lessonId = 203, type = "SELECT", question = "How do you say 'Excuse me / Sorry'?", audioSrc = "asset:///audio/ja/sumimasen.ogg", orderIndex = 1),
                ChallengeEntity(id = 2015, lessonId = 203, type = "WORD_BANK", question = "Assemble: 'Yes, thank you'", orderIndex = 2),
                ChallengeEntity(id = 2016, lessonId = 203, type = "LISTEN", question = "Tap what you hear", audioSrc = "asset:///audio/ja/sumimasen.ogg", orderIndex = 3),
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
                ChallengeOptionEntity(id = 20052, challengeId = 2015, text = "いいえ", romaji = "iie", correct = false),

                ChallengeOptionEntity(id = 20053, challengeId = 2015, text = "すみません", romaji = "sumimasen", correct = false),

                ChallengeOptionEntity(id = 20054, challengeId = 2016, text = "Excuse me / Sorry", correct = true),
                ChallengeOptionEntity(id = 20055, challengeId = 2016, text = "Goodbye", correct = false),
                ChallengeOptionEntity(id = 20056, challengeId = 2016, text = "Hello", correct = false),
            )
        )
    }

    private suspend fun seedExpandedCurricula() {
        val spanishUnits = com.duo.app.data.local.curriculum.ExpandedCurriculumData.spanishExpandedUnits
        val japaneseUnits = com.duo.app.data.local.curriculum.ExpandedCurriculumData.japaneseExpandedUnits

        for (payload in (spanishUnits + japaneseUnits)) {
            courseDao.insertUnits(listOf(payload.unit))
            courseDao.insertLessons(payload.lessons)
            lessonDao.insertChallenges(payload.challenges)
            lessonDao.insertOptions(payload.options)
        }
    }

    private suspend fun seedAdvancedCurricula() {
        val spanishUnits = com.duo.app.data.local.curriculum.AdvancedCurriculumData.spanishAdvancedUnits
        val japaneseUnits = com.duo.app.data.local.curriculum.AdvancedCurriculumData.japaneseAdvancedUnits

        for (payload in (spanishUnits + japaneseUnits)) {
            courseDao.insertUnits(listOf(payload.unit))
            courseDao.insertLessons(payload.lessons)
            lessonDao.insertChallenges(payload.challenges)
            lessonDao.insertOptions(payload.options)
        }
    }

    private suspend fun seedB1Curricula() {
        val spanishUnits = com.duo.app.data.local.curriculum.B1CurriculumData.spanishB1Units
        val japaneseUnits = com.duo.app.data.local.curriculum.B1CurriculumData.japaneseB1Units

        for (payload in (spanishUnits + japaneseUnits)) {
            courseDao.insertUnits(listOf(payload.unit))
            courseDao.insertLessons(payload.lessons)
            lessonDao.insertChallenges(payload.challenges)
            lessonDao.insertOptions(payload.options)
        }
    }
}

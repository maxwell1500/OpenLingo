package com.duo.app.data.local.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class OpenLingoBackup(
    val version: Int = 2,
    val exportedAt: Long = System.currentTimeMillis(),
    val userProgress: UserProgressBackup? = null,
    val completedChallengeIds: List<Int> = emptyList(),
    val characterMastery: List<CharacterMasteryBackup> = emptyList(),
    val mistakes: List<MistakeBackup> = emptyList(),
    val dailyActivity: List<DailyActivityBackup> = emptyList(),
    val checkpointScores: List<CheckpointScoreBackup> = emptyList(),
    val vocabSchedule: List<VocabScheduleBackup> = emptyList(),
)

@Serializable
data class UserProgressBackup(
    val points: Int = 0,
    val hearts: Int = 5,
    val streak: Int = 1,
    val lastActiveDate: String,
    val showRomaji: Boolean = true,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val themeAccent: String = "TEAL",
    val themeMode: String = "SYSTEM",
)

@Serializable
data class CharacterMasteryBackup(
    val character: String,
    val script: String,
    val attempts: Int,
    val masteredAt: Long,
)

@Serializable
data class MistakeBackup(
    val challengeId: Int,
    val lessonId: Int,
    val timestamp: Long,
)

@Serializable
data class DailyActivityBackup(
    val date: String,
    val xp: Int,
)

@Serializable
data class CheckpointScoreBackup(
    val courseId: Int,
    val level: String,
    val correct: Int,
    val total: Int,
    val timestamp: Long,
)

@Serializable
data class VocabScheduleBackup(
    val id: String,
    val language: String,
    val foreign: String,
    val romaji: String? = null,
    val translation: String,
    val audioSrc: String? = null,
    val category: String,
    val difficulty: Double = 5.0,
    val stability: Double = 2.0,
    val reps: Int = 0,
    val lapses: Int = 0,
    val state: Int = 0,
    val lastReview: Long = 0L,
    val due: Long = 0L,
)

object BackupJson {
    val format = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
}

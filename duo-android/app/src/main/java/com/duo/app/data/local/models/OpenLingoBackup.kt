package com.duo.app.data.local.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class OpenLingoBackup(
    val version: Int = 1,
    val exportedAt: Long = System.currentTimeMillis(),
    val userProgress: UserProgressBackup? = null,
    val completedChallengeIds: List<Int> = emptyList(),
    val characterMastery: List<CharacterMasteryBackup> = emptyList(),
    val mistakes: List<MistakeBackup> = emptyList(),
    val dailyActivity: List<DailyActivityBackup> = emptyList(),
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

object BackupJson {
    val format = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
}

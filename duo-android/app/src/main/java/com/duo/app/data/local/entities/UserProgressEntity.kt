package com.duo.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val userId: String,
    val userName: String = "Guest",
    val userImageSrc: String = "/mascot.svg",
    val activeCourseId: Int = 1, // 1 = Spanish, 2 = Japanese
    val hearts: Int = 5,
    val points: Int = 0,
    val streak: Int = 1,
    val lastActiveDate: String = java.time.LocalDate.now().toString(),
    val showRomaji: Boolean = true,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val onboardingSeen: Boolean = false,
    val brokenStreak: Int = 0,
    val lastSynced: Long = System.currentTimeMillis(),
)

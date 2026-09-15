package com.duo.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "challenge_progress")
data class ChallengeProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val challengeId: Int,
    val completed: Boolean,
    val synced: Boolean = false, // false for local offline progress
    val lastSynced: Long = System.currentTimeMillis(),
)

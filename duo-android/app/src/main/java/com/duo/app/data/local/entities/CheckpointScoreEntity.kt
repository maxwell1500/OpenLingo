package com.duo.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checkpoint_scores")
data class CheckpointScoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val courseId: Int,
    val level: String, // "A1", "B1", "N5", "N4"
    val correct: Int,
    val total: Int,
    val timestamp: Long = System.currentTimeMillis(),
)

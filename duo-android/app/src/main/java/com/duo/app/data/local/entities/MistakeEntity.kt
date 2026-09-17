package com.duo.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Wrong answers awaiting review. One row per challenge; cleared when answered correctly. */
@Entity(tableName = "mistakes")
data class MistakeEntity(
    @PrimaryKey val challengeId: Int,
    val lessonId: Int,
    val timestamp: Long = System.currentTimeMillis(),
)

/** Mistake joined with its challenge text for the Practice review list. */
data class MistakeEntry(
    val challengeId: Int,
    val lessonId: Int,
    val lessonName: String,
    val question: String,
    val type: String,
    val timestamp: Long,
)

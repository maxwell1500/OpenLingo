package com.duo.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "challenges")
data class ChallengeEntity(
    @PrimaryKey val id: Int,
    val lessonId: Int,
    val type: String, // SELECT | ASSIST | WORD_BANK | LISTEN
    val question: String,
    val romaji: String? = null,
    val orderIndex: Int,
    val audioSrc: String? = null,
    val lastSynced: Long = System.currentTimeMillis(),
)

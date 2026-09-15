package com.duo.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "challenge_options")
data class ChallengeOptionEntity(
    @PrimaryKey val id: Int,
    val challengeId: Int,
    val text: String,
    val correct: Boolean,
    val romaji: String? = null,
    val imageSrc: String? = null,
    val audioSrc: String? = null,
    val lastSynced: Long = System.currentTimeMillis(),
)

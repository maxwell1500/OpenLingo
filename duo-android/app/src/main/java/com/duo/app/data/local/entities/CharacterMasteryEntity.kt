package com.duo.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Per-character kana tracing mastery. One row per character (guest-local, offline). */
@Entity(tableName = "character_mastery")
data class CharacterMasteryEntity(
    @PrimaryKey val character: String,
    val script: String, // "HIRAGANA" | "KATAKANA"
    val attempts: Int = 0,
    val masteredAt: Long = System.currentTimeMillis(),
)

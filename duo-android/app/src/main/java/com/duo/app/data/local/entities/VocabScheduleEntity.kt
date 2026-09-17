package com.duo.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Vocab item tracked via Free Spaced Repetition Scheduler (FSRS-4.5 inspired).
 * Fully client-side and offline.
 */
@Entity(tableName = "vocab_schedule")
data class VocabScheduleEntity(
    @PrimaryKey val id: String, // e.g. "es:hola" or "ja:こんにちは"
    val language: String, // "es" or "ja"
    val foreign: String,
    val romaji: String? = null,
    val translation: String,
    val audioSrc: String? = null,
    val category: String,
    val difficulty: Double = 5.0, // 1.0 (easiest) to 10.0 (hardest)
    val stability: Double = 2.0,  // days until retrievability falls to 90%
    val reps: Int = 0,
    val lapses: Int = 0,
    val state: Int = 0,          // 0 = New, 1 = Learning, 2 = Review, 3 = Relearning
    val lastReview: Long = 0L,   // timestamp ms
    val due: Long = 0L,          // timestamp ms
)

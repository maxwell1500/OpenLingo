package com.duo.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tracks aggregate accuracy per exercise type (SELECT, ASSIST, WORD_BANK, LISTEN, MATCH_PAIRS, STORY)
 * to adaptively prioritize weak areas.
 */
@Entity(tableName = "exercise_type_stats")
data class ExerciseTypeStatsEntity(
    @PrimaryKey val type: String,
    val attempts: Int = 0,
    val correct: Int = 0,
) {
    val accuracyPercent: Int
        get() = if (attempts > 0) (correct * 100) / attempts else 100
}

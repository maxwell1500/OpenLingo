package com.openlingo.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Wrong answers awaiting review. One row per challenge; cleared when answered correctly. */
@Entity(tableName = "mistakes")
data class MistakeEntity(
    @PrimaryKey val challengeId: Int,
    val lessonId: Int,
    val timestamp: Long = System.currentTimeMillis(),
)

/**
 * Mistake joined with its challenge text for the Practice review list.
 *
 * [grammaticalFocus] and [ruleText] are the challenge's own columns, read
 * through the join rather than copied into `mistakes` (WI-16). Both are
 * nullable because the column is nullable: a mistake on a challenge written
 * before the grammar overhaul has no rule, and the review list has to render
 * that honestly instead of showing an empty card.
 */
data class MistakeEntry(
    val challengeId: Int,
    val lessonId: Int,
    val lessonName: String,
    val question: String,
    val type: String,
    val timestamp: Long,
    val grammaticalFocus: String? = null,
    val ruleText: String? = null,
)

package com.duo.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.duo.app.data.local.models.ChallengeType

@Entity(tableName = "challenges")
data class ChallengeEntity(
    @PrimaryKey val id: Int,
    val lessonId: Int,
    // SELECT | ASSIST | WORD_BANK | LISTEN | MATCH_PAIRS | STORY | CONJUGATE | FILL_BLANK
    val type: ChallengeType,
    val question: String,
    val romaji: String? = null,
    val orderIndex: Int,
    val audioSrc: String? = null,
    val lastSynced: Long = System.currentTimeMillis(),
    /** Short tag for the grammar point being drilled, e.g. "PRETERITE_INDEFINIDO". */
    val grammaticalFocus: String? = null,
    /** The 2-4 line rule shown to the learner, in English with target-language examples. */
    val ruleText: String? = null,
    /** Pipe-delimited accepted answers for FILL_BLANK, e.g. "soy|estoy". Null for every other type. */
    val acceptedAnswers: String? = null,
    /**
     * Held-out checkpoint item (WI-08). Held-out challenges are seeded with the rest of the
     * curriculum but are filtered out of every lesson-path query, so a checkpoint can test
     * a grammar structure the learner was taught but never saw a sentence of.
     */
    val heldOut: Boolean = false,
)

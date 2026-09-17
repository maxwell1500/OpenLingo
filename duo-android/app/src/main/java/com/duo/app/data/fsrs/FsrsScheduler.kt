package com.duo.app.data.fsrs

import com.duo.app.data.local.entities.VocabScheduleEntity
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Free Spaced Repetition Scheduler (FSRS-4.5) implementation.
 *
 * Ratings:
 * 1 = Again (forgot)
 * 2 = Hard (recalled with serious difficulty)
 * 3 = Good (recalled with normal effort)
 * 4 = Easy (recalled immediately / effortlessly)
 *
 * States:
 * 0 = New
 * 1 = Learning
 * 2 = Review
 * 3 = Relearning
 */
object FsrsScheduler {

    const val RATING_AGAIN = 1
    const val RATING_HARD = 2
    const val RATING_GOOD = 3
    const val RATING_EASY = 4

    private const val REQUEST_RETENTION = 0.90
    private const val ONE_DAY_MS = 24 * 60 * 60 * 1000L

    // Default FSRS-4.5 parameter weights
    private val w = doubleArrayOf(
        0.4072, 1.1827, 3.1262, 15.4722, // initial stability for Again, Hard, Good, Easy
        7.2102, 0.5316, 1.0651, 0.0234,  // difficulty updates
        1.616, 0.1544, 1.0824,           // stability updates
        1.9813, 0.0953, 0.2975, 0.472    // recall / retrievability
    )

    fun schedule(
        entity: VocabScheduleEntity,
        rating: Int,
        now: Long = System.currentTimeMillis(),
    ): VocabScheduleEntity {
        val clampedRating = rating.coerceIn(1, 4)
        val isNew = entity.state == 0 || entity.reps == 0

        val newDifficulty: Double
        val newStability: Double
        val newState: Int
        val newLapses = if (clampedRating == RATING_AGAIN && !isNew) entity.lapses + 1 else entity.lapses

        if (isNew) {
            newDifficulty = initDifficulty(clampedRating)
            newStability = initStability(clampedRating)
            newState = if (clampedRating == RATING_AGAIN) 1 else 2
        } else {
            val elapsedDays = if (entity.lastReview > 0L) {
                max(0.0, (now - entity.lastReview).toDouble() / ONE_DAY_MS)
            } else 0.0

            newDifficulty = nextDifficulty(entity.difficulty, clampedRating)

            if (clampedRating == RATING_AGAIN) {
                newStability = nextForgetStability(entity.difficulty, entity.stability, retrievability(entity.stability, elapsedDays))
                newState = 3 // Relearning
            } else {
                newStability = nextRecallStability(entity.difficulty, entity.stability, retrievability(entity.stability, elapsedDays), clampedRating)
                newState = 2 // Review
            }
        }

        val intervalDays = nextInterval(newStability)
        val intervalMs = (intervalDays * ONE_DAY_MS).toLong()
        val dueTime = now + max(60_000L, intervalMs) // At least 1 minute into the future

        return entity.copy(
            difficulty = (newDifficulty * 100.0).toLong() / 100.0,
            stability = (newStability * 100.0).toLong() / 100.0,
            reps = entity.reps + 1,
            lapses = newLapses,
            state = newState,
            lastReview = now,
            due = dueTime,
        )
    }

    private fun initDifficulty(rating: Int): Double {
        val d = w[4] - exp(w[5] * (rating - 1).toDouble()) + 1.0
        return min(10.0, max(1.0, d))
    }

    private fun initStability(rating: Int): Double {
        return max(0.1, w[rating - 1])
    }

    private fun nextDifficulty(d: Double, rating: Int): Double {
        val delta = -w[6] * (rating - 3).toDouble()
        val nextD = d + delta * ((10.0 - d) / 9.0)
        return min(10.0, max(1.0, nextD))
    }

    private fun retrievability(stability: Double, elapsedDays: Double): Double {
        if (stability <= 0.0) return 0.0
        return (1.0 + elapsedDays / (9.0 * stability)).pow(-1.0)
    }

    private fun nextRecallStability(d: Double, s: Double, r: Double, rating: Int): Double {
        val hardPenalty = if (rating == RATING_HARD) w[15] else 1.0
        val easyBonus = if (rating == RATING_EASY) w[16] else 1.0
        val sPrime = s * (1.0 + exp(w[8]) *
                (11.0 - d) *
                s.pow(-w[9]) *
                (exp((1.0 - r) * w[10]) - 1.0) *
                hardPenalty *
                easyBonus)
        return max(0.1, sPrime)
    }

    private fun nextForgetStability(d: Double, s: Double, r: Double): Double {
        val sPrime = w[11] * d.pow(-w[12]) * ((s + 1.0).pow(w[13]) - 1.0) * exp((1.0 - r) * w[14])
        return min(s, max(0.1, sPrime))
    }

    private fun nextInterval(stability: Double): Double {
        val interval = (stability / 9.0) * ((1.0 / REQUEST_RETENTION) - 1.0)
        return max(1.0, interval)
    }
}

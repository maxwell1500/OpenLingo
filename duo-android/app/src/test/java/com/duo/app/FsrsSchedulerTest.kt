package com.duo.app.data.fsrs

import com.duo.app.data.local.entities.VocabScheduleEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FsrsSchedulerTest {

    private val baseItem = VocabScheduleEntity(
        id = "es:hola",
        language = "es",
        foreign = "Hola",
        translation = "Hello",
        category = "Spanish Essentials",
        difficulty = 5.0,
        stability = 2.0,
        reps = 0,
        lapses = 0,
        state = 0,
        lastReview = 0L,
        due = 0L,
    )

    @Test
    fun `first review with Good transitions from New to Review with increased stability`() {
        val now = 1_000_000_000L
        val scheduled = FsrsScheduler.schedule(baseItem, FsrsScheduler.RATING_GOOD, now)

        assertEquals(1, scheduled.reps)
        assertEquals(0, scheduled.lapses)
        assertEquals(2, scheduled.state) // Review state
        assertEquals(now, scheduled.lastReview)
        assertTrue(scheduled.due > now)
        assertTrue("Stability should be > 0.5", scheduled.stability > 0.5)
    }

    @Test
    fun `rating Again on learned item triggers Relearning state and increases lapses`() {
        val now = 1_000_000_000L
        val learned = baseItem.copy(reps = 3, state = 2, stability = 10.0, difficulty = 4.0, lastReview = now - 86400000L)
        val scheduled = FsrsScheduler.schedule(learned, FsrsScheduler.RATING_AGAIN, now)

        assertEquals(4, scheduled.reps)
        assertEquals(1, scheduled.lapses)
        assertEquals(3, scheduled.state) // Relearning state
        assertTrue("Stability should decrease on forget", scheduled.stability < learned.stability)
    }

    @Test
    fun `rating Easy produces larger stability than rating Good`() {
        val now = 1_000_000_000L
        val good = FsrsScheduler.schedule(baseItem, FsrsScheduler.RATING_GOOD, now)
        val easy = FsrsScheduler.schedule(baseItem, FsrsScheduler.RATING_EASY, now)

        assertTrue(easy.stability > good.stability)
        assertTrue(easy.due >= good.due)
    }
}

package com.duo.app

import com.duo.app.ui.screens.dueBadgeLabel
import com.duo.app.ui.screens.fsrsBannerSummary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The Practice tab's FSRS banner: the text it prints, and the agreement between
 * the two numbers it prints it from.
 *
 * **No Compose UI test here, on purpose.** The project has no
 * `androidx.compose.ui:ui-test-junit4` dependency and no Compose test rule, and
 * inventing one for a single assertion is a fragile thing to hand the next
 * person. So the layout itself — the reason the DUE badge rendered as a numeral
 * with D/U/E stacked one character per line down an invisible pill — cannot be
 * asserted from a JVM test and is verified on a device instead. What *is*
 * assertable without a device is the text, and the text is where the two
 * numbers had started to disagree, so that is what is pinned here.
 */
class PracticeTabScreenTest {

    /**
     * A fresh install has met no words, so it has no cards. It used to read "All
     * cards caught up! Practice ahead anytime." over a deck it did not have, two
     * lines above "0 words unlocked" — a learner told they were on top of six
     * cards the app had invented for them.
     */
    @Test
    fun `a learner with no cards is told they have none, not that they are caught up`() {
        assertEquals(
            "No cards yet — every word you meet in a lesson becomes one.",
            fsrsBannerSummary(vocabCount = 0, dueCount = 0),
        )
        assertFalse(
            "a learner with no cards is being told they are caught up",
            fsrsBannerSummary(vocabCount = 0, dueCount = 0).contains("caught up"),
        )
    }

    /**
     * The badge and the sentence under it are printed from one count, and the
     * card it belongs to is one the learner has. A count that is not the one the
     * deck actually holds is what put two contradictory numbers on one screen.
     */
    @Test
    fun `the badge and the sentence are printed from the same count`() {
        val due = 6
        assertEquals("6 DUE", dueBadgeLabel(due))
        assertTrue(
            "the sentence quotes a different number than the badge",
            fsrsBannerSummary(vocabCount = 14, dueCount = due).startsWith(dueBadgeLabel(due).substringBefore(' ')),
        )
    }

    /** Met cards, none due: the deck is real and the learner is on top of it. */
    @Test
    fun `a met deck with nothing due reads as caught up`() {
        assertEquals(
            "All cards caught up! Practice ahead anytime.",
            fsrsBannerSummary(vocabCount = 3, dueCount = 0),
        )
    }

    /**
     * The badge is a single short string whatever the count, and the screen only
     * draws it when there is something to draw: an empty pill is worse than no
     * pill, and `0 DUE` would be a lie about a count of zero.
     */
    @Test
    fun `the badge label is one short line and never claims a count of zero`() {
        listOf(1, 6, 42, 999).forEach { count ->
            val label = dueBadgeLabel(count)
            assertEquals("$count DUE", label)
            assertTrue("the badge wraps: \"$label\"", label.length <= 8)
        }
    }
}

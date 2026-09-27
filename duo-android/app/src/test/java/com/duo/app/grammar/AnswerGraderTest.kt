package com.duo.app.grammar

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Answer comparison is the only part of the typed-production mechanic that can
 * silently accept a wrong inflected form or reject a right one, so the
 * tolerance rules are pinned here rather than left to keyboard accident.
 */
class AnswerGraderTest {

    @Test
    fun `accepted variants split on pipe and trim surrounding whitespace`() {
        assertEquals(listOf("soy", "estoy"), AnswerGrader.acceptedVariants(" soy | estoy "))
    }

    @Test
    fun `blank accepted-answer segments never make an empty answer correct`() {
        assertEquals(listOf("soy"), AnswerGrader.acceptedVariants("soy||"))
        assertFalse(AnswerGrader.matches("   ", AnswerGrader.acceptedVariants("soy|")))
    }

    @Test
    fun `null accepted answers fall back to the primary answer only`() {
        assertTrue(AnswerGrader.matches("hablo", emptyList<String>()).not())
        assertTrue(AnswerGrader.matches("hablo", listOf("hablo")))
    }

    @Test
    fun `matching ignores case and surrounding whitespace`() {
        assertTrue(AnswerGrader.matches("  Hablo ", listOf("hablo")))
        assertTrue(AnswerGrader.matches("hablo", listOf("HABLO")))
    }

    @Test
    fun `a missing Spanish accent is forgiven`() {
        // The learner is not being tested on orthography, and a phone keyboard
        // makes the accent easy to miss.
        assertTrue(AnswerGrader.matches("esta", listOf("está")))
        assertTrue(AnswerGrader.matches("ESTÁ", listOf("esta")))
        assertTrue(AnswerGrader.matches("estoy", listOf("estoy")))
        assertTrue(AnswerGrader.matches("senor", listOf("señor")))
    }

    @Test
    fun `a wrong tense or person is still rejected after normalisation`() {
        // Accent tolerance must not become a general "close enough" rule: these
        // are exactly the errors the mechanic exists to catch.
        assertFalse(AnswerGrader.matches("hablaba", listOf("habló")))
        assertFalse(AnswerGrader.matches("hablas", listOf("hablo")))
        assertFalse(AnswerGrader.matches("hablare", listOf("hablo")))
    }

    @Test
    fun `full-width Japanese IME output matches half-width authored text`() {
        // A Japanese IME in romaji-direct mode emits half-width katakana
        // (ﾊﾟﾝ) or full-width Latin (ｓ); the authored answer uses the
        // conventional full-width / half-width forms.
        assertTrue(AnswerGrader.matches("ﾊﾟﾝ", listOf("パン")))
        assertTrue(AnswerGrader.matches("ｓ", listOf("s")))
        assertTrue(AnswerGrader.matches("食べます", listOf("食べます")))
    }

    @Test
    fun `Japanese dakuten is a word contrast and is never stripped`() {
        // か + ゙ is が. Folding it away would make two different words compare
        // equal, which is a correctness bug, not a keyboard nicety.
        assertFalse(AnswerGrader.matches("か", listOf("が")))
        assertFalse(AnswerGrader.matches("が", listOf("か")))
    }

    @Test
    fun `internal whitespace runs collapse so a stray space cannot fail the answer`() {
        assertTrue(AnswerGrader.matches("soy  estudiante", listOf("soy estudiante")))
    }

    @Test
    fun `extra words are rejected - production is not a containment check`() {
        assertFalse(AnswerGrader.matches("yo soy estudiante", listOf("soy")))
    }
}

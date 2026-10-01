package com.openlingo.app.grammar

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BlankScaffoldTest {

    @Test
    fun `a prompt with a blank run splits around it`() {
        val parsed = BlankPlaceholder.parse("Yo ___ estudiante")
        assertEquals("Yo ", parsed?.before)
        assertEquals("___", parsed?.blank)
        assertEquals(" estudiante", parsed?.after)
    }

    @Test
    fun `longer blank runs are treated as one blank`() {
        val parsed = BlankPlaceholder.parse("食べ_____ます")
        assertEquals("食べ", parsed?.before)
        assertEquals("_____", parsed?.blank)
        assertEquals("ます", parsed?.after)
    }

    @Test
    fun `a prompt with no blank returns null so the caller renders it verbatim`() {
        assertNull(BlankPlaceholder.parse("Translate: 'Good morning'"))
        assertNull(BlankPlaceholder.parse("A _ single underscore is not a blank"))
    }
}

class GrammarFocusTest {

    /**
     * The defect, and the contract in one place: the learner sees a name a teacher
     * would use, never the slug prettified. Found on device as `RULE • SUBJUNCTIVE
     * WANTS` on a rule card and `PRETERITO IRREGULAR` on a drill tab — which is
     * `es.subjunctive.wants` and `es.preterito.irregular` with the separators
     * turned into spaces and the whole thing shouted.
     */
    @Test
    fun `a focus is shown as the name of the grammar point`() {
        assertEquals("Regular preterite", GrammarFocus.label("es.preterito.regular"))
        assertEquals("Subjunctive after a verb of wanting", GrammarFocus.label("es.subjunctive.wants"))
        assertEquals("Imperfecto: the scene it sets", GrammarFocus.label("es.imperfecto"))
        assertEquals("Polite request: 〜てください", GrammarFocus.label("ja.request_polite"))
    }

    /**
     * The newest focuses are the ones that regressed last, so they are pinned by
     * name: subjunctive, reported speech, por/para, the Japanese conditionals,
     * volition, keigo and the られる pair.
     */
    @Test
    fun `the newest grammar points are named`() {
        assertEquals("Subjunctive for emotion and doubt", GrammarFocus.label("es.subjunctive.emotion_doubt"))
        assertEquals("Subjunctive for purpose and concession", GrammarFocus.label("es.subjunctive.purpose_concession"))
        assertEquals("Subjunctive perfect: ojalá and unreal pasts", GrammarFocus.label("es.subjunctive.past_perfect"))
        assertEquals("Reported speech: moving the verb back", GrammarFocus.label("es.reported_speech.backshift"))
        assertEquals("Reported speech: a verb already in the past", GrammarFocus.label("es.reported_speech.no_backshift"))
        assertEquals("por for a reason", GrammarFocus.label("es.por_para"))
        assertEquals("〜れば: the written if", GrammarFocus.label("ja.conditional_ba"))
        assertEquals("〜なら: supposing a case", GrammarFocus.label("ja.conditional_nara"))
        assertEquals("〜たら: the everyday if", GrammarFocus.label("ja.conditional_tara"))
        assertEquals("Polite proposal: 〜ましょう", GrammarFocus.label("ja.volition_polite"))
        assertEquals("Keigo: 尊敬語, lifting the other person", GrammarFocus.label("ja.keigo_honorific"))
        assertEquals("Keigo: 謙譲語, lowering yourself", GrammarFocus.label("ja.keigo_humble"))
        assertEquals("The two られる forms side by side", GrammarFocus.label("ja.rareru_readings"))
    }

    /**
     * Whatever the name is, it must not be the slug wearing capitals: a display
     * name is a real piece of authored text, and this holds for every focus there
     * is, so a new entry cannot quietly reintroduce the old derivation.
     */
    @Test
    fun `no name is the slug uppercased with its separators replaced`() {
        val named = GrammarFocus.namedFocuses()
        assertTrue("no focus is named at all, so the check proved nothing", named.size > 50)
        named.forEach { focus ->
            val prettified = focus.split('_', '-', '.')
                .filter { it.isNotBlank() }
                .drop(1)
                .joinToString(" ") { it.uppercase() }
            assertNotEquals(
                "$focus is still shown as a prettified slug",
                prettified,
                GrammarFocus.label(focus),
            )
        }
    }

    /**
     * A focus nobody has named yields no heading, rather than the fallback the old
     * humaniser produced for anything it did not recognise.
     */
    @Test
    fun `a focus with no name of its own has no label`() {
        assertNull(GrammarFocus.label("es.subjuntivo"))
        assertNull(GrammarFocus.label(null))
        assertNull(GrammarFocus.label("   "))
    }
}

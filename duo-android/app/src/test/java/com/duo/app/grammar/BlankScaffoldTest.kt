package com.duo.app.grammar

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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

    @Test
    fun `the language prefix is dropped and the rest is humanised`() {
        assertEquals("PRETERITO REGULAR", GrammarFocus.label("es.preterito.regular"))
        assertEquals("TE FORM", GrammarFocus.label("ja.te_form"))
    }

    @Test
    fun `a focus with no language prefix still labels`() {
        assertEquals("IMPERFECTO", GrammarFocus.label("imperfecto"))
    }

    @Test
    fun `absent or blank focus yields no label`() {
        assertNull(GrammarFocus.label(null))
        assertNull(GrammarFocus.label("   "))
    }
}

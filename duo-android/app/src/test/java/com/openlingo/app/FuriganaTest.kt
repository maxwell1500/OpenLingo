package com.openlingo.app

import com.openlingo.app.ui.components.FuriganaParser
import com.openlingo.app.ui.components.RubySegment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pure-JVM unit tests for [FuriganaParser].
 *
 * These verify the ruby segmentation logic without needing an Android
 * runtime, catching parser regressions at build time.
 */
class FuriganaTest {

    @Test
    fun `parse explicit ruby markup`() {
        val result = FuriganaParser.parseRubyMarkup("二人[ふたり]")
        assertEquals(listOf(RubySegment("二人", "ふたり")), result)
    }

    @Test
    fun `parse explicit ruby markup with surrounding text`() {
        val result = FuriganaParser.parseRubyMarkup("公園[こうえん]に行きます")
        assertEquals(
            listOf(
                RubySegment("公園", "こうえん"),
                RubySegment("に行きます", null),
            ),
            result,
        )
    }

    @Test
    fun `parse mixed kanji and kana with reading`() {
        val result = FuriganaParser.parse("公園に行きます", "こうえんにいきます")
        assertEquals(
            listOf(
                RubySegment("公園", "こうえん"),
                RubySegment("に", null),
                RubySegment("行", "い"),
                RubySegment("きます", null),
            ),
            result,
        )
    }

    @Test
    fun `parse plain kana returns single segment without ruby`() {
        val result = FuriganaParser.parse("あ", null)
        assertEquals(listOf(RubySegment("あ", null)), result)
    }

    @Test
    fun `parse non-Japanese text returns single segment`() {
        val result = FuriganaParser.parse("Hello", null)
        assertEquals(listOf(RubySegment("Hello", null)), result)
    }

    @Test
    fun `parse kanji only with reading`() {
        val result = FuriganaParser.parse("漢字", "かんじ")
        assertEquals(listOf(RubySegment("漢字", "かんじ")), result)
    }

    @Test
    fun `parse empty string returns empty list`() {
        val result = FuriganaParser.parse("", null)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `parse with null reading returns single segment`() {
        val result = FuriganaParser.parse("公園", null)
        assertEquals(listOf(RubySegment("公園", null)), result)
    }

    @Test
    fun `containsJapanese detects kana`() {
        assertTrue(FuriganaParser.containsJapanese("あ"))
        assertTrue(FuriganaParser.containsJapanese("公園"))
        assertTrue(FuriganaParser.containsJapanese("Hello あ"))
    }

    @Test
    fun `containsJapanese rejects non-Japanese`() {
        assertFalse(FuriganaParser.containsJapanese("Hello"))
        assertFalse(FuriganaParser.containsJapanese("123"))
    }

    @Test
    fun `containsKanji detects kanji only`() {
        assertTrue(FuriganaParser.containsKanji("公園"))
        assertTrue(FuriganaParser.containsKanji("漢字"))
        assertFalse(FuriganaParser.containsKanji("あ"))
        assertFalse(FuriganaParser.containsKanji("Hello"))
    }

    @Test
    fun `parse delegates to markup when brackets present`() {
        val result = FuriganaParser.parse("二人[ふたり]")
        assertEquals(listOf(RubySegment("二人", "ふたり")), result)
    }
}

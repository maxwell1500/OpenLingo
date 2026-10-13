package com.openlingo.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

/**
 * A segment of text with optional ruby (furigana) annotation.
 *
 * @property base The base text (typically kanji or kana).
 * @property ruby The reading placed above the base text, or null if no ruby.
 */
data class RubySegment(
    val base: String,
    val ruby: String? = null,
)

/**
 * Parser for Japanese ruby (furigana) text.
 *
 * Supports two modes:
 * 1. Explicit ruby markup: `漢字[かんじ]` → [RubySegment("漢字", "かんじ")]
 * 2. Base text + reading: `parse("公園に行きます", "こうえんにいきます")` segments
 *    kanji compounds with their readings, leaving kana segments without ruby.
 */
object FuriganaParser {

    private val KANJI_RANGE = '\u4E00'..'\u9FFF'
    private val KANA_RANGE = '\u3040'..'\u30FF'

    /** Returns true if [c] is a kanji character. */
    fun isKanji(c: Char): Boolean = c in KANJI_RANGE

    /** Returns true if [c] is a kana character (hiragana or katakana). */
    fun isKana(c: Char): Boolean = c in KANA_RANGE

    /** Returns true if [text] contains any Japanese characters (kanji or kana). */
    fun containsJapanese(text: String): Boolean =
        text.any { it in KANJI_RANGE || it in KANA_RANGE }

    /** Returns true if [text] contains any kanji characters. */
    fun containsKanji(text: String): Boolean =
        text.any { it in KANJI_RANGE }

    /**
     * Parse explicit ruby markup like `漢字[かんじ]` or `二人[ふたり]`.
     *
     * Format: `base[ruby]` pairs. Text outside brackets has no ruby.
     * Returns a list of [RubySegment]s.
     */
    fun parseRubyMarkup(input: String): List<RubySegment> {
        val segments = mutableListOf<RubySegment>()
        val base = StringBuilder()
        val ruby = StringBuilder()
        var inRuby = false

        for (c in input) {
            when {
                c == '[' && !inRuby -> {
                    inRuby = true
                }
                c == ']' && inRuby -> {
                    segments.add(RubySegment(base.toString(), ruby.toString()))
                    base.clear()
                    ruby.clear()
                    inRuby = false
                }
                inRuby -> ruby.append(c)
                else -> base.append(c)
            }
        }

        if (base.isNotEmpty()) {
            segments.add(RubySegment(base.toString(), null))
        }

        return segments
    }

    /**
     * Parse a base Japanese string with an optional reading.
     *
     * If the base contains explicit ruby markup (`[...]`), delegates to
     * [parseRubyMarkup]. Otherwise, if [reading] is provided and the base
     * contains kanji, segments the kanji compounds with their readings as
     * ruby text, leaving kana segments without ruby.
     *
     * If no reading is provided or the base has no kanji, returns a single
     * segment with no ruby.
     */
    fun parse(base: String, reading: String? = null): List<RubySegment> {
        if (base.isEmpty()) return emptyList()

        // Check for explicit ruby markup
        if (base.contains('[') && base.contains(']')) {
            return parseRubyMarkup(base)
        }

        // If no reading or no kanji, return as single segment
        if (reading.isNullOrBlank() || !containsKanji(base)) {
            return listOf(RubySegment(base, null))
        }

        return segmentWithReading(base, reading)
    }

    /**
     * Segment a base string with a reading by aligning kana in the base
     * to the reading and assigning the intervening reading characters as
     * ruby for kanji compounds.
     */
    private fun segmentWithReading(base: String, reading: String): List<RubySegment> {
        val segments = mutableListOf<RubySegment>()
        val readingChars = reading.toList()
        var readingPos = 0

        var i = 0
        while (i < base.length) {
            val c = base[i]
            if (isKanji(c)) {
                // Find the kanji compound
                val compoundStart = i
                while (i < base.length && isKanji(base[i])) {
                    i++
                }
                val compound = base.substring(compoundStart, i)

                // Find where this compound's reading ends by looking for
                // the next kana in the base after this compound
                val remainingBase = base.substring(i)
                val nextKana = remainingBase.firstOrNull { isKana(it) }

                val rubyEnd = if (nextKana != null) {
                    val kanaIndex = reading.indexOf(nextKana, readingPos)
                    if (kanaIndex >= readingPos) kanaIndex else readingChars.size
                } else {
                    readingChars.size
                }

                val ruby = readingChars.subList(readingPos, rubyEnd).joinToString("")
                readingPos = rubyEnd

                segments.add(RubySegment(compound, ruby.takeIf { it.isNotBlank() }))
            } else {
                // Non-kanji character (kana, punctuation, etc.)
                val nonKanjiStart = i
                while (i < base.length && !isKanji(base[i])) {
                    i++
                }
                val nonKanji = base.substring(nonKanjiStart, i)
                // Advance reading past matching kana
                for (kc in nonKanji) {
                    if (readingPos < readingChars.size && readingChars[readingPos] == kc) {
                        readingPos++
                    }
                }
                segments.add(RubySegment(nonKanji, null))
            }
        }

        return segments
    }
}

/**
 * Renders Japanese text with ruby (furigana) annotations.
 *
 * Segments with ruby render as a [Column] with the reading on top and the
 * base text below. Segments without ruby render as plain text aligned with
 * the bottom baseline. When [showRuby] is false or [reading] is null,
 * renders standard text without ruby.
 *
 * @param text The base text to render.
 * @param reading The reading (romaji or kana) for the text, or null.
 * @param showRuby Whether to show ruby annotations.
 * @param modifier The [Modifier] to apply.
 * @param style The [TextStyle] for the base text.
 * @param rubyStyle The [TextStyle] for the ruby text.
 * @param baseColor The color for the base text.
 * @param rubyColor The color for the ruby text.
 * @param isSelected Whether the text is selected (affects font weight and color).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FuriganaText(
    text: String,
    reading: String?,
    showRuby: Boolean = true,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    rubyStyle: TextStyle = MaterialTheme.typography.labelSmall,
    baseColor: Color = Color(0xFF4B4B4B),
    rubyColor: Color = Color(0xFF3D7EA6),
    isSelected: Boolean = false,
) {
    val segments = remember(text, reading, showRuby) {
        if (!showRuby || reading.isNullOrBlank()) {
            listOf(RubySegment(text, null))
        } else {
            FuriganaParser.parse(text, reading)
        }
    }

    val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
    val effectiveBaseColor = if (isSelected) Color(0xFF1899D6) else baseColor

    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
    ) {
        segments.forEach { segment ->
            if (segment.ruby != null && showRuby) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = segment.ruby,
                        style = rubyStyle.copy(
                            fontSize = 11.sp,
                            color = rubyColor,
                        ),
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = segment.base,
                        style = style.copy(
                            fontWeight = fontWeight,
                            color = effectiveBaseColor,
                        ),
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                Text(
                    text = segment.base,
                    style = style.copy(
                        fontWeight = fontWeight,
                        color = effectiveBaseColor,
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Bottom),
                )
            }
        }
    }
}

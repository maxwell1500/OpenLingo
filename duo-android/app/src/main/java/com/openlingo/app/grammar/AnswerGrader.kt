package com.openlingo.app.grammar

import java.text.Normalizer
import java.util.Locale

/**
 * Answer comparison for typed production challenges (`FILL_BLANK`).
 *
 * The learner types a target-language string on a phone keyboard, so the raw
 * text is not byte-identical to the authored answer even when the learner knows
 * it. Two normalisations make the comparison fair; both are applied to the typed
 * text *and* to the accepted answers, so the rule cannot favour either side:
 *
 *  - **Accents are forgiven (Spanish, and harmless elsewhere).** An answer is
 *    NFD-normalised and Latin combining marks are dropped before comparison, so
 *    a learner who types `esta` for `está` — no accent key on their keyboard,
 *    or they are mid-lesson and not being tested on orthography — is not
 *    punished. Accents are still *taught*: the correct form with its accent is
 *    what the incorrect-answer feedback shows.
 *  - **Full-width input is folded (Japanese IME, and harmless elsewhere).** A
 *    Japanese IME commonly emits `ｓ`/`ー` full-width forms for the ASCII the
 *    authored answer used. NFKC folding maps them back, so `ｲｲ` matches `いい`.
 *
 * Deliberately *not* normalised: word order, extra words, or a different
 * inflected form. Those are the errors this mechanic exists to catch.
 */
object AnswerGrader {

    /**
     * Parses the pipe-delimited `acceptedAnswers` column into a trimmed,
     * non-empty list. Blank segments are dropped so a trailing `"|"` in authored
     * content cannot make a blank answer acceptable.
     */
    fun acceptedVariants(acceptedAnswers: String?): List<String> =
        acceptedAnswers
            ?.split('|')
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()

    /**
     * Normalises one string for comparison: whitespace collapse, NFKC folding
     * (full-width → half-width), NFD + Latin-combining-mark stripping (accents),
     * lower-casing. Locale-independent on purpose — a Turkish keyboard must not
     * change how `I` compares.
     */
    fun normalize(input: String): String {
        val collapsed = input.trim().replace(WHITESPACE_RUN, " ")
        val folded = Normalizer.normalize(collapsed, Normalizer.Form.NFKC)
        val decomposed = Normalizer.normalize(folded, Normalizer.Form.NFD)
        val withoutLatinMarks = StringBuilder(decomposed.length)
        for ((index, ch) in decomposed.withIndex()) {
            if (Character.getType(ch) == Character.NON_SPACING_MARK.toInt()) {
                // Keep the mark when it belongs to a non-Latin letter: dropping
                // the dakuten in か + ゙ would make が and か compare equal, which
                // is a real Japanese word contrast, not a keyboard artefact.
                val base = decomposed.take(index).lastOrNull { !it.isWhitespace() }
                if (base != null && Character.UnicodeScript.of(base.code) == Character.UnicodeScript.LATIN) {
                    continue
                }
            }
            withoutLatinMarks.append(ch)
        }
        return withoutLatinMarks.toString().lowercase(Locale.ROOT)
    }

    /** True when [typed] normalises to the same string as any accepted answer. */
    fun matches(typed: String, acceptedAnswers: List<String>): Boolean {
        val candidate = normalize(typed)
        if (candidate.isEmpty()) return false
        return acceptedAnswers.any { normalize(it) == candidate }
    }

    /** Convenience overload for the raw `acceptedAnswers` column value. */
    fun matches(typed: String, acceptedAnswers: String?): Boolean =
        matches(typed, acceptedVariants(acceptedAnswers))

    private val WHITESPACE_RUN = Regex("\\s+")
}

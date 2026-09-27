package com.duo.app.grammar

/**
 * Presentation helper for `ChallengeEntity.grammaticalFocus`.
 *
 * The column is a machine-comparable join key (`es.preterito.regular`,
 * `ja.te_form`) and the spec forbids displaying it raw, so the leading language
 * segment is dropped and the remainder is humanised: `es.preterito.regular`
 * shows as "PRETERITO REGULAR", `ja.te_form` as "TE FORM".
 */
object GrammarFocus {

    fun label(focus: String?): String? {
        if (focus.isNullOrBlank()) return null
        val segments = focus.split('_', '-', '.').filter { it.isNotBlank() }
        // `es.preterito.regular` -> "PRETERITO REGULAR": the leading language
        // code is a data-layer join key, not something the learner is taught.
        val body = if (segments.size > 1) segments.drop(1) else segments
        return body
            .joinToString(" ") { it.uppercase() }
            .takeIf { it.isNotBlank() }
    }
}

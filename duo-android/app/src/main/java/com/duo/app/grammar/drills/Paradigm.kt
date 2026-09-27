package com.duo.app.grammar.drills

import com.duo.app.grammar.AnswerGrader
import com.duo.app.grammar.BlankPlaceholder
import com.duo.app.grammar.GrammarFocus

/**
 * One conjugated form, lifted out of the curriculum and made quotable on its own.
 *
 * The corpus already ships, for 78 of its challenges, everything a conjugation table
 * needs — a [grammaticalFocus] that says which paradigm the item belongs to, a
 * `ruleText` that explains it, and a set of `correct`/`incorrect` options where the
 * incorrect ones carry a machine-readable [errorTag]. A drill is therefore not new
 * content: it is the same material, regrouped. See [StructureDrill] for the grouping.
 *
 * [accepted] is deliberately in [AnswerGrader] form rather than a raw column string, so
 * the drill screen cannot grade with a different comparison than the lesson does. A
 * Spanish `es.imperfecto` drill for `vivía` accepts `vivia` as well, because the
 * authored challenge accepts both, and a Japanese answer keeps its dakuten.
 */
data class ParadigmEntry(
    /** The curriculum row this form was read from; the drill never creates one. */
    val challengeId: Int,
    /** The lesson this form was taught in, so a caller can group rows by unit. */
    val lessonId: Int,
    /** The machine-comparable paradigm slug, e.g. `es.preterito.regular`. */
    val focus: String,
    /** The target-language form itself, e.g. `hablamos`, `待って`. */
    val form: String,
    /** Every string [AnswerGrader] should accept, authored or derived. */
    val accepted: List<String>,
    /** The corpus prompt verbatim, blank run intact. */
    val prompt: String,
    /** The person or frame the form fills, when the prompt names one. */
    val slot: String?,
    /** The infinitive or dictionary form the prompt names, when it names one. */
    val lemma: String?,
    val ruleText: String?,
    val romaji: String?,
    /** Audio authored *for this form*. Null means "speak it", never a missing file. */
    val audioSrc: String?,
    /** Wrong forms of the same lemma, each already carrying its curriculum errorTag. */
    val distractors: List<ParadigmDistractor>,
) {
    /**
     * The shortest honest label for a table row: the person or frame when the prompt
     * names one, otherwise the blanked sentence this form completes.
     */
    val cue: String
        get() = slot ?: blankedPrompt ?: prompt

    /** The prompt with the blank spelled out, or null when it has no blank. */
    val blankedPrompt: String?
        get() = BlankPlaceholder.parse(prompt)?.let { scaffold ->
            listOf(scaffold.before.trim(), scaffold.blank, scaffold.after.trim())
                .filter { it.isNotEmpty() }
                .joinToString(" ")
        }

    /** True when [typed] is the form, by the same comparison the lesson uses. */
    fun accepts(typed: String): Boolean = AnswerGrader.matches(typed, accepted)
}

/**
 * A wrong form of the same lemma.
 *
 * [errorTag] is never invented: it is copied from the curriculum row the distractor
 * came from, so the drill can only ever explain a mistake in the terms the rest of the
 * app already explains it in. See [ErrorTag].
 */
data class ParadigmDistractor(
    val form: String,
    val errorTag: String,
)

/**
 * Every form the corpus teaches for one [grammaticalFocus], in curriculum order.
 *
 * A paradigm is a *set of rows*, not a new lesson: nothing here is written back, and
 * nothing here is a challenge. A focus with no extractable material yields a paradigm
 * with no entries rather than a crash, which is what lets new content be authored in
 * the ordinary way and simply not be drillable until it names a form.
 */
data class Paradigm(
    val focus: String,
    val entries: List<ParadigmEntry>,
) {
    /** Human label for the focus header, e.g. `es.preterito.regular` -> "PRETERITO REGULAR". */
    val label: String? get() = GrammarFocus.label(focus)

    /** The distinct lemmas the prompt named, in first-seen order; empty when none did. */
    val lemmas: List<String>
        get() = entries.mapNotNull { it.lemma }.distinct()

    /** Rows for [lemma]; the rows whose prompt named no lemma when it is null. */
    fun entriesFor(lemma: String?): List<ParadigmEntry> =
        entries.filter { it.lemma == lemma }

    /** True when there is nothing to show; callers render an empty state, not a blank table. */
    val isEmpty: Boolean get() = entries.isEmpty()
}

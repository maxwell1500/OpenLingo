package com.openlingo.app.grammar

/**
 * A challenge prompt split around the blank the learner has to fill.
 *
 * `ASSIST` and `FILL_BLANK` both carry their scaffold inside `question` as a run
 * of three or more underscores (`___`), so no extra schema column is needed:
 * "I ___ hungry" parses to [before] = "I ", [after] = " hungry".
 *
 * Content without a blank parses to `null`, and the caller renders the plain
 * question — the pre-scaffold behaviour.
 */
data class BlankScaffold(
    val before: String,
    val blank: String,
    val after: String,
) {
    /** The prompt with the blank replaced, for use as a one-line label. */
    fun isBlanked(): Boolean = before.isNotEmpty() || after.isNotEmpty()
}

object BlankPlaceholder {

    private val RUN = Regex("_{3,}")

    /** Splits [question] around its first `___` run, or returns `null` if absent. */
    fun parse(question: String): BlankScaffold? {
        val match = RUN.find(question) ?: return null
        return BlankScaffold(
            before = question.substring(0, match.range.first),
            blank = match.value,
            after = question.substring(match.range.last + 1),
        )
    }
}

package com.duo.app.data.local.models

/**
 * The interaction vocabulary of a challenge, persisted in `challenges.type` as TEXT.
 *
 * The [rawValue] of every constant is the exact string that was used before this type
 * existed, so the persisted wire format is unchanged. [fromRaw] is the only way back,
 * and it throws on anything it does not recognise: a challenge row whose type is not a
 * known constant is a corrupt curriculum, and silently degrading it to a default would
 * hide that behind a blank exercise screen.
 */
enum class ChallengeType(val rawValue: String) {
    SELECT("SELECT"),
    ASSIST("ASSIST"),
    WORD_BANK("WORD_BANK"),
    LISTEN("LISTEN"),
    MATCH_PAIRS("MATCH_PAIRS"),
    STORY("STORY"),
    CONJUGATE("CONJUGATE"),
    FILL_BLANK("FILL_BLANK");

    /** True when the learner picks one of several offered options. */
    fun isChoice(): Boolean = when (this) {
        SELECT, ASSIST, MATCH_PAIRS, LISTEN, STORY, CONJUGATE -> true
        WORD_BANK, FILL_BLANK -> false
    }

    /** True when the learner must build or type a target-language string themselves. */
    fun isProduction(): Boolean = when (this) {
        WORD_BANK, FILL_BLANK -> true
        SELECT, ASSIST, MATCH_PAIRS, LISTEN, STORY, CONJUGATE -> false
    }

    companion object {
        fun fromRaw(raw: String): ChallengeType =
            entries.firstOrNull { it.rawValue == raw }
                ?: throw IllegalArgumentException(
                    "Unknown challenge type '$raw'; expected one of " +
                        entries.joinToString { it.rawValue },
                )
    }
}

package com.openlingo.app.grammar.drills

/**
 * The one thing about `challenge_options.errorTag` the drill needs on its own.
 *
 * The vocabulary itself and the sentence each tag earns live in
 * [com.openlingo.app.grammar.ErrorHint], which the drill reuses so a wrong answer in a drill
 * and a wrong answer in a lesson are described the same way. This object holds only the
 * distinction that is specific to a *paradigm*: which tags name a different form of the
 * same word.
 *
 * That distinction is what lets the generator tell a conjugation from its
 * neighbourhood. `WRONG_CLASSIFIER` (a different counter) and `UNRELATED` (a different
 * word) name something that is not a form of the answer, so a challenge whose only
 * distractors carry them is a vocabulary item wearing a grammar focus, and it does not
 * become a row of a conjugation table.
 */
object ErrorTag {

    /**
     * Tags that mean "the same word, the wrong form": the learner picked a different
     * member of the paradigm rather than a different word.
     */
    val SAME_LEMMA: Set<String> = setOf(
        "WRONG_TENSE",
        "WRONG_PERSON",
        "WRONG_FORM",
        "WRONG_COPULA",
        "WRONG_REGISTER",
    )
}

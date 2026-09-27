package com.duo.app.grammar

/**
 * Turns the two things the data already knows about a wrong answer — which
 * option the learner picked ([com.duo.app.data.local.entities.ChallengeOptionEntity.errorTag])
 * and which grammar point the challenge drills
 * ([com.duo.app.data.local.entities.ChallengeEntity.grammaticalFocus]) — into one
 * plain-English sentence naming what was wrong.
 *
 * The app owned this signal before it used it: a learner who picked the plain
 * form against the Japanese potential was told "try again", while the row
 * behind the tile said `WRONG_FORM`. Feedback is derived here rather than
 * authored per challenge, so a new lesson gets teaching feedback for free and
 * two challenges on the same grammar point cannot drift apart.
 *
 * A hint is a sentence list, joined in a fixed order:
 *
 *  1. the **diagnosis**, from the errorTag — what kind of mistake this is;
 *  2. the **rule**, from the focus — what the grammar point actually requires,
 *     omitted for `UNRELATED` because a different word is not a grammar slip;
 *  3. the **answer** — the correct form, always named when there is one to name.
 *
 * Every value in [TAGS] is mapped. A tag outside [TAGS] returns `null` from
 * [forChoice] rather than a generic or empty string, so a tag shipped without
 * guidance here shows up as a failed `ErrorHintTest`, not as blank feedback in
 * front of a learner.
 */
object ErrorHint {

    /** Tag for a distractor that is a different word, not a wrong form. */
    private const val UNRELATED = "UNRELATED"

    /**
     * The errorTag vocabulary this object can explain, mirroring the KDoc on
     * `ChallengeOptionEntity.errorTag`.
     *
     * Declared here as well as in `CurriculumIntegrityTest` so that adding a tag
     * to the vocabulary is a two-place change with a test on both sides: a tag
     * with no sentence behind it fails `ErrorHintTest` instead of degrading
     * into empty feedback.
     */
    val TAGS: Set<String> = setOf(
        "WRONG_TENSE",
        "WRONG_PERSON",
        "WRONG_FORM",
        "WRONG_COPULA",
        "WRONG_CLASSIFIER",
        "WRONG_REGISTER",
        UNRELATED,
    )

    /**
     * How a grammar point introduces itself in feedback: [label] is a phrase
     * that reads after "For ...", and [rule] is the one sentence of orientation
     * the diagnosis hangs off. A null [label] means the focus is unknown — older
     * challenges predate the column — and the sentence stays bare rather than
     * inventing a grammar point that is not there.
     */
    private data class FocusProfile(val label: String?, val rule: String = "")

    private val GENERIC = FocusProfile(label = null)

    private val focusProfiles: Map<String, FocusProfile> = mapOf(
        // Spanish verbs: the ending carries tense and person together.
        "es.preterito.regular" to FocusProfile(
            "the Spanish preterite",
            "A preterite verb ends in a past-tense ending that also changes with the subject's person.",
        ),
        "es.imperfecto" to FocusProfile(
            "the Spanish imperfect",
            "The imperfect describes ongoing, repeated or background actions, and its ending changes with the subject's person.",
        ),
        "es.present_person" to FocusProfile(
            "the Spanish present tense",
            "Every person has its own present ending, so the verb has to match the subject.",
        ),
        "es.tener_present" to FocusProfile(
            "the present of tener",
            "tener inflects by person exactly like any other Spanish verb.",
        ),
        // Irregular pasts: the stem the verb carries, not the person ending.
        "es.preterito.irregular" to FocusProfile(
            "the irregular Spanish preterite",
            "An irregular preterite replaces the infinitive's stem outright, so the person ending sits on tuv-, pud-, hic-, dij-, quis-, vin-, pus- or estuv-.",
        ),
        "es.preterito.stem_changing" to FocusProfile(
            "the stem-changing Spanish preterite",
            "A stem-changing verb dips in the present and the imperfect, but its preterite either keeps the plain stem (dormí, pedí) or takes a strong one (siguió).",
        ),
        "es.past_perfect" to FocusProfile(
            "the Spanish past perfect",
            "The past perfect is haber in the imperfect plus a participle: había comido, había hecho, había dicho.",
        ),
        // The conditional: whole infinitive for the regular verbs, a rewritten stem
        // for the irregular ones, and the polite periphrasis built on top of it.
        "es.conditional.regular" to FocusProfile(
            "the Spanish conditional",
            "The regular conditional keeps the whole infinitive and adds -ría: hablar → hablaría, comer → comería.",
        ),
        "es.conditional.irregular" to FocusProfile(
            "the irregular Spanish conditional",
            "An irregular conditional is a rewritten stem plus -ría: tener → tendría, poder → podría, decir → diría.",
        ),
        "es.conditional.periphrasis" to FocusProfile(
            "a polite Spanish request",
            "me gustaría, querría and podría plus an infinitive soften what is being asked for; the bare present wants the same thing but says so bluntly.",
        ),
        // Connectives: the choice is which relation the clause carries.
        "es.connectives" to FocusProfile(
            "the Spanish connectives",
            "para states a purpose, porque a reason, así que or entonces a result, and aunque a concession — a connective that states a different relation cannot fill the slot.",
        ),
        // ser vs estar: a usage choice, plus agreement once the choice is made.
        "es.ser_estar" to FocusProfile(
            "ser vs estar",
            "ser states identity and lasting qualities; estar covers places, states and temporary conditions.",
        ),
        "es.ser_present" to FocusProfile(
            "ser in the present tense",
            "ser states identity and lasting description, and it agrees with the subject like any other verb.",
        ),
        // Japanese verbs change by form, not by tense.
        "ja.potential" to FocusProfile(
            "the Japanese potential form",
            "The potential form (られる) is what says \"can\" or \"is able to\".",
        ),
        "ja.potential_nominal" to FocusProfile(
            "the Japanese potential noun",
            "The noun form of the potential (られるもの) names the ability itself, not the ability to do.",
        ),
        "ja.te_form" to FocusProfile(
            "the Japanese て-form",
            "The て-form is the connective shape: it links actions and carries ください in a request.",
        ),
        "ja.request_polite" to FocusProfile(
            "a polite Japanese request",
            "A polite request puts ください after the て-form; a bare verb is the plain, abrupt version.",
        ),
        // Register points: the grammar is right, the politeness is not.
        "ja.polite_verb" to FocusProfile(
            "Japanese polite speech",
            "ます and ました are polite; だ and である are plain, and the plain form is the wrong level here.",
        ),
        "ja.polite_register" to FocusProfile(
            "the polite Japanese register",
            "This point is about politeness level, not about which word you reach for.",
        ),
        "ja.copula_polite" to FocusProfile(
            "polite です / でした",
            "です and でした are the polite copula; だ is the plain one.",
        ),
        // The past, plain against polite, and the negative: the first three
        // points the N4 units 11-12 add, and the ones every earlier verb was
        // missing because a form with no past has no tense to conjugate.
        "ja.past_polite" to FocusProfile(
            "the Japanese polite past",
            "The polite past replaces the final す of the ます form with ました, so 飲みます becomes 飲みました.",
        ),
        "ja.plain_vs_polite" to FocusProfile(
            "the Japanese plain form",
            "The plain form (断定形) leaves off ます and です: 食べた is plain, 食べました is polite.",
        ),
        "ja.negative" to FocusProfile(
            "the Japanese ない-form negative",
            "A negative is ません in polite speech and ない in the plain form: 飲まなかった is the plain negative past.",
        ),
        // Adjectives are a class distinction, not a tense one: a な-adjective
        // has no stem change to conjugate at all, which is why きれいかった
        // can only ever be a mistake.
        "ja.i_adjective" to FocusProfile(
            "an い-adjective",
            "An い-adjective conjugates its own stem: く + ない for negative and かった for past, so 寒かった.",
        ),
        "ja.na_adjective" to FocusProfile(
            "a な-adjective",
            "A な-adjective never changes its stem: きれい takes でした and だった, never きれいかった.",
        ),
        "ja.ability_polite" to FocusProfile(
            "the Japanese ability frame",
            "Ability is the potential (乗れます) or the plain verb + ことができます (運転することができます): こと is the する→す nominaliser, so the verb keeps its plain form.",
        ),
        // 意見 and giving/receiving: the two everyday frames that carry unit 12.
        "ja.think" to FocusProfile(
            "the Japanese と思います opinion",
            "The word before と思います keeps its plain form, so 高いと思います and not 高く or 高かった.",
        ),
        "ja.giving_receiving" to FocusProfile(
            "the Japanese giving and receiving verbs",
            "あげる is I give to someone, くれる is someone gives to me, and もらう is I receive; から names the source.",
        ),
        // Counters: the numeral is right in every distractor.
        "ja.counter_people" to FocusProfile(
            "a Japanese counter for people",
            "People are counted with 人数, which already means \"people\" — a bare numeral is not enough.",
        ),
        "ja.counter_time" to FocusProfile(
            "a Japanese counter for time",
            "Spans of time take their own counter, such as 日 for days or 時間 for hours.",
        ),
        "ja.counter_classifier" to FocusProfile(
            "a Japanese counter",
            "Counters follow the shape of the thing counted — 枚 for flat things, 本 for long things, 匹 for animals.",
        ),
    )

    private fun profile(focus: String?): FocusProfile =
        focus?.let { focusProfiles[it] } ?: GENERIC

    private fun quote(text: String): String = "“${text.trim()}”"

    /**
     * The closing sentence, or null when there is no correct answer to name.
     * A correct option carrying no text is a data defect, and the honest
     * response to it is to leave the sentence out — not to tell the learner
     * that the answer is "the blank".
     */
    private fun needs(profile: FocusProfile, correct: String?): String? {
        val text = correct?.takeIf { it.isNotBlank() } ?: return null
        return if (profile.label != null) {
            "For ${profile.label}, this sentence needs ${quote(text)}."
        } else {
            "This sentence needs ${quote(text)}."
        }
    }

    /**
     * The diagnosis: what kind of mistake the picked option is, said in the
     * learner's words. [UNRELATED] is the one tag that does not get the grammar
     * treatment, because a different word is a vocabulary error and dressing it
     * up as a tense lesson would be a lie.
     */
    private fun diagnosis(tag: String, chosen: String): String? = when (tag) {
        "WRONG_TENSE" -> "${quote(chosen)} puts the verb in the wrong tense."
        "WRONG_PERSON" -> "${quote(chosen)} is the right tense but the wrong person or number."
        "WRONG_FORM" -> "${quote(chosen)} is a different form of the verb."
        "WRONG_COPULA" -> "${quote(chosen)} is the wrong choice of “to be” for this sentence."
        "WRONG_CLASSIFIER" -> "${quote(chosen)} pairs the right number with the wrong counter."
        "WRONG_REGISTER" -> "${quote(chosen)} is grammatical, but the wrong level of politeness."
        UNRELATED -> "${quote(chosen)} is a different word altogether — this is not a grammar slip."
        else -> null
    }

    /**
     * Feedback for a challenge answered by picking an option.
     *
     * @param chosenText the option the learner picked.
     * @param correctText the text of the correct option.
     * @param errorTag the [TAGS] value carried by [chosenText]'s row.
     * @param focus the challenge's `grammaticalFocus` slug.
     * @return the explanation, or `null` when there is nothing to derive it
     *   from — an untagged option, or a tag outside [TAGS], which is a data
     *   defect `ErrorHintTest` fails on. The caller falls back to the
     *   challenge's `ruleText`.
     */
    fun forChoice(
        chosenText: String?,
        correctText: String?,
        errorTag: String?,
        focus: String?,
    ): String? {
        val tag = errorTag?.takeIf { it.isNotBlank() } ?: return null
        val lead = diagnosis(tag, chosenText?.takeIf { it.isNotBlank() } ?: "that option")
            ?: return null
        val sentences = mutableListOf(lead)
        if (tag == UNRELATED) {
            // A different word is a vocabulary error: the learner is told what
            // is wrong and what is needed, and no grammar lesson is implied.
            correctText?.takeIf { it.isNotBlank() }
                ?.let { sentences += "The word this sentence needs is ${quote(it)}." }
        } else {
            val profile = profile(focus)
            if (profile.rule.isNotBlank()) sentences += profile.rule
            needs(profile, correctText)?.let { sentences += it }
        }
        return sentences.joinToString(" ")
    }

    /**
     * Feedback for a typed answer (FILL_BLANK / ASSIST), where there is no
     * picked option and therefore no [TAGS] value to read.
     *
     * The diagnosis has to come from somewhere else: what the learner typed is
     * named first, then the rule the focus requires, then the form that fills
     * the slot. This is the path that keeps a typed wrong form from falling
     * back to a bare "try again".
     */
    fun forTypedAnswer(
        typedAnswer: String?,
        correctAnswer: String?,
        focus: String?,
    ): String? {
        val correct = correctAnswer?.takeIf { it.isNotBlank() } ?: return null
        val profile = profile(focus)
        val sentences = mutableListOf(
            typedAnswer?.takeIf { it.isNotBlank() }
                ?.let { "You typed ${quote(it)}, but this blank needs ${quote(correct)}." }
                ?: needs(profile, correct)!!,
        )
        if (profile.rule.isNotBlank()) {
            sentences += profile.rule
            if (profile.label != null) sentences += "That is ${profile.label}."
        }
        return sentences.joinToString(" ")
    }
}

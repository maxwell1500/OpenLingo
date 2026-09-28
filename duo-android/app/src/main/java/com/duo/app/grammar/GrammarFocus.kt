package com.duo.app.grammar

/**
 * Presentation helper for `ChallengeEntity.grammaticalFocus`.
 *
 * The column is a machine-comparable join key (`es.preterito.regular`,
 * `ja.te_form`) and the spec forbids displaying it raw, so the learner gets a
 * name instead: `es.subjunctive.wants` reads **"Subjunctive after wants"**,
 * `es.preterito.irregular` reads **"Irregular preterite"**.
 *
 * **The names are authored, not derived.** Uppercasing the slug and replacing
 * its separators printed `SUBJUNCTIVE WANTS` and `PRETERITO IRREGULAR` — a
 * learner's-eye view of the database's join key, with the underscores of
 * `ja.rareru_readings` showing up as spaces. Deriving a name from the slug cannot
 * do better than that, because the slug says nothing a teacher would not say
 * differently. Deriving one from the challenge's own `ruleText` does not work
 * either: that text is a paragraph (`The negative tú imperative is no + the
 * subjunctive, so no corras con prisa.`), and no rule for turning a paragraph
 * into a heading can be written without inventing grammar the corpus does not
 * teach. So every focus has a real display name, written from the rule the
 * corpus already teaches for it, and [DISPLAY_NAMES] is the whole mechanism.
 *
 * A focus with no name returns null rather than a prettified slug. A learner who
 * has never been taught a rule is better served by a card that shows the rule
 * text and no heading than by one that shows `TE_FORM_2`; `GrammarFocusTest`
 * fails when the corpus grows a focus that has not been named, so the fallback is
 * never what ships.
 */
object GrammarFocus {

    /**
     * What the learner is shown for each focus, keyed by the slug.
     *
     * Every entry is the name a teacher would use for the rule its challenges
     * teach, and no entry states anything the corpus's own `ruleText` does not.
     * Kept in one map so a new grammar point is named in the same place it is
     * authored, and so the coverage test has something to check.
     */
    private val DISPLAY_NAMES = mapOf(
        // --- Spanish: ser and estar -------------------------------------
        "es.ser_estar" to "ser and estar",
        "es.ser_present" to "Present of ser",
        "es.tener_present" to "Present of tener",
        "es.gustar" to "Gustar: agreement with what is liked",

        // --- Spanish: the present ----------------------------------------
        "es.present_person" to "Present person endings",

        // --- Spanish: the preterite and imperfecto ----------------------
        "es.preterito.regular" to "Regular preterite",
        "es.preterito.irregular" to "Irregular preterite",
        "es.preterito.stem_changing" to "Stem-changing verbs in the preterite",
        "es.imperfecto" to "Imperfecto: the scene it sets",
        "es.past_perfect" to "Past perfect",

        // --- Spanish: reflexives, pronouns, orders ----------------------
        "es.reflexive.pronoun" to "Reflexive pronouns",
        "es.reflexive.impersonal_se" to "Impersonal se",
        "es.object_pronoun.direct" to "Direct object pronouns",
        "es.object_pronoun.indirect" to "Indirect object pronouns",
        "es.imperative.affirmative" to "Affirmative tú imperative",
        "es.imperative.negative" to "Negative tú imperative",
        "es.imperative.irregular" to "Irregular affirmative tú imperatives",

        // --- Spanish: the subjunctive ------------------------------------
        "es.subjunctive.wants" to "Subjunctive after a verb of wanting",
        "es.subjunctive.emotion_doubt" to "Subjunctive for emotion and doubt",
        "es.subjunctive.purpose_concession" to "Subjunctive for purpose and concession",
        "es.subjunctive.past_perfect" to "Subjunctive perfect: ojalá and unreal pasts",

        // --- Spanish: conditionals, por/para, reported speech ------------
        "es.conditional.regular" to "Regular conditional",
        "es.conditional.irregular" to "Irregular conditional",
        "es.conditional.periphrasis" to "Polite request: me gustaría",
        "es.conditional.unreal_past" to "A reason that really happened: porque + past perfect",
        "es.unreal_past" to "An if-clause that is not a fact",
        "es.por_para" to "por for a reason",
        "es.connectives" to "Connecting with porque",
        "es.reported_speech.no_backshift" to "Reported speech: a verb already in the past",
        "es.reported_speech.backshift" to "Reported speech: moving the verb back",
        // --- Spanish: themed vocabulary units ---------------------------
        "es.vocab.everyday_life" to "Everyday vocabulary",
        "es.vocab.travel" to "Travel vocabulary",

        // --- Japanese: the polite frame ----------------------------------
        "ja.polite_verb" to "Polite verb: the ます form",
        "ja.past_polite" to "Polite past: 〜ました",
        "ja.te_form" to "Polite progressive: 〜ています",
        "ja.plain_vs_polite" to "Plain form: 断定形",
        "ja.negative" to "Polite negative: 〜ません",
        "ja.polite_register" to "Polite あります: ございます",
        "ja.copula_polite" to "です and でした",
        "ja.request_polite" to "Polite request: 〜てください",
        "ja.volition_polite" to "Polite proposal: 〜ましょう",

        // --- Japanese: adjectives, counters, nouns -----------------------
        "ja.i_adjective" to "い-adjective past: 〜かった",
        "ja.na_adjective" to "な-adjective forms",
        "ja.counter_people" to "Counting people: 人",
        "ja.counter_classifier" to "Counters for things: 冊, 本, 個",
        "ja.counter_time" to "Counting time: 年, 月, 日",

        // --- Japanese: giving and receiving ------------------------------
        "ja.giving_receiving" to "くれる, あげる, もらう",
        "ja.think" to "A plain verb before と思います",
        "ja.relative_clause" to "Relative clauses with の",

        // --- Japanese: ability, potential, passive, causative ------------
        "ja.ability_polite" to "Saying you can: the potential",
        "ja.potential" to "Potential: 〜eru",
        "ja.potential_nominal" to "An ability in words: ことができます",
        "ja.passive_formation" to "Plain passive: 〜られる",
        "ja.passive_particles" to "Who did it to whom: に in the passive",
        "ja.passive_teiru" to "Passive in progress: 〜られています",
        "ja.causative_formation" to "Causative: 〜させる",
        "ja.causative_teiru" to "Causative in progress: 〜させています",
        "ja.rareru_readings" to "The two られる forms side by side",

        // --- Japanese: keigo ---------------------------------------------
        "ja.keigo_honorific" to "Keigo: 尊敬語, lifting the other person",
        "ja.keigo_humble" to "Keigo: 謙譲語, lowering yourself",
        // --- Japanese: modality -----------------------------------------
        "ja.necessity" to "Saying you must: 〜なければなりません",
        "ja.permission" to "Asking and giving permission: 〜てもいい",
        "ja.experience" to "Talking about experience: 〜たことがあります",

        // --- Japanese: conditions ----------------------------------------
        "ja.conditional_ba" to "〜れば: the written if",
        "ja.conditional_nara" to "〜なら: supposing a case",
        "ja.conditional_tara" to "〜たら: the everyday if",
    )

    /**
     * The learner's name for [focus], or null when the corpus has never named it.
     *
     * Null is a real answer: [com.duo.app.ui.components.RuleCard] drops the
     * heading and still shows the rule text, which is the honest rendering of a
     * grammar point this build has no name for. A slug is not an alternative —
     * it is the internal identifier, spelled with underscores.
     */
    fun label(focus: String?): String? {
        if (focus.isNullOrBlank()) return null
        return DISPLAY_NAMES[focus.trim()]
    }

    /** Every focus that has a learner-visible name, for the coverage test. */
    fun namedFocuses(): Set<String> = DISPLAY_NAMES.keys
}

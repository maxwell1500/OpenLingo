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
        // A past that did not happen: the subjunctive perfect, and the two
        // conditions and the one factual counterpart that turn on it.
        "es.subjunctive.past_perfect" to FocusProfile(
            "the Spanish subjunctive past perfect",
            "The pluscuamperfecto de subjuntivo is hubiera or hubiese plus a participle, and it states a past that did not happen: hubiera comido, como si lo hubiera sabido.",
        ),
        "es.unreal_past" to FocusProfile(
            "the Spanish unreal past condition",
            "A condition that did not happen takes hubiera plus a participle, and the open condition answers with the conditional: si hubiera sabido, habría venido.",
        ),
        "es.conditional.unreal_past" to FocusProfile(
            "the Spanish unreal past against a real past",
            "The subjunctive perfect is for what did not happen and the indicative past perfect for what did, so the form you need depends on which one the sentence means.",
        ),
        // Reported speech: one step back, and the two frames that do not take it.
        "es.reported_speech.backshift" to FocusProfile(
            "Spanish reported speech",
            "A verb inside dijo que moves one step back — present to imperfect, future to conditional — while Spanish also allows the unshifted form.",
        ),
        "es.reported_speech.no_backshift" to FocusProfile(
            "a Spanish report that keeps its verb",
            "A verb that was already in the past keeps its past form inside dijo que, and a reported question takes si rather than que.",
        ),
        // por and para: the same two prepositions the connectives introduce,
        // asked directly now that a learner is choosing between them.
        "es.por_para" to FocusProfile(
            "Spanish por and para",
            "por marks a cause, an exchange and a result; para marks a purpose, a direction and a recipient.",
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
        // The subjunctive: a second verb in a clause that is not the main one.
        "es.subjunctive.wants" to FocusProfile(
            "the Spanish subjunctive after querer",
            "After querer, esperar, necesitar or buscar plus que the second verb is subjunctive: quiero que vengas, not quiero que viene.",
        ),
        "es.subjunctive.emotion_doubt" to FocusProfile(
            "emotion, doubt and negation in Spanish",
            "Emotion, doubt and negation all take the subjunctive: no creo que sea, me alegro de que estén listos.",
        ),
        "es.subjunctive.purpose_concession" to FocusProfile(
            "Spanish purpose and concession",
            "Para que and a menos que point at a wanted or an unwanted outcome, so the verb they take is subjunctive: te lo digo para que lo sepas.",
        ),
        // Object pronouns: two different gaps, filled by two different pronouns.
        "es.object_pronoun.direct" to FocusProfile(
            "the Spanish direct object pronoun",
            "The direct object pronoun agrees with the thing in gender and number — lo, la, los, las — and stands in front of the verb.",
        ),
        "es.object_pronoun.indirect" to FocusProfile(
            "the Spanish indirect object pronoun",
            "le and les name the receiver of the action, where the direct pronoun would name the thing being handed over instead.",
        ),
        "es.gustar" to FocusProfile(
            "the Spanish verb gustar",
            "With gustar the verb agrees with the thing liked, not with the person who likes it: nos gusta el mar, but me gustan tus dibujos.",
        ),
        "es.reflexive.pronoun" to FocusProfile(
            "a Spanish reflexive pronoun",
            "A reflexive verb needs the pronoun that agrees with its subject: me levanto, se peina, nos alegramos.",
        ),
        "es.reflexive.impersonal_se" to FocusProfile(
            "impersonal se in Spanish",
            "Impersonal se has no subject of its own, so it is third person and the verb agrees with the thing instead: se habla español, but se hablan muchos idiomas.",
        ),
        // The imperative: affirmative is the present, negative is the subjunctive.
        "es.imperative.affirmative" to FocusProfile(
            "the Spanish affirmative imperative",
            "The affirmative command is the present form: with tú the final -s is dropped and with usted the third person is kept — habla, hable.",
        ),
        "es.imperative.irregular" to FocusProfile(
            "the irregular Spanish affirmative imperative",
            "These affirmative tú commands are the irregular ones — ven, pon, sal, ten, haz, di, ve — and have to be learned as they stand.",
        ),
        "es.imperative.negative" to FocusProfile(
            "the Spanish negative imperative",
            "A negative command is no plus the subjunctive, never the indicative: no corras, no digas eso.",
        ),
        // The three conditions, the volitional and 敬語: the N4 block units
        // 15-16 add on top of the passive, the causative and the relative clause.
        "ja.conditional_tara" to FocusProfile(
            "the Japanese 〜たら condition",
            "〜たら is the plain past plus たら — 降る → 降った → 降ったら — and it is the one condition that can carry a request, which と will not: 明日、雨が降ったら、家にいてください.",
        ),
        "ja.conditional_nara" to FocusProfile(
            "the Japanese 〜なら condition",
            "〜なら takes the verb exactly as a dictionary lists it — 行く → 行くなら — and a なら clause carries no tense of its own: 雨が降るなら、旅行に行きましょう.",
        ),
        "ja.conditional_ba" to FocusProfile(
            "the Japanese 〜ば condition",
            "ば is the written and formal way of saying if: an い-adjective drops the final い and a う-verb takes the 音便 れば — 忙しい → 忙しければ, 買う → 買えば — and in speech 忙しければ gives way to 忙しかったら.",
        ),
        "ja.volition_polite" to FocusProfile(
            "the Japanese polite volitional (〜ましょう)",
            "The polite volitional is the ます-stem plus ましょう: 行く → 行きましょう, and it is a proposal the other person is free to refuse. 見よう is the plain 意向形, and でしょう is the speaker's guess rather than anything proposed.",
        ),
        // 敬語 here is an introduction, not the system a company teaches: a
        // handful of everyday substitutions, told apart only by who they lift.
        "ja.keigo_honorific" to FocusProfile(
            "Japanese 尊敬語",
            "尊敬語 lifts the other person's action, and it is a small set of everyday substitutions rather than a system: 食べる → 召し上がる, する → なさる, 来る → いらっしゃる. いただきます is 謙譲語, which lowers the speaker instead.",
        ),
        "ja.keigo_humble" to FocusProfile(
            "Japanese 謙譲語",
            "謙譲語 lowers the speaker's own action toward the other person, and it is the same small introduction: 行く → 伺う, 見る → 拝見する, する → いたす. なさいます is 尊敬語, and about your own action it would lift precisely what the humble form exists to lower.",
        ),
        "ja.rareru_readings" to FocusProfile(
            "the three readings of Japanese られる",
            "られる is potential, passive and — in a small closed set such as 得る, 求める, 採る — can get, so the ending alone never says which: 読める is can read or is read, while いい結果が得られます is the 得る reading on its own. What settles it is who stands in the subject slot.",
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
        // 受身, 使役 and 修飾節: the three areas units 13-14 added.
        "ja.passive_formation" to FocusProfile(
            "the Japanese passive (受身)",
            "The plain passive puts れる on the stem — 読む → 読まれる, 書く → 書かれる — and only a 他動詞 can take one; 読める is the potential, and られる by itself never says which of the two it is.",
        ),
        "ja.passive_particles" to FocusProfile(
            "the Japanese passive particles",
            "In the passive the を-object becomes the topic and the one doing it takes に: 母に叱られました, 車は工場で作られます.",
        ),
        "ja.passive_teiru" to FocusProfile(
            "受け身 + ている",
            "受け身 + ている says the state is on-going right now: the passive stem plus ている or ています — 使われている, 汚染されています.",
        ),
        "ja.causative_formation" to FocusProfile(
            "the Japanese causative (使役)",
            "The causative adds せ to the stem — 食べる → 食べさせる, する → させる — and that added せ is what separates it from the passive's れ: 読まれる, 食べられる.",
        ),
        "ja.causative_teiru" to FocusProfile(
            "a Japanese causative in progress",
            "A causative carries ている and ます just like any other verb: 食べさせている, 走らせています, 野菜を食べさせました.",
        ),
        "ja.relative_clause" to FocusProfile(
            "a Japanese relative clause (修飾節)",
            "A clause in front of the noun it describes is bound to it with の and keeps its plain form — 日本語を話せる人, 漢字を読めない人, 読みやすい日本語の本 — and は cannot head it.",
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
        // Themed vocabulary: the frames are right and the wrong word is in the
        // slot, so the honest diagnosis is a form error (the word does not
        // agree with the article, the number and the ending that go with it)
        // or a person/number error. UNRELATED is deliberately absent: a
        // different noun is not a grammar slip, and CurriculumIntegrityTest
        // refuses UNRELATED on any focused challenge, so a themed unit states
        // the kind of mistake its distractors actually make.
        "es.vocab.everyday_life" to FocusProfile(
            "everyday Spanish vocabulary",
            "That word does not fit the slot: check the article, the number and the ending that go with it.",
        ),
        "es.vocab.travel" to FocusProfile(
            "travel Spanish vocabulary",
            "That is not the word this sentence needs: check the article, the number and the ending that go with it.",
        ),
        // 〜なければなりません, 〜てもいい and 〜たことがあります: the three
        // N4 modality frames units 18-19 add. Each is an ending built in two
        // halves, so every distractor is a wrong form, a wrong register (the
        // plain form where the item states a polite one) or a wrong tense
        // where the ます-form carries one.
        "ja.necessity" to FocusProfile(
            "Japanese 〜なければなりません (necessity)",
            "Necessity is 〜なければなりません: the ない-form minus い plus なければ, plus なりません — and nothing may follow なければ.",
        ),
        "ja.permission" to FocusProfile(
            "Japanese 〜てもいい (permission)",
            "Permission is 〜てもいい: the て-form plus てもいい, and てもいいですか asks the question and expects an answer back.",
        ),
        "ja.experience" to FocusProfile(
            "Japanese 〜たことがあります (experience)",
            "Experience is 〜たことがあります: the plain past た plus こと plus が plus あります, and the が is what makes it a clause.",
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

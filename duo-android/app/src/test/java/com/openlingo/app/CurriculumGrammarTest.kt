package com.openlingo.app

import com.openlingo.app.data.local.curriculum.UnitPayload
import com.openlingo.app.data.local.models.ChallengeType
import com.openlingo.app.grammar.AnswerGrader
import com.openlingo.app.grammar.GrammarFocus
import com.openlingo.app.grammar.drills.ErrorTag
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Grammar integrity: error tags stay honest to their focus, fill-blank answer
 * keys never clash, and every taught grammar point has a displayable name.
 */
class CurriculumGrammarTest {

    private val allPayloads: List<UnitPayload> = Corpus.allPayloads

    private val honestTagsByFocus = mapOf(
        // Spanish verbs: right frame, wrong ending - person, number or tense.
        "es.imperfecto" to setOf("WRONG_TENSE", "WRONG_PERSON"),
        "es.present_person" to setOf("WRONG_PERSON", "WRONG_TENSE"),
        "es.preterito.regular" to setOf("WRONG_TENSE", "WRONG_PERSON"),
        "es.tener_present" to setOf("WRONG_PERSON"),
        // Irregular and stem-changing preterites: right tense, wrong person or
        // number, or a form the regular preterite would have produced. The
        // infinitive and the present are a frame error, so WRONG_FORM is honest
        // where a distractor leaves a dictionary form in the slot.
        "es.preterito.irregular" to setOf("WRONG_TENSE", "WRONG_PERSON", "WRONG_FORM"),
        "es.preterito.stem_changing" to setOf("WRONG_TENSE", "WRONG_PERSON", "WRONG_FORM"),
        // The past perfect: a plain preterite or present perfect is a tense error,
        // an -ndo form or a wrongly-built participle is a form error, and another
        // subject is a person error.
        "es.past_perfect" to setOf("WRONG_TENSE", "WRONG_FORM", "WRONG_PERSON"),
        // The conditional. Regular and irregular share the same two honest errors:
        // the future of the same verb is a tense error, another person is a person
        // error, and an infinitive left in the slot is a form error.
        "es.conditional.regular" to setOf("WRONG_TENSE", "WRONG_PERSON", "WRONG_FORM"),
        "es.conditional.irregular" to setOf("WRONG_TENSE", "WRONG_PERSON", "WRONG_FORM"),
        // The polite periphrasis is a register choice first (quiero vs me
        // gustaría) and a form choice second (me gusta, the infinitive).
        "es.conditional.periphrasis" to setOf("WRONG_REGISTER", "WRONG_FORM", "WRONG_TENSE"),
        // Connectives: a distractor is a connective that cannot carry the relation
        // the sentence states — a different purpose/cause/result/concession in the
        // same slot — which is the form the sentence calls for, not a different word.
        "es.connectives" to setOf("WRONG_FORM"),
        // ser vs estar: a usage choice, plus agreement for the distractors that get that right.
        "es.ser_estar" to setOf("WRONG_COPULA", "WRONG_PERSON"),
        "es.ser_present" to setOf("WRONG_COPULA", "WRONG_PERSON"),
        // Spanish units 13-14. A verb slot under the subjunctive or the
        // imperative can miss on the ending (WRONG_FORM), on the person
        // (WRONG_PERSON) or, for the two tú/usted commands, on the level of
        // formality - a tú command answered with the usted form is
        // grammatically right and the wrong register to hand a friend.
        "es.subjunctive.wants" to setOf("WRONG_FORM", "WRONG_PERSON"),
        "es.subjunctive.emotion_doubt" to setOf("WRONG_FORM", "WRONG_PERSON"),
        "es.subjunctive.purpose_concession" to setOf("WRONG_FORM", "WRONG_PERSON"),
        // Spanish units 15-16. The subjunctive perfect and the unreal past
        // are a tense choice (había against hubiera), a person choice (hubiera
        // against hubieras) and a form choice wherever a distractor leaves the
        // infinitive or a bare participle in the slot. The same three are
        // honest for reported speech, where the shifted and the unshifted tense
        // are both right and only the frame says which belongs. por/para is a
        // preposition in a slot that wants a preposition, so only the form and
        // the agreement of the noun behind it can miss.
        "es.subjunctive.past_perfect" to setOf("WRONG_TENSE", "WRONG_FORM", "WRONG_PERSON"),
        "es.unreal_past" to setOf("WRONG_TENSE", "WRONG_FORM", "WRONG_PERSON"),
        "es.conditional.unreal_past" to setOf("WRONG_TENSE", "WRONG_FORM", "WRONG_PERSON"),
        "es.reported_speech.backshift" to setOf("WRONG_TENSE", "WRONG_FORM", "WRONG_PERSON"),
        "es.reported_speech.no_backshift" to setOf("WRONG_TENSE", "WRONG_FORM", "WRONG_PERSON"),
        "es.por_para" to setOf("WRONG_FORM", "WRONG_PERSON"),
        "es.object_pronoun.direct" to setOf("WRONG_FORM", "WRONG_PERSON"),
        "es.object_pronoun.indirect" to setOf("WRONG_FORM", "WRONG_PERSON"),
        "es.gustar" to setOf("WRONG_FORM", "WRONG_PERSON"),
        "es.reflexive.pronoun" to setOf("WRONG_FORM", "WRONG_PERSON"),
        "es.reflexive.impersonal_se" to setOf("WRONG_FORM", "WRONG_PERSON"),
        "es.imperative.affirmative" to setOf("WRONG_FORM", "WRONG_PERSON", "WRONG_REGISTER"),
        "es.imperative.irregular" to setOf("WRONG_FORM", "WRONG_PERSON"),
        "es.imperative.negative" to setOf("WRONG_FORM", "WRONG_PERSON"),
        // Japanese counters: the numeral is right in every distractor.
        "ja.counter_classifier" to setOf("WRONG_CLASSIFIER"),
        "ja.counter_people" to setOf("WRONG_CLASSIFIER"),
        "ja.counter_time" to setOf("WRONG_CLASSIFIER"),
        // Japanese verbs inflect for form, not tense: only ます/ました and です/でした carry one.
        "ja.potential" to setOf("WRONG_FORM", "WRONG_TENSE"),
        "ja.potential_nominal" to setOf("WRONG_FORM"),
        "ja.te_form" to setOf("WRONG_FORM", "WRONG_TENSE"),
        "ja.request_polite" to setOf("WRONG_FORM", "WRONG_REGISTER", "WRONG_TENSE"),
        "ja.polite_verb" to setOf("WRONG_REGISTER", "WRONG_FORM", "WRONG_TENSE"),
        "ja.polite_register" to setOf("WRONG_REGISTER"),
        "ja.copula_polite" to setOf("WRONG_REGISTER", "WRONG_TENSE"),
        // Japanese units 11-12. A verb slot here can miss on the ending
        // (WRONG_FORM), on the register it was inflected in (WRONG_REGISTER —
        // 買った against 買いました is the whole point of the plain/polite
        // pair) or on whether it inflects for the past at all (WRONG_TENSE —
        // 行きます against 行きました), so all three are honest per focus.
        "ja.past_polite" to setOf("WRONG_TENSE", "WRONG_FORM", "WRONG_REGISTER"),
        "ja.plain_vs_polite" to setOf("WRONG_REGISTER", "WRONG_TENSE", "WRONG_FORM"),
        "ja.negative" to setOf("WRONG_FORM", "WRONG_REGISTER", "WRONG_TENSE"),
        // The adjective classes: a wrong ending is a form error, and dropping
        // the past off 寒かった is a tense error. Neither has a register to get
        // wrong — 寒いです and 寒かった are the same word in two of its own forms.
        "ja.i_adjective" to setOf("WRONG_FORM", "WRONG_TENSE"),
        "ja.na_adjective" to setOf("WRONG_FORM", "WRONG_TENSE", "WRONG_REGISTER"),
        "ja.ability_polite" to setOf("WRONG_FORM", "WRONG_REGISTER", "WRONG_TENSE"),
        "ja.think" to setOf("WRONG_FORM", "WRONG_REGISTER", "WRONG_TENSE"),
        "ja.giving_receiving" to setOf("WRONG_FORM", "WRONG_TENSE", "WRONG_REGISTER"),
        // Japanese units 13-14. As everywhere else in Japanese the error is a
        // wrong form, not a wrong tense; only ます/ました and です/でした carry a
        // tense at all. The causative adds a register to that, because the
        // plain 読ませます against the polite 読ませました is a register choice.
        "ja.passive_formation" to setOf("WRONG_FORM", "WRONG_TENSE"),
        "ja.passive_particles" to setOf("WRONG_FORM", "WRONG_TENSE"),
        "ja.passive_teiru" to setOf("WRONG_FORM", "WRONG_TENSE"),
        "ja.causative_formation" to setOf("WRONG_FORM", "WRONG_TENSE", "WRONG_REGISTER"),
        "ja.causative_teiru" to setOf("WRONG_FORM", "WRONG_TENSE"),
        "ja.relative_clause" to setOf("WRONG_FORM", "WRONG_TENSE"),
        // Japanese units 15-16. The conditions and the volitional are the same
        // three the rest of this block admits: a wrong form (行きます for
        // 行ったら), a register (行こう for 行きましょう, a 意向形 against a
        // ましょう) and a tense where the ます form carries one. 〜ば pairs with
        // ない/ありません and so has no tense of its own to get wrong.
        "ja.conditional_tara" to setOf("WRONG_FORM", "WRONG_REGISTER", "WRONG_TENSE"),
        "ja.conditional_nara" to setOf("WRONG_FORM", "WRONG_REGISTER", "WRONG_TENSE"),
        "ja.conditional_ba" to setOf("WRONG_FORM", "WRONG_REGISTER"),
        "ja.volition_polite" to setOf("WRONG_FORM", "WRONG_REGISTER", "WRONG_TENSE"),
        // 敬語 is the register question itself: 召し上がる against いただきます
        // is right grammar in the wrong clothes, so WRONG_REGISTER is the
        // honest tag there, and what the plain forms miss is the form or tense.
        "ja.keigo_honorific" to setOf("WRONG_FORM", "WRONG_REGISTER"),
        "ja.keigo_humble" to setOf("WRONG_FORM", "WRONG_REGISTER", "WRONG_TENSE"),
        "ja.rareru_readings" to setOf("WRONG_FORM", "WRONG_REGISTER", "WRONG_TENSE"),
        // The themed vocabulary units (Spanish 18-19, Japanese units 18-19).
        // A themed unit teaches nouns in context, so the frame is right and
        // the tile is the wrong word for it. Every distractor is therefore a
        // form error (a word that cannot carry the article, the number or the
        // ending the slot needs) or a person/number error (a noun of the
        // wrong gender or number). UNRELATED is deliberately absent from both
        // Spanish vocab tags: this test forbids UNRELATED on any focused
        // challenge, because a challenge that carries a grammaticalFocus has
        // told the learner the grammar point is what is being tested, and a
        // distractor that is simply a different noun contradicts that. The
        // Japanese modality frames are the same three every other Japanese
        // focus admits - a wrong form, a plain form where a polite one is
        // asked for, and a tense where the ます form carries one.
        "es.vocab.everyday_life" to setOf("WRONG_FORM", "WRONG_PERSON", "WRONG_TENSE"),
        "es.vocab.travel" to setOf("WRONG_FORM", "WRONG_PERSON", "WRONG_TENSE"),
        "ja.necessity" to setOf("WRONG_FORM", "WRONG_REGISTER", "WRONG_TENSE"),
        "ja.permission" to setOf("WRONG_FORM", "WRONG_REGISTER", "WRONG_TENSE"),
        "ja.experience" to setOf("WRONG_FORM", "WRONG_REGISTER", "WRONG_TENSE"),
    )

    @Test
    fun `grammar-tagged challenges explain why at least one distractor is wrong`() {
        val optionsByChallenge = allPayloads.flatMap { it.options }.groupBy { it.challengeId }
        allPayloads.flatMap { it.challenges }
            .filter { it.grammaticalFocus != null }
            .forEach { challenge ->
                val focus = challenge.grammaticalFocus
                val tags = optionsByChallenge[challenge.id].orEmpty()
                    .filterNot { it.correct }
                    .mapNotNull { it.errorTag }
                val explained = tags.any { it != "UNRELATED" }
                assertTrue(
                    "challenge ${challenge.id} is tagged with grammaticalFocus '$focus' but no " +
                        "incorrect option carries a specific errorTag; option errorTags were " +
                        "${optionsByChallenge[challenge.id].orEmpty().map { it.errorTag }} " +
                        "(tag one distractor WRONG_TENSE, WRONG_PERSON, WRONG_FORM, WRONG_COPULA, " +
                        "WRONG_CLASSIFIER or WRONG_REGISTER so the learner sees why it is wrong)",
                    explained,
                )
            }
    }

    @Test
    fun `error tags stay inside the fixed vocabulary and never mark a correct option`() {
        val vocabulary = ErrorTag.ALL
        val allOptions = allPayloads.flatMap { it.options }

        val unknown = allOptions.mapNotNull { it.errorTag }.distinct().filter { it !in vocabulary }
        assertEquals(
            "errorTag values outside the fixed vocabulary: $unknown",
            emptyList<String>(),
            unknown,
        )

        val mislabelled = allOptions.filter { it.correct && it.errorTag != null }.map { it.id }
        assertEquals(
            "options that are the correct answer but carry an errorTag: $mislabelled",
            emptyList<Int>(),
            mislabelled,
        )
    }

    @Test
    fun `a grammar-tagged challenge never keeps an unrelated distractor`() {
        val optionsByChallenge = allPayloads.flatMap { it.options }.groupBy { it.challengeId }
        allPayloads.flatMap { it.challenges }
            .filter { it.grammaticalFocus != null }
            .forEach { challenge ->
                val unrelated = optionsByChallenge[challenge.id].orEmpty()
                    .filter { it.errorTag == "UNRELATED" }
                    .map { it.id }
                assertTrue(
                    "challenge ${challenge.id} is tagged with grammaticalFocus " +
                        "'${challenge.grammaticalFocus}' but still offers UNRELATED distractors " +
                        "$unrelated; a grammar item must be passable only by a grammatical error",
                    unrelated.isEmpty(),
                )
            }
    }

    @Test
    fun `an error tag is compatible with the grammatical focus of its challenge`() {
        val optionsByChallenge = allPayloads.flatMap { it.options }.groupBy { it.challengeId }
        val unmappedFocuses = mutableListOf<String>()
        val dishonestTags = mutableListOf<String>()
        allPayloads.flatMap { it.challenges }
            .filter { it.grammaticalFocus != null }
            .forEach { challenge ->
                val focus = challenge.grammaticalFocus!!
                val honest = honestTagsByFocus[focus]
                if (honest == null) {
                    unmappedFocuses += "challenge ${challenge.id} uses grammaticalFocus '$focus', " +
                        "which honestTagsByFocus does not cover; add it with the tags its " +
                        "distractors may honestly carry"
                } else {
                    optionsByChallenge[challenge.id].orEmpty()
                        .mapNotNull { option -> option.errorTag?.let { option to it } }
                        .filterNot { (_, tag) -> tag in honest }
                        .forEach { (option, tag) ->
                            dishonestTags += "option ${option.id} ('${option.text}') on challenge " +
                                "${challenge.id} ($focus) is tagged $tag; the honest tags for " +
                                "$focus are ${honest.sorted()}"
                        }
                }
            }
        assertEquals(
            "grammaticalFocus values with no declared set of honest error tags: $unmappedFocuses",
            emptyList<String>(),
            unmappedFocuses,
        )
        assertEquals(
            "error tags that contradict the grammatical focus of their challenge",
            emptyList<String>(),
            dishonestTags,
        )
    }

    @Test
    fun `every grammar point the corpus teaches has a name the learner can be shown`() {
        val unnamed = allPayloads.flatMap { it.challenges }
            .mapNotNull { it.grammaticalFocus }
            .distinct()
            .filter { GrammarFocus.label(it) == null }
        assertEquals(
            "grammar points with no display name, so the learner would see no heading at all: $unnamed",
            emptyList<String>(),
            unnamed,
        )
    }

    @Test
    fun `a fill blank accepts no form taught as another focus's answer`() {
        val optionsByChallenge = allPayloads.flatMap { it.options }.groupBy { it.challengeId }
        val correctByText = allPayloads.flatMap { it.challenges }
            .filter { it.grammaticalFocus != null }
            .flatMap { challenge ->
                optionsByChallenge[challenge.id].orEmpty()
                    .filter { it.correct }
                    .map { option ->
                        Triple(option.text, challenge.grammaticalFocus!!, challenge.id)
                    }
            }

        val clashes = mutableListOf<String>()
        allPayloads.flatMap { it.challenges }
            .filter { it.type == ChallengeType.FILL_BLANK }
            .forEach { challenge ->
                val focus = challenge.grammaticalFocus ?: return@forEach
                AnswerGrader.acceptedVariants(challenge.acceptedAnswers).forEach { accepted ->
                    correctByText
                        .filter { (text, otherFocus, ownerId) ->
                            text == accepted && ownerId != challenge.id && otherFocus != focus
                        }
                        .forEach { (_, otherFocus, ownerId) ->
                            clashes += "challenge ${challenge.id} ($focus) accepts '$accepted', " +
                                "which challenge $ownerId teaches as its $otherFocus answer"
                        }
                }
            }
        assertEquals(
            "fill blanks accepting a form the corpus assigns to another grammatical focus: $clashes",
            emptyList<String>(),
            clashes,
        )
    }

    @Test
    fun `a fill blank accepts no form another fill blank already owns`() {
        val optionsByChallenge = allPayloads.flatMap { it.options }.groupBy { it.challengeId }
        val items = allPayloads.flatMap { it.challenges }
            .filter { it.type == ChallengeType.FILL_BLANK }
            .map { challenge ->
                Triple(
                    challenge,
                    optionsByChallenge[challenge.id].orEmpty().firstOrNull { it.correct }?.text,
                    AnswerGrader.acceptedVariants(challenge.acceptedAnswers),
                )
            }

        val clashes = mutableListOf<String>()
        items.forEach { (challenge, answer, accepted) ->
            accepted.forEach { variant ->
                items
                    .filter { it.first.id != challenge.id && it.second != answer && variant in it.third }
                    .forEach { (other, otherAnswer, _) ->
                        clashes += "challenge ${challenge.id} (answer '$answer') accepts " +
                            "'$variant', which challenge ${other.id} already accepts for its " +
                            "own answer '$otherAnswer'; the held-out pool must not reuse a form " +
                            "the lesson path already teaches"
                    }
            }
        }
        assertEquals(
            "fill blanks sharing an answer key entry with a differently-answered item: $clashes",
            emptyList<String>(),
            clashes,
        )
    }

    @Test
    fun `a ことができます slot is filled with a verb こと can nominalise`() {
        val verbFinalKana = "うくぐすつぬぶむる"
        val politeEndings = listOf("ます", "ません", "ましょう", "たい", "そう")

        val optionsByChallenge = allPayloads.flatMap { it.options }.groupBy { it.challengeId }
        val malformed = mutableListOf<String>()
        allPayloads.flatMap { it.challenges }
            .filter { it.type == ChallengeType.FILL_BLANK || it.type == ChallengeType.CONJUGATE }
            .filter { it.question.contains("ことができます") }
            .forEach { challenge ->
                val answers = AnswerGrader.acceptedVariants(challenge.acceptedAnswers) +
                    optionsByChallenge[challenge.id].orEmpty()
                        .filter { it.correct }
                        .map { it.text }
                answers.forEach { accepted ->
                    val where = "challenge ${challenge.id} ('${challenge.question}') takes '$accepted'"
                    if (accepted.isNotEmpty() && accepted.takeLast(1) !in verbFinalKana) {
                        malformed += "$where, which does not end in a verb: こと can only " +
                            "nominalise a verb, and a する-verb keeps its する"
                    }
                    if (politeEndings.any { accepted.endsWith(it) }) {
                        malformed += "$where, which is a polite form: こと cannot nominalise a " +
                            "ます form, so $accepted こと is not a word"
                    }
                }
            }
        assertEquals(
            "forms keyed into a ことができます slot that do not compose into a sentence: $malformed",
            emptyList<String>(),
            malformed,
        )
    }

    @Test
    fun `a rule card that names multiple correct forms has all of them in the options`() {
        val optionsByChallenge = allPayloads.flatMap { it.options }.groupBy { it.challengeId }
        val markers = listOf("both are correct", "all correct", "both are accepted")
        val failures = mutableListOf<String>()
        // English function words that can sit directly in front of a marker
        // phrase. None of them is a form any rule card would endorse.
        val englishFunctionWords = setOf(
            "both", "all", "are", "is", "the", "a", "an", "so", "and", "of", "in", "on",
            "or", "to", "it", "that", "this", "with", "for", "as", "also", "correct", "accepted",
        )

        allPayloads.flatMap { it.challenges }.forEach { challenge ->
            val rule = challenge.ruleText ?: return@forEach
            val marker = markers.firstOrNull { rule.contains(it, ignoreCase = true) }
                ?: return@forEach

            // The corpus convention: forms appear immediately before parenthetical
            // annotations like "(vosotros)"/"(ustedes)" or before the marker itself.
            // Extract words that appear right before "(...)" or before the marker.
            val before = rule.substring(0, rule.indexOf(marker, ignoreCase = true))
            val namedForms = mutableListOf<String>()

            // Pattern: word(s) before a parenthetical annotation
            Regex("([a-záéíóúñü]+(?:/[a-záéíóúñü]+)*)\\s*\\([^)]*\\)").findAll(before).forEach { m ->
                m.groupValues[1].split("/").forEach { namedForms.add(it) }
            }
            // Pattern: word(s) immediately before the marker (no parenthetical).
            //
            // This pattern is the noisiest of the two, because the markers are
            // English phrases ("both are correct") and the word sitting in front
            // of one is usually the last word of an English sentence rather than
            // a Spanish form. Two guards, in order:
            //
            //  1. A named form sits immediately before the marker, so `before`
            //     must end in a letter. If it ends in . ! ? , ; : ) — the text is
            //     prose, not an endorsed form.
            //  2. The harvested token must not be an English function word.
            //     Without this, "…in Latin America, so both are correct" yields
            //     the form "so" and fails a challenge that is perfectly correct.
            //     Every form the corpus actually endorses is a Spanish content
            //     word (estéis, estén, nevera, frigorífico), so this discards
            //     false positives without discarding real ones.
            val trimmed = before.trimEnd()
            if (trimmed.lastOrNull()?.isLetter() == true) {
                Regex("([a-záéíóúñü]+(?:/[a-záéíóúñü]+)*)\\s*$", RegexOption.IGNORE_CASE).find(trimmed)?.let { m ->
                    val forms = m.groupValues[1].split("/")
                    if (forms.none { it.lowercase() in englishFunctionWords }) {
                        forms.forEach { namedForms.add(it) }
                    }
                }
            }

            if (namedForms.size < 2) return@forEach

            val correctTexts = optionsByChallenge[challenge.id].orEmpty()
                .filter { it.correct }
                .map { it.text }
                .toSet()

            namedForms.forEach { form ->
                if (form !in correctTexts) {
                    failures += "challenge ${challenge.id} rule card names '$form' as correct " +
                        "but it is not among the correct options ($correctTexts)"
                }
            }
        }

        assertEquals(
            "rule cards whose named correct forms are missing from the option grid: $failures",
            emptyList<String>(),
            failures,
        )
    }

}

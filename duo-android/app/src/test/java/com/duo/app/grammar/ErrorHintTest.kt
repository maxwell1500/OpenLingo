package com.duo.app.grammar

import com.duo.app.data.local.curriculum.AdvancedCurriculumData
import com.duo.app.data.local.curriculum.B1CurriculumData
import com.duo.app.data.local.curriculum.ExpandedCurriculumData
import com.duo.app.data.local.curriculum.JapaneseN4CurriculumData
import com.duo.app.data.local.curriculum.UnitPayload
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Error-specific feedback is the app's only in-the-moment explanation of a
 * wrong answer, so the mapping from (errorTag, grammaticalFocus) to a sentence
 * is pinned here rather than left to whoever adds the next lesson.
 *
 * The three guards that matter:
 *  - the tag vocabulary and the mapping cannot drift apart in either direction;
 *  - an unmapped tag produces no hint at all rather than an empty one;
 *  - every tagged distractor in the bundled corpus, and every grammar focus the
 *    corpus uses, resolves to a real sentence.
 */
class ErrorHintTest {

    private val allPayloads: List<UnitPayload> =
        ExpandedCurriculumData.spanishExpandedUnits +
            ExpandedCurriculumData.japaneseExpandedUnits +
            AdvancedCurriculumData.spanishAdvancedUnits +
            AdvancedCurriculumData.japaneseAdvancedUnits +
            B1CurriculumData.spanishA2Units +
            B1CurriculumData.spanishB1Units +
            B1CurriculumData.japaneseN4Units +
            JapaneseN4CurriculumData.japaneseN4ExtensionUnits

    /** The fixed errorTag vocabulary, mirroring the KDoc on ChallengeOptionEntity.errorTag. */
    private val errorTagVocabulary = setOf(
        "WRONG_TENSE",
        "WRONG_PERSON",
        "WRONG_FORM",
        "WRONG_COPULA",
        "WRONG_CLASSIFIER",
        "WRONG_REGISTER",
        "UNRELATED",
    )

    // --- the fail-on-unmapped-tag guard -------------------------------------

    @Test
    fun `the mapping covers the whole shipped tag vocabulary and nothing else`() {
        // A tag added to ErrorHint.TAGS without a sentence behind it, or a
        // sentence implemented for a tag the vocabulary dropped, both fail here
        // rather than reaching a learner as blank or dead feedback.
        assertEquals(
            "ErrorHint.TAGS and the documented errorTag vocabulary have drifted apart",
            errorTagVocabulary,
            ErrorHint.TAGS,
        )
    }

    @Test
    fun `a tag outside the vocabulary produces no hint instead of empty feedback`() {
        // The contract is a visible null the tests can catch, not a blank
        // string the bottom bar would render as an empty line.
        listOf("WRONG_CASE", "wrong_tense", "WRONG_TENSE ", "", "MISSPELLED").forEach { tag ->
            assertNull(
                "unmapped tag '$tag' must not produce a hint",
                ErrorHint.forChoice("hablábamos", "hablaron", tag, "es.preterito.regular"),
            )
        }
        assertNull(
            "an untagged option has no diagnosis to derive",
            ErrorHint.forChoice("hablábamos", "hablaron", null, "es.preterito.regular"),
        )
    }

    @Test
    fun `every tag in the vocabulary yields a hint that names both forms`() {
        ErrorHint.TAGS.forEach { tag ->
            val hint = ErrorHint.forChoice("hablábamos", "hablaron", tag, "es.preterito.regular")
            require(hint != null) { "tag '$tag' produced no hint" }
            assertTrue("hint for '$tag' never names the chosen form: $hint", hint.contains("hablábamos"))
            assertTrue("hint for '$tag' never names the correct form: $hint", hint.contains("hablaron"))
            assertTrue("hint for '$tag' leaked the raw tag: $hint", !hint.contains(tag))
        }
    }

    @Test
    fun `every tagged distractor in the corpus produces a hint`() {
        val focusByChallenge = allPayloads.flatMap { it.challenges }
            .associate { it.id to it.grammaticalFocus }
        val correctTextByChallenge = allPayloads.flatMap { it.options }
            .filter { it.correct }
            .associate { it.challengeId to it.text }

        val unresolved = allPayloads.flatMap { it.options }
            .mapNotNull { option ->
                val tag = option.errorTag ?: return@mapNotNull null
                val hint = ErrorHint.forChoice(
                    chosenText = option.text,
                    correctText = correctTextByChallenge[option.challengeId],
                    errorTag = tag,
                    focus = focusByChallenge[option.challengeId],
                )
                if (hint.isNullOrBlank()) {
                    "option ${option.id} ('${option.text}') tagged $tag"
                } else {
                    null
                }
            }

        assertEquals("tagged distractors with no hint: $unresolved", emptyList<String>(), unresolved)
    }

    @Test
    fun `every grammar focus in the corpus has a profile`() {
        // A focus with no profile still gets a hint (the generic path names the
        // answer), so this is about sentence *quality*: a named grammar point
        // teaches, "this sentence needs ..." only corrects.
        val withoutProfile = allPayloads.flatMap { it.challenges }
            .mapNotNull { it.grammaticalFocus }
            .distinct()
            .filter { focus ->
                val hint = ErrorHint.forChoice("だ", "です", "WRONG_REGISTER", focus)
                hint == null || !hint.contains("For ")
            }

        assertEquals(
            "grammar focuses with no ErrorHint profile, so their feedback never names the rule: $withoutProfile",
            emptyList<String>(),
            withoutProfile,
        )
    }

    // --- the per-tag behaviour ---------------------------------------------

    @Test
    fun `a wrong tense names the ending and the correct preterite form`() {
        val hint = ErrorHint.forChoice(
            chosenText = "hablábamos",
            correctText = "hablaron",
            errorTag = "WRONG_TENSE",
            focus = "es.preterito.regular",
        )
        require(hint != null)
        assertTrue("no tense diagnosis: $hint", hint.contains("wrong tense"))
        assertTrue("does not name the preterite: $hint", hint.contains("preterite"))
        assertTrue("does not state the answer: $hint", hint.contains("“hablaron”"))
    }

    @Test
    fun `a wrong person is distinguished from a wrong tense`() {
        val person = ErrorHint.forChoice("hablas", "hablo", "WRONG_PERSON", "es.present_person")
        require(person != null)
        assertTrue("WRONG_PERSON was reported as a tense error: $person", person.contains("person or number"))
        assertTrue("does not name the agreement rule: $person", person.contains("match the subject"))

        val tense = ErrorHint.forChoice("hablé", "hablo", "WRONG_TENSE", "es.present_person")
        require(tense != null)
        assertTrue("WRONG_TENSE was reported as an agreement error: $tense", tense.contains("wrong tense"))
    }

    @Test
    fun `a wrong verb form is never described as a tense error`() {
        // Japanese does not inflect for tense: the plain form against the
        // potential is a form error, and saying "wrong tense" would teach
        // something false.
        val hint = ErrorHint.forChoice("書きます", "書けます", "WRONG_FORM", "ja.potential")
        require(hint != null)
        assertTrue("no form diagnosis: $hint", hint.contains("different form"))
        assertTrue("claims a tense error in a form-only language: $hint", !hint.contains("tense"))
        assertTrue("does not explain what the potential means: $hint", hint.contains("can"))
    }

    @Test
    fun `a wrong copula explains the ser estar choice`() {
        val hint = ErrorHint.forChoice("soy", "estoy", "WRONG_COPULA", "es.ser_estar")
        require(hint != null)
        assertTrue("no copula diagnosis: $hint", hint.contains("to be"))
        assertTrue("does not state the ser / estar distinction: $hint", hint.contains("ser"))
        assertTrue("does not name the answer: $hint", hint.contains("“estoy”"))
    }

    @Test
    fun `a wrong counter is about the counter, not the number`() {
        val hint = ErrorHint.forChoice("三枚", "三人", "WRONG_CLASSIFIER", "ja.counter_people")
        require(hint != null)
        assertTrue("blames the numeral when the numeral is right: $hint", hint.contains("right number"))
        assertTrue("does not name the people counter: $hint", hint.contains("人数"))
        assertTrue("does not name the answer: $hint", hint.contains("“三人”"))
    }

    @Test
    fun `a wrong register is called right grammar in the wrong clothes`() {
        val hint = ErrorHint.forChoice("学生だ", "学生です", "WRONG_REGISTER", "ja.copula_polite")
        require(hint != null)
        assertTrue("does not concede the grammar is right: $hint", hint.contains("grammatical"))
        assertTrue("does not name the register: $hint", hint.contains("politeness"))
        assertTrue("does not name the answer: $hint", hint.contains("“学生です”"))
    }

    @Test
    fun `an unrelated distractor is called a different word, not a grammar slip`() {
        val hint = ErrorHint.forChoice("猫", "犬", "UNRELATED", "ja.potential")
        require(hint != null)
        assertTrue("does not say what the problem actually is: $hint", hint.contains("different word"))
        // The grammar treatment must not leak in: dressing a vocabulary mistake
        // up as a potential-form lesson is the failure mode here.
        assertTrue("dresses a vocabulary error up as a grammar lesson: $hint", !hint.contains("potential"))
        assertTrue("does not state the answer: $hint", hint.contains("“犬”"))
    }

    @Test
    fun `an unrelated distractor on a focusless challenge stays bare`() {
        val hint = ErrorHint.forChoice("perro", "gato", "UNRELATED", null)
        require(hint != null)
        assertTrue("invents a grammar point for a focusless challenge: $hint", !hint.contains("For "))
        assertTrue("does not name the answer: $hint", hint.contains("“gato”"))
    }

    // --- degradation --------------------------------------------------------

    @Test
    fun `a focusless or unknown challenge still gets a hint naming the answer`() {
        listOf(null, "", "es.future_unmapped", "zz.unknown").forEach { focus ->
            val hint = ErrorHint.forChoice("hablábamos", "hablaron", "WRONG_TENSE", focus)
            require(hint != null) { "no hint for focus '$focus'" }
            assertTrue(
                "hint for focus '$focus' omits the answer: $hint",
                hint.contains("This sentence needs “hablaron”."),
            )
        }
    }

    @Test
    fun `a blank correct answer drops the answer sentence instead of naming a placeholder`() {
        val tag = ErrorHint.forChoice("だ", "  ", "WRONG_REGISTER", "ja.copula_polite")
        require(tag != null)
        assertTrue("invented a placeholder answer: $tag", !tag.contains("the blank"))
        assertTrue("lost the diagnosis with it: $tag", tag.contains("politeness"))

        val unrelated = ErrorHint.forChoice("猫", "", "UNRELATED", null)
        require(unrelated != null)
        assertTrue(
            "claims a word is needed when there is none: $unrelated",
            !unrelated.contains("word this sentence needs"),
        )
    }

    // --- Spanish units 13-14 ------------------------------------------------

    @Test
    fun `a subjunctive mistake names the trigger that puts the second verb there`() {
        val wants = ErrorHint.forChoice("quiero que viene", "quiero que venga", "WRONG_FORM", "es.subjunctive.wants")
        require(wants != null)
        assertTrue("does not name the form the sentence needs: $wants", wants.contains("quiero que venga"))
        assertTrue("does not say what triggers the subjunctive: $wants", wants.contains("After querer, esperar, necesitar or buscar"))
        assertTrue("does not name the grammar point: $wants", wants.contains("subjunctive after querer"))

        val emotion = ErrorHint.forChoice("creo que es verdad", "no creo que sea verdad", "WRONG_FORM", "es.subjunctive.emotion_doubt")
        require(emotion != null)
        assertTrue("does not name the feeling that takes the subjunctive: $emotion", emotion.contains("Emotion, doubt and negation"))
        assertTrue("does not name the form: $emotion", emotion.contains("no creo que sea"))

        val purpose = ErrorHint.forChoice("te lo digo para que lo sabe", "te lo digo para que lo sepa", "WRONG_FORM", "es.subjunctive.purpose_concession")
        require(purpose != null)
        assertTrue("does not name the connective that takes it: $purpose", purpose.contains("Para que"))
        assertTrue("does not name the form: $purpose", purpose.contains("para que lo sepa"))
    }

    @Test
    fun `an object pronoun mistake names the pronoun the sentence needs`() {
        val direct = ErrorHint.forChoice("Los veo en el parque", "Los veo en la parque", "WRONG_PERSON", "es.object_pronoun.direct")
        require(direct != null)
        assertTrue("does not name the form: $direct", direct.contains("Los veo en la parque"))
        assertTrue("does not say what the pronoun agrees with: $direct", direct.contains("gender and number"))

        val indirect = ErrorHint.forChoice("Lo doy a mi hermana", "Le doy el libro a mi hermana", "WRONG_FORM", "es.object_pronoun.indirect")
        require(indirect != null)
        assertTrue("does not name the form: $indirect", indirect.contains("Le doy el libro a mi hermana"))
        assertTrue("does not separate the receiver from the thing: $indirect", indirect.contains("le and les name the receiver"))
    }

    @Test
    fun `gustar is explained as agreement with the thing, not with the person`() {
        val hint = ErrorHint.forChoice("Me gusta el mar", "Nos gusta el mar", "WRONG_PERSON", "es.gustar")
        require(hint != null)
        assertTrue("does not name the form: $hint", hint.contains("Nos gusta el mar"))
        assertTrue(
            "blames the person rather than the agreement: $hint",
            hint.contains("agrees with the thing liked, not with the person"),
        )
    }

    @Test
    fun `a reflexive mistake names the pronoun the subject has to agree with`() {
        val pronoun = ErrorHint.forChoice("Se levanta todos los días", "Me levanto todos los días", "WRONG_PERSON", "es.reflexive.pronoun")
        require(pronoun != null)
        assertTrue("does not name the form: $pronoun", pronoun.contains("Me levanto todos los días"))
        assertTrue("does not say what the pronoun agrees with: $pronoun", pronoun.contains("agrees with its subject"))

        val impersonal = ErrorHint.forChoice("En este país hablan muchos idiomas", "En este país se hablan muchos idiomas", "WRONG_FORM", "es.reflexive.impersonal_se")
        require(impersonal != null)
        assertTrue("does not name the form: $impersonal", impersonal.contains("En este país se hablan muchos idiomas"))
        assertTrue("does not say what impersonal se stands for: $impersonal", impersonal.contains("no subject of its own"))
    }

    @Test
    fun `the imperative hints separate the affirmative command from the negative one`() {
        val affirmative = ErrorHint.forChoice("habla", "hable", "WRONG_REGISTER", "es.imperative.affirmative")
        require(affirmative != null)
        assertTrue("conceals that the tú form is right: $affirmative", affirmative.contains("grammatical"))
        assertTrue("does not name the form: $affirmative", affirmative.contains("hable"))
        assertTrue("does not say how tú differs from usted: $affirmative", affirmative.contains("tú"))

        val irregular = ErrorHint.forChoice("Vienes", "Ven", "WRONG_FORM", "es.imperative.irregular")
        require(irregular != null)
        assertTrue("does not name the form: $irregular", irregular.contains("Ven"))
        assertTrue("does not list the irregulars: $irregular", irregular.contains("ven, pon, sal, ten, haz, di, ve"))

        val negative = ErrorHint.forChoice("no corres", "no corras", "WRONG_FORM", "es.imperative.negative")
        require(negative != null)
        assertTrue("does not name the form: $negative", negative.contains("no corras"))
        assertTrue("does not rule out the indicative: $negative", negative.contains("no plus the subjunctive, never the indicative"))
    }

    // --- Japanese units 13-14 ----------------------------------------------

    @Test
    fun `the passive hint does not hide that られる is also the potential`() {
        val hint = ErrorHint.forChoice("読める", "読まれる", "WRONG_FORM", "ja.passive_formation")
        require(hint != null)
        assertTrue("does not name the form: $hint", hint.contains("読まれる"))
        assertTrue("does not name the potential it collides with: $hint", hint.contains("読める"))
        assertTrue(
            "papers over the ambiguity instead of naming it: $hint",
            hint.contains("られる by itself never says which"),
        )
    }

    @Test
    fun `the passive hints say where the object and the agent go`() {
        val particles = ErrorHint.forChoice("母を叱られました", "母に叱られました", "WRONG_FORM", "ja.passive_particles")
        require(particles != null)
        assertTrue("does not name the form: $particles", particles.contains("母に叱られました"))
        assertTrue("does not say which particle the agent takes: $particles", particles.contains("takes に"))

        val teiru = ErrorHint.forChoice("この川は汚染しています", "この川は汚染されています", "WRONG_FORM", "ja.passive_teiru")
        require(teiru != null)
        assertTrue("does not name the form: $teiru", teiru.contains("汚染されています"))
        assertTrue("does not say what 受け身 + ている adds: $teiru", teiru.contains("on-going right now"))
    }

    @Test
    fun `the causative hint is told apart from the passive by the added せ`() {
        val formation = ErrorHint.forChoice("読まれる", "読ませる", "WRONG_FORM", "ja.causative_formation")
        require(formation != null)
        assertTrue("does not name the form: $formation", formation.contains("読ませる"))
        assertTrue("does not say what tells it from the passive: $formation", formation.contains("passive's れ"))

        val teiru = ErrorHint.forChoice("食べさせられました", "食べさせました", "WRONG_TENSE", "ja.causative_teiru")
        require(teiru != null)
        assertTrue("does not name the form: $teiru", teiru.contains("食べさせました"))
        assertTrue("does not state the rule: $teiru", teiru.contains("食べさせている"))
    }

    @Test
    fun `a relative clause hint says the clause binds with の and cannot take は`() {
        val hint = ErrorHint.forChoice("読みやすいのは日本語の本", "読みやすい日本語の本", "WRONG_FORM", "ja.relative_clause")
        require(hint != null)
        assertTrue("does not name the form: $hint", hint.contains("読みやすい日本語の本"))
        assertTrue("does not say what binds the clause to the noun: $hint", hint.contains("bound to it with の"))
        assertTrue("does not rule out は in the clause: $hint", hint.contains("は cannot head it"))
    }

    // --- Spanish units 15-16 ----------------------------------------------

    @Test
    fun `the subjunctive perfect hint says what the tense is made of`() {
        val hint = ErrorHint.forChoice("había comido", "hubiera comido", "WRONG_TENSE", "es.subjunctive.past_perfect")
        require(hint != null)
        assertTrue("does not state that the past did not happen: $hint", hint.contains("a past that did not happen"))
        assertTrue("does not name the two spellings: $hint", hint.contains("hubiera or hubiese"))
        assertTrue("does not name the grammar point: $hint", hint.contains("subjunctive past perfect"))
        assertTrue("does not state the answer: $hint", hint.contains("“hubiera comido”"))
    }

    @Test
    fun `the unreal past hint names both halves of the condition`() {
        val hint = ErrorHint.forChoice("Si sabía, habría venido", "Si hubiera sabido, habría venido", "WRONG_TENSE", "es.unreal_past")
        require(hint != null)
        assertTrue("does not say the condition did not happen: $hint", hint.contains("condition that did not happen"))
        assertTrue("does not say what answers it: $hint", hint.contains("answers with the conditional"))
        assertTrue("does not name the grammar point: $hint", hint.contains("unreal past condition"))
    }

    @Test
    fun `the unreal-past hint tells the indicative past perfect apart from the subjunctive one`() {
        // The two halves of the corpus's porque and como si items: one states
        // what did happen, the other what did not, and the two tenses differ
        // only in the form of the auxiliary.
        val factual = ErrorHint.forChoice("ya hubieran salido", "ya habían salido", "WRONG_TENSE", "es.conditional.unreal_past")
        require(factual != null)
        assertTrue("does not separate the two tenses: $factual", factual.contains("for what did not happen") && factual.contains("indicative past perfect for what did"))
        assertTrue("does not name the grammar point: $factual", factual.contains("unreal past against a real past"))

        val counterfactual = ErrorHint.forChoice("había perdido el tren", "hubiera perdido el tren", "WRONG_TENSE", "es.conditional.unreal_past")
        require(counterfactual != null)
        assertTrue("does not state the answer: $counterfactual", counterfactual.contains("“hubiera perdido el tren”"))
    }

    @Test
    fun `the reported speech hint says the verb steps back and that it may not have to`() {
        val shifted = ErrorHint.forChoice("Dijo que está cansado", "Dijo que estaba cansado", "WRONG_TENSE", "es.reported_speech.backshift")
        require(shifted != null)
        assertTrue("does not say the verb moves: $shifted", shifted.contains("moves one step back"))
        assertTrue("does not say the shift is not compulsory: $shifted", shifted.contains("also allows the unshifted form"))
        assertTrue("does not state the answer: $shifted", shifted.contains("“Dijo que estaba cansado”"))

        val future = ErrorHint.forChoice("Dijo que llama", "Dijo que llamaría", "WRONG_TENSE", "es.reported_speech.backshift")
        require(future != null)
        assertTrue("does not say where a future goes: $future", future.contains("future to conditional"))
    }

    @Test
    fun `the unshifted report hint covers the past that stays and the si question`() {
        val past = ErrorHint.forChoice("Dijo que había salido", "Dijo que ya había salido", "WRONG_FORM", "es.reported_speech.no_backshift")
        require(past != null)
        assertTrue("does not say an already-past verb keeps its past: $past", past.contains("keeps its past form"))
        assertTrue("does not name the grammar point: $past", past.contains("a Spanish report that keeps its verb"))

        val question = ErrorHint.forChoice("Preguntó que podía sentarme", "Preguntó si podía sentarme", "WRONG_FORM", "es.reported_speech.no_backshift")
        require(question != null)
        assertTrue("does not say a reported question takes si: $question", question.contains("si rather than que"))
        assertTrue("does not state the answer: $question", question.contains("“Preguntó si podía sentarme”"))
    }

    @Test
    fun `the por and para hint states the division without claiming it is total`() {
        val por = ErrorHint.forChoice("Gracias para tu ayuda", "Gracias por tu ayuda", "WRONG_FORM", "es.por_para")
        require(por != null)
        assertTrue("does not say what por marks: $por", por.contains("por marks a cause, an exchange and a result"))
        assertTrue("does not say what para marks: $por", por.contains("para marks a purpose, a direction and a recipient"))
        assertTrue("does not name the answer: $por", por.contains("“Gracias por tu ayuda”"))

        val para = ErrorHint.forChoice("Salimos para la lluvia", "Salimos por la lluvia", "WRONG_FORM", "es.por_para")
        require(para != null)
        assertTrue("does not state the answer: $para", para.contains("“Salimos por la lluvia”"))
    }

    // --- Japanese units 15-16 ---------------------------------------------

    @Test
    fun `the three conditional hints name the shape each condition is built on`() {
        val tara = ErrorHint.forChoice("雨が降りますと、家にいてください", "雨が降ったら、家にいてください", "WRONG_FORM", "ja.conditional_tara")
        require(tara != null)
        assertTrue("does not say たら is built on the plain past: $tara", tara.contains("plain past plus たら"))
        assertTrue("does not say たら is the one that carries a request: $tara", tara.contains("can carry a request"))
        assertTrue("does not name the grammar point: $tara", tara.contains("〜たら condition"))
        assertTrue("does not state the answer: $tara", tara.contains("“雨が降ったら、家にいてください”"))

        val nara = ErrorHint.forChoice("雨が降りますなら、旅行に行きましょう", "雨が降るなら、旅行に行きましょう", "WRONG_FORM", "ja.conditional_nara")
        require(nara != null)
        assertTrue("does not say なら takes the dictionary form: $nara", nara.contains("as a dictionary lists it"))
        assertTrue("does not say a なら clause carries no tense: $nara", nara.contains("carries no tense of its own"))
        assertTrue("does not name the grammar point: $nara", nara.contains("〜なら condition"))

        val ba = ErrorHint.forChoice("忙ししかったら、来なくていいです", "忙しければ、来なくていいです", "WRONG_FORM", "ja.conditional_ba")
        require(ba != null)
        assertTrue("does not say ば is the written and formal way: $ba", ba.contains("written and formal"))
        assertTrue("does not say what speech uses instead: $ba", ba.contains("gives way to 忙しかったら"))
        assertTrue("does not name the grammar point: $ba", ba.contains("〜ば condition"))
    }

    @Test
    fun `the volitional hint says ましょう proposes and でしょう only guesses`() {
        val hint = ErrorHint.forChoice("見ましょう", "見ます", "WRONG_TENSE", "ja.volition_polite")
        require(hint != null)
        assertTrue("does not say it is a proposal: $hint", hint.contains("free to refuse"))
        assertTrue("does not rule out でしょう: $hint", hint.contains("guess rather than anything proposed"))
        assertTrue("does not name the grammar point: $hint", hint.contains("polite volitional"))
    }

    @Test
    fun `the keigo hints keep 尊敬語 and 謙譲語 apart and call them an introduction`() {
        val honorific = ErrorHint.forChoice("社長がお昼をいただきます", "社長がお昼を召し上がります", "WRONG_REGISTER", "ja.keigo_honorific")
        require(honorific != null)
        assertTrue("does not say who it lifts: $honorific", honorific.contains("lifts the other person's action"))
        assertTrue("overstates what the course teaches: $honorific", honorific.contains("rather than a system"))
        assertTrue("does not name the answer: $honorific", honorific.contains("“社長がお昼を召し上がります”"))

        val humble = ErrorHint.forChoice("明日は行なさいます", "明日はうかがいます", "WRONG_REGISTER", "ja.keigo_humble")
        require(humble != null)
        assertTrue("does not say who it lowers: $humble", humble.contains("lowers the speaker's own action"))
        assertTrue("does not say why なさいます is wrong here: $humble", humble.contains("なさいます is 尊敬語"))
    }

    @Test
    fun `the られる hint refuses to pick a reading and agrees with the passive one`() {
        val hint = ErrorHint.forChoice("いい結果が食べられます", "いい結果が得られます", "WRONG_FORM", "ja.rareru_readings")
        require(hint != null)
        assertTrue("does not say the ending alone is ambiguous: $hint", hint.contains("the ending alone never says which"))
        assertTrue("does not name the three readings: $hint", hint.contains("potential, passive"))
        assertTrue("does not name the 得る set: $hint", hint.contains("得る"))
        assertTrue("does not say what settles it: $hint", hint.contains("subject slot"))
    }

    // --- FILL_BLANK ---------------------------------------------------------

    @Test
    fun `a typed wrong form is named and the required rule is stated`() {
        val hint = ErrorHint.forTypedAnswer("食べます", "食べられます", "ja.potential")
        require(hint != null)
        assertTrue("does not name what was typed: $hint", hint.contains("食べます"))
        assertTrue("does not name the needed form: $hint", hint.contains("食べられます"))
        assertTrue("does not state the potential rule: $hint", hint.contains("can"))
        assertTrue("does not name the grammar point: $hint", hint.contains("potential"))
    }

    @Test
    fun `a typed answer derives from the focus when there is no option to tag`() {
        val spanish = ErrorHint.forTypedAnswer("hablamos", "hablaron", "es.preterito.regular")
        require(spanish != null)
        assertTrue("no preterite rule: $spanish", spanish.contains("past-tense ending"))
        assertTrue("no correct form: $spanish", spanish.contains("“hablaron”"))

        val counter = ErrorHint.forTypedAnswer("三", "三人", "ja.counter_people")
        require(counter != null)
        assertTrue("no counter rule: $counter", counter.contains("人数"))
    }

    @Test
    fun `a typed answer with nothing to say still states the required form`() {
        val hint = ErrorHint.forTypedAnswer("perro", "gato", null)
        require(hint != null)
        assertTrue("does not name what was typed: $hint", hint.contains("perro"))
        assertTrue("does not name the needed form: $hint", hint.contains("“gato”"))
        assertTrue("invents a grammar point: $hint", !hint.contains("For "))
    }

    @Test
    fun `a typed answer with no correct form to name produces no hint`() {
        // Nothing to say beats saying nothing useful; the caller keeps the rule.
        assertNull(ErrorHint.forTypedAnswer("dog", "", "es.preterito.regular"))
        assertNull(ErrorHint.forTypedAnswer("dog", null, "es.preterito.regular"))
    }
}

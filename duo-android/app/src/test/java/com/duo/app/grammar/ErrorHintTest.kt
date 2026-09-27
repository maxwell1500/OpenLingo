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

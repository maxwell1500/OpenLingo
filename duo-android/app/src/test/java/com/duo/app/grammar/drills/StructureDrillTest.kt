package com.duo.app.grammar.drills

import com.duo.app.data.local.curriculum.AdvancedCurriculumData
import com.duo.app.data.local.curriculum.B1CurriculumData
import com.duo.app.data.local.curriculum.ExpandedCurriculumData
import com.duo.app.data.local.curriculum.UnitPayload
import com.duo.app.data.local.entities.ChallengeEntity
import com.duo.app.data.local.entities.ChallengeOptionEntity
import com.duo.app.data.local.models.ChallengeType
import com.duo.app.data.repository.ChallengeWithOptions
import com.duo.app.grammar.AnswerGrader
import com.duo.app.grammar.ErrorHint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * JVM tests over the generated drills.
 *
 * The corpus is handed to the generator as payloads, never read through Room, so these
 * run in the plain unit-test source set with no device, no database and no emulator.
 */
class StructureDrillTest {

    private val allPayloads: List<UnitPayload> =
        ExpandedCurriculumData.spanishExpandedUnits +
            ExpandedCurriculumData.japaneseExpandedUnits +
            AdvancedCurriculumData.spanishAdvancedUnits +
            AdvancedCurriculumData.japaneseAdvancedUnits +
            B1CurriculumData.spanishA2Units +
            B1CurriculumData.spanishB1Units +
            B1CurriculumData.japaneseN4Units

    /** The shipped corpus, shaped exactly as the repository hands it to the generator. */
    private val corpus: List<ChallengeWithOptions> = allPayloads.flatMap { payload ->
        val optionsByChallenge = payload.options.groupBy { it.challengeId }
        payload.challenges.map { challenge ->
            ChallengeWithOptions(
                challenge = challenge,
                options = optionsByChallenge[challenge.id].orEmpty(),
                isCompleted = false,
            )
        }
    }

    private fun paradigms() = StructureDrill.paradigms(corpus)

    private fun paradigm(focus: String) =
        paradigms().firstOrNull { it.focus == focus }
            ?: error("no paradigm extracted for '$focus'; got ${paradigms().map { it.focus }}")

    private fun formsOf(focus: String) = paradigm(focus).entries.map { it.form }

    private fun challenge(
        id: Int = 1,
        question: String,
        focus: String?,
        type: ChallengeType = ChallengeType.CONJUGATE,
        accepted: String? = null,
        rule: String? = null,
        options: List<ChallengeOptionEntity> = emptyList(),
        heldOut: Boolean = false,
    ) = ChallengeWithOptions(
        challenge = ChallengeEntity(
            id = id,
            lessonId = 1,
            type = type,
            question = question,
            orderIndex = 0,
            grammaticalFocus = focus,
            ruleText = rule,
            acceptedAnswers = accepted,
            heldOut = heldOut,
        ),
        options = options,
        isCompleted = false,
    )

    private fun option(
        id: Int,
        challengeId: Int = 1,
        text: String,
        correct: Boolean = false,
        tag: String? = null,
    ) = ChallengeOptionEntity(
        id = id,
        challengeId = challengeId,
        text = text,
        correct = correct,
        errorTag = tag,
    )

    /**
     * A paradigm that ships in the corpus is extracted with the forms, labels and
     * near-misses the curriculum authored — nothing invented, nothing dropped.
     *
     * Catches a generator that stops reading `acceptedAnswers` (the unaccented `vivia`
     * would vanish), that loses the lemma the prompt names, or that mangles a Japanese
     * option's romaji.
     */
    @Test
    fun `shipped paradigms are extracted with the forms the curriculum teaches`() {
        assertEquals(
            listOf("hablamos", "viajamos", "llegaron", "compró", "hablaron"),
            formsOf("es.preterito.regular"),
        )

        val preterite = paradigm("es.preterito.regular")
        assertEquals("PRETERITO REGULAR", preterite.label)
        assertEquals(listOf("hablar", "llegar"), preterite.lemmas)
        assertEquals(listOf("hablamos", "hablaron"), preterite.entriesFor("hablar").map { it.form })

        // A blanked sentence contributes a row keyed by the sentence, and the accepted
        // spelling the challenge authored comes along with it.
        val viajamos = preterite.entries.first { it.form == "viajamos" }
        assertEquals("Ayer nosotros ___ a Sevilla.", viajamos.cue)
        assertEquals(listOf("viajamos"), viajamos.accepted)

        // Two challenges teach `vivía`; the drill shows one row that carries both
        // accepted spellings and the union of their near-misses.
        val vivia = paradigm("es.imperfecto").entries.first { it.form == "vivía" }
        assertEquals("vivir", vivia.lemma)
        assertEquals("yo", vivia.slot)
        assertEquals(listOf("vivía", "vivia"), vivia.accepted)
        assertEquals(listOf("viví", "vivimos", "viven", "vivo"), vivia.distractors.map { it.form })

        val ser = paradigm("es.ser_present")
        assertEquals("SER PRESENT", ser.label)
        val soy = ser.entries.single()
        assertEquals("soy", soy.form)
        assertEquals("ser", soy.lemma)
        assertEquals(
            listOf(ParadigmDistractor("son", "WRONG_PERSON"), ParadigmDistractor("eres", "WRONG_PERSON")),
            soy.distractors.take(2),
        )

        // Japanese rows carry the authored romaji and the bare particle slot.
        val hanasemasu = paradigm("ja.potential").entries.first { it.form == "話せます" }
        assertEquals("話す", hanasemasu.lemma)
        assertEquals("hanasemasu", hanasemasu.romaji)
        assertEquals(listOf("話せます", "はなせます"), hanasemasu.accepted)
        assertEquals("ください", paradigm("ja.request_polite").entries.first { it.form == "待って" }.slot)
    }

    /**
     * A paradigm the corpus has never heard of reaches the drill with no drill code.
     *
     * This is the feature's whole claim. `es.futuro.regular` appears nowhere in the
     * source; the only thing that makes it a drill is that the challenges carry a focus
     * slug and the prompt shapes the curriculum already uses. Catches any drift that
     * quietly keys the generator on a known list of focuses or a per-language branch.
     */
    @Test
    fun `a paradigm added to the data reaches the drill without any drill code`() {
        val fixture = listOf(
            challenge(
                id = 9001,
                question = "Which future form of hablar goes with 'yo'?",
                focus = "es.futuro.regular",
                rule = "The future takes the whole infinitive plus endings.",
                options = listOf(
                    option(1, 9001, "hablaré", correct = true),
                    option(2, 9001, "hablarás", tag = "WRONG_PERSON"),
                    option(3, 9001, "hablé", tag = "WRONG_TENSE"),
                ),
            ),
            challenge(
                id = 9002,
                question = "Which future form of hablar goes with 'nosotros'?",
                focus = "es.futuro.regular",
                options = listOf(
                    option(4, 9002, "hablaremos", correct = true),
                    option(5, 9002, "hablará", tag = "WRONG_PERSON"),
                ),
            ),
            challenge(
                id = 9003,
                question = "Mañana yo ___ a la oficina.",
                focus = "es.futuro.regular",
                type = ChallengeType.FILL_BLANK,
                accepted = "iré|ire",
                options = listOf(
                    option(6, 9003, "iré", correct = true),
                    option(7, 9003, "iba", tag = "WRONG_TENSE"),
                ),
            ),
        )

        val generated = StructureDrill.paradigms(fixture)
        assertEquals(listOf("es.futuro.regular"), generated.map { it.focus })

        val futuro = generated.single()
        assertEquals("FUTURO REGULAR", futuro.label)
        assertEquals(listOf("hablaré", "hablaremos", "iré"), futuro.entries.map { it.form })
        assertEquals(listOf("hablar"), futuro.lemmas)
        assertEquals("yo", futuro.entries.first { it.form == "hablaré" }.slot)
        assertEquals(listOf("iré", "ire"), futuro.entries.first { it.form == "iré" }.accepted)
        assertEquals(
            listOf(ParadigmDistractor("hablarás", "WRONG_PERSON"), ParadigmDistractor("hablé", "WRONG_TENSE")),
            futuro.entries.first { it.form == "hablaré" }.distractors,
        )

        // The same rows come back through the focus-addressed accessor, so a screen that
        // was handed a focus it has not seen before still renders instead of failing.
        assertEquals(3, StructureDrill.paradigm(fixture, "es.futuro.regular").entries.size)
    }

    /**
     * A focus with nothing drillable in it is an empty paradigm, not a crash and not a
     * blank screen with no explanation.
     *
     * Covers the three ways it happens in practice: a focus nobody has authored yet, a
     * focus whose material is all recognition or sentence assembly, and a focus whose
     * challenges carry no options at all.
     */
    @Test
    fun `a focus with no extractable paradigm is handled gracefully`() {
        val neverAuthored = StructureDrill.paradigm(corpus, "es.subjuntivo")
        assertTrue(neverAuthored.isEmpty)
        assertTrue(neverAuthored.entries.isEmpty())
        assertEquals("SUBJUNTIVO", neverAuthored.label)
        assertTrue(neverAuthored.lemmas.isEmpty())
        assertTrue(neverAuthored.entriesFor("hablar").isEmpty())
        assertTrue(paradigms().none { it.focus == "es.subjuntivo" })

        // ja.counter_classifier really does ship challenges; they are all recognition
        // items whose answer is a whole noun phrase, which is not a conjugation row.
        val classifiers = StructureDrill.paradigm(corpus, "ja.counter_classifier")
        assertTrue(
            "classifier challenges are not single forms, so they must not become rows",
            classifiers.isEmpty,
        )
        assertTrue(corpus.any { it.challenge.grammaticalFocus == "ja.counter_classifier" })

        // A focus-bearing challenge with no options at all and no prompt frame is skipped
        // rather than crashing on a missing correct answer.
        val optionless = listOf(
            challenge(id = 9100, question = "Which form of hablar goes with 'yo'?", focus = "es.optionless"),
        )
        assertTrue(StructureDrill.paradigms(optionless).isEmpty())
    }

    /**
     * The wrong forms the drill offers are the curriculum's own, carrying the
     * curriculum's own tags, and they describe themselves with the same feedback a
     * lesson would show for the same mistake.
     *
     * The seven strings are pinned independently by `CurriculumIntegrityTest` and owned
     * by `ErrorHint`; if the drill ever grew its own tag or its own wording the two
     * would drift and the learner would be told two different things about one error
     * depending on which surface they met it on.
     */
    @Test
    fun `generated distractors carry the curriculum's errorTag vocabulary`() {
        val allDistractors = paradigms().flatMap { paradigm -> paradigm.entries.flatMap { it.distractors } }
        assertTrue("the corpus should yield distractors to check", allDistractors.isNotEmpty())

        val outsideVocabulary = allDistractors.map { it.errorTag }.distinct().filterNot { it in ErrorHint.TAGS }
        assertEquals(emptyList<String>(), outsideVocabulary)

        // Every tag the drill carries can be turned into a diagnosis, so a wrong answer
        // in a drill explains itself instead of degrading to a bare "incorrect".
        val unexplained = allDistractors
            .filter { ErrorHint.forChoice(it.form, "answer", it.errorTag, "es.present_person") == null }
            .map { it.errorTag }
            .distinct()
        assertEquals(emptyList<String>(), unexplained)

        // Every tag the drill actually carries names a wrong *form* of the same word.
        // `WRONG_CLASSIFIER` (a different counter) and `UNRELATED` (a different word)
        // are not forms of anything, and a paradigm row that offered them would be
        // teaching vocabulary, not conjugation.
        assertEquals(
            setOf("WRONG_TENSE", "WRONG_PERSON", "WRONG_FORM", "WRONG_COPULA", "WRONG_REGISTER"),
            allDistractors.map { it.errorTag }.toSet(),
        )
        // A distractor is never the answer again, and never a spelling of it the learner
        // could not have typed differently on purpose.
        paradigms().forEach { paradigm ->
            paradigm.entries.forEach { entry ->
                val answer = AnswerGrader.normalize(entry.form)
                entry.distractors.forEach { distractor ->
                    assertFalse(
                        "'${distractor.form}' repeats '${entry.form}' in ${paradigm.focus}",
                        AnswerGrader.normalize(distractor.form) == answer,
                    )
                }
            }
        }

    }

    /**
     * The drill grades with the same comparison the lesson does.
     *
     * `AnswerGrader` is the only comparison in the app that forgives a missing accent,
     * folds a Japanese IME's full-width output, and — the part that would be easy to
     * break by adding a second grader — keeps the dakuten, so 吐いて and 剥いで stay
     * different words. `はいて` / `はいで` is that contrast.
     */
    @Test
    fun `drill grading reuses AnswerGrader rather than a second comparison`() {
        val dakuten = StructureDrill.paradigms(
            listOf(
                challenge(
                    id = 9200,
                    question = "Which te-form of 吐 goes with 'ください'?",
                    focus = "ja.test.te_form",
                    options = listOf(
                        option(1, 9200, "はいて", correct = true),
                        option(2, 9200, "はいで", tag = "WRONG_FORM"),
                    ),
                ),
            ),
        ).single().entries.single()

        assertTrue(dakuten.accepts("はいて"))
        assertFalse("はいで is 剥いで, a different word", dakuten.accepts("はいで"))
        assertFalse(dakuten.accepts("はいてた"))

        // Accent tolerance comes from the authored variants, not from a looser compare.
        val spanish = StructureDrill.paradigms(
            listOf(
                challenge(
                    id = 9300,
                    question = "Ayer yo ___ una carta.",
                    focus = "es.test.preterito",
                    type = ChallengeType.FILL_BLANK,
                    accepted = "escribí|escribi",
                    options = listOf(option(1, 9300, "escribí", correct = true)),
                ),
            ),
        ).single().entries.single()

        assertEquals(listOf("escribí", "escribi"), spanish.accepted)
        assertTrue(spanish.accepts("escribi"))
        assertTrue(spanish.accepts("  ESCRIBÍ  "))
        assertTrue("full-width Latin from an IME must still be understood", spanish.accepts("ｅｓｃｒｉｂí"))
        assertFalse(spanish.accepts("escribía"))
    }

    /**
     * A potential-form drill does not accept the plain form.
     *
     * The whole point of a conjugation drill is confusing one form of a verb for
     * another, and Japanese potential is the sharpest case: 話せます 'I can speak' and
     * 話します 'I speak' are a suffix apart. If the drill graded anything looser than
     * [AnswerGrader], or graded with a comparison of its own that folded the endings
     * together, the drill would mark the exact error it exists to catch as correct.
     *
     * The plain form is also authored as a `WRONG_FORM` distractor, so the drill can say
     * *why* it is wrong rather than only that it is.
     */
    @Test
    fun `a potential-form drill does not accept the plain form`() {
        val potential = paradigm("ja.potential")

        val hanasemasu = potential.entries.first { it.form == "話せます" }
        assertTrue(hanasemasu.accepts("話せます"))
        assertTrue("the authored reading of the potential is accepted", hanasemasu.accepts("はなせます"))
        assertEquals(
            listOf(ParadigmDistractor("話します", "WRONG_FORM"), ParadigmDistractor("話せました", "WRONG_TENSE")),
            hanasemasu.distractors.take(2),
        )

        assertFalse(
            "書けます is the potential, 書きます is the plain",
            potential.entries.first { it.form == "書けます" }.accepts("書きます"),
        )
        // 読めます is taught only by a held-out checkpoint item, so it is not in the
        // drill at all — see the held-out test — but its plain form would be rejected
        // the same way if it were.
        assertTrue(potential.entries.none { it.form == "読めます" })
    }

    /**
     * A held-out checkpoint item never surfaces in the drill.
     *
     * `escribí` is taught only by challenge 31002, which is held out. If the drill read
     * the whole corpus rather than the lesson path, the learner would meet a checkpoint
     * answer while browsing.
     */
    @Test
    fun `held-out checkpoint items stay out of the drill`() {
        val preterite = paradigm("es.preterito.regular")
        assertTrue(preterite.entries.none { it.challengeId == 31002 })
        assertFalse(formsOf("es.preterito.regular").contains("escribí"))
        assertTrue(corpus.any { it.challenge.id == 31002 && it.challenge.heldOut })

        // Even a held-out row that is otherwise perfectly drillable is refused.
        val fixture = listOf(
            challenge(
                id = 9400,
                question = "Which te-form of 帰 goes with 'ください'?",
                focus = "ja.test.heldout",
                heldOut = true,
                options = listOf(
                    option(1, 9400, "帰って", correct = true),
                    option(2, 9400, "帰る", tag = "WRONG_FORM"),
                ),
            ),
        )
        assertTrue(StructureDrill.paradigms(fixture).isEmpty())
    }

    /** A recognition item keeps its focus but never becomes a table row. */
    @Test
    fun `recognition and assembly challenges are not rows`() {
        assertTrue(
            "Assemble: 'I live in Madrid' teaches the noun Madrid, not a form",
            paradigms().none { paradigm -> paradigm.entries.any { it.form == "Madrid" } },
        )
        assertTrue(
            "a whole sentence is not a conjugation",
            paradigms().flatMap { it.entries }.none { it.form.contains(' ') },
        )
    }

    /**
     * A per-unit form count has to be built from a per-unit slice of the corpus,
     * never from one call over every unit.
     *
     * `paradigms` folds two rows for the same form of the same focus into one, and
     * that fold is right for a table: a form taught twice is one conjugation. Run
     * across two units at once it also folds a form that *each* unit teaches under
     * the same slug, and the survivor belongs to only one of them — so the other
     * unit's count silently lost a row. Found on device: the unit header read
     * "DRILLS · 5 forms" above a drill screen reading "6 forms across 3 patterns".
     */
    @Test
    fun `counting a unit's forms needs that unit's own slice, not a merged whole-corpus count`() {
        val sharedForm = "hablamos"
        val focus = "es.preterito.regular"
        // The same form, same focus, taught by a challenge in each of two units.
        fun inUnit(unitLessonId: Int, challengeId: Int) = ChallengeWithOptions(
            challenge = ChallengeEntity(
                id = challengeId,
                lessonId = unitLessonId,
                type = ChallengeType.FILL_BLANK,
                question = "Ayer nosotros ___ a Sevilla.",
                orderIndex = 0,
                grammaticalFocus = focus,
                acceptedAnswers = sharedForm,
            ),
            options = listOf(option(id = challengeId * 10, challengeId = challengeId, text = sharedForm, correct = true)),
            isCompleted = false,
        )
        val unitOne = listOf(inUnit(unitLessonId = 116, challengeId = 9001))
        val unitTwo = listOf(inUnit(unitLessonId = 118, challengeId = 9002))

        // Each unit on its own keeps its row - this is what the drill screen shows.
        assertEquals(1, StructureDrill.paradigms(unitOne).sumOf { it.entries.size })
        assertEquals(1, StructureDrill.paradigms(unitTwo).sumOf { it.entries.size })

        // One call over both collapses them into a single row, which is why a
        // chip counting that way cannot match the table it opens.
        assertEquals(1, StructureDrill.paradigms(unitOne + unitTwo).sumOf { it.entries.size })
    }

}

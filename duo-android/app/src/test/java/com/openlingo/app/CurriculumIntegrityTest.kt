package com.openlingo.app

import com.openlingo.app.data.local.character.KanaRepository
import com.openlingo.app.data.local.curriculum.AdvancedCurriculumData
import com.openlingo.app.data.local.curriculum.B1CurriculumData
import com.openlingo.app.data.local.curriculum.ExpandedCurriculumData
import com.openlingo.app.data.local.curriculum.JapaneseN4CurriculumData
import com.openlingo.app.data.local.curriculum.UnitPayload
import com.openlingo.app.data.local.entities.ChallengeEntity
import com.openlingo.app.data.local.models.ChallengeType
import com.openlingo.app.grammar.AnswerGrader
import com.openlingo.app.grammar.GrammarFocus
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pure-JVM integrity tests over the bundled curriculum payloads.
 *
 * These catch the class of bug that previously only surfaced on-device
 * (wrong IDs, dangling foreign keys, missing audio files) at build time.
 */
class CurriculumIntegrityTest {

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

    /**
     * Which errorTags are honest for each grammaticalFocus the corpus ships.
     *
     * A focus missing from this map fails the test below, so new content has to
     * declare the tags its distractors may carry instead of inheriting whatever
     * happens to fit. Several focuses deliberately allow more than one tag: a
     * verb paradigm can be wrong in person or in tense (es.preterito.regular),
     * and ja.potential can be answered with a plain form (WRONG_FORM) or with
     * the past potential (WRONG_TENSE), because the ます form really does
     * inflect for the past.
     */
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
    fun `all entity ids are globally unique`() {
        val unitIds = allPayloads.map { it.unit.id }
        val lessonIds = allPayloads.flatMap { it.lessons }.map { it.id }
        val challengeIds = allPayloads.flatMap { it.challenges }.map { it.id }
        val optionIds = allPayloads.flatMap { it.options }.map { it.id }

        assertEquals(unitIds.size, unitIds.toSet().size)
        assertEquals(lessonIds.size, lessonIds.toSet().size)
        assertEquals(challengeIds.size, challengeIds.toSet().size)
        assertEquals(optionIds.size, optionIds.toSet().size)
    }

    @Test
    fun `foreign keys resolve within the payload set`() {
        val unitIds = allPayloads.map { it.unit.id }.toSet()
        val lessonIds = allPayloads.flatMap { it.lessons }.map { it.id }.toSet()
        val challengeIds = allPayloads.flatMap { it.challenges }.map { it.id }.toSet()

        allPayloads.forEach { payload ->
            payload.lessons.forEach { lesson ->
                assertEquals(
                    "lesson ${lesson.id} must belong to its own unit ${payload.unit.id}",
                    payload.unit.id,
                    lesson.unitId,
                )
                assertTrue("unit ${lesson.unitId} must exist", lesson.unitId in unitIds)
            }
            payload.challenges.forEach { challenge ->
                assertTrue(
                    "challenge ${challenge.id} references missing lesson ${challenge.lessonId}",
                    challenge.lessonId in lessonIds,
                )
            }
            payload.options.forEach { option ->
                assertTrue(
                    "option ${option.id} references missing challenge ${option.challengeId}",
                    option.challengeId in challengeIds,
                )
            }
        }
    }

    @Test
    fun `order indexes are contiguous within each unit and lesson`() {
        allPayloads.forEach { payload ->
            val lessonOrder = payload.lessons.map { it.orderIndex }.sorted()
            assertEquals(
                "unit ${payload.unit.id} lessons must be 0-based contiguous",
                (0 until payload.lessons.size).toList(),
                lessonOrder,
            )
            payload.lessons.forEach { lesson ->
                val lessonChallenges = payload.challenges.filter { it.lessonId == lesson.id }
                val challengeOrder = lessonChallenges.map { it.orderIndex }.sorted()
                assertEquals(
                    "lesson ${lesson.id} challenges must be 0-based contiguous",
                    (0 until lessonChallenges.size).toList(),
                    challengeOrder,
                )
            }
        }
    }

    @Test
    fun `every challenge is answerable and choice challenges have distractors`() {
        val optionsByChallenge = allPayloads.flatMap { it.options }.groupBy { it.challengeId }
        allPayloads.flatMap { it.challenges }.forEach { challenge ->
            val options = optionsByChallenge[challenge.id].orEmpty()
            assertTrue("challenge ${challenge.id} has no options", options.isNotEmpty())
            assertTrue(
                "challenge ${challenge.id} has no correct option",
                options.any { it.correct },
            )
            if (challenge.type != ChallengeType.LISTEN) {
                assertTrue(
                    "${challenge.type.rawValue} challenge ${challenge.id} needs at least 2 options",
                    options.size >= 2,
                )
            }
        }
    }

    @Test
    fun `every referenced audio file exists in assets`() {
        val audioDir = File("src/main/assets/audio")
        val available = audioDir.walkTopDown()
            .filter { it.isFile }
            .map { it.name }
            .toSet()
        val referenced = allPayloads.flatMap { it.challenges }.mapNotNull { it.audioSrc } +
            allPayloads.flatMap { it.options }.mapNotNull { it.audioSrc }
        assertTrue("no audio referenced at all", referenced.isNotEmpty())
        referenced.forEach { src ->
            val file = src.substringAfterLast("/")
            assertTrue("missing audio asset for $src", file in available)
        }
    }

    /**
     * A `LISTEN` challenge with no clip is a dead exercise: the prompt says
     * "Tap what you hear" and the learner is given nothing to tap.
     *
     * The test above proves every clip that *is* referenced exists on disk,
     * which is the opposite direction — it passes silently over a `LISTEN`
     * challenge whose `audioSrc` was simply never filled in. This asserts the
     * missing half: the challenge must name a clip, and that clip must be one
     * the assets actually carry.
     */
    @Test
    fun `every LISTEN challenge carries a clip that exists in assets`() {
        val available = File("src/main/assets/audio")
            .walkTopDown()
            .filter { it.isFile }
            .map { it.name }
            .toSet()
        val dead = allPayloads.flatMap { it.challenges }
            .filter { it.type == ChallengeType.LISTEN }
            .mapNotNull { challenge ->
                val src = challenge.audioSrc
                when {
                    src.isNullOrBlank() ->
                        "LISTEN challenge ${challenge.id} has no audioSrc, so the learner is told to tap what they hear and hears nothing"
                    src.substringAfterLast("/") !in available ->
                        "LISTEN challenge ${challenge.id} points at $src, which is not in assets"
                    else -> null
                }
            }
        assertEquals(
            "LISTEN challenges with no playable clip: $dead",
            emptyList<String>(),
            dead,
        )
    }

    @Test
    fun `kana syllabaries are complete and unique`() {
        val hiragana = KanaRepository.hiraganaList
        val katakana = KanaRepository.katakanaList

        assertEquals(46, hiragana.size)
        assertEquals(46, katakana.size)
        assertEquals(46, hiragana.map { it.character }.toSet().size)
        assertEquals(46, katakana.map { it.character }.toSet().size)
        (hiragana + katakana).forEach {
            assertTrue("blank romaji for ${it.character}", it.romaji.isNotBlank())
            assertTrue("no strokes for ${it.character}", it.strokes.isNotEmpty())
        }
    }

    @Test
    fun `every challenge type in the corpus is a known ChallengeType`() {
        val unknown = allPayloads.flatMap { it.challenges }
            .map { it.type.rawValue }
            .distinct()
            .filter { raw ->
                try {
                    ChallengeType.fromRaw(raw)
                    false
                } catch (e: IllegalArgumentException) {
                    true
                }
            }
        assertEquals("challenge types not in the ChallengeType vocabulary: $unknown", emptyList<String>(), unknown)
    }

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
        val vocabulary = errorTagVocabulary
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

    /**
     * The in-context hint is written from these strings, so a tag that names the
     * wrong category teaches the learner something false - a learner who answers
     * 話します instead of 話せます has picked the wrong verb form, and must never be
     * told they got the tense wrong when Japanese has no tense inflection. This is
     * the guard against that class of defect: every tag is checked against the
     * tags its own grammaticalFocus declares honest.
     */
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

    /**
     * `acceptedAnswers` is the key a typed `FILL_BLANK` is graded against, so
     * every entry must be a form this item's own `grammaticalFocus` licenses.
     *
     * A form the corpus teaches as the answer to a *different* focus is exactly
     * what the item's rule text is written to rule out. Challenge 30007 asked
     * for the 3rd person preterite ("Mi amigo ___ el billete ayer", focus
     * `es.preterito.regular`) and its rule text names `compra` as the present —
     * but the key also accepted `compro`, which challenge 1040 teaches as the
     * `es.present_person` answer, so the item graded a first person present as
     * the 3rd person preterite.
     */
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

    /**
     * A held-out item must ask for a form the rest of the corpus does not
     * already hand the learner. When a key entry is shared with another
     * `FILL_BLANK` whose own answer differs, the shared string belongs to that
     * other item's verb, and the held-out item stops testing anything: a
     * learner who learned the taught item alone still passes.
     *
     * Challenge 31011 (held out) teaches the te-form of 言う and its rule text
     * says the answer is 言って — yet the key also accepted `いって`, which
     * challenge 30024 already accepts as its answer, the te-form of the
     * unrelated verb 行く.
     */
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

    /**
     * `ことができます` is 「〜こと が できます」 and `こと` is the する→す
     * nominaliser, so whatever fills the slot in front of it has to be a verb
     * in its **plain** form. Two things follow, and the corpus broke both:
     *
     * 1. The slot needs a whole verb. Every Japanese verb ends in a kana — an
     *    ichidan verb in る, a godan verb in the u-row of its ます stem — and a
     *    する-verb keeps its する in writing, because こと is the nominaliser.
     *    Challenge 60025 asked 「車を___ことができます。」 and keyed 運転, so the
     *    learner composed 車を運転ことができます: a noun in a verb slot, and not
     *    the sentence the item's own clip speaks.
     * 2. The slot must not take a ます form. こと cannot nominalise a polite
     *    verb — 泳ぎますこと is not a word — so 泳ぎますことができます is not a
     *    sentence. Challenge 60024 keyed exactly that, while its own rule text
     *    derived 泳ぐことができます and called 泳ぐ a `WRONG_FORM`.
     *
     * Nothing else here could see either: the grader matches strings, so a key
     * or an answer that composes into nothing is one that passes every other
     * assertion in this file.
     *
     * Rule 1 is "ends in a kana that can end a verb" rather than "ends in
     * する", because a godan ます stem (書く, keyed by 30029) and an ichidan る
     * form (読める, keyed by 31013) fill the same slot and have to stay legal.
     * The bare する-verb stem is the one form that cannot end a verb. Both the
     * typed key and the correct option are checked, because a `FILL_BLANK`
     * grades against the key while a `CONJUGATE` is picked from the grid, and
     * 60025's two halves had to be moved together.
     */
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

    /**
     * The rule text is the only place a held-out item states which forms are
     * wrong, so it has to name every form the option set tags as an error, and
     * the answer key must stay clear of all of them. Together the two say the
     * key never contains a form the rule text calls incorrect.
     */
    @Test
    fun `held-out rule text names every wrong form and the answer key names none`() {
        val optionsByChallenge = allPayloads.flatMap { it.options }.groupBy { it.challengeId }
        val unexplained = mutableListOf<String>()
        val contradictions = mutableListOf<String>()
        allPayloads.flatMap { it.challenges }
            .filter { it.heldOut }
            .forEach { challenge ->
                val rule = challenge.ruleText.orEmpty()
                val wrong = optionsByChallenge[challenge.id].orEmpty()
                    .filterNot { it.correct }
                    .filter { it.errorTag != null }
                    .map { it.text }
                wrong.filterNot { it in rule }.forEach { text ->
                    unexplained += "challenge ${challenge.id} offers '$text' as a wrong form " +
                        "but its rule text never names it, so the learner is shown a form " +
                        "with no stated reason"
                }
                AnswerGrader.acceptedVariants(challenge.acceptedAnswers)
                    .filter { it in wrong }
                    .forEach { text ->
                        contradictions += "challenge ${challenge.id} accepts '$text' as an " +
                            "answer while its own option set tags that same form as an error"
                    }
            }
        assertEquals(
            "held-out wrong forms the rule text never explains: $unexplained",
            emptyList<String>(),
            unexplained,
        )
        assertEquals(
            "held-out answer keys containing a form the item tags as wrong: $contradictions",
            emptyList<String>(),
            contradictions,
        )
    }

    /**
     * Why one `FILL_BLANK`'s key is ungradeable, or null when it is complete.
     * The rule is the same for every typed item whatever pool it is seeded into,
     * so the two tests below differ only in which challenges they hand it.
     */
    private fun ungradeableKeyReason(challenge: ChallengeEntity): String? {
        val raw = challenge.acceptedAnswers
        val entries = raw?.split('|').orEmpty()
        return when {
            raw.isNullOrBlank() ->
                "challenge ${challenge.id} is a FILL_BLANK with no acceptedAnswers; " +
                    "nothing it can be graded against"
            entries.any { it.isBlank() } ->
                "challenge ${challenge.id} has a blank entry in '$raw'; AnswerGrader " +
                    "drops it, so the authored key and the graded key disagree"
            else -> null
        }
    }

    /**
     * A typed held-out item is ungradeable without a key: `AnswerGrader` matches
     * only against `acceptedAnswers`, so a missing or partly blank one leaves
     * the learner with no correct string to type. The blank-segment case matters
     * because the grader silently drops it, leaving the authored key and the
     * graded key quietly different.
     */
    @Test
    fun `every held-out fill blank carries a complete answer key`() {
        val malformed = allPayloads.flatMap { it.challenges }
            .filter { it.heldOut && it.type == ChallengeType.FILL_BLANK }
            .mapNotNull { ungradeableKeyReason(it) }
        assertEquals(
            "held-out fill blanks with a missing or partly blank answer key: $malformed",
            emptyList<String>(),
            malformed,
        )
    }

    /**
     * The lesson path is graded the same way, and the held-out test above only
     * reaches the checkpoint pool, so a key dropped from a taught item ships
     * uncaught. This is not hypothetical: challenge 60012 (`ja.negative`, lesson
     * 401) lost `acceptedAnswers` and its accepted set collapsed to the correct
     * option's text alone, so a learner who typed `ふりません` — the kana spelling
     * the bundled clip `ashita_ame_ga_furimasen.ogg` actually speaks — was marked
     * wrong, while every sibling FILL_BLANK accepts both scripts.
     *
     * The authored answer and the graded answer are separate things: the grader
     * unions `acceptedVariants(acceptedAnswers)` with the correct option's text,
     * so a null key does not make the item unsolvable, it silently narrows what
     * counts as correct. That is why this is a contract on the key rather than on
     * the options.
     */
    @Test
    fun `every fill blank on the lesson path carries a complete answer key`() {
        val malformed = allPayloads.flatMap { it.challenges }
            .filterNot { it.heldOut }
            .filter { it.type == ChallengeType.FILL_BLANK }
            .mapNotNull { ungradeableKeyReason(it) }
        assertEquals(
            "lesson-path fill blanks with a missing or partly blank answer key: $malformed",
            emptyList<String>(),
            malformed,
        )
    }

    /**
     * SPEC line 418 requires the pool to be real curriculum-quality content. A
     * held-out row is what the learner is graded on, so an empty question, a
     * missing focus or a missing rule is a placeholder shipped into the one
     * mechanic whose whole job is to test transfer.
     */
    @Test
    fun `held-out items carry the content they are graded on`() {
        val placeholders = allPayloads.flatMap { it.challenges }
            .filter { it.heldOut }
            .mapNotNull { challenge ->
                val missing = listOfNotNull(
                    "question".takeIf { challenge.question.isNullOrBlank() },
                    "grammaticalFocus".takeIf { challenge.grammaticalFocus.isNullOrBlank() },
                    "ruleText".takeIf { challenge.ruleText.isNullOrBlank() },
                )
                missing.takeIf { it.isNotEmpty() }?.let {
                    "challenge ${challenge.id} is held-out with no ${it.joinToString(" and ")}"
                }
            }
        assertEquals(
            "held-out items missing the content they are graded on: $placeholders",
            emptyList<String>(),
            placeholders,
        )
    }

    /**
     * `LessonDao` filters `heldOut = 0` out of `getChallengesForLesson` and
     * `getChallengesForUnits`, and `getHeldOutChallengesForUnits` is the only
     * query that selects them, scoped to the unit ids `getUnitIdsForLevel`
     * returns. A held-out item outside those units is therefore filtered out of
     * every lesson and selected by no checkpoint: content nothing can reach.
     * Symmetrically, a lesson that held nothing but held-out items would vanish
     * from the path entirely and stop being completable.
     */
    @Test
    fun `held-out items are reachable by a checkpoint and leave their lesson intact`() {
        val checkpointUnits = (10..33).toSet() + setOf(40, 41, 42, 43)
        val unitOfLesson = allPayloads
            .flatMap { payload -> payload.lessons.map { it.id to payload.unit.id } }
            .toMap()
        val heldOut = allPayloads.flatMap { it.challenges }.filter { it.heldOut }

        val unreachable = heldOut
            .filter { unitOfLesson[it.lessonId] !in checkpointUnits }
            .map { challenge ->
                "challenge ${challenge.id} is held-out in unit ${unitOfLesson[challenge.lessonId]}, " +
                    "which no checkpoint level selects; it is filtered out of every lesson and " +
                    "reachable by no checkpoint"
            }
        assertEquals(
            "held-out items no checkpoint can reach: $unreachable",
            emptyList<String>(),
            unreachable,
        )

        val emptied = heldOut.map { it.lessonId }.toSet().filter { lessonId ->
            allPayloads.flatMap { it.challenges }.none { it.lessonId == lessonId && !it.heldOut }
        }
        assertEquals(
            "lessons left with no taught challenge once their held-out items are filtered out: $emptied",
            emptyList<Int>(),
            emptied.sorted(),
        )
    }

    /**
     * `startCheckpoint` returns early only when both pools are empty, so an
     * empty held-out pool does not fail loudly — it silently serves taught
     * items and the checkpoint stops testing anything unseen. The UI offers
     * A1/A2/B1 for Spanish and N5/N4 for Japanese, and those levels map to
     * units 10-17, 18/19, 30-33 and 20-27, 28/29/40-43, so every one of those pools
     * has to be populated.
     */
    @Test
    fun `every checkpoint level's held-out pool is populated`() {
        val unitOfLesson = allPayloads
            .flatMap { payload -> payload.lessons.map { it.id to payload.unit.id } }
            .toMap()
        val heldOutUnits = allPayloads.flatMap { it.challenges }
            .filter { it.heldOut }
            .mapNotNull { unitOfLesson[it.lessonId] }
            .toSet()

        val empty = mapOf(
            "A2" to listOf(18, 19),
            "B1" to listOf(30, 31),
            "N4" to listOf(28, 29, 40, 41),
        )
            .filterValues { units -> units.none { it in heldOutUnits } }
            .keys
        assertEquals(
            "checkpoint levels whose units hold no held-out items, so the checkpoint falls " +
                "back to taught ones: $empty",
            emptySet<String>(),
            empty,
        )
    }

    /**
     * Every grammar point the corpus teaches has to have a name the learner can be
     * shown, in the same way every challenge has to declare the errorTags its
     * distractors may carry. Without this the fallback is not an error state: a new
     * focus simply renders with no heading, and the rule card reads `RULE` over a
     * paragraph the learner cannot place.
     */
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

    /**
     * Every Japanese reading that has been audited against the audio it ships
     * with, and what that audio actually pronounces.
     *
     * `romaji` is what the learner reads under the word, so a wrong one is a
     * contradiction the app states in its own text.  Three classes are here:
     *
     *  * an extra or missing mora -- `見られる` was declared `mirerareru`
     *    (みれるられる) for a 4-mora word, `待たせる` `matasaseru` carries a さ
     *    the word does not, and `sennen`, `ryoushuusho` and `teikyuubi` dropped
     *    morae the same way;
     *  * a transcription slip -- `shigato` for 仕事 (しごと), `okomi` for
     *    お読み (oyomi), `gomiru` for ご覧 (goran);
     *  * a romaji carried over from a sibling challenge -- `hirougohan`
     *    (昼ご飯) left on options whose own text says お昼, and `ie ni te
     *    kudasai` on options whose own text says 家にいてください.  These
     *    reached the correct answer AND its distractors, which is why each
     *    family has four rows here and not one.
     *
     * The text is asserted alongside the reading so an id cannot be re-pointed
     * at different content to make a row pass.
     */
    private val auditedJapaneseReadings = listOf(
        Triple(6300086, "見られる", "mirareru"),
        Triple(6300121, "見られる", "mirareru"),
        Triple(6300285, "見られる", "mirareru"),
        Triple(6300584, "待たせる", "mataseru"),
        Triple(6300586, "飲ませる", "nomaseru"),
        Triple(6400001, "雨が降ったら、家にいてください。", "ame ga futtara, ie ni ite kudasai"),
        Triple(6400002, "雨が降ると、家にいてください。", "ame ga furuto, ie ni ite kudasai"),
        Triple(6400003, "雨が降るたら、家にいてください。", "ame ga furutara, ie ni ite kudasai"),
        Triple(6400004, "雨が降りましたら、家にいてください。", "ame ga furimashitara, ie ni ite kudasai"),
        Triple(6400013, "仕事が終わったら、帰りましょう。", "shigoto ga owattara, kaerimashou"),
        Triple(6400014, "仕事が変わるなら、帰りましょう。", "shigoto ga kawarunara, kaerimashou"),
        Triple(6400015, "仕事が終われば、帰りましょう。", "shigoto ga owareba, kaerimashou"),
        Triple(6400016, "仕事が終わって、帰りましょう。", "shigoto ga owatte, kaerimashou"),
        Triple(6400065, "社長がお昼を召し上がります。", "shachou ga ohiru o meshimasu"),
        Triple(6400066, "お昼をいただきます。", "ohiru o itadakimasu"),
        Triple(6400067, "社長がお昼を食べます。", "shachou ga ohiru o tabemasu"),
        Triple(6400068, "社長がお昼を食べなさいます。", "shachou ga ohiru o tabenasaimasu"),
        Triple(6400069, "部長が契約書を お読みになります。", "bucho ga keiyakusho o oyomi ni narimasu"),
        Triple(6400071, "部長が契約書をお読みます。", "bucho ga keiyakusho o oyomimasu"),
        Triple(6400076, "ご覧になる", "goran ni naru"),
        Triple(6400109, "私はその映画を見られます。", "watashi wa sono eiga o miraremasu"),
        Triple(6500061, "定休日", "teikyuubi"),
        Triple(6500072, "現金で払わなければなりません。", "genkin de harawanakereba narimasen"),
        Triple(6500074, "現金で払わなくてもいいです。", "genkin de harawanakutemo ii desu"),
        Triple(6500075, "現金で払わなかった。", "genkin de harawanakatta"),
        Triple(6500113, "領収書", "ryoushuusho"),
        Triple(6500153, "領収書", "ryoushuusho"),
        Triple(6500172, "領収書", "ryoushuusho"),
        Triple(7500013, "停留所", "teiryuujyo"),
        Triple(7500053, "停留所", "teiryuujyo"),
        Triple(7500073, "停留所", "teiryuujyo"),
        Triple(7500220, "今日は風が強いです。", "kyou wa kaze ga tsuyoi desu"),
        Triple(7500221, "今日は風が強くです。", "kyou wa kaze ga tsuyoku desu"),
        Triple(7500222, "今日は風が強いでした。", "kyou wa kaze ga tsuyoi deshita"),
        Triple(20102, "千円", "Sennen"),
        Triple(20104, "千円", "Sennen"),
    )

    @Test
    fun `a japanese reading that the audio pronounces differently is declared as the audio says it`() {
        val optionsById = allPayloads.flatMap { it.options }.associateBy { it.id }
        val drifted = auditedJapaneseReadings.mapNotNull { (id, text, romaji) ->
            val option = optionsById[id] ?: return@mapNotNull "option $id is gone"
            if (option.text == text && option.romaji == romaji) {
                null
            } else {
                "option $id reads ${option.text} as '${option.romaji}', " +
                    "expected $text as '$romaji'"
            }
        }
        assertEquals(
            "declared readings that contradict the shipped audio",
            emptyList<String>(),
            drifted,
        )
    }

    /**
     * The same contract seen from the audio side: the option that plays a clip
     * is the option whose reading the learner is shown, so the two cannot be
     * corrected independently and drift apart again.
     */
    @Test
    fun `the option that plays an audited clip declares the reading that clip pronounces`() {
        val audited = auditedJapaneseReadings
            .mapNotNull { (id, _, romaji) ->
                allPayloads.flatMap { it.options }
                    .firstOrNull { it.id == id && it.audioSrc != null }
                    ?.let { it.audioSrc!! to romaji }
            }
            .toMap()
        assertTrue("no audited clip-carrying option was found", audited.isNotEmpty())

        val optionsByAudio = allPayloads.flatMap { it.options }
            .filter { it.audioSrc != null }
            .groupBy { it.audioSrc }
        val mismatched = audited.filter { (audio, romaji) ->
            val declared = optionsByAudio[audio].orEmpty().mapNotNull { it.romaji }.distinct()
            declared.isNotEmpty() && romaji !in declared
        }
        assertEquals(
            "clips whose declaring option no longer states the audited reading",
            emptyMap<String, String>(),
            mismatched.toMap(),
        )
    }

    /**
     * Japanese writes reduplication with the iteration mark 々, never by
     * doubling the character itself — 人々, 日々, 様々, 時々, 段々.  So a kanji
     * sitting immediately next to a copy of itself is a typing slip, and a
     * learner both reads and is graded against it.
     *
     * This is the check that catches the class rather than one instance: it
     * needed no allowlist, because 々 is a separate character and so never
     * reads as a repeated kanji.  An earlier clip census reported 人人 in
     * `辞書を使わない人人は少ないです`, which turned out to be an artefact of
     * the census's own fill-in-the-blank reconstruction rather than a real
     * string — but had it reached the Kotlin, this is what would have caught
     * it.  The corpus currently has no instance, so the assertion is empty
     * and its value is in holding the line for content added later.
     *
     * Scans every field a learner is shown or graded against, not just
     * `romaji`: the doubling is in the Japanese itself.
     */
    @Test
    fun `no japanese text repeats a kanji instead of using the iteration mark`() {
        fun kanjiRuns(field: String, where: String): List<String> {
            val hits = mutableListOf<String>()
            field.forEachIndexed { i, c ->
                if (c.code in 0x4E00..0x9FFF && i + 1 < field.length &&
                    field[i + 1] == c
                ) {
                    hits += "$where: $field (doubled ${c} at $i)"
                }
            }
            return hits
        }

        val doubled = allPayloads.flatMap { payload ->
            val fromOptions = payload.options.flatMap {
                kanjiRuns(it.text, "option ${it.id}")
            }
            val fromChallenges = payload.challenges.flatMap { challenge ->
                val at = "challenge ${challenge.id}"
                kanjiRuns(challenge.question, "$at question") +
                    (challenge.ruleText?.let { kanjiRuns(it, "$at ruleText") }
                        ?: emptyList()) +
                    // acceptedAnswers is a |-separated list of ALTERNATIVES, so
                    // only each alternative is scanned: a repeat across the
                    // separator is two spellings, not one word typed twice.
                    (challenge.acceptedAnswers
                        ?.split("|")
                        ?.flatMap { kanjiRuns(it, "$at answer") }
                        ?: emptyList())
            }
            fromOptions + fromChallenges
        }
        assertEquals(
            "kanji doubled instead of written with 々, so a learner reads the " +
                "typo and is graded against it",
            emptyList<String>(),
            doubled,
        )
    }

    /**
     * Where a rule card states that more than one form is correct, every form
     * it names as correct must be present in that challenge's correct options.
     *
     * The corpus convention for naming multiple correct forms is an explicit
     * marker — "both are correct", "all correct", or "both are accepted" —
     * with the forms listed before it, separated by " or " or "/". This test
     * finds every rule card that uses one of those markers, extracts the forms
     * it names, and asserts each one appears among the challenge's correct
     * options.
     *
     * This is a contract test, not a corpus restatement: it does not care
     * WHICH forms are correct, only that the rule card and the answer key
     * agree. A rule card that promises two answers and an option grid that
     * delivers one is a defect this catches regardless of what the forms are.
     */
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

    /**
     * A challenge that offers more than one option with `correct = true` must
     * not carry a challenge-level `audioSrc`.
     *
     * When a challenge has multiple correct options there is no single target
     * form for a clip to speak. A CONJUGATE clip names one form as the answer;
     * if the learner is graded correct for producing a different form, the audio
     * actively misleads about which to pick. The audio must live on the options
     * (where each form can carry its own clip) or not at all.
     *
     * WORD_BANK is excluded: its challenge-level clip speaks the full assembled
     * sentence the learner is building, not a single target tile. Multiple
     * correct tiles are all part of that sentence, so the clip models the target
     * without naming one tile as the answer.
     *
     * This test is corpus-independent: it does not care WHICH forms are correct,
     * only that a multi-answer challenge does not carry a clip that names one.
     */
    @Test
    fun `a challenge with multiple correct options carries no challenge-level audio`() {
        val optionsByChallenge = allPayloads.flatMap { it.options }.groupBy { it.challengeId }
        val failures = mutableListOf<String>()

        allPayloads.flatMap { it.challenges }.forEach { challenge ->
            if (challenge.type == ChallengeType.WORD_BANK) return@forEach
            if (challenge.audioSrc == null) return@forEach
            val correctCount = optionsByChallenge[challenge.id].orEmpty().count { it.correct }
            if (correctCount > 1) {
                failures += "challenge ${challenge.id} (${challenge.type.rawValue}) has $correctCount correct options " +
                    "but carries audioSrc '${challenge.audioSrc}', which speaks one form as the answer"
            }
        }

        assertEquals(
            "multi-answer challenges carrying challenge-level audio: $failures",
            emptyList<String>(),
            failures,
        )
    }
}

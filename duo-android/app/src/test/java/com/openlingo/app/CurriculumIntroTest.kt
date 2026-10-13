package com.openlingo.app

import com.openlingo.app.data.local.curriculum.UnitPayload
import com.openlingo.app.data.local.models.ChallengeType
import java.text.Normalizer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * INTRO integrity: a lesson teaches a term before grading on it, and no
 * distractor is built from vocabulary the course never teaches.
 */
class CurriculumIntroTest {

    private val allPayloads: List<UnitPayload> = Corpus.allPayloads

    // =====================================================================
    // SPEC items 5.1 and 5.2 - a lesson teaches a term before it grades on
    // it, and a wrong answer is never a word the learner has never met.
    // =====================================================================

    private val vocabularyChallengeTypes = setOf(ChallengeType.SELECT, ChallengeType.WORD_BANK)

    private val japaneseRun = Regex("[぀-ヿ一-鿿]+")

    private val latinWord = Regex("[\\p{L}\\p{N}]+")

    private fun latinize(text: String): String =
        Normalizer.normalize(text.lowercase(), Normalizer.Form.NFD)
            .replace("\\p{M}+".toRegex(), "")

    private val japaneseParticles =
        listOf("から", "まで", "が", "を", "は", "に", "で", "と", "も", "の", "へ", "や")

    private fun stripParticles(text: String): String {
        var stem = text
        var stripped = true
        while (stripped) {
            stripped = false
            for (particle in japaneseParticles) {
                if (stem.length > particle.length && stem.endsWith(particle)) {
                    stem = stem.dropLast(particle.length)
                    stripped = true
                }
            }
        }
        return stem
    }

    private fun taughtWords(texts: Iterable<String>): Set<String> =
        texts.flatMap { text ->
            latinWord.findAll(latinize(text)).map { it.value } +
                japaneseRun.findAll(text).map { stripParticles(it.value) }
        }.filter { it.isNotBlank() }.toSet()

    private fun wordsOf(text: String): List<String> =
        if (japaneseRun.containsMatchIn(text)) {
            japaneseRun.findAll(text).map { stripParticles(it.value) }.toList()
        } else {
            latinWord.findAll(latinize(text)).map { it.value }.toList()
        }

    private val closedClassWords = setOf(
        "a", "al", "de", "del", "en", "con", "por", "para", "sin", "sobre", "hasta",
        "desde", "entre", "hacia", "y", "e", "o",
        "el", "la", "los", "las", "un", "una", "unos", "unas",
        "yo", "tu", "ella", "nosotros", "vosotros", "ellos", "ellas",
        "me", "te", "se", "lo", "le", "les", "nos",
    )

    private fun isTaught(text: String, taught: Set<String>): Boolean {
        val words = wordsOf(text)
        return words.isEmpty() || words.all { it in taught || it in closedClassWords }
    }

    @Test
    fun `all INTRO challenges have valid non-blank text and audio`() {
        val optionsByChallenge = allPayloads.flatMap { it.options }.groupBy { it.challengeId }
        val available = AudioAssets.available

        val blanks = mutableListOf<String>()
        val brokenAudio = mutableListOf<String>()
        allPayloads.flatMap { it.challenges }
            .filter { it.type == ChallengeType.INTRO }
            .forEach { challenge ->
                val term = optionsByChallenge[challenge.id].orEmpty().firstOrNull()?.text
                if (challenge.question.isNullOrBlank() && term.isNullOrBlank()) {
                    blanks += "INTRO challenge ${challenge.id} has neither a question nor a term to show"
                }
                val src = challenge.audioSrc
                if (!src.isNullOrBlank() && src.substringAfterLast("/") !in available) {
                    brokenAudio += "INTRO challenge ${challenge.id} points at $src, which is not in assets"
                }
            }

        assertEquals(
            "INTRO cards with nothing to show: $blanks",
            emptyList<String>(),
            blanks,
        )
        assertEquals(
            "INTRO cards pointing at audio that is not in assets: $brokenAudio",
            emptyList<String>(),
            brokenAudio,
        )
    }

    @Test
    fun `lessons with INTRO cards introduce terms before graded testing`() {
        val misordered = mutableListOf<String>()
        val untaught = mutableListOf<String>()
        var lessonsWithIntros = 0

        allPayloads.forEach { payload ->
            val optionsByChallenge = payload.options.groupBy { it.challengeId }
            payload.lessons.forEach { lesson ->
                val challenges = payload.challenges.filter { it.lessonId == lesson.id }
                val intros = challenges.filter { it.type == ChallengeType.INTRO }
                if (intros.isEmpty()) return@forEach
                lessonsWithIntros++

                val firstGraded = challenges
                    .filterNot { it.type == ChallengeType.INTRO }
                    .minOfOrNull { it.orderIndex }
                if (firstGraded != null) {
                    intros
                        .filter { it.orderIndex > firstGraded }
                        .forEach { intro ->
                            misordered += "INTRO challenge ${intro.id} sits at orderIndex " +
                                "${intro.orderIndex}, after the first graded challenge in lesson " +
                                "${lesson.id} (orderIndex $firstGraded), so the lesson grades a " +
                                "term before the card that introduces it"
                        }
                }

                val cardTerms = intros.flatMap { card ->
                    optionsByChallenge[card.id].orEmpty().map { it.text }
                } + intros.map { it.question }
                val introduced = taughtWords(cardTerms)

                challenges
                    .filter { it.type in vocabularyChallengeTypes }
                    .forEach { challenge ->
                        val taught = introduced + taughtWords(listOfNotNull(challenge.ruleText))
                        optionsByChallenge[challenge.id].orEmpty()
                            .filter { it.correct }
                            .filterNot { isTaught(it.text, taught) }
                            .forEach { option ->
                                untaught += "challenge ${challenge.id} ('${challenge.question}') " +
                                    "grades on '${option.text}', which lesson ${lesson.id} never " +
                                    "shows on an INTRO card or on its own rule card; the cards " +
                                    "teach ${cardTerms.distinct()}"
                            }
                    }
            }
        }

        assertTrue(
            "no lesson in the corpus teaches a term on an INTRO card, so this " +
                "test proves nothing",
            lessonsWithIntros > 0,
        )
        assertEquals(
            "INTRO cards that do not come first in their lesson: $misordered",
            emptyList<String>(),
            misordered,
        )
        assertEquals(
            "graded items that test a term the lesson never introduced: $untaught",
            emptyList<String>(),
            untaught,
        )
    }

    @Test
    fun `rebuilt lessons contain no untaught vocabulary distractors and defend against known regressions`() {
        val optionsByChallenge = allPayloads.flatMap { it.options }.groupBy { it.challengeId }
        val courseOfLesson = allPayloads
            .flatMap { payload -> payload.lessons.map { it.id to payload.unit.courseId } }
            .toMap()

        val coldTerms = optionsByChallenge[1026].orEmpty()
            .filterNot { it.correct }
            .filter { latinize(it.text).contains("ingles") }
            .map { it.id }
        assertEquals(
            "challenge 1026 distractors that reintroduce the untaught cold term " +
                "inglés, which lesson 108 never teaches",
            emptyList<Int>(),
            coldTerms,
        )

        val taughtByCourse = mutableMapOf<Int, Set<String>>()
        allPayloads.forEach { payload ->
            val taught = taughtWords(
                payload.options.filter { it.correct }.map { it.text } +
                    payload.challenges.mapNotNull { it.ruleText } +
                    payload.challenges
                        .filter { it.type == ChallengeType.INTRO }
                        .map { it.question },
            )
            val course = payload.unit.courseId
            taughtByCourse[course] = taughtByCourse.getOrElse(course) { emptySet() } + taught
        }

        val untaught = mutableListOf<String>()
        var lessonsChecked = 0
        allPayloads.forEach { payload ->
            val optionsInUnit = payload.options.groupBy { it.challengeId }
            payload.lessons.forEach { lesson ->
                val challenges = payload.challenges.filter { it.lessonId == lesson.id }
                if (challenges.none { it.type == ChallengeType.INTRO }) return@forEach
                lessonsChecked++
                val course = courseOfLesson.getValue(lesson.id)
                val taught = taughtByCourse.getValue(course)
                challenges
                    .filter { it.type in vocabularyChallengeTypes }
                    .flatMap { challenge ->
                        optionsInUnit[challenge.id].orEmpty().map { challenge to it }
                    }
                    .filterNot { it.second.correct }
                    .filter { it.second.errorTag == null }
                    .filterNot { isTaught(it.second.text, taught) }
                    .forEach { (challenge, option) ->
                        untaught += "lesson ${lesson.id} offers '${option.text}' as a wrong " +
                            "answer on challenge ${challenge.id} ('${challenge.question}'); it " +
                            "carries no errorTag and none of its words is taught anywhere in " +
                            "course $course"
                    }
            }
        }

        assertTrue(
            "no lesson with INTRO cards was checked, so this test proves nothing",
            lessonsChecked > 0,
        )
        assertEquals(
            "wrong answers built from vocabulary the course never teaches: $untaught",
            emptyList<String>(),
            untaught,
        )
    }

}

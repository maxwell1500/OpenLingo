package com.openlingo.app

import com.openlingo.app.data.local.curriculum.UnitPayload
import com.openlingo.app.data.local.entities.ChallengeEntity
import com.openlingo.app.data.local.models.ChallengeType
import com.openlingo.app.grammar.AnswerGrader
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Held-out item integrity: rule text and answer keys agree, held-out items are
 * reachable by a checkpoint, and every level's held-out pool is populated.
 */
class CurriculumHeldOutTest {

    private val allPayloads: List<UnitPayload> = Corpus.allPayloads

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

}

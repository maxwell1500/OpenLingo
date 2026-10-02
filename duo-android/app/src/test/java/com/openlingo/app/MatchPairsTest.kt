package com.openlingo.app

import com.openlingo.app.data.local.curriculum.AdvancedCurriculumData
import com.openlingo.app.data.local.curriculum.B1CurriculumData
import com.openlingo.app.data.local.curriculum.ExpandedCurriculumData
import com.openlingo.app.data.local.curriculum.JapaneseN4CurriculumData
import com.openlingo.app.data.local.curriculum.UnitPayload
import com.openlingo.app.data.local.entities.ChallengeOptionEntity
import com.openlingo.app.data.local.models.ChallengeType
import com.openlingo.app.ui.MatchPairs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * A MATCH_PAIRS board can only be cleared by tapping the pairs it was authored
 * as, and the completion check fires only once every tile is off the board. The
 * two have to agree, or the exercise is a dead end: a perfect run leaves tiles on
 * the board, CHECK never enables, and the learner cannot continue.
 *
 * That is what happened on the placement test. `selectPairTile` inferred pairs
 * from option-id parity (`firstId xor 1 == optionId`), which is the authored
 * pairing only while a board's first option id is even. Boards authored from an
 * odd id — 26 of the corpus, every one of them Japanese, and all 26 are on the
 * Japanese N5/N4 units placement draws from — left their first and last tile
 * without a partner, so `matchedPairIds` could never reach `options.size`.
 *
 * Every test here plays the shipped boards through the production pairing rule
 * and asserts the completion predicate is satisfiable on them.
 */
class MatchPairsTest {

    private val allPayloads: List<UnitPayload> =
        ExpandedCurriculumData.spanishExpandedUnits +
            ExpandedCurriculumData.japaneseExpandedUnits +
            AdvancedCurriculumData.spanishAdvancedUnits +
            AdvancedCurriculumData.japaneseAdvancedUnits +
            B1CurriculumData.spanishA2Units +
            B1CurriculumData.spanishB1Units +
            B1CurriculumData.japaneseN4Units +
            JapaneseN4CurriculumData.japaneseN4ExtensionUnits

    /** Every board in the corpus, with the unit its lesson belongs to. */
    private val boards: List<Board> = allPayloads.flatMap { payload ->
        payload.challenges
            .filter { it.type == ChallengeType.MATCH_PAIRS }
            .map { challenge ->
                Board(
                    unitId = payload.unit.id,
                    challengeId = challenge.id,
                    options = payload.options.filter { it.challengeId == challenge.id },
                )
            }
    }

    private data class Board(
        val unitId: Int,
        val challengeId: Int,
        val options: List<ChallengeOptionEntity>,
    )

    /**
     * Plays the board perfectly and asserts the completion check agrees.
     *
     * "Perfectly" means tapping every authored pair — the pairs `pairings`
     * reports — and the check then has to pass on every tile being matched. The
     * second assertion is the one the parity rule failed: a tile that the app
     * will not pair with anything can never leave the board, so the board cannot
     * be cleared however well the learner plays it.
     */
    private fun assertClearable(board: Board) {
        val ids = board.options.map { it.id }
        val pairs = MatchPairs.pairings(board.options)
        assertEquals(
            "board ${board.challengeId}: ${pairs.size} pairs for ${ids.size} tiles",
            ids.size,
            pairs.size * 2,
        )
        pairs.forEach { (first, second) ->
            assertTrue(
                "board ${board.challengeId}: the app rejects the authored pair $first + $second",
                MatchPairs.isPair(board.options, first, second),
            )
            assertEquals(
                "board ${board.challengeId}: $first and $second do not pair back",
                first,
                MatchPairs.partnerOf(board.options, second),
            )
        }
        assertEquals(
            "board ${board.challengeId}: a perfect run still leaves a tile that can never be paired",
            ids.size,
            ids.count { MatchPairs.partnerOf(board.options, it) != null },
        )
        assertTrue(
            "board ${board.challengeId}: a perfect run does not satisfy the completion check",
            MatchPairs.isComplete(board.options, ids.toSet()),
        )
    }

    @Test
    fun `a perfect run clears every tile on every board`() {
        assertTrue("corpus changed: no match-pairs boards to test", boards.size >= 14)
        boards.forEach(::assertClearable)
    }

    @Test
    fun `every board placement can draw is clearable`() {
        // MainViewModel.startPlacementTest samples the Japanese course from these units.
        val placementUnits = (20..29).toSet() + (40..47).toSet()
        val placementBoards = boards.filter { it.unitId in placementUnits }
        assertTrue("placement path has no match-pairs boards", placementBoards.size >= 4)
        // The control that keeps this from passing vacuously: the placement path
        // really does draw boards whose first option id is odd, which is the shape
        // the parity rule could not pair.
        assertTrue(
            "corpus changed: no placement board starts on an odd id, so this test no " +
                "longer covers the bug it was written for",
            placementBoards.any { board -> board.options.minOf { it.id } % 2 == 1 },
        )
        placementBoards.forEach(::assertClearable)
    }

    @Test
    fun `a board authored from an odd id pairs its first two tiles`() {
        // 62029 is the shape the placement path draws: 休まれる, 休ませる, 待たれる,
        // 待たせる — ids 6300581..6300584. Under `id xor 1` none of them paired.
        val board = listOf(
            option(6300581, "休まれる"),
            option(6300582, "休ませる"),
            option(6300583, "待たれる"),
            option(6300584, "待たせる"),
        )
        assertEquals(
            listOf(6300581 to 6300582, 6300583 to 6300584),
            MatchPairs.pairings(board),
        )
        assertTrue(MatchPairs.isPair(board, 6300581, 6300582))
        assertTrue(MatchPairs.isPair(board, 6300583, 6300584))
        assertFalse(
            "tiles from two different pairs are not a pair",
            MatchPairs.isPair(board, 6300582, 6300583),
        )
        assertEquals(6300582, MatchPairs.partnerOf(board, 6300581))
    }

    @Test
    fun `a board is incomplete until its last pair is matched`() {
        val board = listOf(
            option(6300581, "休まれる"),
            option(6300582, "休ませる"),
            option(6300583, "待たれる"),
            option(6300584, "待たせる"),
        )
        assertFalse(MatchPairs.isComplete(board, emptySet()))
        assertFalse(MatchPairs.isComplete(board, setOf(6300581, 6300582)))
        assertTrue(MatchPairs.isComplete(board, setOf(6300581, 6300582, 6300583, 6300584)))
    }

    @Test
    fun `a tile with no partner cannot block a board`() {
        // Not a shipped shape — every board in the corpus is even — but a stray
        // tile must leave the lesson completable rather than soft-lock it.
        val board = listOf(
            option(1, "one"),
            option(2, "two"),
            option(3, "stray"),
        )
        assertNull(MatchPairs.partnerOf(board, 3))
        assertTrue(MatchPairs.isComplete(board, setOf(1, 2)))
        assertFalse(MatchPairs.isComplete(board, setOf(1)))
    }

    private fun option(id: Int, text: String) =
        ChallengeOptionEntity(id = id, challengeId = 62029, text = text, correct = true)
}

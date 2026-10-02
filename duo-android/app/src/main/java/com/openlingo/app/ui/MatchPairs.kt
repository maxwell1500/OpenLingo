package com.openlingo.app.ui

import com.openlingo.app.data.local.entities.ChallengeOptionEntity

/**
 * The pairing rule for a MATCH_PAIRS board.
 *
 * A board is authored as consecutive pairs: the two tiles of one pair sit next to
 * each other in the payload, in ascending option-id order, so the pair at
 * position `2k` is the tile at `2k` together with the tile at `2k + 1`. The rule
 * here is therefore *positional* over the board's id-ascending sequence, which is
 * the authored pairing whatever number the first option happens to carry.
 *
 * It used to be `id xor 1`, which agrees with the authored pairing only while a
 * board's first option id is even. Twenty-six boards — every one of them
 * Japanese, e.g. 62029's `6300581` — are authored from an odd id, and for those
 * the first and the last tile had no partner under `xor 1`. The completion check
 * requires every tile matched ([isComplete], and the CHECK button's
 * `hasSelection`), so a perfect run at such a board cleared the six middle tiles,
 * left two on the board, and never enabled CHECK: the learner could not continue.
 * Placement draws those boards from the N5/N4 units, which is where this was met.
 */
object MatchPairs {

    /**
     * The pairs of [options], in board order, as `(firstId, secondId)`.
     *
     * A tile left over from an odd-sized board (no partner) is omitted: it cannot
     * be matched, so it is not a pair.
     */
    fun pairings(options: List<ChallengeOptionEntity>): List<Pair<Int, Int>> =
        options.map { it.id }.sorted().chunked(2).filter { it.size == 2 }.map { it[0] to it[1] }

    /** The id paired with [optionId], or null when the tile has no partner. */
    fun partnerOf(options: List<ChallengeOptionEntity>, optionId: Int): Int? =
        partners(options)[optionId]

    /** True when [firstId] and [secondId] are the two halves of one pair. */
    fun isPair(options: List<ChallengeOptionEntity>, firstId: Int, secondId: Int): Boolean =
        firstId != secondId && partnerOf(options, firstId) == secondId

    /**
     * True when every tile that *has* a partner has been matched.
     *
     * The board's pairs are what the learner clears, so a tile with no partner
     * cannot block: a malformed board (an odd option set, a stray distractor)
     * stays completable instead of soft-locking the lesson. No shipped board has
     * one — MatchPairsTest pins that.
     */
    fun isComplete(options: List<ChallengeOptionEntity>, matchedIds: Set<Int>): Boolean =
        partners(options).keys.all { it in matchedIds }

    /** `id -> partner id` for every tile that has a partner. */
    private fun partners(options: List<ChallengeOptionEntity>): Map<Int, Int> =
        options.map { it.id }.sorted()
            .chunked(2)
            .filter { it.size == 2 }
            .flatMap { (first, second) -> listOf(first to second, second to first) }
            .toMap()
}

package com.duo.app.ui

import com.duo.app.data.local.entities.UnitWithLessons
import com.duo.app.data.local.entities.VocabScheduleEntity
import com.duo.app.data.repository.LocalProgressRepository

/**
 * WI-12: which scheduled words belong to which unit.
 *
 * `vocab_schedule` rows carry a free-text `category` but no unit id, so "the words of
 * unit N" cannot be read off the table — and adding a column would mean a schema change
 * and a migration for a table that is otherwise correct. What the table *is* missing is
 * answered by a fact the database already holds: the target-language strings a unit's
 * own challenges mark as **correct** answers. A word belongs to a unit exactly when that
 * unit's exercises teach it, and a distractor is by definition the opposite of that.
 *
 * That distinction is load-bearing. The Spanish seed offers the tile `gracias`
 * (`correct = false`) as a wrong answer to "Sí por favor", so a vocabulary list that
 * bucketed every option would present "Gracias" as Unit 1's taught word on the strength
 * of a word the unit is teaching the learner to reject.
 *
 * Deriving the grouping this way keeps one source of truth — the FSRS rows the Practice
 * tab reviews — and adds no column, no migration and no DAO query.
 *
 * The honest consequence: only words a unit actually teaches are listed, so a unit with
 * nothing scheduled for it has no vocabulary to show. Callers render that as an empty
 * state rather than inventing rows.
 */
object UnitVocabularyIndex {

    /**
     * Comparison key for an authored target-language term.
     *
     * Option text and a scheduled word are written by hand in two different files, so
     * case and surrounding whitespace may drift and nothing else may. Deliberately not
     * accent-folded: whether `café` and `cafe` are the same word is a content decision,
     * not something a matcher should decide for the author.
     */
    fun key(term: String): String = term.trim().lowercase()

    /**
     * Buckets the target-language strings each unit's lesson-path challenges teach,
     * keyed by unit id.
     *
     * Only `correct` options count: a SELECT/LISTEN challenge's wrong option and a
     * WORD_BANK's decoy tile are things the learner must reject, not words the unit puts
     * its name to. A word-bank sentence contributes all of its correct tiles, so a unit
     * that only assembles phrases still has terms to show.
     *
     * Held-out checkpoint items are excluded by
     * [LocalProgressRepository.getChallengesForUnits] for the same reason the unit
     * header excludes them: a word the learner was never taught is not this unit's
     * vocabulary.
     */
    suspend fun taughtTermsByUnit(
        repository: LocalProgressRepository,
        units: List<UnitWithLessons>,
    ): Map<Int, Set<String>> {
        if (units.isEmpty()) return emptyMap()
        val unitIdByLesson = units.flatMap { u -> u.lessons.map { it.id to u.unit.id } }.toMap()
        val terms = LinkedHashMap<Int, MutableSet<String>>()
        repository.getChallengesForUnits(units.map { it.unit.id }).forEach { withOptions ->
            val unitId = unitIdByLesson[withOptions.challenge.lessonId] ?: return@forEach
            terms.getOrPut(unitId) { mutableSetOf() }
                .addAll(withOptions.options.filter { it.correct }.map { key(it.text) })
        }
        return terms.mapValues { (_, value) -> value.toSet() }
    }

    /** The subset of [vocab] whose word this unit teaches, order preserved. */
    fun wordsFor(
        vocab: List<VocabScheduleEntity>,
        taught: Set<String>,
    ): List<VocabScheduleEntity> {
        if (taught.isEmpty()) return emptyList()
        return vocab.filter { key(it.foreign) in taught }
    }

    /**
     * Word count per unit, for the unit header. Units with nothing scheduled are left
     * out entirely so a caller can tell "no vocabulary" from "not loaded yet".
     */
    fun countsByUnit(
        vocab: List<VocabScheduleEntity>,
        taughtTermsByUnit: Map<Int, Set<String>>,
    ): Map<Int, Int> = taughtTermsByUnit
        .mapValues { (_, taught) -> wordsFor(vocab, taught).size }
        .filterValues { it > 0 }
}

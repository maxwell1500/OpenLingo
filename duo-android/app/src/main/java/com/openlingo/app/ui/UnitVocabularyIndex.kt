package com.openlingo.app.ui

import com.openlingo.app.data.local.entities.UnitWithLessons
import com.openlingo.app.data.repository.ChallengeWithOptions
import com.openlingo.app.data.repository.LocalProgressRepository

/**
 * One headword a unit teaches, carrying only what the corpus itself says about it.
 *
 * Only [romaji] and [audioSrc] are nullable, and both stay null when the challenge that
 * teaches the word gave neither. There is no `translation` here and no `category`: the
 * corpus states no meaning for most of its own correct answers, and the FSRS topic labels
 * belong to a 14-row review seed, not to a unit. The gloss a learner sees is read from
 * the offline dictionary at render time, which is the one place the app states a meaning
 * outright; a word the dictionary cannot gloss simply shows no gloss.
 */
data class UnitWord(
    /** The target-language surface form, as the corpus writes it. */
    val term: String,
    /** Japanese reading, or null when the teaching challenge gave none. */
    val romaji: String? = null,
    /** A bundled clip carried by the teaching option, or null. Never a URL. */
    val audioSrc: String? = null,
    /**
     * The lesson that first teaches the word inside this unit. This is the list's
     * grouping key, and it is a fact the corpus states: `Lesson 5: Ordering at a Café`
     * is the unit's own account of what it covers, where an FSRS topic would be a
     * hand-written label for one of fourteen review cards.
     */
    val lessonTitle: String,
)

/**
 * Which words belong to which unit.
 *
 * `vocab_schedule` rows carry a free-text `category` but no unit id, so "the words of
 * unit N" cannot be read off the FSRS table — and adding a column would mean a schema
 * change and a migration for a table that is otherwise correct. What a unit *teaches* is
 * a fact the database already holds: the target-language strings that unit's own
 * challenges mark as **correct** answers. A word belongs to a unit exactly when that
 * unit's exercises teach it, and a distractor is by definition the opposite of that.
 *
 * That distinction is load-bearing. The Spanish seed offers the tile `gracias`
 * (`correct = false`) as a wrong answer to "Sí por favor", so a vocabulary list that
 * bucketed every option would present "Gracias" as Unit 1's taught word on the strength
 * of a word the unit is teaching the learner to reject.
 *
 * **The FSRS schedule is not the gate.** An earlier version of this file intersected the
 * taught terms against `vocab_schedule` to produce the list. That table is a hand-written
 * seed of 14 review cards and never grows with the corpus, so the intersection capped
 * every one of the 28 units at what those 14 rows happened to contain — 3 Spanish and 8
 * Japanese words across the whole course, and `VOCABULARY · NOT YET ADDED` on most unit
 * headers, while the units themselves were teaching 245 and 234 headwords. Study and
 * review are different concerns: the scheduler decides what is *due*, and it must not
 * decide what a unit is capable of *listing*.
 *
 * Held-out checkpoint items are excluded, and deliberately so. They arrive from
 * [LocalProgressRepository.getChallengesForUnits], which filters `heldOut = 0`, because
 * held-out means "not on the lesson path": a browsable list of a unit's words is a study
 * aid, and printing a checkpoint's unseen answer next to the taught ones would hand over
 * the thing the checkpoint exists to test. The 9 Spanish and 10 Japanese headwords that
 * appear only in the held-out pool therefore stay out of the list, exactly as they stay
 * out of the unit header and the grammar drills.
 *
 * A unit that teaches no indexable term has no vocabulary, and callers render that as an
 * empty state. It is now genuinely empty rather than empty because of an unrelated table.
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
     * The headwords each unit teaches, keyed by unit id, in the order the unit teaches
     * them — unit, then lesson, then challenge — with one entry per distinct term.
     *
     * Only `correct` options count: a SELECT/LISTEN challenge's wrong option and a
     * WORD_BANK's decoy tile are things the learner must reject, not words the unit puts
     * its name to. A word-bank sentence contributes all of its correct tiles, so a unit
     * that only assembles phrases still has terms to show.
     *
     * A term taught more than once in the same unit is one entry, filed under the lesson
     * that teaches it *first*: `La cuenta` is the answer to both 1019 and 1021, and the
     * learner met it in 1019's lesson. A term taught by two different units appears in
     * both lists, because both units do teach it.
     */
    suspend fun wordsByUnit(
        repository: LocalProgressRepository,
        units: List<UnitWithLessons>,
    ): Map<Int, List<UnitWord>> = bucketByUnit(
        repository.getChallengesForUnits(units.map { it.unit.id }),
        units,
    )

    /**
     * Word count per unit, for the unit header. Units with nothing to list are left out
     * entirely so a caller can tell "no vocabulary" from "not loaded yet".
     */
    fun countsByUnit(wordsByUnit: Map<Int, List<UnitWord>>): Map<Int, Int> =
        wordsByUnit
            .mapValues { (_, words) -> words.size }
            .filterValues { it > 0 }
}

/**
 * The words the learner has actually met, across the whole course.
 *
 * This is a question about the *learner*, not about the corpus: a word is unlocked when
 * the learner has completed a challenge that teaches it as its correct answer. It is the
 * same derivation as [UnitVocabularyIndex.wordsByUnit] — the same key, the same
 * correct-options-only rule, the same held-out exclusion — narrowed to completed
 * challenges and flattened across units, so a learner who has met `Buenos días` in
 * Spanish unit 1 and `おはようございます` in Japanese unit 1 has both.
 *
 * Nothing here writes. Reading a learner's progress to describe it is study, and this
 * surface is not an attempt: it records no progress, no mistake and no exercise-type
 * statistic. The learner who has done nothing has unlocked nothing, and the count a
 * screen renders from this is zero until they have answered something.
 */
object LearnerVocabulary {

    /**
     * Distinct headwords the completed challenges teach, in course order.
     *
     * A word met twice is one word: `La cuenta` is the answer to both 1019 and 1021, and
     * the learner unlocked it once. The first lesson that teaches it wins the grouping,
     * because that is where the learner met it.
     */
    suspend fun wordsMet(
        repository: LocalProgressRepository,
        units: List<UnitWithLessons>,
        completedChallengeIds: Set<Int>,
    ): List<UnitWord> {
        if (units.isEmpty() || completedChallengeIds.isEmpty()) return emptyList()
        val taught = repository.getChallengesForUnits(units.map { it.unit.id })
            .filter { it.challenge.id in completedChallengeIds }
        return bucketByUnit(taught, units).values.flatten()
    }
}

/**
 * The FSRS cards a learner can actually be quizzed on.
 *
 * `vocab_schedule` is a hand-written table of fourteen review cards that ships
 * with the app, so every new install has rows in it before the learner has
 * answered anything. Counting those rows as *due* made the Practice tab claim
 * six Spanish cards for a learner who had not met a single Spanish word, two
 * inches above "0 words unlocked" — two numbers about the same learner that
 * cannot both be true.
 *
 * The scheduler is not the thing at fault and is not changed here: it still
 * computes stability, reps, lapses and due dates exactly as before, and the
 * rows are still the corpus's own authored glosses. What changed is which of
 * them the review surface offers. A card is offered once the learner has met
 * its word — the same derivation as [LearnerVocabulary.wordsMet], compared on
 * the same [UnitVocabularyIndex.key] — so the due count can never exceed the
 * unlocked count, and a learner who has answered nothing is owed nothing.
 *
 * The seed stays out of what a unit *lists*: [UnitVocabularyIndex] is derived
 * from the corpus and reads none of this, so this coupling cannot creep back
 * into vocabulary breadth.
 */
object LearnerReview {

    /**
     * The cards in [cards] whose word the learner has met, order untouched.
     *
     * Membership is decided on [UnitVocabularyIndex.key], the key the unit
     * vocabulary list files a term under, so `Buenos días` in the schedule and
     * `Buenos días` unlocked by a completed challenge are the same word.
     */
    fun cardsMet(
        cards: List<com.openlingo.app.data.local.entities.VocabScheduleEntity>,
        met: List<UnitWord>,
    ): List<com.openlingo.app.data.local.entities.VocabScheduleEntity> {
        if (cards.isEmpty() || met.isEmpty()) return emptyList()
        val metKeys = met.mapTo(HashSet(met.size)) { UnitVocabularyIndex.key(it.term) }
        return cards.filter { UnitVocabularyIndex.key(it.foreign) in metKeys }
    }
}

/**
 * Groups the correct answers of [challenges] by the unit that owns the lesson they sit
 * in, one entry per distinct term, in the order the challenges arrive.
 *
 * Shared by both surfaces above so they cannot disagree: a word the unit list shows and a
 * word the unlocked count counts are the same word by construction, not by two
 * implementations agreeing.
 */
private fun bucketByUnit(
    challenges: List<ChallengeWithOptions>,
    units: List<UnitWithLessons>,
): Map<Int, List<UnitWord>> {
    if (challenges.isEmpty()) return emptyMap()
    val unitIdByLesson = units.flatMap { u -> u.lessons.map { it.id to u.unit.id } }.toMap()
    val lessonTitleById = units.flatMap { u -> u.lessons }.associate { it.id to it.title }
    val byUnit = LinkedHashMap<Int, LinkedHashMap<String, UnitWord>>()
    for (withOptions in challenges) {
        val lessonId = withOptions.challenge.lessonId
        val unitId = unitIdByLesson[lessonId] ?: continue
        val lessonTitle = lessonTitleById[lessonId].orEmpty()
        val words = byUnit.getOrPut(unitId) { LinkedHashMap() }
        for (option in withOptions.options) {
            if (!option.correct) continue
            val term = option.text.trim()
            if (term.isEmpty()) continue
            val existing = words[UnitVocabularyIndex.key(term)]
            if (existing != null) {
                // First lesson wins the grouping, but a later option may still be the
                // one that carries a reading or a clip.
                words[UnitVocabularyIndex.key(term)] = existing.copy(
                    romaji = existing.romaji ?: option.romaji,
                    audioSrc = existing.audioSrc ?: option.audioSrc,
                )
                continue
            }
            words[UnitVocabularyIndex.key(term)] = UnitWord(
                term = term,
                romaji = option.romaji,
                audioSrc = option.audioSrc,
                lessonTitle = lessonTitle,
            )
        }
    }
    return byUnit.mapValues { (_, words) -> words.values.toList() }
}

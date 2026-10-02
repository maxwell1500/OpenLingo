package com.openlingo.app

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.openlingo.app.data.local.OpenLingoDatabase
import com.openlingo.app.data.local.entities.UnitWithLessons
import com.openlingo.app.data.repository.LocalProgressRepository
import com.openlingo.app.dictionary.DictionaryIndex
import com.openlingo.app.ui.LearnerReview
import com.openlingo.app.ui.LearnerVocabulary
import com.openlingo.app.ui.UnitVocabularyIndex
import com.openlingo.app.ui.UnitWord
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * The per-unit vocabulary list and the Practice tab's unlocked count both read the same
 * fact: the target-language strings a challenge marks as its **correct** answer.
 *
 * What is worth locking is the observable contract a learner can see. Before this was
 * fixed the list was the intersection of a unit's taught words with the 14-row FSRS review
 * seed, so across 28 units at most 3 Spanish and 8 Japanese words could ever be listed
 * and most unit headers read `VOCABULARY · NOT YET ADDED`. Every test here therefore
 * asserts against the corpus rather than against a snapshot of it, and the headline test
 * asserts a *magnitude* — a unit that teaches words must list them, not three of them.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class UnitVocabularyIndexTest {

    private lateinit var db: OpenLingoDatabase
    private lateinit var repository: LocalProgressRepository

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            OpenLingoDatabase::class.java,
        ).allowMainThreadQueries().build()
        repository = LocalProgressRepository(db)
    }

    @After
    fun tearDown() = db.close()

    private suspend fun unitsOf(courseId: Int): List<UnitWithLessons> =
        db.courseDao().getUnitsForCourseDirect(courseId).map { unit ->
            UnitWithLessons(unit = unit, lessons = db.courseDao().getLessonsForUnit(unit.id).first())
        }

    private suspend fun challengesOf(unit: UnitWithLessons) =
        db.lessonDao().getChallengesForUnits(listOf(unit.unit.id))
            .flatMap { db.lessonDao().getOptionsForChallenge(it.id) }

    /** Marks a challenge complete the way an answered lesson does. */
    private suspend fun complete(challengeId: Int) =
        repository.submitAnswer(challengeId, isCorrect = true)

    @Test
    fun `each unit lists every distinct headword its own correct answers teach`() = runTest {
        repository.initializeIfNeeded()
        for (courseId in listOf(1, 2)) {
            val units = unitsOf(courseId)
            val byUnit = UnitVocabularyIndex.wordsByUnit(repository, units)

            for (unit in units) {
                val taught = challengesOf(unit).filter { it.correct }
                    .mapTo(mutableSetOf()) { UnitVocabularyIndex.key(it.text) }
                val actual = byUnit[unit.unit.id].orEmpty()
                    .mapTo(mutableSetOf()) { UnitVocabularyIndex.key(it.term) }
                assertEquals(
                    "unit ${unit.unit.id} lists the wrong words",
                    taught.map { it }.toSet(),
                    actual.toSet(),
                )
            }
        }
    }

    /**
     * The defect itself. The FSRS review seed is 14 rows and never grows with the corpus,
     * so a list gated on it could show at most 3 Spanish and 8 Japanese words in the whole
     * course. Each course here teaches far more than that on its lesson path, and each
     * unit must therefore list the words it teaches rather than the intersection.
     */
    @Test
    fun `a unit lists its whole vocabulary rather than the fourteen-card review seed`() = runTest {
        repository.initializeIfNeeded()
        val scheduledRows = db.vocabScheduleDao().getTotalCount()
        assertTrue("the review seed should be a small hand-written set", scheduledRows in 1..14)

        for (courseId in listOf(1, 2)) {
            val units = unitsOf(courseId)
            val counts = UnitVocabularyIndex.countsByUnit(
                UnitVocabularyIndex.wordsByUnit(repository, units),
            )
            val listed = counts.values.sum()
            assertTrue(
                "course $courseId listed only $listed words — the FSRS seed is gating the list again",
                listed > scheduledRows * 4,
            )
            // No unit may be empty while its lessons teach answers in the target language.
            for (unit in units) {
                val teachesSomething = challengesOf(unit).any { it.correct }
                if (teachesSomething) {
                    assertTrue(
                        "unit ${unit.unit.id} teaches words but advertises none",
                        (counts[unit.unit.id] ?: 0) > 0,
                    )
                }
            }
        }
    }

    @Test
    fun `a word a unit only offers as a distractor is not that unit's vocabulary`() = runTest {
        repository.initializeIfNeeded()
        val units = unitsOf(1)
        val byUnit = UnitVocabularyIndex.wordsByUnit(repository, units)
        var checked = 0

        for (unit in units) {
            val options = challengesOf(unit)
            val taught = options.filter { it.correct }
                .mapTo(mutableSetOf()) { UnitVocabularyIndex.key(it.text) }
            val distractors = options.filterNot { it.correct }
                .mapTo(mutableSetOf()) { UnitVocabularyIndex.key(it.text) } - taught
            if (distractors.isEmpty()) continue
            checked++
            val leaks = byUnit[unit.unit.id].orEmpty()
                .mapTo(mutableSetOf()) { UnitVocabularyIndex.key(it.term) }
                .filterTo(mutableSetOf()) { it in distractors }
            assertEquals("unit ${unit.unit.id} lists a distractor as vocabulary", emptySet<String>(), leaks.toSet())
        }
        assertTrue("no unit offered a distractor, so the check proved nothing", checked > 0)
    }

    /**
     * Held-out means "not on the lesson path". A browsable list of a unit's words is a
     * study aid, so printing a checkpoint's unseen answer beside the taught ones would
     * hand over the thing the checkpoint exists to test.
     */
    @Test
    fun `no held-out checkpoint answer is listed by the unit that holds it`() = runTest {
        repository.initializeIfNeeded()
        for (courseId in listOf(1, 2)) {
            val units = unitsOf(courseId)
            val listed = UnitVocabularyIndex.wordsByUnit(repository, units).values.flatten()
                .mapTo(mutableSetOf()) { UnitVocabularyIndex.key(it.term) }
            val heldOut = repository
                .getHeldOutChallengesForUnits(units.map { it.unit.id })
                .flatMap { db.lessonDao().getOptionsForChallenge(it.challenge.id) }
                .filter { it.correct }
                .map { UnitVocabularyIndex.key(it.text) }
                .filter { it.isNotEmpty() }
                .toSet()
            assertTrue("no held-out items to check against", heldOut.isNotEmpty())
            // A word may legitimately be both taught and held out; only a word that is
            // held out and taught nowhere may be listed.
            val taughtSomewhere = units.flatMap { u ->
                db.lessonDao().getChallengesForUnits(listOf(u.unit.id))
                    .flatMap { db.lessonDao().getOptionsForChallenge(it.id) }
            }.filter { it.correct }.mapTo(mutableSetOf()) { UnitVocabularyIndex.key(it.text) }
            val leaks = heldOut - taughtSomewhere
            assertTrue("a held-out-only headword is listed: $leaks", leaks.none { it in listed })
        }
    }

    @Test
    fun `counts agree with the lists and omit units with nothing to show`() = runTest {
        repository.initializeIfNeeded()
        for (courseId in listOf(1, 2)) {
            val units = unitsOf(courseId)
            val byUnit = UnitVocabularyIndex.wordsByUnit(repository, units)
            val counts = UnitVocabularyIndex.countsByUnit(byUnit)

            for (unit in units) {
                val words = byUnit[unit.unit.id].orEmpty()
                if (words.isEmpty()) {
                    assertFalse("empty unit ${unit.unit.id} is advertised", unit.unit.id in counts)
                } else {
                    assertEquals("wrong count for unit ${unit.unit.id}", words.size, counts[unit.unit.id])
                }
            }
        }
    }

    @Test
    fun `countsByUnit leaves out a unit with no words`() {
        val counts = UnitVocabularyIndex.countsByUnit(
            mapOf(
                10 to listOf(UnitWord("Hola", lessonTitle = "Greetings")),
                11 to emptyList(),
            ),
        )

        assertEquals(mapOf(10 to 1), counts)
    }

    @Test
    fun `no units means no vocabulary`() = runTest {
        assertEquals(emptyMap<Int, List<UnitWord>>(), UnitVocabularyIndex.wordsByUnit(repository, emptyList()))
    }

    // --- the Practice tab's unlocked count -----------------------------------

    /**
     * A brand-new install has unlocked nothing, so the count the Practice tab renders is
     * zero. It used to read 14 from a hardcoded list before the learner had done anything.
     */
    @Test
    fun `a learner who has done nothing has unlocked no words`() = runTest {
        repository.initializeIfNeeded()
        val units = unitsOf(1)

        assertEquals(
            emptyList<UnitWord>(),
            LearnerVocabulary.wordsMet(repository, units, completedChallengeIds = emptySet()),
        )
    }

    @Test
    fun `a completed challenge unlocks exactly the words it teaches as correct answers`() = runTest {
        repository.initializeIfNeeded()
        val unit = unitsOf(1).first()
        val challenge = db.lessonDao().getChallengesForUnits(listOf(unit.unit.id)).first()
        complete(challenge.id)

        val expected = db.lessonDao().getOptionsForChallenge(challenge.id)
            .filter { it.correct }
            .map { UnitVocabularyIndex.key(it.text) }
            .toSet()
        assertTrue("the challenge taught nothing to unlock", expected.isNotEmpty())

        val met = LearnerVocabulary.wordsMet(repository, unitsOf(1), setOf(challenge.id))
        assertEquals(expected, met.map { UnitVocabularyIndex.key(it.term) }.toSet())
    }

    /** Unlocking is idempotent: a word met in two challenges is still one word. */
    @Test
    fun `a word met in several challenges counts once`() = runTest {
        repository.initializeIfNeeded()
        val taught = db.lessonDao().getAllOptions().filter { it.correct }
        assertNotNull(taught)
        val repeated = taught.groupBy { UnitVocabularyIndex.key(it.text) }
            .filterKeys { it.isNotEmpty() }
            .filterValues { it.map { it.challengeId }.distinct().size > 1 }
        assertTrue("no word is taught by two challenges, so the check proved nothing", repeated.isNotEmpty())

        val (term, options) = repeated.entries.first()
        val challengeIds = options.map { it.challengeId }.distinct()
        challengeIds.forEach { complete(it) }

        val met = LearnerVocabulary.wordsMet(repository, unitsOf(1), challengeIds.toSet())
        assertEquals(
            "a word met in ${challengeIds.size} challenges must be one unlocked word",
            1,
            met.count { UnitVocabularyIndex.key(it.term) == term },
        )
    }

    /** The unlocked list is the unit lists flattened, so the two cannot drift apart. */
    @Test
    fun `unlocking every challenge in a course yields that course's taught headwords`() = runTest {
        repository.initializeIfNeeded()
        val units = unitsOf(2)
        val everyChallenge = db.lessonDao().getChallengesForUnits(units.map { it.unit.id })
        everyChallenge.forEach { complete(it.id) }

        val met = LearnerVocabulary.wordsMet(
            repository,
            units,
            everyChallenge.map { it.id }.toSet(),
        ).mapTo(mutableSetOf()) { UnitVocabularyIndex.key(it.term) }
        val taught = UnitVocabularyIndex.wordsByUnit(repository, units).values.flatten()
            .mapTo(mutableSetOf()) { UnitVocabularyIndex.key(it.term) }

        assertEquals(taught, met)
    }

    // --- the FSRS deck, which has to agree with the unlocked count -----------

    /**
     * The defect. A fresh install ships fourteen authored review cards, six of them
     * Spanish, and every one of them sits due — so the Practice tab read "6 cards
     * due for optimal memory retention" a few lines above "0 words unlocked", and
     * both numbers were being computed for the same learner.
     *
     * The scheduler is not the defect and is not changed: the rows are still there,
     * still carry the corpus's own glosses, and still owe a review. What the review
     * surface offers is now narrowed to the words this learner has met, so a learner
     * who has answered nothing is owed nothing.
     */
    @Test
    fun `a learner who has done nothing is owed no review cards`() = runTest {
        repository.initializeIfNeeded()
        val due = repository.getDueVocab("es").first()
        assertTrue("the seeded schedule owes Spanish cards before the fix", due.isNotEmpty())

        val met = LearnerVocabulary.wordsMet(repository, unitsOf(1), completedChallengeIds = emptySet())
        assertEquals(
            emptyList<com.openlingo.app.data.local.entities.VocabScheduleEntity>(),
            LearnerReview.cardsMet(due, met),
        )
    }

    /**
     * The invariant the screen depends on, checked at several points in a course
     * rather than only at zero: the due count is a subset of the unlocked words, so
     * the two numbers can never describe a learner who has met nothing as owing
     * something.
     */
    @Test
    fun `the cards due can never outnumber the words unlocked`() = runTest {
        repository.initializeIfNeeded()
        val units = unitsOf(1)
        val onPath = repository.getChallengesForUnits(units.map { it.unit.id })
        val due = repository.getDueVocab("es").first()
        assertTrue("nothing is due, so the check proved nothing", due.isNotEmpty())

        for (taken in listOf(0, 1, onPath.size / 2, onPath.size)) {
            val completed = onPath.take(taken).map { it.challenge.id }.toSet()
            val met = LearnerVocabulary.wordsMet(repository, units, completed)
            val offered = LearnerReview.cardsMet(due, met)
            assertTrue(
                "after $taken challenges: ${offered.size} cards due against ${met.size} words unlocked",
                offered.size <= met.size,
            )
        }
    }

    /**
     * The other half: the deck is not emptied, it is earned. A card appears the
     * moment the learner has met its word, and the scheduler is untouched.
     */
    @Test
    fun `a card becomes reviewable once the learner has met its word`() = runTest {
        repository.initializeIfNeeded()
        val units = unitsOf(1)
        val scheduled = repository.getAllVocab("es").first()
            .associateBy { UnitVocabularyIndex.key(it.foreign) }
        val onPath = repository.getChallengesForUnits(units.map { it.unit.id })

        val teaching = onPath.firstOrNull { withOptions ->
            withOptions.options.any { it.correct && UnitVocabularyIndex.key(it.text) in scheduled }
        }
        assertNotNull("no lesson in the course teaches a scheduled review word", teaching)
        val word = teaching!!.options
            .first { it.correct && UnitVocabularyIndex.key(it.text) in scheduled }
        complete(teaching.challenge.id)

        val met = LearnerVocabulary.wordsMet(repository, units, setOf(teaching.challenge.id))
        val offered = LearnerReview.cardsMet(repository.getDueVocab("es").first(), met)
        assertTrue(
            "${word.text} was met but its card is not reviewable",
            offered.any { DictionaryIndex.key(it.foreign) == UnitVocabularyIndex.key(word.text) },
        )
    }
}

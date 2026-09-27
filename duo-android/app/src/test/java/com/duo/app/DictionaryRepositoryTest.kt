package com.duo.app

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.duo.app.data.local.DuoDatabase
import com.duo.app.data.local.models.ChallengeType
import com.duo.app.dictionary.DictionaryIndex
import com.duo.app.data.repository.LocalProgressRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * WI-11 at the seam the app actually uses: a dictionary built from the seeded
 * database, not from the curriculum payload objects.
 *
 * That distinction is load-bearing, and only a database-backed test can see it.
 * Spanish Unit 1 and Japanese Units 1-2 are seeded by the repository's own code
 * rather than by a `UnitPayload`, so an index built from the payload objects
 * would open on a learner's very first unit and find nothing. These tests fail
 * if the loader goes back to reading payloads, and fail if a lookup ever starts
 * writing.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DictionaryRepositoryTest {

    private lateinit var db: DuoDatabase
    private lateinit var repository: LocalProgressRepository

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            DuoDatabase::class.java,
        ).allowMainThreadQueries().build()
        repository = LocalProgressRepository(db)
    }

    @After
    fun tearDown() = db.close()

    /**
     * `Hola` is the correct answer of a Unit 1 challenge the repository seeds
     * directly, and appears in no curriculum payload.
     */
    @Test
    fun `a word the repository's own seed teaches is in the dictionary`() = runTest {
        repository.initializeIfNeeded()
        val hola = repository.loadDictionary().lookup("Hola")
        assertNotNull("Hola is Unit 1's first taught word", hola)
        assertEquals("Hello", hola!!.gloss)
    }

    @Test
    fun `a japanese word from the repository seed carries its reading`() = runTest {
        repository.initializeIfNeeded()
        val hello = repository.loadDictionary().lookup("こんにちは")
        assertNotNull("こんにちは is the Japanese Unit 1 greeting", hello)
        assertEquals("こんにちは", hello!!.term)
        assertEquals("Konnichiwa", hello.romaji)
    }

    /**
     * The FSRS row states a gloss outright, so it outranks a gloss read back
     * out of a prompt: `ja:konnichiwa` says "Hello / Good day" and the
     * dictionary has to say the same thing.
     */
    @Test
    fun `the schedule's own gloss outranks one read out of a prompt`() = runTest {
        repository.initializeIfNeeded()
        val dictionary = repository.loadDictionary()
        val scheduled = repository.getAllVocab("ja").first()
        assertTrue("no japanese vocabulary scheduled", scheduled.isNotEmpty())
        scheduled.forEach { word ->
            val entry = dictionary.lookup(word.foreign)
            assertNotNull("${word.foreign} is not indexed", entry)
            assertEquals(word.translation, entry!!.gloss)
        }
    }

    /**
     * The Spanish seed offers `gracias` as the wrong answer to "Sí por favor".
     * A distractor is not vocabulary, and in a lookup it is worse than in a
     * list, because the sheet is read as a definition.
     */
    @Test
    fun `a word the seed only offers as a wrong answer is not defined`() = runTest {
        repository.initializeIfNeeded()
        val dictionary = repository.loadDictionary()
        val taught = dictionary.entries.map { DictionaryIndex.key(it.term) }.toSet()
        val distractors = db.lessonDao().getAllOptions()
            .filter { !it.correct }
            .map { DictionaryIndex.key(it.text) }
            .toSet()

        // The control that keeps this from passing vacuously.
        assertTrue(
            "the Unit 1 answer the distractors compete with is missing",
            taught.any { it.contains("por favor") },
        )

        val neverTaught = distractors.filterNot { it in taught }
        assertTrue("corpus offers no distractor-only word to test", neverTaught.size > 50)
        neverTaught.forEach { assertNull("distractor indexed as vocabulary: $it", dictionary.lookup(it)) }
    }

    /**
     * A word-bank prompt translates the sentence its tiles assemble, never a
     * single tile: `Translate: 'Hello, good morning'` is the whole sentence.
     *
     * This lives here rather than in the payload test because the three
     * translating word banks are in the repository's own Unit 1 seed, which a
     * payload-built index never sees. The rule itself is a type check, not a
     * count, so a one-tile word bank added tomorrow cannot turn a sentence
     * translation into a one-word definition either.
     */
    @Test
    fun `a word bank prompt never becomes a tile's gloss`() = runTest {
        repository.initializeIfNeeded()
        val dictionary = repository.loadDictionary()
        val allOptions = db.lessonDao().getAllOptions()
        val wordBankIds = db.lessonDao().getAllChallenges()
            .filter { it.type == ChallengeType.WORD_BANK }
            .map { it.id }
        val translating = db.lessonDao().getAllChallenges()
            .filter { it.id in wordBankIds && it.question.contains("Translate: '") }
        assertTrue("corpus changed: no translating word bank to test", translating.isNotEmpty())

        val scheduled = db.vocabScheduleDao().getAllVocabDirect()
            .associate { DictionaryIndex.key(it.foreign) to it.translation }

        translating.forEach { bank ->
            val tiles = allOptions.filter { it.challengeId == bank.id && it.correct }
            assertTrue("word bank ${bank.id} has no correct tiles", tiles.isNotEmpty())
            val quoted = bank.question.substringAfter("Translate: '").substringBefore("'").trim()

            tiles.forEach { tile ->
                val entry = dictionary.lookup(tile.text)
                assertNotNull("${tile.text} is a correct tile of ${bank.id}", entry)
                val key = DictionaryIndex.key(tile.text)
                // The sentence's translation belongs to the sentence. The one
                // thing allowed to put it on a single word is the FSRS table,
                // which states that word's own translation outright.
                if (scheduled[key] != quoted) {
                    assertNotEquals(
                        "${tile.text} took its own bank's sentence translation as a gloss",
                        quoted,
                        entry!!.gloss,
                    )
                }
                // A word can be a tile of more than one bank; the entry keeps
                // one of those sentences, and it has to be one of them.
                val sentences = allOptions
                    .filter { it.correct }
                    .groupBy { it.challengeId }
                    .mapNotNull { (_, options) ->
                        val group = options.map { it.text.trim() }
                        if (tile.text.trim() in group) group.joinToString(" ") else null
                    }
                assertTrue(
                    "${tile.text} carries an example it is not part of: ${entry!!.example}",
                    entry.example in sentences,
                )
            }
        }
    }

    /**
     * A definition sheet is a study surface. Loading the index must not queue a
     * mistake, mark a challenge done, or move an exercise-type statistic —
     * otherwise looking a word up would cost the learner a heart.
     */
    @Test
    fun `building the dictionary writes nothing to the lesson tables`() = runTest {
        repository.initializeIfNeeded()
        val mistakesBefore = db.mistakeDao().getAllMistakesDirect()
        val progressBefore = db.challengeProgressDao()
            .getCompletedChallengeIdsDirect(LocalProgressRepository.GUEST_USER_ID)
        val statsBefore = db.exerciseTypeStatsDao().getAllStatsDirect()

        repository.loadDictionary()

        assertEquals(mistakesBefore, db.mistakeDao().getAllMistakesDirect())
        assertEquals(
            progressBefore,
            db.challengeProgressDao().getCompletedChallengeIdsDirect(LocalProgressRepository.GUEST_USER_ID),
        )
        assertEquals(statsBefore, db.exerciseTypeStatsDao().getAllStatsDirect())
    }

    /** The whole seeded corpus, not just the payload-backed units. */
    @Test
    fun `the seeded corpus teaches words in both languages`() = runTest {
        repository.initializeIfNeeded()
        val terms = repository.loadDictionary().entries.map { it.term }
        assertTrue("spanish words missing", terms.any { term -> term.any { it in 'a'..'z' } })
        assertTrue("japanese words missing", terms.any { term -> term.any { it.code in 0x4E00..0x9FFF } })
    }
}

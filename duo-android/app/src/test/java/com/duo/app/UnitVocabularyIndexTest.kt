package com.duo.app

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.duo.app.data.local.DuoDatabase
import com.duo.app.data.local.entities.UnitWithLessons
import com.duo.app.data.local.entities.VocabScheduleEntity
import com.duo.app.data.repository.LocalProgressRepository
import com.duo.app.ui.UnitVocabularyIndex
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * WI-12. The per-unit vocabulary list joins `vocab_schedule` to units by way of the
 * correct answers a unit's own challenges offer, so what is worth locking is the join:
 * that it never widens beyond the scheduled words a unit teaches, that a distractor does
 * not smuggle a word in, and that a unit with nothing scheduled says nothing.
 *
 * Assertions are on invariants computed from the database rather than on a snapshot of
 * the corpus, because the curriculum is content that changes; pinning word lists here
 * would only assert that the seed has not been edited.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class UnitVocabularyIndexTest {

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

    private suspend fun unitsOf(courseId: Int): List<UnitWithLessons> =
        db.courseDao().getUnitsForCourseDirect(courseId).map { unit ->
            UnitWithLessons(unit = unit, lessons = db.courseDao().getLessonsForUnit(unit.id).first())
        }

    private suspend fun vocabOf(courseId: Int): List<VocabScheduleEntity> =
        repository.getAllVocab(if (courseId == 1) "es" else "ja").first()

    private suspend fun optionsOf(unit: UnitWithLessons) =
        db.lessonDao().getChallengesForUnits(listOf(unit.unit.id))
            .flatMap { db.lessonDao().getOptionsForChallenge(it.id) }

    private fun vocab(
        id: String,
        foreign: String,
        category: String = "C",
    ) = VocabScheduleEntity(
        id = id, language = "es", foreign = foreign, translation = "t-$id", category = category,
    )

    @Test
    fun `each unit lists exactly the scheduled words its own correct answers teach`() = runTest {
        repository.initializeIfNeeded()
        for (courseId in listOf(1, 2)) {
            val scheduled = vocabOf(courseId)
            val units = unitsOf(courseId)
            val index = UnitVocabularyIndex.taughtTermsByUnit(repository, units)

            for (unit in units) {
                val taught = optionsOf(unit).filter { it.correct }
                    .mapTo(mutableSetOf()) { UnitVocabularyIndex.key(it.text) }
                val expected = scheduled.filter { UnitVocabularyIndex.key(it.foreign) in taught }
                val actual = UnitVocabularyIndex.wordsFor(scheduled, index[unit.unit.id].orEmpty())
                assertEquals(
                    "unit ${unit.unit.id} lists the wrong words",
                    expected.map { it.id }.toSet(),
                    actual.map { it.id }.toSet(),
                )
            }
        }
    }

    @Test
    fun `a word a unit only offers as a distractor is not that unit's vocabulary`() = runTest {
        repository.initializeIfNeeded()
        val scheduled = vocabOf(1)
        val units = unitsOf(1)
        val index = UnitVocabularyIndex.taughtTermsByUnit(repository, units)
        var checked = 0

        for (unit in units) {
            val options = optionsOf(unit)
            val taught = options.filter { it.correct }
                .mapTo(mutableSetOf()) { UnitVocabularyIndex.key(it.text) }
            val distractors = options.filterNot { it.correct }
                .mapTo(mutableSetOf()) { UnitVocabularyIndex.key(it.text) } - taught
            if (distractors.isEmpty()) continue
            checked++
            val listed = UnitVocabularyIndex.wordsFor(scheduled, index[unit.unit.id].orEmpty())
            val leaks = listed.map { UnitVocabularyIndex.key(it.foreign) }
                .filterTo(mutableSetOf()) { it in distractors }
            assertEquals("unit ${unit.unit.id} lists a distractor as vocabulary", emptySet<String>(), leaks.toSet())
        }
        assertTrue("no unit offered a distractor, so the check proved nothing", checked > 0)
    }

    @Test
    fun `counts agree with the lists and omit units with nothing to show`() = runTest {
        repository.initializeIfNeeded()
        for (courseId in listOf(1, 2)) {
            val scheduled = vocabOf(courseId)
            val units = unitsOf(courseId)
            val index = UnitVocabularyIndex.taughtTermsByUnit(repository, units)
            val counts = UnitVocabularyIndex.countsByUnit(scheduled, index)

            for (unit in units) {
                val listed = UnitVocabularyIndex.wordsFor(scheduled, index[unit.unit.id].orEmpty())
                if (listed.isEmpty()) {
                    assertFalse("empty unit ${unit.unit.id} is advertised", unit.unit.id in counts)
                } else {
                    assertEquals("wrong count for unit ${unit.unit.id}", listed.size, counts[unit.unit.id])
                }
            }
        }
    }

    @Test
    fun `wordsFor intersects the schedule with what the unit teaches`() {
        val table = listOf(vocab("es:hola", "Hola"), vocab("es:gracias", "Gracias"))

        // No unit terms at all, and terms with nothing in common: both must yield nothing
        // rather than falling back to the whole table.
        assertEquals(emptyList<VocabScheduleEntity>(), UnitVocabularyIndex.wordsFor(table, emptySet()))
        assertEquals(
            emptyList<VocabScheduleEntity>(),
            UnitVocabularyIndex.wordsFor(table, setOf("adios", "despedida")),
        )
        assertEquals(
            listOf("Hola"),
            UnitVocabularyIndex.wordsFor(table, setOf("hola")).map { it.foreign },
        )
    }

    @Test
    fun `countsByUnit leaves out a unit whose words are all outside the schedule`() {
        val table = listOf(vocab("es:hola", "Hola"))
        val counts = UnitVocabularyIndex.countsByUnit(
            vocab = table,
            taughtTermsByUnit = mapOf(10 to setOf("hola"), 11 to setOf("adios")),
        )

        assertEquals(mapOf(10 to 1), counts)
    }
}

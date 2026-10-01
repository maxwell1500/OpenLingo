package com.openlingo.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openlingo.app.data.local.entities.MistakeEntity
import com.openlingo.app.data.local.entities.MistakeEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface MistakeDao {

    /**
     * A mistake joined to the rule it broke (WI-16), scoped to one course.
     *
     * `grammaticalFocus` and `ruleText` are the challenge's own columns, so
     * selecting them is a query change only — no new table, no new column, and
     * therefore no migration. Without them the review list could only re-ask
     * the raw item, which is the memorization loop: a learner who cannot name
     * why "hablábamos" is wrong will keep re-answering until they pass by
     * recall rather than by rule.
     *
     * The course comes from the mistake's lesson → unit, so a miss recorded in
     * one course can never surface in the other's review list.
     */
    @Query(
        "SELECT m.challengeId AS challengeId, m.lessonId AS lessonId, " +
            "l.title AS lessonName, " +
            "c.question AS question, c.type AS type, m.timestamp AS timestamp, " +
            "c.grammaticalFocus AS grammaticalFocus, c.ruleText AS ruleText " +
            "FROM mistakes AS m " +
            "INNER JOIN challenges AS c ON c.id = m.challengeId " +
            "INNER JOIN lessons AS l ON l.id = m.lessonId " +
            "INNER JOIN units AS u ON u.id = l.unitId " +
            "WHERE u.courseId = :courseId " +
            "ORDER BY m.timestamp DESC"
    )
    fun getMistakeEntries(courseId: Int): Flow<List<MistakeEntry>>

    @Query(
        "SELECT COUNT(*) FROM mistakes AS m " +
            "INNER JOIN lessons AS l ON l.id = m.lessonId " +
            "INNER JOIN units AS u ON u.id = l.unitId " +
            "WHERE u.courseId = :courseId"
    )
    fun getMistakeCount(courseId: Int): Flow<Int>

    @Query(
        "SELECT m.* FROM mistakes AS m " +
            "INNER JOIN lessons AS l ON l.id = m.lessonId " +
            "INNER JOIN units AS u ON u.id = l.unitId " +
            "WHERE u.courseId = :courseId"
    )
    suspend fun getMistakesForCourseDirect(courseId: Int): List<MistakeEntity>

    /** Every mistake across courses: a backup export is a full snapshot. */
    @Query("SELECT * FROM mistakes")
    suspend fun getAllMistakesDirect(): List<MistakeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMistake(mistake: MistakeEntity)

    @Query("DELETE FROM mistakes WHERE challengeId = :challengeId")
    suspend fun clearMistake(challengeId: Int)

    /**
     * The Practice tab's "Clear all" clears the list it is showing, so it must
     * not reach into the other course's queue.
     */
    @Query(
        "DELETE FROM mistakes WHERE lessonId IN (" +
            "SELECT l.id FROM lessons AS l " +
            "INNER JOIN units AS u ON u.id = l.unitId " +
            "WHERE u.courseId = :courseId)"
    )
    suspend fun clearMistakesForCourse(courseId: Int)

    /** Full wipe: the reset and backup-import paths, which cover every course. */
    @Query("DELETE FROM mistakes")
    suspend fun clearAllMistakes()
}

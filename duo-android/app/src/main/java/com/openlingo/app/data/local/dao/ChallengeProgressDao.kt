package com.openlingo.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openlingo.app.data.local.entities.ChallengeProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeProgressDao {

    @Query("SELECT challengeId FROM challenge_progress WHERE userId = :userId AND completed = 1")
    fun getCompletedChallengeIds(userId: String): Flow<List<Int>>
    @Query("SELECT challengeId FROM challenge_progress WHERE userId = :userId AND completed = 1")
    suspend fun getCompletedChallengeIdsDirect(userId: String): List<Int>
    /**
     * The lessons the learner has finished.
     *
     * An INTRO card presents a word; it is never graded and writes no progress
     * row (see `MainViewModel.checkAnswer`), so it is excluded from both sides of
     * the comparison below. Counting it left every lesson that introduces a word
     * permanently un-completable — the crown never appeared and the next lesson
     * stayed locked — which is what the Unit 1 kana ramp would have hit.
     */
    @Query("""
        SELECT c.lessonId FROM challenges c
        INNER JOIN challenge_progress cp ON c.id = cp.challengeId AND cp.userId = :userId AND cp.completed = 1
        WHERE c.heldOut = 0 AND c.type != 'INTRO'
        GROUP BY c.lessonId
        HAVING COUNT(c.id) = (SELECT COUNT(c2.id) FROM challenges c2 WHERE c2.lessonId = c.lessonId AND c2.heldOut = 0 AND c2.type != 'INTRO')
    """)
    fun getCompletedLessonIds(userId: String): Flow<List<Int>>

    @Query("""
        SELECT c.lessonId FROM challenges c
        INNER JOIN challenge_progress cp ON c.id = cp.challengeId AND cp.userId = :userId AND cp.completed = 1
        WHERE c.heldOut = 0 AND c.type != 'INTRO'
        GROUP BY c.lessonId
        HAVING COUNT(c.id) = (SELECT COUNT(c2.id) FROM challenges c2 WHERE c2.lessonId = c.lessonId AND c2.heldOut = 0 AND c2.type != 'INTRO')
    """)
    suspend fun getCompletedLessonIdsDirect(userId: String): List<Int>


    @Query("SELECT EXISTS(SELECT 1 FROM challenge_progress WHERE userId = :userId AND challengeId = :challengeId AND completed = 1)")
    suspend fun isChallengeCompleted(userId: String, challengeId: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun markChallengeCompleted(progress: ChallengeProgressEntity)

    /**
     * Deletes the learner's progress row for one challenge.
     *
     * [markChallengeCompleted] cannot dedupe on its own — the primary key is an
     * auto-generated row id, so REPLACE never fires for a re-answer and every
     * insert appends. The write path deletes first so the table keeps exactly one
     * row per (userId, challengeId).
     */
    @Query("DELETE FROM challenge_progress WHERE userId = :userId AND challengeId = :challengeId")
    suspend fun clearChallengeFor(userId: String, challengeId: Int)

    @Query("DELETE FROM challenge_progress WHERE userId = :userId")
    suspend fun clearProgressForUser(userId: String)
}

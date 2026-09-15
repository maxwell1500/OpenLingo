package com.duo.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.duo.app.data.local.entities.ChallengeProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeProgressDao {

    @Query("SELECT challengeId FROM challenge_progress WHERE userId = :userId AND completed = 1")
    fun getCompletedChallengeIds(userId: String): Flow<List<Int>>
    @Query("SELECT challengeId FROM challenge_progress WHERE userId = :userId AND completed = 1")
    suspend fun getCompletedChallengeIdsDirect(userId: String): List<Int>
    @Query("""
        SELECT c.lessonId FROM challenges c
        INNER JOIN challenge_progress cp ON c.id = cp.challengeId AND cp.userId = :userId AND cp.completed = 1
        GROUP BY c.lessonId
        HAVING COUNT(c.id) = (SELECT COUNT(c2.id) FROM challenges c2 WHERE c2.lessonId = c.lessonId)
    """)
    fun getCompletedLessonIds(userId: String): Flow<List<Int>>

    @Query("""
        SELECT c.lessonId FROM challenges c
        INNER JOIN challenge_progress cp ON c.id = cp.challengeId AND cp.userId = :userId AND cp.completed = 1
        GROUP BY c.lessonId
        HAVING COUNT(c.id) = (SELECT COUNT(c2.id) FROM challenges c2 WHERE c2.lessonId = c.lessonId)
    """)
    suspend fun getCompletedLessonIdsDirect(userId: String): List<Int>


    @Query("SELECT EXISTS(SELECT 1 FROM challenge_progress WHERE userId = :userId AND challengeId = :challengeId AND completed = 1)")
    suspend fun isChallengeCompleted(userId: String, challengeId: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun markChallengeCompleted(progress: ChallengeProgressEntity)

    @Query("SELECT * FROM challenge_progress WHERE userId = :userId AND synced = 0")
    suspend fun getUnsyncedProgress(userId: String): List<ChallengeProgressEntity>

    @Query("UPDATE challenge_progress SET synced = 1, lastSynced = :timestamp WHERE id IN (:ids)")
    suspend fun markSynced(ids: List<Int>, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE challenge_progress SET userId = :newUserId, lastSynced = :timestamp WHERE userId = :oldUserId")
    suspend fun rekeyProgress(oldUserId: String, newUserId: String, timestamp: Long = System.currentTimeMillis())
    @Query("DELETE FROM challenge_progress WHERE userId = :userId")
    suspend fun clearProgressForUser(userId: String)
}

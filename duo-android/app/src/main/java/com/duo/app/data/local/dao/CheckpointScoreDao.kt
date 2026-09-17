package com.duo.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.duo.app.data.local.entities.CheckpointScoreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckpointScoreDao {

    @Insert
    suspend fun insert(score: CheckpointScoreEntity): Long

    @Query("SELECT * FROM checkpoint_scores WHERE userId = :userId AND courseId = :courseId AND level = :level ORDER BY timestamp DESC LIMIT 1")
    fun getLatestScore(userId: String, courseId: Int, level: String): Flow<CheckpointScoreEntity?>

    @Query("SELECT * FROM checkpoint_scores WHERE userId = :userId AND courseId = :courseId ORDER BY timestamp DESC")
    fun getAllScores(userId: String, courseId: Int): Flow<List<CheckpointScoreEntity>>

    @Query("SELECT * FROM checkpoint_scores WHERE userId = :userId")
    suspend fun getAllScoresDirect(userId: String): List<CheckpointScoreEntity>

    @Query("DELETE FROM checkpoint_scores WHERE userId = :userId")
    suspend fun clearAllScoresForUser(userId: String)
}

package com.duo.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.duo.app.data.local.entities.ExerciseTypeStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseTypeStatsDao {

    @Query("SELECT * FROM exercise_type_stats")
    fun getAllStats(): Flow<List<ExerciseTypeStatsEntity>>

    @Query("SELECT * FROM exercise_type_stats")
    suspend fun getAllStatsDirect(): List<ExerciseTypeStatsEntity>

    @Query("SELECT * FROM exercise_type_stats WHERE type = :type LIMIT 1")
    suspend fun getStatsForType(type: String): ExerciseTypeStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStats(stats: ExerciseTypeStatsEntity)

    @Query("UPDATE exercise_type_stats SET attempts = attempts + 1, correct = correct + :correctDelta WHERE type = :type")
    suspend fun recordAttempt(type: String, correctDelta: Int): Int
}

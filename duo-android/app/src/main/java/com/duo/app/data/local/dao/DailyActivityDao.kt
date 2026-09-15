package com.duo.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyActivityDao {
    @Query("SELECT xp FROM daily_activity WHERE date = :date")
    fun getXp(date: String): Flow<Int?>

    @Query("SELECT xp FROM daily_activity WHERE date = :date LIMIT 1")
    suspend fun getXpDirect(date: String): Int?

    @Query("SELECT * FROM daily_activity")
    suspend fun getAllDailyActivityDirect(): List<com.duo.app.data.local.entities.DailyActivityEntity>

    @Query(
        "INSERT INTO daily_activity (date, xp) VALUES (:date, :delta) " +
            "ON CONFLICT(date) DO UPDATE SET xp = xp + :delta"
    )
    suspend fun addXp(date: String, delta: Int)
}

package com.openlingo.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openlingo.app.data.local.entities.DailyActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyActivityDao {
    @Query("SELECT xp FROM daily_activity WHERE date = :date")
    fun getXp(date: String): Flow<Int?>

    @Query("SELECT xp FROM daily_activity WHERE date = :date LIMIT 1")
    suspend fun getXpDirect(date: String): Int?

    @Query("SELECT * FROM daily_activity")
    suspend fun getAllDailyActivityDirect(): List<com.openlingo.app.data.local.entities.DailyActivityEntity>

    @Query(
        "INSERT INTO daily_activity (date, xp) VALUES (:date, :delta) " +
            "ON CONFLICT(date) DO UPDATE SET xp = xp + :delta"
    )
    suspend fun addXp(date: String, delta: Int)

    /**
     * Writes the day's total outright rather than adding to it.
     *
     * [addXp] is the accumulator the answering path needs — each correct
     * answer adds to today's total. A backup row is not a delta, it is the
     * total as it stood at export time, so restoring it through [addXp] adds
     * the exported XP on top of whatever is already in the row.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun putDay(item: DailyActivityEntity)

    /** Wipes the table so an import replaces it instead of adding to it. */
    @Query("DELETE FROM daily_activity")
    suspend fun clearAllActivity()
}

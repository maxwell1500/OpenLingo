package com.duo.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.duo.app.data.local.entities.MistakeEntity
import com.duo.app.data.local.entities.MistakeEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface MistakeDao {

    @Query(
        "SELECT m.challengeId AS challengeId, m.lessonId AS lessonId, " +
            "c.question AS question, c.type AS type, m.timestamp AS timestamp " +
            "FROM mistakes AS m INNER JOIN challenges AS c ON c.id = m.challengeId " +
            "ORDER BY m.timestamp DESC"
    )
    fun getMistakeEntries(): Flow<List<MistakeEntry>>

    @Query("SELECT COUNT(*) FROM mistakes")
    fun getMistakeCount(): Flow<Int>

    @Query("SELECT * FROM mistakes")
    suspend fun getAllMistakesDirect(): List<MistakeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMistake(mistake: MistakeEntity)

    @Query("DELETE FROM mistakes WHERE challengeId = :challengeId")
    suspend fun clearMistake(challengeId: Int)
    @Query("DELETE FROM mistakes")
    suspend fun clearAllMistakes()
}

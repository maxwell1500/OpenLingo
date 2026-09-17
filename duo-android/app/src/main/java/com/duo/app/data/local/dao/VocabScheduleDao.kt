package com.duo.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.duo.app.data.local.entities.VocabScheduleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabScheduleDao {

    @Query("SELECT * FROM vocab_schedule ORDER BY due ASC")
    fun getAllVocab(): Flow<List<VocabScheduleEntity>>

    @Query("SELECT * FROM vocab_schedule")
    suspend fun getAllVocabDirect(): List<VocabScheduleEntity>

    @Query("SELECT * FROM vocab_schedule WHERE language = :language ORDER BY due ASC")
    fun getVocabForLanguage(language: String): Flow<List<VocabScheduleEntity>>

    @Query("SELECT * FROM vocab_schedule WHERE due <= :now ORDER BY due ASC")
    fun getDueVocab(now: Long = System.currentTimeMillis()): Flow<List<VocabScheduleEntity>>

    @Query("SELECT * FROM vocab_schedule WHERE language = :language AND due <= :now ORDER BY due ASC")
    fun getDueVocabForLanguage(language: String, now: Long = System.currentTimeMillis()): Flow<List<VocabScheduleEntity>>

    @Query("SELECT COUNT(*) FROM vocab_schedule WHERE due <= :now")
    fun getDueCount(now: Long = System.currentTimeMillis()): Flow<Int>

    @Query("SELECT COUNT(*) FROM vocab_schedule WHERE language = :language AND due <= :now")
    fun getDueCountForLanguage(language: String, now: Long = System.currentTimeMillis()): Flow<Int>

    @Query("SELECT * FROM vocab_schedule WHERE id = :id LIMIT 1")
    suspend fun getVocabById(id: String): VocabScheduleEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<VocabScheduleEntity>)

    @Update
    suspend fun update(item: VocabScheduleEntity)

    @Query("SELECT COUNT(*) FROM vocab_schedule")
    suspend fun getTotalCount(): Int
}

package com.duo.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.duo.app.data.local.entities.CharacterMasteryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterMasteryDao {

    @Query("SELECT * FROM character_mastery")
    fun getAllMastery(): Flow<List<CharacterMasteryEntity>>
    @Query("SELECT * FROM character_mastery")
    suspend fun getAllMasteryDirect(): List<CharacterMasteryEntity>


    @Query("SELECT * FROM character_mastery WHERE `character` = :character LIMIT 1")
    suspend fun getMasteryDirect(character: String): CharacterMasteryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMastery(mastery: CharacterMasteryEntity)
    @Query("DELETE FROM character_mastery")
    suspend fun clearAllMastery()
}

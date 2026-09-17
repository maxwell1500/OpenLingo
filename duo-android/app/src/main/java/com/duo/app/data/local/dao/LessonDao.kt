package com.duo.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.duo.app.data.local.entities.ChallengeEntity
import com.duo.app.data.local.entities.ChallengeOptionEntity

@Dao
interface LessonDao {

    @Query("SELECT * FROM challenges WHERE lessonId = :lessonId ORDER BY orderIndex ASC")
    suspend fun getChallengesForLesson(lessonId: Int): List<ChallengeEntity>

    @Query("SELECT * FROM challenges WHERE id = :challengeId LIMIT 1")
    suspend fun getChallengeById(challengeId: Int): ChallengeEntity?

    @Query("SELECT * FROM challenges WHERE id IN (:challengeIds) ORDER BY lessonId ASC, orderIndex ASC")
    suspend fun getChallengesByIds(challengeIds: List<Int>): List<ChallengeEntity>

    @Query("SELECT c.* FROM challenges c INNER JOIN lessons l ON l.id = c.lessonId INNER JOIN units u ON u.id = l.unitId WHERE u.id IN (:unitIds) ORDER BY u.orderIndex ASC, l.orderIndex ASC, c.orderIndex ASC")
    suspend fun getChallengesForUnits(unitIds: List<Int>): List<ChallengeEntity>



    @Query("SELECT * FROM challenge_options WHERE challengeId = :challengeId")
    suspend fun getOptionsForChallenge(challengeId: Int): List<ChallengeOptionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenges(challenges: List<ChallengeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOptions(options: List<ChallengeOptionEntity>)
}

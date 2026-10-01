package com.openlingo.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openlingo.app.data.local.entities.ChallengeEntity
import com.openlingo.app.data.local.entities.ChallengeOptionEntity

@Dao
interface LessonDao {

    @Query("SELECT * FROM challenges WHERE lessonId = :lessonId AND heldOut = 0 ORDER BY orderIndex ASC")
    suspend fun getChallengesForLesson(lessonId: Int): List<ChallengeEntity>

    @Query("SELECT * FROM challenges WHERE id = :challengeId LIMIT 1")
    suspend fun getChallengeById(challengeId: Int): ChallengeEntity?

    @Query("SELECT * FROM challenges WHERE id IN (:challengeIds) ORDER BY lessonId ASC, orderIndex ASC")
    suspend fun getChallengesByIds(challengeIds: List<Int>): List<ChallengeEntity>

    @Query("SELECT c.* FROM challenges c INNER JOIN lessons l ON l.id = c.lessonId INNER JOIN units u ON u.id = l.unitId WHERE u.id IN (:unitIds) AND c.heldOut = 0 ORDER BY u.orderIndex ASC, l.orderIndex ASC, c.orderIndex ASC")
    suspend fun getChallengesForUnits(unitIds: List<Int>): List<ChallengeEntity>

    /**
     * WI-08: the held-out half of the same join. These rows are seeded alongside the taught
     * challenges but are excluded from every lesson-path query above, so a checkpoint can
     * assess a structure the learner was taught on sentences they never saw.
     */
    @Query("SELECT c.* FROM challenges c INNER JOIN lessons l ON l.id = c.lessonId INNER JOIN units u ON u.id = l.unitId WHERE u.id IN (:unitIds) AND c.heldOut = 1 ORDER BY u.orderIndex ASC, l.orderIndex ASC, c.orderIndex ASC")
    suspend fun getHeldOutChallengesForUnits(unitIds: List<Int>): List<ChallengeEntity>



    /**
     * WI-11: every seeded challenge, lesson path or not.
     *
     * The dictionary reads the whole corpus rather than the learner's current
     * unit, because a lookup is "what does this word mean" and not "what am I
     * being tested on right now" — and because the seed that covers Spanish
     * and Japanese Units 1-2 lives in the repository's own seeding code rather
     * than in a curriculum payload, so reading the tables is the only way to
     * see it. Two flat reads and a group in Kotlin beat a join here: the
     * dictionary is built once, not per question.
     */
    @Query("SELECT * FROM challenges ORDER BY id ASC")
    suspend fun getAllChallenges(): List<ChallengeEntity>

    @Query("SELECT * FROM challenge_options ORDER BY challengeId ASC, id ASC")
    suspend fun getAllOptions(): List<ChallengeOptionEntity>

    @Query("SELECT * FROM challenge_options WHERE challengeId = :challengeId")
    suspend fun getOptionsForChallenge(challengeId: Int): List<ChallengeOptionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenges(challenges: List<ChallengeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOptions(options: List<ChallengeOptionEntity>)
}

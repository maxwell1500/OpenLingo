package com.duo.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.duo.app.data.local.entities.UserProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {

    @Query("SELECT * FROM user_progress WHERE userId = :userId LIMIT 1")
    fun getUserProgress(userId: String): Flow<UserProgressEntity?>

    @Query("SELECT * FROM user_progress WHERE userId = :userId LIMIT 1")
    suspend fun getUserProgressDirect(userId: String): UserProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserProgress(progress: UserProgressEntity)

    @Query("UPDATE user_progress SET hearts = :hearts, lastSynced = :timestamp WHERE userId = :userId")
    suspend fun updateHearts(userId: String, hearts: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE user_progress SET points = points + :pointsDelta, lastSynced = :timestamp WHERE userId = :userId")
    suspend fun addPoints(userId: String, pointsDelta: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE user_progress SET activeCourseId = :courseId, lastSynced = :timestamp WHERE userId = :userId")
    suspend fun setActiveCourse(userId: String, courseId: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE user_progress SET streak = :streak, lastActiveDate = :date, lastSynced = :timestamp WHERE userId = :userId")
    suspend fun updateStreak(userId: String, streak: Int, date: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE user_progress SET showRomaji = :showRomaji, lastSynced = :timestamp WHERE userId = :userId")
    suspend fun setShowRomaji(userId: String, showRomaji: Boolean, timestamp: Long = System.currentTimeMillis())
    @Query("UPDATE user_progress SET soundEnabled = :enabled, lastSynced = :timestamp WHERE userId = :userId")
    suspend fun setSoundEnabled(userId: String, enabled: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE user_progress SET hapticsEnabled = :enabled, lastSynced = :timestamp WHERE userId = :userId")
    suspend fun setHapticsEnabled(userId: String, enabled: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE user_progress SET onboardingSeen = :seen, lastSynced = :timestamp WHERE userId = :userId")
    suspend fun setOnboardingSeen(userId: String, seen: Boolean, timestamp: Long = System.currentTimeMillis())
    @Query("UPDATE user_progress SET brokenStreak = :value, lastSynced = :timestamp WHERE userId = :userId")
    suspend fun setBrokenStreak(userId: String, value: Int, timestamp: Long = System.currentTimeMillis())
    @Query("UPDATE user_progress SET themeAccent = :accent, lastSynced = :timestamp WHERE userId = :userId")
    suspend fun setThemeAccent(userId: String, accent: String, timestamp: Long = System.currentTimeMillis())
    @Query("UPDATE user_progress SET themeMode = :mode, lastSynced = :timestamp WHERE userId = :userId")
    suspend fun setThemeMode(userId: String, mode: String, timestamp: Long = System.currentTimeMillis())
}

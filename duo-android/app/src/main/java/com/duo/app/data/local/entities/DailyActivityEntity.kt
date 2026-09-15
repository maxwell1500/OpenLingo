package com.duo.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * XP earned per calendar day (local date string `yyyy-MM-dd`).
 * Backs the daily quest; a missing row means zero XP for that day.
 */
@Entity(tableName = "daily_activity")
data class DailyActivityEntity(
    @PrimaryKey val date: String,
    val xp: Int = 0,
)

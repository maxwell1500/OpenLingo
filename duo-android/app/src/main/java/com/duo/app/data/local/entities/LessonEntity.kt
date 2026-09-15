package com.duo.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey val id: Int,
    val unitId: Int,
    val title: String,
    val orderIndex: Int,
    val lastSynced: Long = System.currentTimeMillis(),
)

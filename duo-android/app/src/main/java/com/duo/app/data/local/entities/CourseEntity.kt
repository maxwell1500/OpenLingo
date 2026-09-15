package com.duo.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val imageSrc: String,
    val lastSynced: Long = System.currentTimeMillis(),
)

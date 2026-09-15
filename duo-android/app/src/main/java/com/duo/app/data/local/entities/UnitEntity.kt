package com.duo.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "units")
data class UnitEntity(
    @PrimaryKey val id: Int,
    val courseId: Int,
    val title: String,
    val description: String,
    val orderIndex: Int,
    val lastSynced: Long = System.currentTimeMillis(),
)

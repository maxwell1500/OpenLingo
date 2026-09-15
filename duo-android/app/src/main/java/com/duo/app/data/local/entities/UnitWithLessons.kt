package com.duo.app.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

data class UnitWithLessons(
    @Embedded val unit: UnitEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "unitId"
    )
    val lessons: List<LessonEntity>
)

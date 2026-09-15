package com.duo.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.duo.app.data.local.entities.UnitWithLessons
import com.duo.app.data.local.entities.CourseEntity
import com.duo.app.data.local.entities.LessonEntity
import com.duo.app.data.local.entities.UnitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {

    @Query("SELECT * FROM courses ORDER BY id ASC")
    fun getAllCourses(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE id = :courseId LIMIT 1")
    suspend fun getCourseById(courseId: Int): CourseEntity?

    @Query("SELECT * FROM units WHERE courseId = :courseId ORDER BY orderIndex ASC")
    fun getUnitsForCourse(courseId: Int): Flow<List<UnitEntity>>

    @Query("SELECT * FROM units WHERE courseId = :courseId ORDER BY orderIndex ASC")
    suspend fun getUnitsForCourseDirect(courseId: Int): List<UnitEntity>
    @Transaction
    @Query("SELECT * FROM units WHERE courseId = :courseId ORDER BY orderIndex ASC")
    fun getUnitsWithLessonsForCourse(courseId: Int): Flow<List<UnitWithLessons>>


    @Query("SELECT * FROM lessons WHERE unitId = :unitId ORDER BY orderIndex ASC")
    fun getLessonsForUnit(unitId: Int): Flow<List<LessonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<CourseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnits(units: List<UnitEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<LessonEntity>)
}

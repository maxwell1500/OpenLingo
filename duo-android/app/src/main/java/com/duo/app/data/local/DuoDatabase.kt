package com.duo.app.data.local
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.duo.app.data.local.dao.CharacterMasteryDao
import com.duo.app.data.local.dao.ChallengeProgressDao
import com.duo.app.data.local.dao.DailyActivityDao
import com.duo.app.data.local.dao.CourseDao
import com.duo.app.data.local.dao.LessonDao
import com.duo.app.data.local.dao.MistakeDao
import com.duo.app.data.local.dao.UserProgressDao
import com.duo.app.data.local.entities.CharacterMasteryEntity
import com.duo.app.data.local.entities.ChallengeEntity
import com.duo.app.data.local.entities.ChallengeOptionEntity
import com.duo.app.data.local.entities.ChallengeProgressEntity
import com.duo.app.data.local.entities.DailyActivityEntity
import com.duo.app.data.local.entities.CourseEntity
import com.duo.app.data.local.entities.LessonEntity
import com.duo.app.data.local.entities.MistakeEntity
import com.duo.app.data.local.entities.UnitEntity
import com.duo.app.data.local.entities.UserProgressEntity
@Database(
    entities = [
        CourseEntity::class,
        UnitEntity::class,
        LessonEntity::class,
        ChallengeEntity::class,
        ChallengeOptionEntity::class,
        UserProgressEntity::class,
        ChallengeProgressEntity::class,
        DailyActivityEntity::class,
        CharacterMasteryEntity::class,
        MistakeEntity::class,
    ],
    version = 7,
    exportSchema = false,
)
abstract class DuoDatabase : RoomDatabase() {

    abstract fun courseDao(): CourseDao
    abstract fun lessonDao(): LessonDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun challengeProgressDao(): ChallengeProgressDao
    abstract fun characterMasteryDao(): CharacterMasteryDao
    abstract fun mistakeDao(): MistakeDao
    abstract fun dailyActivityDao(): DailyActivityDao

    companion object {
        @Volatile
        private var INSTANCE: DuoDatabase? = null

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `character_mastery` (" +
                        "`character` TEXT NOT NULL, `script` TEXT NOT NULL, " +
                        "`attempts` INTEGER NOT NULL, `masteredAt` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`character`))"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `mistakes` (" +
                        "`challengeId` INTEGER NOT NULL, `lessonId` INTEGER NOT NULL, " +
                        "`timestamp` INTEGER NOT NULL, PRIMARY KEY(`challengeId`))"
                )
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `user_progress` ADD COLUMN `soundEnabled` INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE `user_progress` ADD COLUMN `hapticsEnabled` INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE `user_progress` ADD COLUMN `onboardingSeen` INTEGER NOT NULL DEFAULT 0")
            }
        }
        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `user_progress` ADD COLUMN `brokenStreak` INTEGER NOT NULL DEFAULT 0")
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `daily_activity` (" +
                        "`date` TEXT NOT NULL, `xp` INTEGER NOT NULL, PRIMARY KEY(`date`))"
                )
            }
        }


        fun getInstance(context: Context): DuoDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    DuoDatabase::class.java,
                    "duo_local.db",
                )
                    .addMigrations(MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

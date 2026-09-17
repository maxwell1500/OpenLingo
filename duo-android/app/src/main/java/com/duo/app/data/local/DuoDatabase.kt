package com.duo.app.data.local
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.duo.app.data.local.dao.CheckpointScoreDao
import com.duo.app.data.local.dao.CharacterMasteryDao
import com.duo.app.data.local.dao.ChallengeProgressDao
import com.duo.app.data.local.dao.DailyActivityDao
import com.duo.app.data.local.dao.CourseDao
import com.duo.app.data.local.dao.LessonDao
import com.duo.app.data.local.dao.MistakeDao
import com.duo.app.data.local.dao.UserProgressDao
import com.duo.app.data.local.dao.VocabScheduleDao
import com.duo.app.data.local.dao.ExerciseTypeStatsDao
import com.duo.app.data.local.entities.CheckpointScoreEntity
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
import com.duo.app.data.local.entities.VocabScheduleEntity
import com.duo.app.data.local.entities.ExerciseTypeStatsEntity
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
        CheckpointScoreEntity::class,
        VocabScheduleEntity::class,
        ExerciseTypeStatsEntity::class,
    ],
    version = 12,
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
    abstract fun checkpointScoreDao(): CheckpointScoreDao
    abstract fun vocabScheduleDao(): VocabScheduleDao
    abstract fun exerciseTypeStatsDao(): ExerciseTypeStatsDao

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

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `user_progress` ADD COLUMN `themeAccent` TEXT NOT NULL DEFAULT 'TEAL'")
            }
        }

        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `user_progress` ADD COLUMN `themeMode` TEXT NOT NULL DEFAULT 'SYSTEM'")
            }
        }

        val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `checkpoint_scores` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "`userId` TEXT NOT NULL, `courseId` INTEGER NOT NULL, " +
                        "`level` TEXT NOT NULL, `correct` INTEGER NOT NULL, " +
                        "`total` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL)"
                )
            }
        }

        val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `vocab_schedule` (" +
                        "`id` TEXT NOT NULL, `language` TEXT NOT NULL, " +
                        "`foreign` TEXT NOT NULL, `romaji` TEXT, " +
                        "`translation` TEXT NOT NULL, `audioSrc` TEXT, " +
                        "`category` TEXT NOT NULL, `difficulty` REAL NOT NULL, " +
                        "`stability` REAL NOT NULL, `reps` INTEGER NOT NULL, " +
                        "`lapses` INTEGER NOT NULL, `state` INTEGER NOT NULL, " +
                        "`lastReview` INTEGER NOT NULL, `due` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`id`))"
                )
            }
        }

        val MIGRATION_11_12 = object : Migration(11, 12) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `exercise_type_stats` (" +
                        "`type` TEXT NOT NULL, `attempts` INTEGER NOT NULL, " +
                        "`correct` INTEGER NOT NULL, PRIMARY KEY(`type`))"
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
                    .addMigrations(MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11, MIGRATION_11_12)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

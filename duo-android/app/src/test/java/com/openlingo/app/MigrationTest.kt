package com.openlingo.app

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import com.openlingo.app.data.local.DuoDatabase
import java.io.File
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * WI-08/WI-13: the 12 -> 13 -> 14 -> 15 upgrade, exercised against a real
 * on-disk database.
 *
 * The repository tests all use `Room.inMemoryDatabaseBuilder`, which creates the
 * current schema directly and therefore never runs a migration. A broken
 * MIGRATION_12_13 or MIGRATION_13_14 would leave every other test green while
 * every existing install crashed on upgrade with Room's "Migration didn't
 * properly handle" error. This test closes that hole.
 *
 * Room validates the migrated schema against the current entity definitions on
 * open, so a missing column, a wrong type, a wrong nullability, or a wrong
 * DEFAULT all fail here rather than on a user's phone.
 *
 * Two fixtures, because the chain alone does not prove the last step: one
 * starts at v12 and walks the whole chain, and one starts at v14 - a database
 * that has already been through 12_13 and 13_14 - so MIGRATION_14_15 is executed
 * on its own against the exact schema a v14 install has.
 *
 * Why not `MigrationTestHelper`: `@Database(exportSchema = false)` means there
 * are no exported schema JSONs for it to read a historical schema from, and
 * retro-generating a v12 export would mean building the app as it stood at v12.
 * The DDL below is instead taken verbatim from Room's own generated
 * `createAllTables` with exactly the columns the migrations add removed - so the
 * fixture cannot drift from the entities silently.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MigrationTest {

    private val dbName = "migration_test.db"
    private val dbFile: File
        get() = ApplicationProvider.getApplicationContext<Context>().getDatabasePath(dbName)

    @Before
    fun setUp() {
        dbFile.parentFile?.mkdirs()
        if (dbFile.exists()) dbFile.delete()
    }

    @After
    fun tearDown() {
        if (dbFile.exists()) dbFile.delete()
    }

    /**
     * The v12 schema: identical to the generated v15 `createAllTables` minus
     * `challenges.grammaticalFocus` / `ruleText` / `acceptedAnswers`,
     * `challenge_options.errorTag` (all added by MIGRATION_12_13),
     * `challenges.heldOut` (added by MIGRATION_13_14) and
     * `user_progress.dailyQuestGoal` (added by MIGRATION_14_15).
     */
    private fun createV12Database() {
        val db = SQLiteDatabase.openOrCreateDatabase(dbFile, null)
        db.use {
            it.execSQL("CREATE TABLE IF NOT EXISTS `courses` (`id` INTEGER NOT NULL, `title` TEXT NOT NULL, `imageSrc` TEXT NOT NULL, `lastSynced` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            it.execSQL("CREATE TABLE IF NOT EXISTS `units` (`id` INTEGER NOT NULL, `courseId` INTEGER NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `orderIndex` INTEGER NOT NULL, `lastSynced` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            it.execSQL("CREATE TABLE IF NOT EXISTS `lessons` (`id` INTEGER NOT NULL, `unitId` INTEGER NOT NULL, `title` TEXT NOT NULL, `orderIndex` INTEGER NOT NULL, `lastSynced` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            it.execSQL("CREATE TABLE IF NOT EXISTS `challenges` (`id` INTEGER NOT NULL, `lessonId` INTEGER NOT NULL, `type` TEXT NOT NULL, `question` TEXT NOT NULL, `romaji` TEXT, `orderIndex` INTEGER NOT NULL, `audioSrc` TEXT, `lastSynced` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            it.execSQL("CREATE TABLE IF NOT EXISTS `challenge_options` (`id` INTEGER NOT NULL, `challengeId` INTEGER NOT NULL, `text` TEXT NOT NULL, `correct` INTEGER NOT NULL, `romaji` TEXT, `imageSrc` TEXT, `audioSrc` TEXT, `lastSynced` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            it.execSQL("CREATE TABLE IF NOT EXISTS `user_progress` (`userId` TEXT NOT NULL, `userName` TEXT NOT NULL, `userImageSrc` TEXT NOT NULL, `activeCourseId` INTEGER NOT NULL, `hearts` INTEGER NOT NULL, `points` INTEGER NOT NULL, `streak` INTEGER NOT NULL, `lastActiveDate` TEXT NOT NULL, `showRomaji` INTEGER NOT NULL, `soundEnabled` INTEGER NOT NULL, `hapticsEnabled` INTEGER NOT NULL, `onboardingSeen` INTEGER NOT NULL, `brokenStreak` INTEGER NOT NULL, `themeAccent` TEXT NOT NULL, `themeMode` TEXT NOT NULL, `lastSynced` INTEGER NOT NULL, PRIMARY KEY(`userId`))")
            it.execSQL("CREATE TABLE IF NOT EXISTS `challenge_progress` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` TEXT NOT NULL, `challengeId` INTEGER NOT NULL, `completed` INTEGER NOT NULL, `synced` INTEGER NOT NULL, `lastSynced` INTEGER NOT NULL)")
            it.execSQL("CREATE TABLE IF NOT EXISTS `daily_activity` (`date` TEXT NOT NULL, `xp` INTEGER NOT NULL, PRIMARY KEY(`date`))")
            it.execSQL("CREATE TABLE IF NOT EXISTS `character_mastery` (`character` TEXT NOT NULL, `script` TEXT NOT NULL, `attempts` INTEGER NOT NULL, `masteredAt` INTEGER NOT NULL, PRIMARY KEY(`character`))")
            it.execSQL("CREATE TABLE IF NOT EXISTS `mistakes` (`challengeId` INTEGER NOT NULL, `lessonId` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`challengeId`))")
            it.execSQL("CREATE TABLE IF NOT EXISTS `checkpoint_scores` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` TEXT NOT NULL, `courseId` INTEGER NOT NULL, `level` TEXT NOT NULL, `correct` INTEGER NOT NULL, `total` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL)")
            it.execSQL("CREATE TABLE IF NOT EXISTS `vocab_schedule` (`id` TEXT NOT NULL, `language` TEXT NOT NULL, `foreign` TEXT NOT NULL, `romaji` TEXT, `translation` TEXT NOT NULL, `audioSrc` TEXT, `category` TEXT NOT NULL, `difficulty` REAL NOT NULL, `stability` REAL NOT NULL, `reps` INTEGER NOT NULL, `lapses` INTEGER NOT NULL, `state` INTEGER NOT NULL, `lastReview` INTEGER NOT NULL, `due` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            it.execSQL("CREATE TABLE IF NOT EXISTS `exercise_type_stats` (`type` TEXT NOT NULL, `attempts` INTEGER NOT NULL, `correct` INTEGER NOT NULL, PRIMARY KEY(`type`))")

            // Real pre-upgrade data. Losing a learner's progress on upgrade is the
            // failure this test exists to make impossible.
            it.execSQL("INSERT INTO `challenges` (`id`,`lessonId`,`type`,`question`,`romaji`,`orderIndex`,`audioSrc`,`lastSynced`) VALUES (1001, 100, 'SELECT', 'Which one of these is the man?', NULL, 0, NULL, 111)")
            it.execSQL("INSERT INTO `challenge_options` (`id`,`challengeId`,`text`,`correct`,`romaji`,`imageSrc`,`audioSrc`,`lastSynced`) VALUES (10001, 1001, 'El hombre', 1, NULL, NULL, NULL, 111)")
            it.execSQL("INSERT INTO `user_progress` (`userId`,`userName`,`userImageSrc`,`activeCourseId`,`hearts`,`points`,`streak`,`lastActiveDate`,`showRomaji`,`soundEnabled`,`hapticsEnabled`,`onboardingSeen`,`brokenStreak`,`themeAccent`,`themeMode`,`lastSynced`) VALUES ('guest_local','Learner','',1,3,420,7,'2026-09-01',1,1,1,1,0,'TEAL','SYSTEM',111)")

            it.version = 12
        }
    }

    /**
     * The v14 schema: the v12 DDL plus exactly the columns MIGRATION_12_13
     * (`challenges.grammaticalFocus` / `ruleText` / `acceptedAnswers`,
     * `challenge_options.errorTag`) and MIGRATION_13_14 (`challenges.heldOut`)
     * add, and without `user_progress.dailyQuestGoal`. This is what a learner
     * who installed the app at v14 is carrying when they take the v15 upgrade.
     *
     * The row values are illustrative, not corpus data: ids, names and dates are
     * invented to make the upgrade observable. They are kept honest about the
     * app's content rules all the same - `grammaticalFocus` uses a real focus
     * (`es.ser_estar`), the correct option carries no `errorTag`, and only the
     * distractor carries a tag the focus declares honest (`WRONG_COPULA`) - so
     * this fixture never doubles as an example of a row the app could not
     * legitimately produce.
     */
    private fun createV14Database() {
        val db = SQLiteDatabase.openOrCreateDatabase(dbFile, null)
        db.use {
            it.execSQL("CREATE TABLE IF NOT EXISTS `courses` (`id` INTEGER NOT NULL, `title` TEXT NOT NULL, `imageSrc` TEXT NOT NULL, `lastSynced` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            it.execSQL("CREATE TABLE IF NOT EXISTS `units` (`id` INTEGER NOT NULL, `courseId` INTEGER NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `orderIndex` INTEGER NOT NULL, `lastSynced` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            it.execSQL("CREATE TABLE IF NOT EXISTS `lessons` (`id` INTEGER NOT NULL, `unitId` INTEGER NOT NULL, `title` TEXT NOT NULL, `orderIndex` INTEGER NOT NULL, `lastSynced` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            it.execSQL("CREATE TABLE IF NOT EXISTS `challenges` (`id` INTEGER NOT NULL, `lessonId` INTEGER NOT NULL, `type` TEXT NOT NULL, `question` TEXT NOT NULL, `romaji` TEXT, `orderIndex` INTEGER NOT NULL, `audioSrc` TEXT, `lastSynced` INTEGER NOT NULL, `grammaticalFocus` TEXT, `ruleText` TEXT, `acceptedAnswers` TEXT, `heldOut` INTEGER NOT NULL DEFAULT 0, PRIMARY KEY(`id`))")
            it.execSQL("CREATE TABLE IF NOT EXISTS `challenge_options` (`id` INTEGER NOT NULL, `challengeId` INTEGER NOT NULL, `text` TEXT NOT NULL, `correct` INTEGER NOT NULL, `romaji` TEXT, `imageSrc` TEXT, `audioSrc` TEXT, `lastSynced` INTEGER NOT NULL, `errorTag` TEXT, PRIMARY KEY(`id`))")
            it.execSQL("CREATE TABLE IF NOT EXISTS `user_progress` (`userId` TEXT NOT NULL, `userName` TEXT NOT NULL, `userImageSrc` TEXT NOT NULL, `activeCourseId` INTEGER NOT NULL, `hearts` INTEGER NOT NULL, `points` INTEGER NOT NULL, `streak` INTEGER NOT NULL, `lastActiveDate` TEXT NOT NULL, `showRomaji` INTEGER NOT NULL, `soundEnabled` INTEGER NOT NULL, `hapticsEnabled` INTEGER NOT NULL, `onboardingSeen` INTEGER NOT NULL, `brokenStreak` INTEGER NOT NULL, `themeAccent` TEXT NOT NULL, `themeMode` TEXT NOT NULL, `lastSynced` INTEGER NOT NULL, PRIMARY KEY(`userId`))")
            it.execSQL("CREATE TABLE IF NOT EXISTS `challenge_progress` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` TEXT NOT NULL, `challengeId` INTEGER NOT NULL, `completed` INTEGER NOT NULL, `synced` INTEGER NOT NULL, `lastSynced` INTEGER NOT NULL)")
            it.execSQL("CREATE TABLE IF NOT EXISTS `daily_activity` (`date` TEXT NOT NULL, `xp` INTEGER NOT NULL, PRIMARY KEY(`date`))")
            it.execSQL("CREATE TABLE IF NOT EXISTS `character_mastery` (`character` TEXT NOT NULL, `script` TEXT NOT NULL, `attempts` INTEGER NOT NULL, `masteredAt` INTEGER NOT NULL, PRIMARY KEY(`character`))")
            it.execSQL("CREATE TABLE IF NOT EXISTS `mistakes` (`challengeId` INTEGER NOT NULL, `lessonId` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`challengeId`))")
            it.execSQL("CREATE TABLE IF NOT EXISTS `checkpoint_scores` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` TEXT NOT NULL, `courseId` INTEGER NOT NULL, `level` TEXT NOT NULL, `correct` INTEGER NOT NULL, `total` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL)")
            it.execSQL("CREATE TABLE IF NOT EXISTS `vocab_schedule` (`id` TEXT NOT NULL, `language` TEXT NOT NULL, `foreign` TEXT NOT NULL, `romaji` TEXT, `translation` TEXT NOT NULL, `audioSrc` TEXT, `category` TEXT NOT NULL, `difficulty` REAL NOT NULL, `stability` REAL NOT NULL, `reps` INTEGER NOT NULL, `lapses` INTEGER NOT NULL, `state` INTEGER NOT NULL, `lastReview` INTEGER NOT NULL, `due` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            it.execSQL("CREATE TABLE IF NOT EXISTS `exercise_type_stats` (`type` TEXT NOT NULL, `attempts` INTEGER NOT NULL, `correct` INTEGER NOT NULL, PRIMARY KEY(`type`))")
            it.execSQL("INSERT INTO `challenges` (`id`,`lessonId`,`type`,`question`,`romaji`,`orderIndex`,`audioSrc`,`lastSynced`,`grammaticalFocus`,`heldOut`) VALUES (2001, 200, 'SELECT', 'Which one says \"I am a boy\"?', NULL, 0, NULL, 222, 'es.ser_estar', 0)")
            it.execSQL("INSERT INTO `challenge_options` (`id`,`challengeId`,`text`,`correct`,`romaji`,`imageSrc`,`audioSrc`,`lastSynced`,`errorTag`) VALUES (20001, 2001, 'Soy un niño', 1, NULL, NULL, NULL, 222, NULL)")
            it.execSQL("INSERT INTO `challenge_options` (`id`,`challengeId`,`text`,`correct`,`romaji`,`imageSrc`,`audioSrc`,`lastSynced`,`errorTag`) VALUES (20002, 2001, 'Estoy un niño', 0, NULL, NULL, NULL, 222, 'WRONG_COPULA')")
            it.execSQL("INSERT INTO `user_progress` (`userId`,`userName`,`userImageSrc`,`activeCourseId`,`hearts`,`points`,`streak`,`lastActiveDate`,`showRomaji`,`soundEnabled`,`hapticsEnabled`,`onboardingSeen`,`brokenStreak`,`themeAccent`,`themeMode`,`lastSynced`) VALUES ('guest_local','Learner','',2,4,910,12,'2026-09-20',1,1,1,1,0,'PURPLE','DARK',222)")
            it.execSQL("INSERT INTO `daily_activity` (`date`,`xp`) VALUES ('2026-09-20', 60)")

            it.version = 14
        }
    }

    /**
     * MIGRATION_14_15 on its own, against a v14 database that is already past
     * 12_13 and 13_14. The chain test above also runs it, but only as the third
     * of three steps; this one fails if 14_15 is broken in isolation - e.g. if
     * it were dropped from `addMigrations`, or if its column name drifted.
     */
    @Test
    fun `a real v14 database upgrades to v15 and keeps the learner's data`() = runTest {
        createV14Database()

        val db = Room.databaseBuilder(
            ApplicationProvider.getApplicationContext(),
            DuoDatabase::class.java,
            dbName,
        )
            .addMigrations(DuoDatabase.MIGRATION_14_15)
            .allowMainThreadQueries()
            .build()

        try {
            assertEquals(15, db.openHelper.readableDatabase.version)

            val progress = db.userProgressDao().getUserProgressDirect("guest_local")
            assertTrue("learner progress row was lost", progress != null)
            assertEquals(910, progress!!.points)
            assertEquals(12, progress.streak)
            assertEquals(2, progress.activeCourseId)
            assertEquals("PURPLE", progress.themeAccent)

            // The goal the learner never chose: the migration's DEFAULT puts
            // every existing row on the same 30 XP quest the constant gave them.
            assertEquals(30, progress.dailyQuestGoal)

            // The goal is writable on the upgraded schema, not just readable.
            db.userProgressDao().setDailyQuestGoal("guest_local", 100)
            assertEquals(100, db.userProgressDao().getUserProgressDirect("guest_local")?.dailyQuestGoal)

            // Everything MIGRATION_12_13 and MIGRATION_13_14 already added is
            // still exactly where those migrations put it.
            val challenge = db.lessonDao().getChallengeById(2001)!!
            assertEquals("es.ser_estar", challenge.grammaticalFocus)
            assertEquals(false, challenge.heldOut)

            // Both options came through, and errorTag - the column
            // MIGRATION_12_13 added - still reads back per row: null on the
            // correct option, the tag the distractor was written with.
            val options = db.lessonDao().getOptionsForChallenge(2001)
            assertEquals(2, options.size)
            assertNull(options.first { it.correct }.errorTag)
            assertEquals("WRONG_COPULA", options.first { !it.correct }.errorTag)
        } finally {
            db.close()
        }
    }

    /**
     * Column name to (declared type, NOT NULL, DEFAULT) for one table, read with
     * `PRAGMA table_info`. A `null` entry means the column is absent, which is
     * itself the assertion this test is built on.
     */
    private fun SupportSQLiteDatabase.columnInfo(table: String): Map<String, Triple<String, Boolean, String?>> {
        val info = mutableMapOf<String, Triple<String, Boolean, String?>>()
        query("PRAGMA table_info(`$table`)").use { cursor ->
            val nameIndex = cursor.getColumnIndexOrThrow("name")
            val typeIndex = cursor.getColumnIndexOrThrow("type")
            val notNullIndex = cursor.getColumnIndexOrThrow("notnull")
            val defaultIndex = cursor.getColumnIndexOrThrow("dflt_value")
            while (cursor.moveToNext()) {
                info[cursor.getString(nameIndex)] = Triple(
                    cursor.getString(typeIndex),
                    cursor.getInt(notNullIndex) == 1,
                    if (cursor.isNull(defaultIndex)) null else cursor.getString(defaultIndex),
                )
            }
        }
        return info
    }

    /**
     * MIGRATION_12_13 on its own, against a v12 database, pinning the **v13**
     * schema it leaves behind.
     *
     * The chain test below also runs 12_13, but only as the first of three links,
     * and Room validates the schema once — at v15. A statement that reaches past
     * its own step is therefore invisible to it: had 12_13 also added `heldOut`
     * (a 13_14 column) or `dailyQuestGoal` (a 14_15 column), the v15 database
     * would still validate and the chain would still pass — while every install
     * sitting on v12 or v13 at the time broke, because MIGRATION_13_14 would
     * then fail with "duplicate column name" against a table that already had
     * it. The same masking applies in the other direction: 12_13 dropping a
     * column a later migration re-adds nets out to a valid v15 schema.
     *
     * So this runs the migration object directly, with no successor, and pins
     * the intermediate state from both sides — the four columns it owns, and the
     * two later migrations own that it must leave alone.
     */
    @Test
    fun `MIGRATION_12_13 alone takes a v12 database to exactly v13`() {
        createV12Database()

        // The v12 file already exists with user_version = 12, so the framework
        // helper opens it as it stands: onCreate never runs and onUpgrade never
        // runs. The only thing that changes the schema is the migration itself.
        val helper = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration
                .builder(ApplicationProvider.getApplicationContext())
                .name(dbName)
                .callback(
                    object : SupportSQLiteOpenHelper.Callback(12) {
                        override fun onCreate(db: SupportSQLiteDatabase) = Unit
                        override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
                    },
                )
                .build(),
        )
        val migrated = helper.writableDatabase
        try {
            DuoDatabase.MIGRATION_12_13.migrate(migrated)

            val challenges = migrated.columnInfo("challenges")

            // The three challenge columns 12_13 owns. Nullable TEXT with no
            // DEFAULT: "this row is not a grammar-bearing item" is a meaningful
            // absence, so a pre-upgrade row reads back as null rather than "".
            for (column in listOf("grammaticalFocus", "ruleText", "acceptedAnswers")) {
                val (type, notNull, default) = requireNotNull(challenges[column]) {
                    "MIGRATION_12_13 did not add challenges.$column; v13 columns are ${challenges.keys}"
                }
                assertEquals("challenges.$column type", "TEXT", type)
                assertTrue("challenges.$column must stay nullable", !notNull)
                assertNull("challenges.$column must have no DEFAULT", default)
            }

            val optionColumns = migrated.columnInfo("challenge_options")
            val errorTag = requireNotNull(optionColumns["errorTag"]) {
                "MIGRATION_12_13 did not add challenge_options.errorTag; v13 columns are ${optionColumns.keys}"
            }
            assertEquals("challenge_options.errorTag type", "TEXT", errorTag.first)
            assertTrue("challenge_options.errorTag must stay nullable", !errorTag.second)
            assertNull("challenge_options.errorTag must have no DEFAULT", errorTag.third)

            // The other direction: columns the *next* migrations add. A v13
            // database must not carry them, or MIGRATION_13_14 cannot run.
            assertNull(
                "MIGRATION_12_13 added challenges.heldOut, which is MIGRATION_13_14's column; " +
                    "a v13 database would fail its own 13->14 step with 'duplicate column name'",
                challenges["heldOut"],
            )
            assertNull(
                "MIGRATION_12_13 added user_progress.dailyQuestGoal, which is MIGRATION_14_15's column",
                migrated.columnInfo("user_progress")["dailyQuestGoal"],
            )

            // The pre-upgrade rows came through the ALTER TABLEs untouched, and
            // read back as null on exactly the columns that did not exist before.
            migrated.query("SELECT `question`, `grammaticalFocus`, `ruleText`, `acceptedAnswers` FROM `challenges` WHERE `id` = 1001")
                .use { cursor ->
                    assertTrue("the pre-upgrade challenge row was lost", cursor.moveToFirst())
                    assertEquals("Which one of these is the man?", cursor.getString(0))
                    assertNull(cursor.getString(1))
                    assertNull(cursor.getString(2))
                    assertNull(cursor.getString(3))
                }
            migrated.query("SELECT `text`, `errorTag` FROM `challenge_options` WHERE `id` = 10001")
                .use { cursor ->
                    assertTrue("the pre-upgrade option row was lost", cursor.moveToFirst())
                    assertEquals("El hombre", cursor.getString(0))
                    assertNull(cursor.getString(1))
                }

            // And the v13 schema is writable through the new columns, which is
            // what a v13 install actually does.
            migrated.execSQL(
                "UPDATE `challenges` SET `grammaticalFocus` = ?, `ruleText` = ?, `acceptedAnswers` = ? WHERE `id` = 1001",
                arrayOf("es.preterito.regular", "Ending marks tense and person.", "hablé|hablamos"),
            )
            migrated.query("SELECT `grammaticalFocus`, `acceptedAnswers` FROM `challenges` WHERE `id` = 1001")
                .use { cursor ->
                    assertTrue(cursor.moveToFirst())
                    assertEquals("es.preterito.regular", cursor.getString(0))
                    assertEquals("hablé|hablamos", cursor.getString(1))
                }
        } finally {
            helper.close()
        }
    }

    @Test
    fun `a real v12 database upgrades to v15 and keeps the learner's data`() = runTest {
        createV12Database()

        // Opening runs MIGRATION_12_13, MIGRATION_13_14 then MIGRATION_14_15,
        // then validates the resulting schema against every v15 entity. A bad
        // migration throws here.
        val db = Room.databaseBuilder(
            ApplicationProvider.getApplicationContext(),
            DuoDatabase::class.java,
            dbName,
        )
            .addMigrations(
                DuoDatabase.MIGRATION_12_13,
                DuoDatabase.MIGRATION_13_14,
                DuoDatabase.MIGRATION_14_15,
            )
            .allowMainThreadQueries()
            .build()

        try {
            assertEquals(15, db.openHelper.readableDatabase.version)

            // Pre-upgrade rows survived the ALTER TABLE chain untouched.
            val challenge = db.lessonDao().getChallengeById(1001)
            assertTrue("pre-upgrade challenge row was lost", challenge != null)
            assertEquals(100, challenge!!.lessonId)
            assertEquals("Which one of these is the man?", challenge.question)
            assertEquals(111L, challenge.lastSynced)

            val options = db.lessonDao().getOptionsForChallenge(1001)
            assertEquals(1, options.size)
            assertEquals("El hombre", options[0].text)
            assertTrue(options[0].correct)

            val progress = db.userProgressDao().getUserProgressDirect("guest_local")
            assertTrue("learner progress row was lost", progress != null)
            assertEquals(420, progress!!.points)
            assertEquals(7, progress.streak)

            // MIGRATION_14_15 must not disturb a learner's existing progress;
            // it only adds the goal column, defaulting to the old fixed target.
            assertEquals(30, progress.dailyQuestGoal)

            // Columns added by MIGRATION_12_13 default to null on old rows.
            assertNull(challenge.grammaticalFocus)
            assertNull(challenge.ruleText)
            assertNull(challenge.acceptedAnswers)
            assertNull(options[0].errorTag)

            // heldOut is NOT NULL DEFAULT 0: an upgrade must not silently move
            // existing taught content off the lesson path and into the
            // checkpoint pool.
            assertEquals(false, challenge.heldOut)

            // The migrated database is usable: a fresh insert carrying the new
            // columns round-trips through the same schema.
            db.lessonDao().insertChallenges(
                listOf(
                    com.openlingo.app.data.local.entities.ChallengeEntity(
                        id = 90001,
                        lessonId = 100,
                        type = com.openlingo.app.data.local.models.ChallengeType.CONJUGATE,
                        question = "Which preterite form of escribir goes with yo?",
                        orderIndex = 1,
                        grammaticalFocus = "es.preterito.regular",
                        heldOut = true,
                    ),
                ),
            )
            val written = db.lessonDao().getChallengeById(90001)!!
            assertEquals(true, written.heldOut)
            assertEquals("es.preterito.regular", written.grammaticalFocus)
        } finally {
            db.close()
        }
    }
}

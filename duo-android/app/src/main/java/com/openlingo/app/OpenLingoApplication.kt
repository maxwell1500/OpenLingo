package com.openlingo.app

import android.app.Application
import com.openlingo.app.data.local.OpenLingoDatabase
import com.openlingo.app.data.repository.LocalProgressRepository
import com.openlingo.app.audio.AudioPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
/**
 * `open` so a test can install its own subclass that skips the seeding below and
 * owns the database it runs against (see PlacementFlowTest).
 */
open class OpenLingoApplication : Application() {

    val database: OpenLingoDatabase by lazy { OpenLingoDatabase.getInstance(this) }
    val repository: LocalProgressRepository by lazy { LocalProgressRepository(database) }
    val audioPlayer: AudioPlayer by lazy { AudioPlayer(this) }

    override fun onCreate() {
        super.onCreate()

        // Initialize local SQLite Room database with Spanish & Japanese offline content
        CoroutineScope(Dispatchers.IO).launch {
            repository.initializeIfNeeded()
        }

        // Arm the midnight streak-reset + heart-refill alarm (refreshDailyState
        // also runs on every app start, so a missed alarm is harmless).
        com.openlingo.app.alarm.DailyResetScheduler.schedule(this)
        com.openlingo.app.alarm.StreakReminderScheduler.schedule(this)
    }
}

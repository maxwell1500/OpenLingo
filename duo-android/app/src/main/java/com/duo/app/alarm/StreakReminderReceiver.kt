package com.duo.app.alarm

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.duo.app.DuoApplication
import com.duo.app.MainActivity
import com.duo.app.R
import java.time.LocalDate
import java.util.Calendar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Local daily reminder at 19:00 (7 PM).
 *
 * If the user hasn't earned any XP today, posts a gentle notification
 * reminding them to keep their streak alive. 100% offline, zero network.
 */
class StreakReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val app = context.applicationContext as? DuoApplication ?: return@launch
                val today = LocalDate.now().toString()
                val todayXp = app.database.dailyActivityDao().getXpDirect(today) ?: 0
                val user = app.repository.getUserProgressDirect()
                val streak = user?.streak ?: 1

                val prefs = context.getSharedPreferences("openlingo_settings", Context.MODE_PRIVATE)
                val remindersEnabled = prefs.getBoolean("reminders_enabled", true)

                if (todayXp == 0 && remindersEnabled) {
                    showNotification(context, streak)
                }
            } catch (e: Exception) {
                android.util.Log.e("StreakReminder", "reminder check failed: ${e.message}")
            } finally {
                StreakReminderScheduler.schedule(context)
                pendingResult.finish()
            }
        }
    }

    private fun showNotification(context: Context, streak: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Streak Reminders",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "Gentle daily nudges to protect your learning streak"
            }
            manager.createNotificationChannel(channel)
        }

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Keep your 🔥 $streak-day streak alive!")
            .setContentText("Take 2 minutes to practice and stay chill with your capybara 🦫")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        manager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "streak_reminders"
        const val NOTIFICATION_ID = 2026
    }
}

object StreakReminderScheduler {
    const val REQUEST_CODE = 1702
    const val ACTION_STREAK_REMINDER = "com.duo.app.action.STREAK_REMINDER"

    fun schedule(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 19)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now)) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = Intent(context, StreakReminderReceiver::class.java).apply {
            action = ACTION_STREAK_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        target.timeInMillis,
                        pendingIntent,
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        target.timeInMillis,
                        pendingIntent,
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    target.timeInMillis,
                    pendingIntent,
                )
            }
        } catch (_: SecurityException) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                target.timeInMillis,
                pendingIntent,
            )
        }
    }
}

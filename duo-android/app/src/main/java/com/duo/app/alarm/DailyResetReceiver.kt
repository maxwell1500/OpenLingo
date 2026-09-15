package com.duo.app.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.duo.app.DuoApplication
import java.util.Calendar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Midnight day-rollover: streak reset when a day was missed + daily heart refill.
 * Fires via AlarmManager (exact when permitted, inexact otherwise), then reschedules
 * itself for the next midnight. App start ([LocalProgressRepository.refreshDailyState]
 * from `initializeIfNeeded`) covers the same path, so a missed alarm is harmless.
 */
class DailyResetReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val app = context.applicationContext as? DuoApplication
                app?.repository?.refreshDailyState()
                com.duo.app.widget.OpenLingoWidgetProvider.updateAll(context)
            } catch (e: Exception) {
                android.util.Log.e("DailyReset", "reset failed: ${e.message}")
            } finally {
                DailyResetScheduler.schedule(context)
                pendingResult.finish()
            }
        }
    }
}

object DailyResetScheduler {

    const val REQUEST_CODE = 1701
    const val ACTION_DAILY_RESET = "com.duo.app.action.DAILY_RESET"

    fun schedule(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            ?: return
        val intent = Intent(context, DailyResetReceiver::class.java).apply {
            action = ACTION_DAILY_RESET
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        // Next local midnight.
        val midnight = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 5)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, midnight, pendingIntent)
        } else {
            @Suppress("DEPRECATION")
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, midnight, pendingIntent)
        }
    }
}

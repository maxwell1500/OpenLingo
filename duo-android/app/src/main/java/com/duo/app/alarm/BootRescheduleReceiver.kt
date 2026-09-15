package com.duo.app.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** After a reboot, re-arm the midnight streak-reset alarm. */
class BootRescheduleReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            DailyResetScheduler.schedule(context)
            StreakReminderScheduler.schedule(context)
        }
    }
}

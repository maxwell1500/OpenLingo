package com.duo.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.duo.app.DuoApplication
import com.duo.app.MainActivity
import com.duo.app.R
import java.time.LocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Android Home Screen Widget for OpenLingo.
 *
 * Displays live streak count, daily quest XP progress, and provides
 * a 1-tap shortcut straight into lessons. Battery-friendly, 100% offline.
 */
class OpenLingoWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        updateAll(context)
    }

    companion object {
        fun updateAll(context: Context) {
            val app = context.applicationContext as? DuoApplication ?: return
            val widgetManager = AppWidgetManager.getInstance(context)
            val component = ComponentName(context, OpenLingoWidgetProvider::class.java)
            val ids = widgetManager.getAppWidgetIds(component)
            if (ids.isEmpty()) return

            CoroutineScope(Dispatchers.IO).launch {
                val user = app.repository.getUserProgressDirect()
                val streak = user?.streak ?: 1
                val today = LocalDate.now().toString()
                val todayXp = app.database.dailyActivityDao().getXpDirect(today) ?: 0
                val questGoal = 30
                val questDone = todayXp >= questGoal

                val launchIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    launchIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )

                val views = RemoteViews(context.packageName, R.layout.widget_openlingo).apply {
                    setTextViewText(R.id.widget_streak, "🔥 $streak")
                    setTextViewText(R.id.widget_quest_xp, "$todayXp/$questGoal XP")
                    setProgressBar(R.id.widget_progress, questGoal, todayXp, false)
                    setOnClickPendingIntent(R.id.widget_root, pendingIntent)

                    if (questDone) {
                        setTextViewText(R.id.widget_quest_title, "✅ Quest Complete!")
                        setTextViewText(R.id.widget_subtitle, "Great job today! Stay chill ☕")
                    } else {
                        setTextViewText(R.id.widget_quest_title, "🎯 Daily Quest")
                        setTextViewText(R.id.widget_subtitle, "Tap to practice • Keep the streak! 🦫")
                    }
                }

                ids.forEach { id ->
                    widgetManager.updateAppWidget(id, views)
                }
            }
        }
    }
}

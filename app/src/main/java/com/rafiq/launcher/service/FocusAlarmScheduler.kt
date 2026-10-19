package com.rafiq.launcher.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

object FocusAlarmScheduler {
    private const val PREFS = "rafiq_focus_alarm"
    private const val KEY_END_AT = "end_at"
    private const val REQUEST_CODE = 4202

    fun schedule(context: Context, endAt: Long) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putLong(KEY_END_AT, endAt)
            .apply()
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        val pending = pendingIntent(context)
        alarmManager.cancel(pending)
        val canExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            alarmManager.canScheduleExactAlarms()
        if (canExact) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, endAt, pending)
        } else {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, endAt, pending)
        }
    }

    fun cancel(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .remove(KEY_END_AT)
            .apply()
        context.getSystemService(AlarmManager::class.java)?.cancel(pendingIntent(context))
    }

    fun rescheduleFromPreferences(context: Context) {
        val endAt = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getLong(KEY_END_AT, 0L)
        if (endAt > System.currentTimeMillis()) schedule(context, endAt)
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().remove(KEY_END_AT).apply()
    }

    private fun pendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, FocusAlarmReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}

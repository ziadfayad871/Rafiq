package com.rafiq.launcher.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.rafiq.launcher.data.prayer.CalculationMethod
import com.rafiq.launcher.data.prayer.PrayerCalculator
import com.rafiq.launcher.data.preferences.UserPreferences
import java.time.LocalDateTime
import java.time.ZoneId

object PrayerAlarmScheduler {
    private const val PREFS = "rafiq_prayer_alarm"
    private const val KEY_ENABLED = "enabled"
    private const val KEY_METHOD = "method"
    private const val KEY_LAT = "lat"
    private const val KEY_LNG = "lng"
    private const val KEY_ZONE = "zone"
    private const val REQUEST_CODE = 4101

    fun configure(context: Context, prefs: UserPreferences) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_ENABLED, prefs.prayerNotifications)
            .putString(KEY_METHOD, prefs.calculationMethod)
            .putLong(KEY_LAT, prefs.latitude.toDouble().toRawBits())
            .putLong(KEY_LNG, prefs.longitude.toDouble().toRawBits())
            .putString(KEY_ZONE, prefs.timeZoneId)
            .apply()
        if (prefs.prayerNotifications) scheduleNext(context) else cancel(context)
    }

    fun scheduleNext(context: Context, graceSeconds: Long = 0L) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!prefs.getBoolean(KEY_ENABLED, false)) {
            cancel(context)
            return
        }
        val zone = runCatching {
            ZoneId.of(prefs.getString(KEY_ZONE, ZoneId.systemDefault().id))
        }.getOrDefault(ZoneId.systemDefault())
        val method = CalculationMethod.fromId(prefs.getString(KEY_METHOD, "egyptian") ?: "egyptian")
        val lat = Double.fromBits(prefs.getLong(KEY_LAT, 30.0444.toRawBits()))
        val lng = Double.fromBits(prefs.getLong(KEY_LNG, 31.2357.toRawBits()))
        val now = LocalDateTime.now(zone).plusSeconds(graceSeconds)
        val today = PrayerCalculator.calculate(now.toLocalDate(), lat, lng, zone, method)
        val tomorrow = PrayerCalculator.calculate(now.toLocalDate().plusDays(1), lat, lng, zone, method)
        val next = today.prayers.firstOrNull { it.time.isAfter(now) } ?: tomorrow.prayers.first()
        val triggerAt = next.time.atZone(zone).toInstant().toEpochMilli()

        val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            putExtra("prayer_name", next.name)
            putExtra("prayer_time", next.formattedTime)
        }
        val pending = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        alarmManager.cancel(pending)
        val canExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            alarmManager.canScheduleExactAlarms()
        if (canExact) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
        } else {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
        }
    }

    fun rescheduleFromPreferences(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (prefs.getBoolean(KEY_ENABLED, false)) scheduleNext(context)
    }

    fun canScheduleExact(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        return context.getSystemService(AlarmManager::class.java)?.canScheduleExactAlarms() == true
    }

    fun openExactAlarmSettings(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
        runCatching {
            context.startActivity(
                Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    fun cancel(context: Context) {
        val intent = Intent(context, PrayerAlarmReceiver::class.java)
        val pending = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        context.getSystemService(AlarmManager::class.java)?.cancel(pending)
    }
}

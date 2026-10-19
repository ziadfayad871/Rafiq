package com.rafiq.launcher.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PrayerAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        RafiqNotifications.ensureChannels(context)
        val name = intent.getStringExtra("prayer_name") ?: return
        val time = intent.getStringExtra("prayer_time") ?: ""
        RafiqNotifications.showPrayer(context, name, time)
        PrayerAlarmScheduler.scheduleNext(context, graceSeconds = 60L)
    }
}

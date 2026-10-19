package com.rafiq.launcher.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED -> Unit
            else -> return
        }
        RafiqNotifications.ensureChannels(context)
        PrayerAlarmScheduler.rescheduleFromPreferences(context)
        FocusAlarmScheduler.rescheduleFromPreferences(context)
    }
}

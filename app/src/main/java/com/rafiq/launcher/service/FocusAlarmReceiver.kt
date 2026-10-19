package com.rafiq.launcher.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class FocusAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        RafiqNotifications.ensureChannels(context)
        RafiqNotifications.showFocusComplete(context)
        FocusAlarmScheduler.clear(context)
    }
}

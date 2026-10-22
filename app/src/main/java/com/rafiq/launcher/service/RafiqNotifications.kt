package com.rafiq.launcher.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.rafiq.launcher.MainActivity
import com.rafiq.launcher.R

object RafiqNotifications {
    const val PRAYER_CHANNEL = "prayer_times_adhan_v1"
    const val FOCUS_CHANNEL = "focus_sessions"
    private const val LEGACY_PRAYER_CHANNEL = "prayer_times"
    private const val PRAYER_NOTIFICATION_ID = 2001
    private const val FOCUS_NOTIFICATION_ID = 3001

    fun ensureChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        val prayer = NotificationChannel(
            PRAYER_CHANNEL,
            context.getString(R.string.prayer_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.prayer_channel_description)
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 400, 300, 400)
            setSound(
                Uri.parse("android.resource://${context.packageName}/${R.raw.adhan}"),
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
        }
        val focus = NotificationChannel(
            FOCUS_CHANNEL,
            context.getString(R.string.focus_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.focus_channel_description)
            enableVibration(true)
        }
        manager.createNotificationChannels(listOf(prayer, focus))
        manager.deleteNotificationChannel(LEGACY_PRAYER_CHANNEL)
    }

    @SuppressLint("MissingPermission")
    fun showPrayer(context: Context, prayerName: String, time: String) {
        if (!canPost(context)) return
        val notification = NotificationCompat.Builder(context, PRAYER_CHANNEL)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("حان وقت صلاة $prayerName")
            .setContentText("موعد الصلاة $time — تقبل الله طاعتك")
            .setStyle(NotificationCompat.BigTextStyle().bigText("حان وقت صلاة $prayerName ($time). تقبل الله طاعتك."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(openAppIntent(context))
            .build()
        runCatching {
            NotificationManagerCompat.from(context).notify(PRAYER_NOTIFICATION_ID, notification)
        }
    }

    @SuppressLint("MissingPermission")
    fun showTestPrayer(context: Context) {
        if (!canPost(context)) return
        val notification = NotificationCompat.Builder(context, PRAYER_CHANNEL)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("تنبيه تجريبي")
            .setContentText("هكذا سيصلك تنبيه الصلاة من رفيق.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(openAppIntent(context))
            .build()
        runCatching {
            NotificationManagerCompat.from(context).notify(PRAYER_NOTIFICATION_ID + 1, notification)
        }
    }

    @SuppressLint("MissingPermission")
    fun showFocusComplete(context: Context) {
        if (!canPost(context)) return
        val notification = NotificationCompat.Builder(context, FOCUS_CHANNEL)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("انتهت جلسة التركيز")
            .setContentText("أحسنت. خذ نفسًا، وواصل يومك بهدوء.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(openAppIntent(context))
            .build()
        runCatching {
            NotificationManagerCompat.from(context).notify(FOCUS_NOTIFICATION_ID, notification)
        }
    }

    private fun openAppIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        return PendingIntent.getActivity(
            context,
            900,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun canPost(context: Context): Boolean {
        val permissionGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        return permissionGranted && NotificationManagerCompat.from(context).areNotificationsEnabled()
    }
}

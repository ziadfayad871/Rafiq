package com.rafiq.launcher.data.preferences

import java.util.TimeZone

data class UserPreferences(
    val displayName: String = "رفيق",
    val theme: String = "emerald",
    val language: String = "ar",
    val calculationMethod: String = "egyptian",
    val locationMode: String = "manual",
    val manualCityId: String = "cairo",
    val latitude: Float = 30.0444f,
    val longitude: Float = 31.2357f,
    val locationLabel: String = "القاهرة",
    val timeZoneId: String = TimeZone.getDefault().id,
    val prayerNotifications: Boolean = false,
    val soundsEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val focusDurationMinutes: Int = 25,
    val allowedFocusPackages: Set<String> = emptySet(),
    val quranSurah: Int = 1,
    val quranAyah: Int = 1,
    val tasksSeeded: Boolean = false,
    val focusEndAt: Long = 0L,
    val focusRemainingMs: Long = 0L,
    val focusSessionTitle: String = "الدراسة"
)

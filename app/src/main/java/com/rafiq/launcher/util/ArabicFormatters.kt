package com.rafiq.launcher.util

import android.icu.util.IslamicCalendar
import android.icu.util.ULocale
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.TimeZone

private val dayNames = arrayOf(
    "الأحد", "الاثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت"
)

private val gregorianMonths = arrayOf(
    "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
    "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
)

private val hijriMonths = arrayOf(
    "محرم", "صفر", "ربيع الأول", "ربيع الآخر", "جمادى الأولى", "جمادى الآخرة",
    "رجب", "شعبان", "رمضان", "شوال", "ذو القعدة", "ذو الحجة"
)

fun formatGregorianDate(date: LocalDate = LocalDate.now()): String {
    val day = dayNames[(date.dayOfWeek.value % 7)]
    return "$day ${date.dayOfMonth} ${gregorianMonths[date.monthValue - 1]} ${date.year}"
}

fun formatHijriDate(date: LocalDate = LocalDate.now()): String {
    val calendar = IslamicCalendar(android.icu.util.TimeZone.getDefault(), ULocale("ar"))
    calendar.timeInMillis = date.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
    val day = calendar.get(android.icu.util.Calendar.DAY_OF_MONTH)
    val month = calendar.get(android.icu.util.Calendar.MONTH)
    val year = calendar.get(android.icu.util.Calendar.YEAR)
    return "$day ${hijriMonths[month]} $year هـ"
}

fun formatTime12Hour(time: LocalDateTime): String {
    val hour = when {
        time.hour == 0 -> 12
        time.hour > 12 -> time.hour - 12
        else -> time.hour
    }
    val suffix = if (time.hour < 12) "ص" else "م"
    return "$hour:${time.minute.toString().padStart(2, '0')} $suffix"
}

fun formatCountdown(duration: Duration): String {
    val totalMinutes = duration.toMinutes().coerceAtLeast(0)
    if (totalMinutes < 1) return "أقل من دقيقة"
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return when {
        hours > 0 && minutes > 0 -> "تبقى $hours ساعات و$minutes دقيقة"
        hours > 0 -> "تبقى $hours ساعات"
        else -> "تبقى $minutes دقيقة"
    }
}

fun formatCountdownShort(duration: Duration): String {
    val totalSeconds = duration.seconds.coerceAtLeast(0)
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        "%d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%02d:%02d".format(minutes, seconds)
    }
}

fun prayerTimeLabel(dayOffset: Int, prayerName: String): String =
    if (dayOffset > 0) "غدًا • $prayerName" else prayerName

package com.rafiq.launcher

import com.rafiq.launcher.data.apps.normalizeArabicForSearch
import com.rafiq.launcher.data.prayer.CalculationMethod
import com.rafiq.launcher.data.prayer.PrayerCalculator
import com.rafiq.launcher.data.prayer.PrayerId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class PrayerCalculatorTest {

    private val cairoZone = ZoneId.of("Africa/Cairo")
    private val cairoLat = 30.0444
    private val cairoLng = 31.2357

    private fun cairoTimes(date: LocalDate = LocalDate.of(2026, 10, 19)) =
        PrayerCalculator.calculate(date, cairoLat, cairoLng, cairoZone, CalculationMethod.EGYPTIAN)

    @Test
    fun `prayer order is correct in Cairo`() {
        val times = cairoTimes()
        assertTrue(times.fajr.isBefore(times.sunrise))
        assertTrue(times.sunrise.isBefore(times.dhuhr))
        assertTrue(times.dhuhr.isBefore(times.asr))
        assertTrue(times.asr.isBefore(times.maghrib))
        assertTrue(times.maghrib.isBefore(times.isha))
    }

    @Test
    fun `fajr and isha stay within the day`() {
        val times = cairoTimes()
        assertEquals(19, times.date.dayOfMonth)
        assertTrue(times.fajr.hour in 3..5)
        assertTrue(times.isha.hour in 18..20)
        assertFalse(times.highLatitudeApproximation)
    }

    @Test
    fun `next prayer just after midnight is fajr with yesterday as previous`() {
        val yesterday = cairoTimes(LocalDate.of(2026, 10, 18))
        val today = cairoTimes()
        val tomorrow = cairoTimes(LocalDate.of(2026, 10, 20))
        val now = LocalDateTime.of(2026, 10, 19, 0, 30)
        val info = PrayerCalculator.nextPrayer(today, now, tomorrow, yesterday)
        assertEquals(PrayerId.FAJR, info.prayer.id)
        assertEquals(PrayerId.ISHA, info.previous.id)
        assertFalse(info.tomorrow)
        assertTrue(info.progress > 0f)
    }

    @Test
    fun `next prayer after isha is fajr tomorrow`() {
        val today = cairoTimes()
        val tomorrow = cairoTimes(LocalDate.of(2026, 10, 20))
        val now = today.isha.plusMinutes(5)
        val info = PrayerCalculator.nextPrayer(today, now, tomorrow, cairoTimes(LocalDate.of(2026, 10, 18)))
        assertEquals(PrayerId.FAJR, info.prayer.id)
        assertTrue(info.tomorrow)
    }

    @Test
    fun `arabic search normalization strips diacritics and unifies letters`() {
        assertEquals("احمد", normalizeArabicForSearch("أَحْمَد"))
        assertEquals("مصطفي", normalizeArabicForSearch("مُصْطَفَى"))
        assertEquals("صلاه", normalizeArabicForSearch("صلاة"))
    }
}

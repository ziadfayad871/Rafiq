package com.rafiq.launcher.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rafiq.launcher.data.prayer.ManualCity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.util.TimeZone

private val Context.rafiqDataStore by preferencesDataStore(name = "rafiq_preferences")

class PreferencesRepository(private val context: Context) {

    private object Keys {
        val displayName = stringPreferencesKey("display_name")
        val theme = stringPreferencesKey("theme")
        val language = stringPreferencesKey("language")
        val calculationMethod = stringPreferencesKey("calculation_method")
        val locationMode = stringPreferencesKey("location_mode")
        val manualCityId = stringPreferencesKey("manual_city_id")
        val latitude = intPreferencesKey("latitude_float_bits")
        val longitude = intPreferencesKey("longitude_float_bits")
        val locationLabel = stringPreferencesKey("location_label")
        val timeZoneId = stringPreferencesKey("time_zone_id")
        val prayerNotifications = booleanPreferencesKey("prayer_notifications")
        val soundsEnabled = booleanPreferencesKey("sounds_enabled")
        val hapticsEnabled = booleanPreferencesKey("haptics_enabled")
        val focusDuration = intPreferencesKey("focus_duration")
        val allowedFocusPackages = stringSetPreferencesKey("allowed_focus_packages")
        val quranSurah = intPreferencesKey("quran_surah")
        val quranAyah = intPreferencesKey("quran_ayah")
        val tasksSeeded = booleanPreferencesKey("tasks_seeded")
        val focusEndAt = longPreferencesKey("focus_end_at")
        val focusRemainingMs = longPreferencesKey("focus_remaining_ms")
        val focusSessionTitle = stringPreferencesKey("focus_session_title")
    }

    val preferences: Flow<UserPreferences> = context.rafiqDataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { values ->
            UserPreferences(
                displayName = values[Keys.displayName] ?: "رفيق",
                theme = values[Keys.theme] ?: "emerald",
                language = values[Keys.language] ?: "ar",
                calculationMethod = values[Keys.calculationMethod] ?: "egyptian",
                locationMode = values[Keys.locationMode] ?: "manual",
                manualCityId = values[Keys.manualCityId] ?: "cairo",
                latitude = Float.fromBits(
                    values[Keys.latitude] ?: 30.0444f.toRawBits()
                ),
                longitude = Float.fromBits(
                    values[Keys.longitude] ?: 31.2357f.toRawBits()
                ),
                locationLabel = values[Keys.locationLabel] ?: "القاهرة",
                timeZoneId = values[Keys.timeZoneId] ?: TimeZone.getDefault().id,
                prayerNotifications = values[Keys.prayerNotifications] ?: false,
                soundsEnabled = values[Keys.soundsEnabled] ?: true,
                hapticsEnabled = values[Keys.hapticsEnabled] ?: true,
                focusDurationMinutes = values[Keys.focusDuration] ?: 25,
                allowedFocusPackages = values[Keys.allowedFocusPackages] ?: emptySet(),
                quranSurah = values[Keys.quranSurah] ?: 1,
                quranAyah = values[Keys.quranAyah] ?: 1,
                tasksSeeded = values[Keys.tasksSeeded] ?: false,
                focusEndAt = values[Keys.focusEndAt] ?: 0L,
                focusRemainingMs = values[Keys.focusRemainingMs] ?: 0L,
                focusSessionTitle = values[Keys.focusSessionTitle] ?: "الدراسة"
            )
        }

    suspend fun current(): UserPreferences = preferences.first()

    suspend fun setTheme(theme: String) = edit { it[Keys.theme] = theme }

    suspend fun setDisplayName(name: String) = edit {
        it[Keys.displayName] = name.trim().take(40).ifEmpty { "رفيق" }
    }

    suspend fun setLanguage(language: String) = edit { it[Keys.language] = language }

    suspend fun setCalculationMethod(method: String) = edit {
        it[Keys.calculationMethod] = method
    }

    suspend fun setPrayerNotifications(enabled: Boolean) = edit {
        it[Keys.prayerNotifications] = enabled
    }

    suspend fun setSoundsEnabled(enabled: Boolean) = edit {
        it[Keys.soundsEnabled] = enabled
    }

    suspend fun setHapticsEnabled(enabled: Boolean) = edit {
        it[Keys.hapticsEnabled] = enabled
    }

    suspend fun setFocusDuration(minutes: Int) = edit {
        it[Keys.focusDuration] = minutes.coerceIn(5, 180)
    }

    suspend fun setAllowedFocusPackages(packages: Set<String>) = edit {
        it[Keys.allowedFocusPackages] = packages
    }

    suspend fun setManualLocation(city: ManualCity) = edit {
        it[Keys.locationMode] = "manual"
        it[Keys.manualCityId] = city.id
        it[Keys.latitude] = city.latitude.toFloat().toRawBits()
        it[Keys.longitude] = city.longitude.toFloat().toRawBits()
        it[Keys.locationLabel] = city.name
        it[Keys.timeZoneId] = city.timeZoneId
    }

    suspend fun setDetectedLocation(latitude: Double, longitude: Double) = edit {
        it[Keys.locationMode] = "auto"
        it[Keys.latitude] = latitude.toFloat().toRawBits()
        it[Keys.longitude] = longitude.toFloat().toRawBits()
        it[Keys.locationLabel] = "موقعك الحالي"
        it[Keys.timeZoneId] = TimeZone.getDefault().id
    }

    suspend fun setQuranPosition(surah: Int, ayah: Int) = edit {
        it[Keys.quranSurah] = surah.coerceIn(1, 114)
        it[Keys.quranAyah] = ayah.coerceAtLeast(1)
    }

    suspend fun setTasksSeeded() = edit { it[Keys.tasksSeeded] = true }

    suspend fun setFocusState(endAt: Long, remainingMs: Long, title: String) = edit {
        it[Keys.focusEndAt] = endAt
        it[Keys.focusRemainingMs] = remainingMs
        it[Keys.focusSessionTitle] = title
    }

    suspend fun clearFocusState() = edit {
        it[Keys.focusEndAt] = 0L
        it[Keys.focusRemainingMs] = 0L
    }

    suspend fun clearPersonalizations() = edit { values ->
        values.remove(Keys.displayName)
        values.remove(Keys.quranSurah)
        values.remove(Keys.quranAyah)
        values.remove(Keys.focusSessionTitle)
        values[Keys.tasksSeeded] = true
    }

    private suspend fun edit(block: (androidx.datastore.preferences.core.MutablePreferences) -> Unit) {
        context.rafiqDataStore.edit(block)
    }
}

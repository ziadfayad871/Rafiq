package com.rafiq.launcher.ui.screens.prayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafiq.launcher.AppContainer
import com.rafiq.launcher.data.location.LocationProvider
import com.rafiq.launcher.data.prayer.CalculationMethod
import com.rafiq.launcher.data.prayer.ManualCities
import com.rafiq.launcher.data.prayer.ManualCity
import com.rafiq.launcher.data.prayer.NextPrayerInfo
import com.rafiq.launcher.data.prayer.PrayerCalculator
import com.rafiq.launcher.data.prayer.PrayerTimes
import com.rafiq.launcher.data.preferences.UserPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

data class PrayerUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val now: LocalDateTime = LocalDateTime.now(),
    val locationLabel: String = "القاهرة",
    val locationMode: String = "manual",
    val method: CalculationMethod = CalculationMethod.EGYPTIAN,
    val times: PrayerTimes? = null,
    val next: NextPrayerInfo? = null,
    val notificationsEnabled: Boolean = false,
    val isDetecting: Boolean = false,
    val message: String? = null
)

class PrayerViewModel(private val container: AppContainer) : ViewModel() {

    private val _uiState = MutableStateFlow(PrayerUiState())
    val uiState: StateFlow<PrayerUiState> = _uiState.asStateFlow()

    private val locationProvider = LocationProvider(container.appContext)

    private var prefs: UserPreferences = UserPreferences()
    private var zone: ZoneId = ZoneId.systemDefault()
    private var today: PrayerTimes? = null
    private var tomorrow: PrayerTimes? = null
    private var yesterday: PrayerTimes? = null
    private var lastDate: LocalDate? = null

    val cities: List<ManualCity> = ManualCities.all

    init {
        viewModelScope.launch {
            container.preferencesRepository.preferences.collect { updated ->
                prefs = updated
                zone = runCatching { ZoneId.of(updated.timeZoneId) }
                    .getOrDefault(ZoneId.systemDefault())
                val now = LocalDateTime.now(zone)
                computeTimes(now.toLocalDate())
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    locationLabel = updated.locationLabel,
                    locationMode = updated.locationMode,
                    method = CalculationMethod.fromId(updated.calculationMethod),
                    notificationsEnabled = updated.prayerNotifications
                )
                publish(now)
            }
        }
        viewModelScope.launch {
            while (isActive) {
                delay(1000)
                publish(LocalDateTime.now(zone))
            }
        }
    }

    private fun computeTimes(date: LocalDate) {
        val method = CalculationMethod.fromId(prefs.calculationMethod)
        today = PrayerCalculator.calculate(
            date, prefs.latitude.toDouble(), prefs.longitude.toDouble(), zone, method
        )
        tomorrow = PrayerCalculator.calculate(
            date.plusDays(1), prefs.latitude.toDouble(), prefs.longitude.toDouble(), zone, method
        )
        yesterday = PrayerCalculator.calculate(
            date.minusDays(1), prefs.latitude.toDouble(), prefs.longitude.toDouble(), zone, method
        )
        lastDate = date
    }

    private fun publish(now: LocalDateTime) {
        if (now.toLocalDate() != lastDate) computeTimes(now.toLocalDate())
        val day = today ?: return
        val nextDay = tomorrow ?: return
        val info = runCatching {
            PrayerCalculator.nextPrayer(day, now, nextDay, yesterday)
        }.getOrNull()
        _uiState.value = _uiState.value.copy(now = now, times = day, next = info)
    }

    fun setMethod(method: CalculationMethod) {
        viewModelScope.launch {
            container.preferencesRepository.setCalculationMethod(method.id)
        }
    }

    fun chooseCity(city: ManualCity) {
        viewModelScope.launch {
            container.preferencesRepository.setManualLocation(city)
            _uiState.value = _uiState.value.copy(
                message = "تم الاعتماد على مدينة ${city.name}"
            )
        }
    }

    fun detectLocation() {
        if (!locationProvider.hasPermission()) {
            _uiState.value = _uiState.value.copy(
                message = "لم يُسمح بالوصول إلى الموقع، سنستخدم المدينة اليدوية"
            )
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDetecting = true)
            val detected = locationProvider.currentLocation()
            if (detected == null) {
                _uiState.value = _uiState.value.copy(
                    isDetecting = false,
                    message = "تعذّر تحديد الموقع الآن، سنستخدم المدينة اليدوية"
                )
            } else {
                container.preferencesRepository.setDetectedLocation(
                    detected.latitude,
                    detected.longitude
                )
                _uiState.value = _uiState.value.copy(
                    isDetecting = false,
                    locationLabel = detected.label,
                    message = "تم الاعتماد على ${detected.label}"
                )
            }
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            container.preferencesRepository.setPrayerNotifications(enabled)
        }
    }

    fun locationPermissionGranted(): Boolean = locationProvider.hasPermission()

    fun consumeMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}

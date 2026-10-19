package com.rafiq.launcher.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafiq.launcher.AppContainer
import com.rafiq.launcher.data.apps.DockApp
import com.rafiq.launcher.data.apps.DockKind
import com.rafiq.launcher.data.prayer.CalculationMethod
import com.rafiq.launcher.data.prayer.NextPrayerInfo
import com.rafiq.launcher.data.prayer.PrayerCalculator
import com.rafiq.launcher.data.prayer.PrayerTimes
import com.rafiq.launcher.data.preferences.UserPreferences
import com.rafiq.launcher.util.formatGregorianDate
import com.rafiq.launcher.util.formatHijriDate
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

data class HomeUiState(
    val displayName: String = "رفيق",
    val now: LocalDateTime = LocalDateTime.now(),
    val gregorianDate: String = "",
    val hijriDate: String = "",
    val locationLabel: String = "القاهرة",
    val next: NextPrayerInfo? = null,
    val prayerTimes: PrayerTimes? = null,
    val dockApps: List<DockApp> = emptyList(),
    val isDockLoading: Boolean = true,
    val message: String? = null
)

class HomeViewModel(private val container: AppContainer) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var prefs: UserPreferences = UserPreferences()
    private var zone: ZoneId = ZoneId.systemDefault()
    private var today: PrayerTimes? = null
    private var tomorrow: PrayerTimes? = null
    private var yesterday: PrayerTimes? = null
    private var lastDate: LocalDate? = null

    init {
        viewModelScope.launch {
            container.preferencesRepository.preferences.collect { updated ->
                prefs = updated
                zone = runCatching { ZoneId.of(updated.timeZoneId) }
                    .getOrDefault(ZoneId.systemDefault())
                loadPrayerTimes(LocalDateTime.now(zone).toLocalDate())
                _uiState.value = _uiState.value.copy(
                    displayName = updated.displayName,
                    locationLabel = updated.locationLabel
                )
            }
        }
        viewModelScope.launch {
            while (isActive) {
                delay(1000)
                tick()
            }
        }
        refreshDock()
    }

    private fun loadPrayerTimes(date: LocalDate) {
        val method = CalculationMethod.fromId(prefs.calculationMethod)
        today = PrayerCalculator.calculate(date, prefs.latitude.toDouble(), prefs.longitude.toDouble(), zone, method)
        tomorrow = PrayerCalculator.calculate(date.plusDays(1), prefs.latitude.toDouble(), prefs.longitude.toDouble(), zone, method)
        yesterday = PrayerCalculator.calculate(date.minusDays(1), prefs.latitude.toDouble(), prefs.longitude.toDouble(), zone, method)
        lastDate = date
    }

    private fun tick() {
        val now = LocalDateTime.now(zone)
        if (now.toLocalDate() != lastDate) {
            loadPrayerTimes(now.toLocalDate())
        }
        val day = today ?: return
        val nextDay = tomorrow ?: return
        val info = runCatching {
            PrayerCalculator.nextPrayer(day, now, nextDay, yesterday)
        }.getOrNull()
        _uiState.value = _uiState.value.copy(
            now = now,
            gregorianDate = formatGregorianDate(now.toLocalDate()),
            hijriDate = formatHijriDate(now.toLocalDate()),
            next = info,
            prayerTimes = day
        )
    }

    fun refreshDock() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDockLoading = true)
            val dock = runCatching { container.appsRepository.loadDockApps() }
                .getOrDefault(emptyList())
            _uiState.value = _uiState.value.copy(dockApps = dock, isDockLoading = false)
        }
    }

    fun openDockApp(kind: DockKind) {
        container.appsRepository.launchDock(kind)
            .onFailure { _uiState.value = _uiState.value.copy(message = "تعذّر فتح التطبيق على هذا الجهاز") }
    }

    fun consumeMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}

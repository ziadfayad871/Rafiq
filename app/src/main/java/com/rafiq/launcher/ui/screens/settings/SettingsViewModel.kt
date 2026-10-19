package com.rafiq.launcher.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafiq.launcher.AppContainer
import com.rafiq.launcher.data.prayer.CalculationMethod
import com.rafiq.launcher.data.prayer.ManualCity
import com.rafiq.launcher.data.preferences.UserPreferences
import com.rafiq.launcher.service.FocusAlarmScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val message: String? = null,
    val isClearing: Boolean = false
)

class SettingsViewModel(private val container: AppContainer) : ViewModel() {

    val preferences: StateFlow<UserPreferences> = container.preferencesRepository.preferences.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        UserPreferences()
    )

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun setTheme(theme: String) {
        viewModelScope.launch { container.preferencesRepository.setTheme(theme) }
    }

    fun setDisplayName(name: String) {
        viewModelScope.launch { container.preferencesRepository.setDisplayName(name) }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch { container.preferencesRepository.setLanguage(language) }
    }

    fun setSoundsEnabled(enabled: Boolean) {
        viewModelScope.launch { container.preferencesRepository.setSoundsEnabled(enabled) }
    }

    fun setHapticsEnabled(enabled: Boolean) {
        viewModelScope.launch { container.preferencesRepository.setHapticsEnabled(enabled) }
    }

    fun setPrayerNotifications(enabled: Boolean) {
        viewModelScope.launch { container.preferencesRepository.setPrayerNotifications(enabled) }
    }

    fun setCalculationMethod(method: CalculationMethod) {
        viewModelScope.launch { container.preferencesRepository.setCalculationMethod(method.id) }
    }

    fun setCity(city: ManualCity) {
        viewModelScope.launch {
            container.preferencesRepository.setManualLocation(city)
            _uiState.value = SettingsUiState(message = "تم تحديث مدينة الحساب إلى ${city.name}")
        }
    }

    fun setFocusDuration(minutes: Int) {
        viewModelScope.launch { container.preferencesRepository.setFocusDuration(minutes) }
    }

    fun openAppSettings() {
        container.appsRepository.openAppSettings()
            .onFailure { _uiState.value = SettingsUiState(message = "تعذّر فتح إعدادات النظام") }
    }

    fun openHomeSettings() {
        container.appsRepository.openHomeSettings()
            .onFailure { _uiState.value = SettingsUiState(message = "تعذّر فتح إعدادات الشاشة الرئيسية") }
    }

    fun openLanguageSettings() {
        container.appsRepository.openLanguageSettings()
            .onFailure {
                container.appsRepository.openAppSettings()
            }
    }

    fun openExactAlarmSettings() {
        com.rafiq.launcher.service.PrayerAlarmScheduler.openExactAlarmSettings(container.appContext)
    }

    fun canScheduleExactAlarms(): Boolean =
        com.rafiq.launcher.service.PrayerAlarmScheduler.canScheduleExact(container.appContext)

    fun showTestPrayerNotification() {
        container.appScope.launch {
            com.rafiq.launcher.service.RafiqNotifications.ensureChannels(container.appContext)
            com.rafiq.launcher.service.RafiqNotifications.showTestPrayer(container.appContext)
        }
    }

    fun resetPersonalData() {
        viewModelScope.launch {
            _uiState.value = SettingsUiState(isClearing = true)
            runCatching {
                container.tasksRepository.clearAll()
                container.bookmarksRepository.clearAll()
                container.preferencesRepository.clearPersonalizations()
                container.preferencesRepository.clearFocusState()
                container.tasksRepository.seedDefaults()
            }
            FocusAlarmScheduler.cancel(container.appContext)
            _uiState.value = SettingsUiState(
                isClearing = false,
                message = "تمت إعادة البيانات الشخصية إلى وضعها الافتراضي"
            )
        }
    }

    fun consumeMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}

package com.rafiq.launcher.ui.screens.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafiq.launcher.AppContainer
import com.rafiq.launcher.data.apps.InstalledApp
import com.rafiq.launcher.service.FocusAlarmScheduler
import com.rafiq.launcher.data.preferences.UserPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class FocusPhase { IDLE, RUNNING, PAUSED, COMPLETED }

data class FocusUiState(
    val phase: FocusPhase = FocusPhase.IDLE,
    val remainingMs: Long = 25 * 60 * 1000L,
    val totalMs: Long = 25 * 60 * 1000L,
    val durationMinutes: Int = 25,
    val sessionTitle: String = "الدراسة",
    val allowedPackages: Set<String> = emptySet(),
    val installedApps: List<InstalledApp> = emptyList(),
    val isLoadingApps: Boolean = true,
    val message: String? = null
) {
    val progress: Float
        get() = if (totalMs <= 0L) 0f else {
            val elapsed = (totalMs - remainingMs).coerceIn(0L, totalMs)
            elapsed.toFloat() / totalMs.toFloat()
        }
}

class FocusViewModel(private val container: AppContainer) : ViewModel() {

    private val _uiState = MutableStateFlow(FocusUiState())
    val uiState: StateFlow<FocusUiState> = _uiState.asStateFlow()

    private var initialized = false
    private var prefs: UserPreferences = UserPreferences()

    init {
        viewModelScope.launch {
            container.preferencesRepository.preferences.collect { updated ->
                prefs = updated
                if (!initialized) {
                    initialized = true
                    reconcile(updated)
                } else {
                    applyPreferences(updated)
                }
            }
        }
        viewModelScope.launch {
            while (isActive) {
                delay(500)
                tick()
            }
        }
        loadInstalledApps()
    }

    private fun reconcile(prefs: UserPreferences) {
        applyPreferences(prefs)
        val now = System.currentTimeMillis()
        when {
            prefs.focusEndAt > now -> {
                val remaining = prefs.focusEndAt - now
                _uiState.value = _uiState.value.copy(
                    phase = FocusPhase.RUNNING,
                    remainingMs = remaining,
                    totalMs = prefs.focusDurationMinutes * 60_000L
                )
            }
            prefs.focusRemainingMs > 0L -> {
                _uiState.value = _uiState.value.copy(
                    phase = FocusPhase.PAUSED,
                    remainingMs = prefs.focusRemainingMs,
                    totalMs = prefs.focusDurationMinutes * 60_000L
                )
            }
            else -> {
                if (prefs.focusEndAt != 0L) {
                    viewModelScope.launch { container.preferencesRepository.clearFocusState() }
                }
                _uiState.value = _uiState.value.copy(
                    phase = FocusPhase.IDLE,
                    remainingMs = prefs.focusDurationMinutes * 60_000L,
                    totalMs = prefs.focusDurationMinutes * 60_000L
                )
            }
        }
    }

    private fun applyPreferences(prefs: UserPreferences) {
        _uiState.value = _uiState.value.copy(
            durationMinutes = prefs.focusDurationMinutes,
            sessionTitle = prefs.focusSessionTitle,
            allowedPackages = prefs.allowedFocusPackages
        )
        if (_uiState.value.phase == FocusPhase.IDLE) {
            _uiState.value = _uiState.value.copy(
                remainingMs = prefs.focusDurationMinutes * 60_000L,
                totalMs = prefs.focusDurationMinutes * 60_000L
            )
        }
    }

    private fun tick() {
        val state = _uiState.value
        if (state.phase != FocusPhase.RUNNING) return
        val endAt = prefs.focusEndAt
        val remaining = endAt - System.currentTimeMillis()
        if (remaining <= 0L) {
            completeSession()
            return
        }
        _uiState.value = state.copy(remainingMs = remaining)
    }

    private fun completeSession() {
        _uiState.value = _uiState.value.copy(
            phase = FocusPhase.COMPLETED,
            remainingMs = 0L,
            message = "أُنجزت جلسة التركيز. أحسنت!"
        )
        viewModelScope.launch {
            container.preferencesRepository.clearFocusState()
        }
        FocusAlarmScheduler.cancel(container.appContext)
    }

    fun start() {
        val minutes = _uiState.value.durationMinutes
        val endAt = System.currentTimeMillis() + minutes * 60_000L
        _uiState.value = _uiState.value.copy(
            phase = FocusPhase.RUNNING,
            remainingMs = minutes * 60_000L,
            totalMs = minutes * 60_000L
        )
        viewModelScope.launch {
            container.preferencesRepository.setFocusState(endAt, 0L, _uiState.value.sessionTitle)
        }
        FocusAlarmScheduler.schedule(container.appContext, endAt)
    }

    fun pause() {
        val state = _uiState.value
        if (state.phase != FocusPhase.RUNNING) return
        val remaining = state.remainingMs.coerceAtLeast(0L)
        _uiState.value = state.copy(phase = FocusPhase.PAUSED, remainingMs = remaining)
        viewModelScope.launch {
            container.preferencesRepository.setFocusState(0L, remaining, state.sessionTitle)
        }
        FocusAlarmScheduler.cancel(container.appContext)
    }

    fun resume() {
        val state = _uiState.value
        if (state.phase != FocusPhase.PAUSED) return
        val endAt = System.currentTimeMillis() + state.remainingMs
        _uiState.value = state.copy(phase = FocusPhase.RUNNING)
        viewModelScope.launch {
            container.preferencesRepository.setFocusState(endAt, 0L, state.sessionTitle)
        }
        FocusAlarmScheduler.schedule(container.appContext, endAt)
    }

    fun reset() {
        val minutes = _uiState.value.durationMinutes
        _uiState.value = _uiState.value.copy(
            phase = FocusPhase.IDLE,
            remainingMs = minutes * 60_000L,
            totalMs = minutes * 60_000L
        )
        viewModelScope.launch {
            container.preferencesRepository.clearFocusState()
        }
        FocusAlarmScheduler.cancel(container.appContext)
    }

    fun setDuration(minutes: Int) {
        val safe = minutes.coerceIn(5, 180)
        viewModelScope.launch {
            container.preferencesRepository.setFocusDuration(safe)
        }
    }

    fun setSessionTitle(title: String) {
        val clean = title.trim().take(40).ifEmpty { "التركيز" }
        val state = _uiState.value
        _uiState.value = state.copy(sessionTitle = clean)
        val endAt = when (state.phase) {
            FocusPhase.RUNNING -> System.currentTimeMillis() + state.remainingMs
            else -> 0L
        }
        val remaining = if (state.phase == FocusPhase.PAUSED) state.remainingMs else 0L
        viewModelScope.launch {
            container.preferencesRepository.setFocusState(endAt, remaining, clean)
        }
    }

    fun toggleAllowedPackage(packageName: String) {
        val updated = _uiState.value.allowedPackages.toMutableSet().apply {
            if (!add(packageName)) remove(packageName)
        }
        _uiState.value = _uiState.value.copy(allowedPackages = updated)
        viewModelScope.launch {
            container.preferencesRepository.setAllowedFocusPackages(updated)
        }
    }

    fun loadInstalledApps() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingApps = true)
            val apps = runCatching { container.appsRepository.loadInstalledApps() }
                .getOrDefault(emptyList())
            _uiState.value = _uiState.value.copy(installedApps = apps, isLoadingApps = false)
        }
    }

    fun consumeMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}

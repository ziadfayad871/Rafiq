package com.rafiq.launcher.ui.screens.apps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafiq.launcher.data.apps.AppsRepository
import com.rafiq.launcher.data.apps.BuiltinApp
import com.rafiq.launcher.data.apps.InstalledApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AppsUiState(
    val installedApps: List<InstalledApp> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val message: String? = null
)

class AppsViewModel(private val repository: AppsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AppsUiState())
    val uiState: StateFlow<AppsUiState> = _uiState.asStateFlow()

    val builtinApps: List<BuiltinApp> = repository.builtinApps

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            runCatching { repository.loadInstalledApps() }
                .onSuccess { apps ->
                    _uiState.value = _uiState.value.copy(
                        installedApps = apps,
                        isLoading = false
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "تعذّر قراءة التطبيقات المثبتة"
                    )
                }
        }
    }

    fun launch(app: InstalledApp) {
        repository.launchInstalled(app)
            .onFailure { _uiState.value = _uiState.value.copy(message = "تعذّر فتح ${app.label}") }
    }

    fun consumeMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}

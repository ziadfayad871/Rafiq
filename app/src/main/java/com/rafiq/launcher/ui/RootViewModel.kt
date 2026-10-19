package com.rafiq.launcher.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafiq.launcher.AppContainer
import com.rafiq.launcher.data.preferences.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class RootViewModel(container: AppContainer) : ViewModel() {
    val preferences: StateFlow<UserPreferences> = container.preferencesRepository.preferences.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        UserPreferences()
    )
}

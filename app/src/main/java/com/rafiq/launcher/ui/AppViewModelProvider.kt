package com.rafiq.launcher.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.rafiq.launcher.RafiqApplication
import com.rafiq.launcher.ui.screens.adhkar.AdhkarViewModel
import com.rafiq.launcher.ui.screens.apps.AppsViewModel
import com.rafiq.launcher.ui.screens.assistant.AssistantViewModel
import com.rafiq.launcher.ui.screens.focus.FocusViewModel
import com.rafiq.launcher.ui.screens.home.HomeViewModel
import com.rafiq.launcher.ui.screens.prayer.PrayerViewModel
import com.rafiq.launcher.ui.screens.quran.QuranViewModel
import com.rafiq.launcher.ui.screens.settings.SettingsViewModel
import com.rafiq.launcher.ui.screens.tasks.TasksViewModel

object AppViewModelProvider {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
        initializer { RootViewModel(app().container) }
        initializer { HomeViewModel(app().container) }
        initializer { AppsViewModel(app().container.appsRepository) }
        initializer { QuranViewModel(app().container) }
        initializer { AdhkarViewModel(app().container.adhkarRepository) }
        initializer { PrayerViewModel(app().container) }
        initializer { TasksViewModel(app().container.tasksRepository) }
        initializer { FocusViewModel(app().container) }
        initializer { AssistantViewModel(app().container) }
        initializer { SettingsViewModel(app().container) }
    }
}

private fun CreationExtras.app(): RafiqApplication =
    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as RafiqApplication

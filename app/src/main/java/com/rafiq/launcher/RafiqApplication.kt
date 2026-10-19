package com.rafiq.launcher

import android.app.Application
import android.content.Context
import com.rafiq.launcher.data.adhkar.AdhkarRepository
import com.rafiq.launcher.data.apps.AppsRepository
import com.rafiq.launcher.data.local.AppDatabase
import com.rafiq.launcher.data.local.QuranBookmarksRepository
import com.rafiq.launcher.data.local.TasksRepository
import com.rafiq.launcher.data.preferences.PreferencesRepository
import com.rafiq.launcher.data.quran.QuranRepository
import com.rafiq.launcher.service.PrayerAlarmScheduler
import com.rafiq.launcher.service.RafiqNotifications
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AppContainer(context: Context) {
    val appContext: Context = context.applicationContext
    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val database = AppDatabase.get(appContext)
    val tasksRepository = TasksRepository(database.taskDao())
    val bookmarksRepository = QuranBookmarksRepository(database.bookmarkDao())
    val preferencesRepository = PreferencesRepository(appContext)
    val appsRepository = AppsRepository(appContext)
    val quranRepository = QuranRepository(appContext)
    val adhkarRepository = AdhkarRepository(appContext)
}

class RafiqApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        RafiqNotifications.ensureChannels(this)
        container.appScope.launch {
            runCatching {
                val preferences = container.preferencesRepository.current()
                if (!preferences.tasksSeeded) {
                    container.tasksRepository.seedDefaults()
                    container.preferencesRepository.setTasksSeeded()
                }
                PrayerAlarmScheduler.configure(this@RafiqApplication, preferences)
            }
        }
    }
}

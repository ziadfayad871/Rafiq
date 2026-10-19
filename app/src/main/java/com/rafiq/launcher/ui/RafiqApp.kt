package com.rafiq.launcher.ui

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rafiq.launcher.service.PrayerAlarmScheduler
import com.rafiq.launcher.ui.components.RafiqBackground
import com.rafiq.launcher.ui.components.RafiqBottomBar
import com.rafiq.launcher.ui.theme.LocalClickSound
import com.rafiq.launcher.ui.screens.adhkar.AdhkarScreen
import com.rafiq.launcher.ui.screens.apps.AppsScreen
import com.rafiq.launcher.ui.screens.assistant.AssistantScreen
import com.rafiq.launcher.ui.screens.focus.FocusScreen
import com.rafiq.launcher.ui.screens.home.HomeScreen
import com.rafiq.launcher.ui.screens.prayer.PrayerScreen
import com.rafiq.launcher.ui.screens.quran.QuranScreen
import com.rafiq.launcher.ui.screens.settings.SettingsScreen
import com.rafiq.launcher.ui.screens.tasks.TasksScreen

object RafiqRoutes {
    const val HOME = "home"
    const val APPS = "apps"
    const val QURAN = "quran"
    const val ADHKAR = "adhkar"
    const val PRAYER = "prayer"
    const val FOCUS = "focus"
    const val TASKS = "tasks"
    const val ASSISTANT = "assistant"
    const val SETTINGS = "settings"
}

private val bottomBarRoutes = setOf(
    RafiqRoutes.HOME,
    RafiqRoutes.PRAYER,
    RafiqRoutes.QURAN,
    RafiqRoutes.ADHKAR,
    RafiqRoutes.SETTINGS
)

@Composable
fun RafiqApp(rootViewModel: RootViewModel) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: RafiqRoutes.HOME
    val context = LocalContext.current
    val preferences by rootViewModel.preferences.collectAsStateWithLifecycle()

    LaunchedEffect(preferences) {
        PrayerAlarmScheduler.configure(context, preferences)
    }

    val toneGenerator = remember { runCatching { ToneGenerator(AudioManager.STREAM_SYSTEM, 14) }.getOrNull() }
    DisposableEffect(toneGenerator) {
        onDispose { runCatching { toneGenerator?.release() } }
    }
    val soundsEnabled = preferences.soundsEnabled
    val clickSound: () -> Unit = remember(soundsEnabled, toneGenerator) {
        {
            if (soundsEnabled) {
                runCatching { toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 40) }
            }
        }
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl,
        LocalClickSound provides clickSound
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            RafiqBackground(pattern = true)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    NavHost(
                        navController = navController,
                        startDestination = RafiqRoutes.HOME,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable(RafiqRoutes.HOME) {
                            HomeScreen(
                                onNavigate = { route ->
                                    navController.navigate(route) {
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                        composable(RafiqRoutes.APPS) {
                            AppsScreen(
                                onBack = { navController.popBackStack() },
                                onOpenBuiltin = { route ->
                                    navController.navigate(route) { launchSingleTop = true }
                                }
                            )
                        }
                        composable(RafiqRoutes.QURAN) { QuranScreen() }
                        composable(RafiqRoutes.ADHKAR) { AdhkarScreen() }
                        composable(RafiqRoutes.PRAYER) { PrayerScreen() }
                        composable(RafiqRoutes.FOCUS) {
                            FocusScreen(onBack = { navController.popBackStack() })
                        }
                        composable(RafiqRoutes.TASKS) {
                            TasksScreen(onBack = { navController.popBackStack() })
                        }
                        composable(RafiqRoutes.ASSISTANT) {
                            AssistantScreen(
                                onBack = { navController.popBackStack() },
                                onNavigate = { route ->
                                    navController.navigate(route) { launchSingleTop = true }
                                }
                            )
                        }
                        composable(RafiqRoutes.SETTINGS) { SettingsScreen() }
                    }
                }
                if (currentRoute in bottomBarRoutes) {
                    RafiqBottomBar(
                        selectedRoute = currentRoute,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(RafiqRoutes.HOME) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    }
}

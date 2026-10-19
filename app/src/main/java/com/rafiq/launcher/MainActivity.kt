package com.rafiq.launcher

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rafiq.launcher.ui.AppViewModelProvider
import com.rafiq.launcher.ui.RafiqApp
import com.rafiq.launcher.ui.RootViewModel
import com.rafiq.launcher.ui.theme.LocalHapticsEnabled
import com.rafiq.launcher.ui.theme.RafiqTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContent {
            val rootViewModel: RootViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val preferences by rootViewModel.preferences.collectAsStateWithLifecycle()
            CompositionLocalProvider(LocalHapticsEnabled provides preferences.hapticsEnabled) {
                RafiqTheme(themeId = preferences.theme) {
                    RafiqApp(rootViewModel = rootViewModel)
                }
            }
        }
    }
}

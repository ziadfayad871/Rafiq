package com.rafiq.launcher.ui.screens.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rafiq.launcher.data.apps.BuiltinApp
import com.rafiq.launcher.data.apps.BuiltinDestination
import com.rafiq.launcher.data.apps.InstalledApp
import com.rafiq.launcher.data.apps.normalizeArabicForSearch
import com.rafiq.launcher.ui.AppViewModelProvider
import com.rafiq.launcher.ui.RafiqRoutes
import com.rafiq.launcher.ui.components.AppIconImage
import com.rafiq.launcher.ui.components.ErrorOrnament
import com.rafiq.launcher.ui.components.LoadingOrnament
import com.rafiq.launcher.ui.components.RafiqIcons
import com.rafiq.launcher.ui.components.RoundIconButton
import com.rafiq.launcher.ui.components.TransientMessage
import com.rafiq.launcher.ui.theme.RafiqTheme

@Composable
fun AppsScreen(
    onBack: () -> Unit,
    onOpenBuiltin: (String) -> Unit,
    viewModel: AppsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val palette = RafiqTheme.colors
    var query by remember { mutableStateOf("") }

    val filteredInstalled = remember(state.installedApps, query) {
        if (query.isBlank()) {
            state.installedApps
        } else {
            val needle = normalizeArabicForSearch(query)
            state.installedApps.filter { normalizeArabicForSearch(it.label).contains(needle) }
        }
    }
    val filteredBuiltin = remember(query) {
        if (query.isBlank()) {
            viewModel.builtinApps
        } else {
            val needle = normalizeArabicForSearch(query)
            viewModel.builtinApps.filter { normalizeArabicForSearch(it.label).contains(needle) }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp)
                .padding(top = 40.dp, bottom = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RoundIconButton(
                    icon = RafiqIcons.ArrowLeft,
                    contentDescription = "رجوع",
                    onClick = onBack
                )
                Spacer(Modifier.width(10.dp))
                SearchField(
                    query = query,
                    onQueryChange = { query = it },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(18.dp))
            when {
                state.isLoading -> LoadingOrnament(label = "جارٍ ترتيب التطبيقات")
                state.error != null -> ErrorOrnament(
                    message = state.error ?: "",
                    onRetry = viewModel::refresh
                )
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(filteredBuiltin, key = { "builtin-${it.id}" }) { builtin ->
                            BuiltinAppTile(
                                app = builtin,
                                onClick = { onOpenBuiltin(routeFor(builtin.destination)) }
                            )
                        }
                        items(filteredInstalled, key = { "app-${it.packageName}" }) { app ->
                            InstalledAppTile(app = app, onClick = { viewModel.launch(app) })
                        }
                    }
                    if (filteredBuiltin.isEmpty() && filteredInstalled.isEmpty()) {
                        Text(
                            text = "لا توجد نتائج مطابقة",
                            style = MaterialTheme.typography.bodyMedium.copy(color = palette.textDim),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(4) { index ->
                    val active = index == 0
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .height(if (active) 6.dp else 4.dp)
                            .width(if (active) 18.dp else 4.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (active) palette.gold else palette.borderSoft)
                    )
                }
            }
        }
        TransientMessage(
            message = state.message,
            onConsumed = viewModel::consumeMessage,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
        )
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = RafiqTheme.colors
    Row(
        modifier = modifier
            .height(46.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(palette.surface.copy(alpha = 0.75f))
            .border(1.dp, palette.borderSoft, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = RafiqIcons.Search,
            contentDescription = null,
            tint = palette.goldMuted,
            modifier = Modifier.size(17.dp)
        )
        Spacer(Modifier.width(8.dp))
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (query.isEmpty()) {
                Text(
                    text = "ابحث عن تطبيق",
                    style = MaterialTheme.typography.bodyMedium.copy(color = palette.textDim)
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = palette.textPrimary),
                cursorBrush = SolidColor(palette.gold),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun BuiltinAppTile(app: BuiltinApp, onClick: () -> Unit) {
    val palette = RafiqTheme.colors
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(palette.surfaceHigh)
                .border(1.dp, palette.border, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = builtinIcon(app.destination),
                contentDescription = app.label,
                tint = palette.gold,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(Modifier.height(7.dp))
        Text(
            text = app.label,
            style = MaterialTheme.typography.labelMedium.copy(color = palette.textSecondary),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun InstalledAppTile(app: InstalledApp, onClick: () -> Unit) {
    val palette = RafiqTheme.colors
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppIconImage(bitmap = app.icon, label = app.label, iconSize = 58.dp)
        Spacer(Modifier.height(7.dp))
        Text(
            text = app.label,
            style = MaterialTheme.typography.labelMedium.copy(color = palette.textSecondary),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

private fun builtinIcon(destination: BuiltinDestination): ImageVector = when (destination) {
    BuiltinDestination.QURAN -> RafiqIcons.QuranBook
    BuiltinDestination.ADHKAR -> RafiqIcons.PrayerBeads
    BuiltinDestination.PRAYER -> RafiqIcons.Mosque
    BuiltinDestination.TASKS -> RafiqIcons.Tasks
    BuiltinDestination.FOCUS -> RafiqIcons.Focus
    BuiltinDestination.ASSISTANT -> RafiqIcons.Sparkle
    BuiltinDestination.SETTINGS -> RafiqIcons.Settings
}

private fun routeFor(destination: BuiltinDestination): String = when (destination) {
    BuiltinDestination.QURAN -> RafiqRoutes.QURAN
    BuiltinDestination.ADHKAR -> RafiqRoutes.ADHKAR
    BuiltinDestination.PRAYER -> RafiqRoutes.PRAYER
    BuiltinDestination.TASKS -> RafiqRoutes.TASKS
    BuiltinDestination.FOCUS -> RafiqRoutes.FOCUS
    BuiltinDestination.ASSISTANT -> RafiqRoutes.ASSISTANT
    BuiltinDestination.SETTINGS -> RafiqRoutes.SETTINGS
}

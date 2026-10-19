package com.rafiq.launcher.ui.screens.home

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rafiq.launcher.data.apps.DockKind
import com.rafiq.launcher.ui.components.BuiltinIconTile
import com.rafiq.launcher.ui.components.DawnMosqueScene
import com.rafiq.launcher.ui.components.RafiqCard
import com.rafiq.launcher.ui.components.RafiqIcons
import com.rafiq.launcher.ui.components.RoundIconButton
import com.rafiq.launcher.ui.components.SectionHeader
import com.rafiq.launcher.ui.components.TransientMessage
import com.rafiq.launcher.ui.RafiqRoutes
import com.rafiq.launcher.ui.AppViewModelProvider
import com.rafiq.launcher.ui.theme.RafiqText
import com.rafiq.launcher.ui.theme.RafiqTheme
import com.rafiq.launcher.util.formatCountdown

@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val palette = RafiqTheme.colors

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 38.dp, bottom = 18.dp)
        ) {
            GreetingHeader(
                displayName = state.displayName,
                locationLabel = state.locationLabel,
                hour = state.now.hour,
                onOpenApps = { onNavigate(RafiqRoutes.APPS) }
            )
            Spacer(Modifier.height(14.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "%02d:%02d".format(state.now.hour, state.now.minute),
                    style = RafiqText.Clock.copy(color = palette.textPrimary)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = state.gregorianDate,
                    style = MaterialTheme.typography.bodyMedium.copy(color = palette.textSecondary)
                )
                Text(
                    text = state.hijriDate,
                    style = MaterialTheme.typography.bodySmall.copy(color = palette.goldMuted)
                )
            }
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .border(1.dp, palette.borderSoft, RoundedCornerShape(28.dp))
            ) {
                DawnMosqueScene(modifier = Modifier.fillMaxSize())
            }
            Spacer(Modifier.height(18.dp))
            NextPrayerCard(
                prayerName = state.next?.prayer?.name,
                prayerTime = state.next?.prayer?.formattedTime,
                countdown = state.next?.let {
                    formatCountdown(it.remaining) + if (it.tomorrow) " (غدًا)" else ""
                },
                progress = state.next?.progress ?: 0f,
                onClick = { onNavigate(RafiqRoutes.PRAYER) }
            )
            Spacer(Modifier.height(20.dp))
            SectionHeader(title = "عباداتك اليوم")
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BuiltinIconTile(
                    icon = RafiqIcons.QuranBook,
                    label = "القرآن",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(RafiqRoutes.QURAN) }
                )
                BuiltinIconTile(
                    icon = RafiqIcons.PrayerBeads,
                    label = "الأذكار",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(RafiqRoutes.ADHKAR) }
                )
                BuiltinIconTile(
                    icon = RafiqIcons.Tasks,
                    label = "المهام",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(RafiqRoutes.TASKS) }
                )
                BuiltinIconTile(
                    icon = RafiqIcons.Focus,
                    label = "التركيز",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(RafiqRoutes.FOCUS) }
                )
                BuiltinIconTile(
                    icon = RafiqIcons.Sparkle,
                    label = "مساعد رفيق",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(RafiqRoutes.ASSISTANT) }
                )
            }
            Spacer(Modifier.height(22.dp))
            PageDots(count = 5, activeIndex = 0)
            Spacer(Modifier.height(16.dp))
            DockRow(
                apps = state.dockApps,
                isLoading = state.isDockLoading,
                onOpen = viewModel::openDockApp
            )
            Spacer(Modifier.height(6.dp))
        }
        TransientMessage(
            message = state.message,
            onConsumed = viewModel::consumeMessage,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun GreetingHeader(
    displayName: String,
    locationLabel: String,
    hour: Int,
    onOpenApps: () -> Unit
) {
    val palette = RafiqTheme.colors
    val greeting = when {
        hour in 4..11 -> "صباح الخير"
        hour in 12..16 -> "طاب نهارك"
        hour in 17..20 -> "مساء الخير"
        else -> "طابت ليلتك"
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = greeting,
                style = MaterialTheme.typography.bodySmall.copy(color = palette.textDim)
            )
            Text(
                text = displayName,
                style = MaterialTheme.typography.titleLarge.copy(color = palette.gold)
            )
        }
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(palette.surface.copy(alpha = 0.7f))
                .border(1.dp, palette.borderSoft, RoundedCornerShape(14.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = RafiqIcons.Location,
                contentDescription = null,
                tint = palette.mint,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(5.dp))
            Text(
                text = locationLabel,
                style = MaterialTheme.typography.labelSmall.copy(color = palette.textSecondary),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.width(8.dp))
        RoundIconButton(
            icon = RafiqIcons.MenuLines,
            contentDescription = "كل التطبيقات",
            size = 38.dp,
            onClick = onOpenApps
        )
    }
}

@Composable
private fun NextPrayerCard(
    prayerName: String?,
    prayerTime: String?,
    countdown: String?,
    progress: Float,
    onClick: () -> Unit
) {
    val palette = RafiqTheme.colors
    RafiqCard(onClick = onClick, contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(palette.surfaceHigh.copy(alpha = 0.8f))
                    .border(1.dp, palette.border, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = RafiqIcons.Mosque,
                    contentDescription = null,
                    tint = palette.gold,
                    modifier = Modifier.size(21.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "الصلاة القادمة",
                    style = MaterialTheme.typography.labelSmall.copy(color = palette.textDim)
                )
                Text(
                    text = prayerName ?: "جارٍ الحساب",
                    style = MaterialTheme.typography.titleLarge.copy(color = palette.textPrimary)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = prayerTime ?: "--:--",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = palette.gold,
                        fontWeight = FontWeight.Bold
                    )
                )
                if (countdown != null) {
                    Text(
                        text = countdown,
                        style = MaterialTheme.typography.labelSmall.copy(color = palette.textSecondary)
                    )
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(CircleShape)
                .background(palette.borderSoft.copy(alpha = 0.5f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0.02f, 1f))
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(listOf(palette.goldMuted, palette.goldBright))
                    )
            )
        }
    }
}

@Composable
private fun PageDots(count: Int, activeIndex: Int) {
    val palette = RafiqTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(count) { index ->
            val active = index == activeIndex
            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .height(if (active) 6.dp else 4.dp)
                    .width(if (active) 18.dp else 4.dp)
                    .clip(CircleShape)
                    .background(if (active) palette.gold else palette.borderSoft)
            )
        }
    }
}

@Composable
private fun DockRow(
    apps: List<com.rafiq.launcher.data.apps.DockApp>,
    isLoading: Boolean,
    onOpen: (DockKind) -> Unit
) {
    val palette = RafiqTheme.colors
    val fallbackIcons = mapOf(
        DockKind.PHONE to RafiqIcons.Phone,
        DockKind.MESSAGES to RafiqIcons.Message,
        DockKind.BROWSER to RafiqIcons.Browser,
        DockKind.CAMERA to RafiqIcons.Camera
    )
    if (isLoading && apps.isEmpty()) {
        Spacer(Modifier.height(58.dp))
        return
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(palette.surface.copy(alpha = 0.55f))
            .border(1.dp, palette.borderSoft, RoundedCornerShape(24.dp))
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        apps.forEach { app ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(palette.surfaceHigh)
                        .border(1.dp, palette.borderSoft, RoundedCornerShape(15.dp))
                        .clickable { onOpen(app.kind) },
                    contentAlignment = Alignment.Center
                ) {
                    val icon = fallbackIcons[app.kind]
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = app.label,
                            tint = palette.gold,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = app.label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        color = palette.textDim
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

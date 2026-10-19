package com.rafiq.launcher.ui.screens.prayer

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rafiq.launcher.data.prayer.CalculationMethod
import com.rafiq.launcher.data.prayer.ManualCity
import com.rafiq.launcher.ui.AppViewModelProvider
import com.rafiq.launcher.ui.components.CountBadge
import com.rafiq.launcher.ui.components.LoadingOrnament
import com.rafiq.launcher.ui.components.RafiqBottomSheet
import com.rafiq.launcher.ui.components.RafiqCard
import com.rafiq.launcher.ui.components.RafiqIcons
import com.rafiq.launcher.ui.components.RafiqSecondaryButton
import com.rafiq.launcher.ui.components.RafiqTopBar
import com.rafiq.launcher.ui.components.RoundIconButton
import com.rafiq.launcher.ui.components.TransientMessage
import com.rafiq.launcher.ui.theme.RafiqText
import com.rafiq.launcher.ui.theme.RafiqTheme
import com.rafiq.launcher.util.formatCountdownShort
import com.rafiq.launcher.util.formatTime12Hour

@Composable
fun PrayerScreen(
    viewModel: PrayerViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val palette = RafiqTheme.colors
    var showMethodSheet by remember { mutableStateOf(false) }
    var showCitySheet by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        if (grants.values.any { it }) {
            viewModel.detectLocation()
        } else {
            viewModel.detectLocation()
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.setNotificationsEnabled(granted)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            RafiqTopBar(
                title = "الصلاة",
                subtitle = "المواقيت في ${state.locationLabel}",
                actions = {
                    RoundIconButton(
                        icon = RafiqIcons.Location,
                        contentDescription = "تحديد موقعي",
                        size = 38.dp,
                        onClick = {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    )
                    RoundIconButton(
                        icon = RafiqIcons.Bell,
                        contentDescription = "تنبيهات الصلاة",
                        size = 38.dp,
                        tint = if (state.notificationsEnabled) palette.goldBright else palette.textDim,
                        onClick = {
                            if (state.notificationsEnabled) {
                                viewModel.setNotificationsEnabled(false)
                            } else {
                                notificationPermissionLauncher.launch(
                                    Manifest.permission.POST_NOTIFICATIONS
                                )
                            }
                        }
                    )
                    RoundIconButton(
                        icon = RafiqIcons.Settings,
                        contentDescription = "طريقة الحساب",
                        size = 38.dp,
                        onClick = { showMethodSheet = true }
                    )
                }
            )
            if (state.isLoading) {
                LoadingOrnament(label = "جارٍ حساب المواقيت")
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 22.dp)
                ) {
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CountdownRing(progress = state.next?.progress ?: 0f)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "الصلاة القادمة",
                                style = MaterialTheme.typography.labelSmall.copy(color = palette.textDim)
                            )
                            Text(
                                text = state.next?.prayer?.name ?: "--",
                                style = RafiqText.OrnamentTitle.copy(color = palette.gold)
                            )
                            Text(
                                text = state.next?.prayer?.let { formatTime12Hour(it.time) } ?: "--:--",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = palette.textPrimary
                                )
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = state.next?.let { formatCountdownShort(it.remaining) } ?: "--:--",
                                style = MaterialTheme.typography.titleSmall.copy(color = palette.mint)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Chip(
                            text = state.locationLabel,
                            icon = RafiqIcons.Location,
                            modifier = Modifier.weight(1f),
                            onClick = { showCitySheet = true }
                        )
                        Chip(
                            text = state.method.arabicName,
                            icon = RafiqIcons.Settings,
                            modifier = Modifier.weight(1f),
                            onClick = { showMethodSheet = true }
                        )
                    }
                    Spacer(Modifier.height(18.dp))
                    RafiqCard(contentPadding = PaddingValues(vertical = 6.dp)) {
                        state.times?.prayers?.forEachIndexed { index, prayer ->
                            val isNext = state.next?.prayer?.id == prayer.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 11.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(9.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isNext) palette.gold else palette.borderSoft
                                        )
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = prayer.name,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = if (isNext) palette.gold else palette.textPrimary
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                if (index == 0 && state.times?.highLatitudeApproximation == true) {
                                    CountBadge(text = "تقديري")
                                    Spacer(Modifier.width(8.dp))
                                }
                                Text(
                                    text = formatTime12Hour(prayer.time),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = if (isNext) palette.gold else palette.textSecondary
                                    )
                                )
                            }
                            if (index < (state.times?.prayers?.size ?: 0) - 1) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .height(1.dp)
                                        .background(palette.borderSoft.copy(alpha = 0.5f))
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    if (state.times?.highLatitudeApproximation == true) {
                        Text(
                            text = "في خطوط العرض العالية قد تُقدَّر مواقيت الفجر والعشاء عند تعذّر الحساب الفلكي الدقيق.",
                            style = MaterialTheme.typography.bodySmall.copy(color = palette.textDim),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                    Text(
                        text = "الفجر ${state.times?.let { formatTime12Hour(it.fajr) } ?: "--"} • " +
                            "الشروق ${state.times?.let { formatTime12Hour(it.sunrise) } ?: "--"}",
                        style = MaterialTheme.typography.bodySmall.copy(color = palette.textDim),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(14.dp))
                    RafiqSecondaryButton(
                        text = if (state.notificationsEnabled) "تنبيهات الصلاة مفعّلة" else "تفعيل تنبيهات الصلاة",
                        icon = RafiqIcons.Bell,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            if (state.notificationsEnabled) {
                                viewModel.setNotificationsEnabled(false)
                            } else {
                                notificationPermissionLauncher.launch(
                                    Manifest.permission.POST_NOTIFICATIONS
                                )
                            }
                        }
                    )
                }
            }
        }
        TransientMessage(
            message = state.message,
            onConsumed = viewModel::consumeMessage,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }

    if (showMethodSheet) {
        RafiqBottomSheet(onDismiss = { showMethodSheet = false }) {
            Text(
                text = "طريقة حساب المواقيت",
                style = MaterialTheme.typography.titleLarge,
                color = palette.gold
            )
            Spacer(Modifier.height(8.dp))
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                CalculationMethod.entries.forEach { method ->
                    val selected = method.id == state.method.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (selected) palette.surfaceHigh.copy(alpha = 0.9f) else Color.Transparent
                            )
                            .clickable {
                                viewModel.setMethod(method)
                                showMethodSheet = false
                            }
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = method.arabicName,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (selected) palette.gold else palette.textPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        if (selected) {
                            Icon(
                                imageVector = RafiqIcons.Check,
                                contentDescription = null,
                                tint = palette.gold,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCitySheet) {
        RafiqBottomSheet(onDismiss = { showCitySheet = false }) {
            Text(
                text = "مدينة الحساب",
                style = MaterialTheme.typography.titleLarge,
                color = palette.gold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "اختر مدينة يدوية عند عدم رغبتك في استخدام GPS.",
                style = MaterialTheme.typography.bodySmall.copy(color = palette.textDim)
            )
            Spacer(Modifier.height(8.dp))
            CityList(
                cities = viewModel.cities,
                selectedId = if (state.locationMode == "manual") null else null,
                currentLabel = state.locationLabel,
                onSelect = { city ->
                    viewModel.chooseCity(city)
                    showCitySheet = false
                }
            )
        }
    }
}

@Composable
private fun CityList(
    cities: List<ManualCity>,
    selectedId: String?,
    currentLabel: String,
    onSelect: (ManualCity) -> Unit
) {
    val palette = RafiqTheme.colors
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        cities.forEach { city ->
            val selected = city.name == currentLabel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (selected) palette.surfaceHigh.copy(alpha = 0.9f) else Color.Transparent
                    )
                    .clickable { onSelect(city) }
                    .padding(horizontal = 12.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = city.name,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = if (selected) palette.gold else palette.textPrimary
                    ),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = city.country,
                    style = MaterialTheme.typography.labelSmall.copy(color = palette.textDim)
                )
            }
        }
    }
}

@Composable
private fun CountdownRing(progress: Float) {
    val palette = RafiqTheme.colors
    Canvas(modifier = Modifier.size(230.dp)) {
        val stroke = 10.dp.toPx()
        val inset = stroke / 2f
        val arcSize = Size(size.width - stroke, size.height - stroke)
        drawArc(
            color = palette.borderSoft.copy(alpha = 0.5f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
        drawArc(
            brush = Brush.sweepGradient(
                listOf(palette.goldMuted, palette.gold, palette.goldBright, palette.goldMuted)
            ),
            startAngle = -90f,
            sweepAngle = (progress.coerceIn(0f, 1f) * 360f).coerceAtLeast(4f),
            useCenter = false,
            topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun Chip(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val palette = RafiqTheme.colors
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(palette.surface.copy(alpha = 0.7f))
            .border(1.dp, palette.borderSoft, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = palette.goldMuted,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(color = palette.textSecondary),
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}

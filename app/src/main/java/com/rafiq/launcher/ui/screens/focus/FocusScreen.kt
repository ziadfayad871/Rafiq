package com.rafiq.launcher.ui.screens.focus

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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rafiq.launcher.data.apps.InstalledApp
import com.rafiq.launcher.ui.AppViewModelProvider
import com.rafiq.launcher.ui.components.AppIconImage
import com.rafiq.launcher.ui.components.RafiqCard
import com.rafiq.launcher.ui.components.RafiqIcons
import com.rafiq.launcher.ui.components.RafiqPrimaryButton
import com.rafiq.launcher.ui.components.RafiqSecondaryButton
import com.rafiq.launcher.ui.components.RafiqTopBar
import com.rafiq.launcher.ui.components.TransientMessage
import com.rafiq.launcher.ui.theme.RafiqText
import com.rafiq.launcher.ui.theme.RafiqTheme
import java.time.Duration

@Composable
fun FocusScreen(
    onBack: () -> Unit,
    viewModel: FocusViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val palette = RafiqTheme.colors

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            RafiqTopBar(
                title = "التركيز",
                subtitle = state.sessionTitle,
                onBack = onBack
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp)
            ) {
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentAlignment = Alignment.Center
                ) {
                    FocusRing(progress = state.progress, active = state.phase == FocusPhase.RUNNING)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = when (state.phase) {
                                FocusPhase.IDLE -> "جاهز للبدء"
                                FocusPhase.RUNNING -> "جلسة جارية"
                                FocusPhase.PAUSED -> "متوقفة مؤقتًا"
                                FocusPhase.COMPLETED -> "أُنجزت الجلسة"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(color = palette.textDim)
                        )
                        Text(
                            text = formatRemaining(state.remainingMs),
                            style = RafiqText.Timer.copy(color = palette.textPrimary)
                        )
                        Text(
                            text = state.sessionTitle,
                            style = MaterialTheme.typography.titleSmall.copy(color = palette.gold)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                if (state.phase == FocusPhase.IDLE || state.phase == FocusPhase.PAUSED) {
                    DurationRow(
                        selectedMinutes = state.durationMinutes,
                        enabled = state.phase == FocusPhase.IDLE,
                        onSelect = viewModel::setDuration
                    )
                    Spacer(Modifier.height(12.dp))
                    SessionTitleRow(
                        selected = state.sessionTitle,
                        onSelect = viewModel::setSessionTitle
                    )
                    Spacer(Modifier.height(18.dp))
                }
                when (state.phase) {
                    FocusPhase.IDLE -> RafiqPrimaryButton(
                        text = "ابدأ جلسة التركيز",
                        icon = RafiqIcons.Play,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = viewModel::start
                    )
                    FocusPhase.RUNNING -> Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        RafiqPrimaryButton(
                            text = "إيقاف مؤقت",
                            icon = RafiqIcons.Pause,
                            modifier = Modifier.weight(1f),
                            onClick = viewModel::pause
                        )
                        RafiqSecondaryButton(
                            text = "إنهاء",
                            icon = RafiqIcons.Close,
                            modifier = Modifier.weight(1f),
                            onClick = viewModel::reset
                        )
                    }
                    FocusPhase.PAUSED -> Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        RafiqPrimaryButton(
                            text = "متابعة",
                            icon = RafiqIcons.Play,
                            modifier = Modifier.weight(1f),
                            onClick = viewModel::resume
                        )
                        RafiqSecondaryButton(
                            text = "إنهاء",
                            icon = RafiqIcons.Close,
                            modifier = Modifier.weight(1f),
                            onClick = viewModel::reset
                        )
                    }
                    FocusPhase.COMPLETED -> RafiqPrimaryButton(
                        text = "جلسة جديدة",
                        icon = RafiqIcons.Refresh,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = viewModel::reset
                    )
                }
                Spacer(Modifier.height(22.dp))
                Text(
                    text = "التطبيقات المسموحة أثناء الجلسة",
                    style = MaterialTheme.typography.titleMedium.copy(color = palette.textPrimary)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "يفتح أندرويد واجهات التركيز الرسمية فقط للتطبيقات المثبتة كقاذف. رفيق يحتفظ بقائمتك كمرجع وتذكير، وسيستخدمها في تجربة التركيز الكاملة على الأجهزة التي تدعم ذلك.",
                    style = MaterialTheme.typography.bodySmall.copy(color = palette.textDim)
                )
                Spacer(Modifier.height(10.dp))
                RafiqCard(contentPadding = PaddingValues(vertical = 6.dp)) {
                    if (state.isLoadingApps) {
                        Text(
                            text = "جارٍ قراءة التطبيقات...",
                            style = MaterialTheme.typography.bodySmall.copy(color = palette.textDim),
                            modifier = Modifier.padding(16.dp)
                        )
                    } else if (state.installedApps.isEmpty()) {
                        Text(
                            text = "لا توجد تطبيقات مثبتة ظاهرة",
                            style = MaterialTheme.typography.bodySmall.copy(color = palette.textDim),
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        state.installedApps.forEach { app ->
                            AllowedAppRow(
                                app = app,
                                allowed = state.allowedPackages.contains(app.packageName),
                                onToggle = { viewModel.toggleAllowedPackage(app.packageName) }
                            )
                        }
                    }
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
}

private fun formatRemaining(millis: Long): String {
    val safe = millis.coerceAtLeast(0)
    val duration = Duration.ofMillis(safe)
    val minutes = duration.toMinutes()
    val seconds = duration.seconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

@Composable
private fun FocusRing(progress: Float, active: Boolean) {
    val palette = RafiqTheme.colors
    Canvas(modifier = Modifier.size(230.dp)) {
        val stroke = 12.dp.toPx()
        val inset = stroke / 2f
        val arcSize = Size(size.width - stroke, size.height - stroke)
        val topLeft = Offset(inset, inset)
        drawArc(
            color = palette.borderSoft.copy(alpha = 0.45f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
        drawArc(
            brush = Brush.sweepGradient(
                listOf(
                    if (active) palette.mint else palette.goldMuted,
                    palette.gold,
                    palette.goldBright,
                    palette.goldMuted
                )
            ),
            startAngle = -90f,
            sweepAngle = (progress.coerceIn(0f, 1f) * 360f).coerceAtLeast(3f),
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun DurationRow(
    selectedMinutes: Int,
    enabled: Boolean,
    onSelect: (Int) -> Unit
) {
    val palette = RafiqTheme.colors
    val options = listOf(15, 25, 45, 60)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { minutes ->
            val selected = minutes == selectedMinutes
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (selected) palette.gold.copy(alpha = 0.18f)
                        else palette.surface.copy(alpha = 0.6f)
                    )
                    .border(
                        1.dp,
                        if (selected) palette.gold else palette.borderSoft,
                        RoundedCornerShape(14.dp)
                    )
                    .clickable(enabled = enabled) { onSelect(minutes) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$minutes دقيقة",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = if (selected) palette.goldBright else palette.textSecondary
                    )
                )
            }
        }
    }
}

@Composable
private fun SessionTitleRow(selected: String, onSelect: (String) -> Unit) {
    val palette = RafiqTheme.colors
    val titles = listOf("الدراسة", "العمل", "القرآن", "قراءة")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        titles.forEach { title ->
            val isSelected = title == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(
                        if (isSelected) palette.surfaceHigh else Color.Transparent
                    )
                    .border(
                        1.dp,
                        if (isSelected) palette.border else palette.borderSoft,
                        RoundedCornerShape(13.dp)
                    )
                    .clickable { onSelect(title) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = if (isSelected) palette.gold else palette.textSecondary
                    )
                )
            }
        }
    }
}

@Composable
private fun AllowedAppRow(
    app: InstalledApp,
    allowed: Boolean,
    onToggle: () -> Unit
) {
    val palette = RafiqTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppIconImage(bitmap = app.icon, label = app.label, iconSize = 38.dp)
        Spacer(Modifier.width(12.dp))
        Text(
            text = app.label,
            style = MaterialTheme.typography.bodyMedium.copy(color = palette.textPrimary),
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    if (allowed) palette.gold.copy(alpha = 0.2f) else Color.Transparent
                )
                .border(
                    1.dp,
                    if (allowed) palette.gold else palette.borderSoft,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (allowed) {
                Icon(
                    imageVector = RafiqIcons.Check,
                    contentDescription = null,
                    tint = palette.goldBright,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

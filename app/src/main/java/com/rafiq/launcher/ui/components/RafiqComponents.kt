package com.rafiq.launcher.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.rafiq.launcher.ui.theme.LocalClickSound
import com.rafiq.launcher.ui.theme.LocalHapticsEnabled
import com.rafiq.launcher.ui.theme.RafiqTheme
import com.rafiq.launcher.ui.theme.RafiqText
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RafiqCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(22.dp),
    borderColor: Color = RafiqTheme.colors.borderSoft,
    contentPadding: PaddingValues = PaddingValues(18.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val palette = RafiqTheme.colors
    val haptics = LocalHapticsEnabled.current
    val haptic = LocalHapticFeedback.current
    val click = LocalClickSound.current
    val interaction = remember { MutableInteractionSource() }
    val clickModifier = if (onClick != null) {
        Modifier.clickable(interactionSource = interaction, indication = null) {
            if (haptics) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            click()
            onClick()
        }
    } else Modifier
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        palette.surfaceHigh.copy(alpha = 0.92f),
                        palette.surface.copy(alpha = 0.96f)
                    )
                )
            )
            .border(1.dp, borderColor, shape)
            .then(clickModifier)
    ) {
        Column(modifier = Modifier.padding(contentPadding), content = content)
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    action: (@Composable () -> Unit)? = null
) {
    val palette = RafiqTheme.colors
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = palette.textPrimary
            )
            if (subtitle != null) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = palette.textDim
                )
            }
        }
        if (action != null) action()
    }
}

@Composable
fun OrnamentalDivider(
    modifier: Modifier = Modifier,
    color: Color = RafiqTheme.colors.gold.copy(alpha = 0.35f)
) {
    Canvas(modifier.fillMaxWidth().height(20.dp)) {
        val centerY = size.height / 2f
        val middleGap = 18.dp.toPx()
        drawLine(
            color = color,
            start = Offset(0f, centerY),
            end = Offset(size.width / 2f - middleGap, centerY),
            strokeWidth = 0.8.dp.toPx()
        )
        drawLine(
            color = color,
            start = Offset(size.width / 2f + middleGap, centerY),
            end = Offset(size.width, centerY),
            strokeWidth = 0.8.dp.toPx()
        )
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(size.width / 2f, centerY - 6.dp.toPx())
            lineTo(size.width / 2f + 6.dp.toPx(), centerY)
            lineTo(size.width / 2f, centerY + 6.dp.toPx())
            lineTo(size.width / 2f - 6.dp.toPx(), centerY)
            close()
        }
        drawPath(path, color = color, style = Stroke(1.dp.toPx()))
        drawCircle(color = color, radius = 1.4.dp.toPx(), center = Offset(size.width / 2f, centerY))
    }
}

@Composable
fun RafiqPrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    val palette = RafiqTheme.colors
    val haptics = LocalHapticsEnabled.current
    val haptic = LocalHapticFeedback.current
    val click = LocalClickSound.current
    Row(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(17.dp))
            .background(
                if (enabled) Brush.horizontalGradient(
                    listOf(palette.goldBright, palette.gold, palette.goldMuted)
                ) else Brush.horizontalGradient(
                    listOf(palette.surfaceHigh, palette.surfaceHigh)
                )
            )
            .clickable(enabled = enabled) {
                if (haptics) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                click()
                onClick()
            }
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = palette.backgroundDeep,
                modifier = Modifier.size(19.dp)
            )
            Spacer(Modifier.width(9.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = palette.backgroundDeep
            )
        )
    }
}

@Composable
fun RafiqSecondaryButton(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    contentColor: Color = RafiqTheme.colors.gold,
    onClick: () -> Unit
) {
    val palette = RafiqTheme.colors
    val haptics = LocalHapticsEnabled.current
    val haptic = LocalHapticFeedback.current
    val click = LocalClickSound.current
    Row(
        modifier = modifier
            .height(50.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(palette.surfaceHigh.copy(alpha = 0.6f))
            .border(1.dp, palette.border, RoundedCornerShape(16.dp))
            .clickable {
                if (haptics) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                click()
                onClick()
            }
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall.copy(
                color = contentColor,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
fun RoundIconButton(
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    filled: Boolean = false,
    tint: Color = RafiqTheme.colors.gold,
    onClick: () -> Unit
) {
    val palette = RafiqTheme.colors
    val haptics = LocalHapticsEnabled.current
    val haptic = LocalHapticFeedback.current
    val click = LocalClickSound.current
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(if (filled) palette.surfaceHigh else palette.surface.copy(alpha = 0.45f))
            .border(1.dp, palette.borderSoft, CircleShape)
            .clickable {
                if (haptics) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                click()
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription, tint = tint, modifier = Modifier.size(size * 0.46f))
    }
}

@Composable
fun SegmentedPill(
    options: List<String>,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    onSelected: (Int) -> Unit
) {
    val palette = RafiqTheme.colors
    val haptics = LocalHapticsEnabled.current
    val haptic = LocalHapticFeedback.current
    val click = LocalClickSound.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(17.dp))
            .background(palette.surfaceHigh.copy(alpha = 0.7f))
            .border(1.dp, palette.borderSoft, RoundedCornerShape(17.dp))
            .padding(4.dp)
    ) {
        options.forEachIndexed { index, option ->
            val selected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(
                        if (selected) Brush.horizontalGradient(
                            listOf(palette.goldBright, palette.gold)
                        ) else Brush.horizontalGradient(
                            listOf(Color.Transparent, Color.Transparent)
                        )
                    )
                    .clickable {
                        if (haptics) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        click()
                        onSelected(index)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = if (selected) palette.backgroundDeep else palette.textSecondary
                    )
                )
            }
        }
    }
}

@Composable
fun CountBadge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = RafiqTheme.colors.gold
) {
    val palette = RafiqTheme.colors
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(palette.surfaceHigh.copy(alpha = 0.85f))
            .border(1.dp, palette.borderSoft, RoundedCornerShape(10.dp))
            .padding(horizontal = 9.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                color = color,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    val palette = RafiqTheme.colors
    Column(
        modifier = modifier.fillMaxWidth().padding(vertical = 42.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(82.dp)
                .clip(CircleShape)
                .background(palette.surface.copy(alpha = 0.7f))
                .border(1.dp, palette.border, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = palette.gold, modifier = Modifier.size(34.dp))
        }
        Spacer(Modifier.height(18.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = palette.textPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = palette.textSecondary,
            textAlign = TextAlign.Center
        )
        if (action != null) {
            Spacer(Modifier.height(18.dp))
            action()
        }
    }
}

@Composable
fun LoadingOrnament(
    label: String = "جارٍ التحميل",
    modifier: Modifier = Modifier
) {
    val palette = RafiqTheme.colors
    val transition = rememberInfiniteTransition(label = "loading")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    val pulse by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    Column(
        modifier = modifier.fillMaxWidth().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.size(78.dp), contentAlignment = Alignment.Center) {
            Canvas(Modifier.size(78.dp)) {
                drawArc(
                    color = palette.border,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 1.dp.toPx())
                )
                drawArc(
                    brush = Brush.sweepGradient(
                        listOf(Color.Transparent, palette.gold)
                    ),
                    startAngle = rotation,
                    sweepAngle = 120f,
                    useCenter = false,
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
                    size = Size(size.width, size.height)
                )
            }
            RafiqMark(size = 34.dp * pulse)
        }
        Spacer(Modifier.height(18.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = palette.textSecondary
        )
    }
}

@Composable
fun ErrorOrnament(
    title: String = "تعذّر إكمال الطلب",
    message: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null
) {
    EmptyState(
        icon = RafiqIcons.Info,
        title = title,
        body = message,
        modifier = modifier,
        action = if (onRetry != null) {
            {
                RafiqSecondaryButton(text = "إعادة المحاولة", icon = RafiqIcons.Refresh, onClick = onRetry)
            }
        } else null
    )
}

@Composable
fun RafiqTopBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null
) {
    val palette = RafiqTheme.colors
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(62.dp)
            .padding(horizontal = 18.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = palette.gold,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textSecondary,
                    maxLines = 1
                )
            }
        }
        if (onBack != null) {
            RoundIconButton(
                icon = RafiqIcons.ArrowLeft,
                contentDescription = "رجوع",
                modifier = Modifier.align(Alignment.CenterEnd),
                size = 38.dp,
                onClick = onBack
            )
        }
        if (actions != null) {
            Row(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                content = actions
            )
        }
    }
}

@Composable
fun RafiqBottomBar(
    selectedRoute: String,
    modifier: Modifier = Modifier,
    onNavigate: (String) -> Unit
) {
    val palette = RafiqTheme.colors
    val click = LocalClickSound.current
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = palette.backgroundSecondary.copy(alpha = 0.98f),
        tonalElevation = 0.dp
    ) {
        Column {
            OrnamentalDivider(
                modifier = Modifier.padding(horizontal = 22.dp),
                color = palette.border.copy(alpha = 0.8f)
            )
            val items = listOf(
                BottomItem("home", "الرئيسية", RafiqIcons.Home),
                BottomItem("prayer", "الصلاة", RafiqIcons.Mosque),
                BottomItem("quran", "القرآن", RafiqIcons.BookOpen),
                BottomItem("adhkar", "الأذكار", RafiqIcons.PrayerBeads),
                BottomItem("settings", "الإعدادات", RafiqIcons.Settings)
            )
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                items.forEach { item ->
                    val selected = selectedRoute == item.route ||
                        (item.route == "quran" && selectedRoute.startsWith("quran"))
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                click()
                                onNavigate(item.route)
                            }
                            .padding(vertical = 7.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (selected) palette.gold else palette.textDim,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.5.sp,
                                color = if (selected) palette.gold else palette.textDim,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                        Box(
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .height(2.dp)
                                .width(13.dp)
                                .clip(CircleShape)
                                .background(if (selected) palette.gold else Color.Transparent)
                        )
                    }
                }
                }
            }
        }
    }
}

private data class BottomItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun FloatingGoldButton(
    modifier: Modifier = Modifier,
    icon: ImageVector = RafiqIcons.Plus,
    onClick: () -> Unit
) {
    val palette = RafiqTheme.colors
    val haptics = LocalHapticsEnabled.current
    val haptic = LocalHapticFeedback.current
    val click = LocalClickSound.current
    Box(
        modifier = modifier
            .size(58.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(palette.goldBright, palette.gold, palette.goldMuted),
                    center = Offset(18f, 14f),
                    radius = 72f
                )
            )
            .clickable {
                if (haptics) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                click()
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = "إضافة", tint = palette.backgroundDeep, modifier = Modifier.size(24.dp))
    }
}

@Composable
fun AppIconImage(
    bitmap: ImageBitmap?,
    label: String,
    modifier: Modifier = Modifier,
    iconSize: Dp = 54.dp
) {
    val palette = RafiqTheme.colors
    Box(
        modifier = modifier
            .size(iconSize)
            .clip(RoundedCornerShape(16.dp))
            .background(palette.surfaceHigh)
            .border(1.dp, palette.borderSoft, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = label,
                modifier = Modifier.size(iconSize * 0.68f)
            )
        } else {
            Text(
                text = label.take(1),
                style = MaterialTheme.typography.headlineSmall,
                color = palette.gold
            )
        }
    }
}

@Composable
fun BuiltinIconTile(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    iconSize: Dp = 56.dp,
    onClick: () -> Unit
) {
    val palette = RafiqTheme.colors
    val haptics = LocalHapticsEnabled.current
    val haptic = LocalHapticFeedback.current
    val click = LocalClickSound.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable {
                if (haptics) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                click()
                onClick()
            }
            .padding(vertical = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(iconSize)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.linearGradient(
                        listOf(palette.surfaceHigh, palette.surface.copy(alpha = 0.9f))
                    )
                )
                .border(1.dp, palette.border, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = palette.gold, modifier = Modifier.size(iconSize * 0.46f))
        }
        Spacer(Modifier.height(7.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = palette.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RafiqBottomSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val palette = RafiqTheme.colors
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = palette.backgroundSecondary,
        scrimColor = Color.Black.copy(alpha = 0.65f),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 6.dp)
                    .width(42.dp)
                    .height(3.dp)
                    .clip(CircleShape)
                    .background(palette.gold.copy(alpha = 0.5f))
            )
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 30.dp)
        ) {
            content()
        }
    }
}

@Composable
fun RafiqDialog(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val palette = RafiqTheme.colors
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(palette.surfaceHigh, palette.backgroundSecondary)
                    )
                )
                .border(1.dp, palette.border, RoundedCornerShape(26.dp))
                .padding(22.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = palette.gold,
                        modifier = Modifier.weight(1f)
                    )
                    RoundIconButton(
                        icon = RafiqIcons.Close,
                        contentDescription = "إغلاق",
                        size = 34.dp,
                        onClick = onDismiss
                    )
                }
                Spacer(Modifier.height(14.dp))
                content()
            }
        }
    }
}

@Composable
fun TransientMessage(
    message: String?,
    modifier: Modifier = Modifier,
    onConsumed: () -> Unit
) {
    if (message == null) return
    LaunchedEffect(message) {
        kotlinx.coroutines.delay(2600)
        onConsumed()
    }
    val palette = RafiqTheme.colors
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(palette.surfaceHigh.copy(alpha = 0.97f))
            .border(1.dp, palette.border, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = palette.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SimpleOrnamentRing(
    modifier: Modifier = Modifier,
    color: Color = RafiqTheme.colors.gold,
    segments: Int = 8
) {
    Canvas(modifier) {
        val radius = size.minDimension / 2f
        drawCircle(
            color = color.copy(alpha = 0.3f),
            radius = radius,
            style = Stroke(1.dp.toPx())
        )
        repeat(segments) { index ->
            val angle = Math.toRadians(index * (360.0 / segments))
            val x = center.x + (radius * 0.9f * cos(angle)).toFloat()
            val y = center.y + (radius * 0.9f * sin(angle)).toFloat()
            drawCircle(color = color.copy(alpha = 0.55f), radius = 1.6.dp.toPx(), center = Offset(x, y))
        }
    }
}


package com.rafiq.launcher.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val LocalRafiqPalette = staticCompositionLocalOf { EmeraldPalette }

val LocalHapticsEnabled = staticCompositionLocalOf { true }

val LocalClickSound = staticCompositionLocalOf<() -> Unit> { {} }

object RafiqTheme {
    val colors: RafiqPalette
        @Composable get() = LocalRafiqPalette.current
}

val RafiqShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun RafiqTheme(themeId: String = "emerald", content: @Composable () -> Unit) {
    val palette = paletteFor(themeId)
    val colorScheme = darkColorScheme(
        primary = palette.gold,
        onPrimary = palette.backgroundDeep,
        primaryContainer = palette.surfaceHigh,
        onPrimaryContainer = palette.goldBright,
        secondary = palette.mint,
        onSecondary = palette.backgroundDeep,
        secondaryContainer = palette.surfaceHigh,
        onSecondaryContainer = palette.mint,
        tertiary = palette.goldMuted,
        background = palette.background,
        onBackground = palette.textPrimary,
        surface = palette.surface,
        onSurface = palette.textPrimary,
        surfaceVariant = palette.surfaceHigh,
        onSurfaceVariant = palette.textSecondary,
        outline = palette.border,
        outlineVariant = palette.borderSoft,
        error = palette.danger,
        onError = palette.backgroundDeep,
        scrim = Color.Black
    )
    CompositionLocalProvider(LocalRafiqPalette provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = RafiqTypography,
            shapes = RafiqShapes,
            content = content
        )
    }
}


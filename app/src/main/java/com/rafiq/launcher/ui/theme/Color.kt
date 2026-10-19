package com.rafiq.launcher.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class RafiqPalette(
    val background: Color,
    val backgroundSecondary: Color,
    val backgroundDeep: Color,
    val surface: Color,
    val surfaceHigh: Color,
    val surfaceSoft: Color,
    val border: Color,
    val borderSoft: Color,
    val gold: Color,
    val goldBright: Color,
    val goldMuted: Color,
    val mint: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textDim: Color,
    val danger: Color,
    val success: Color
)

val EmeraldPalette = RafiqPalette(
    background = Color(0xFF02100B),
    backgroundSecondary = Color(0xFF041B14),
    backgroundDeep = Color(0xFF000A07),
    surface = Color(0xFF06231B),
    surfaceHigh = Color(0xFF0A3026),
    surfaceSoft = Color(0xFF0C2C23),
    border = Color(0xFF1B4A3C),
    borderSoft = Color(0xFF12382D),
    gold = Color(0xFFE4CE97),
    goldBright = Color(0xFFF2E4BC),
    goldMuted = Color(0xFFB9A56E),
    mint = Color(0xFF72E2CC),
    textPrimary = Color(0xFFF5F1E7),
    textSecondary = Color(0xFFB5C7BD),
    textDim = Color(0xFF789188),
    danger = Color(0xFFE39A8E),
    success = Color(0xFF75DDC5)
)

val MidnightPalette = EmeraldPalette.copy(
    background = Color(0xFF010B08),
    backgroundSecondary = Color(0xFF031510),
    backgroundDeep = Color(0xFF000604),
    surface = Color(0xFF041B15),
    surfaceHigh = Color(0xFF07261E),
    surfaceSoft = Color(0xFF08251D),
    border = Color(0xFF173E32),
    borderSoft = Color(0xFF0F2D24),
    gold = Color(0xFFD9C58F)
)

val DawnPalette = EmeraldPalette.copy(
    background = Color(0xFF07130F),
    backgroundSecondary = Color(0xFF0B2119),
    backgroundDeep = Color(0xFF020B08),
    surface = Color(0xFF0A2A21),
    surfaceHigh = Color(0xFF10382D),
    surfaceSoft = Color(0xFF0E3127),
    border = Color(0xFF265546),
    borderSoft = Color(0xFF1A4437),
    gold = Color(0xFFE8D3A0),
    mint = Color(0xFF83E6D0)
)

val WarmParchment = Color(0xFFF5E8C8)
val ParchmentDeep = Color(0xFFE5D4AB)
val ParchmentText = Color(0xFF3F392D)
val ParchmentEdge = Color(0xFFC8AE72)
val HeroGold = Color(0xFFC89D57)

fun paletteFor(themeId: String): RafiqPalette = when (themeId) {
    "midnight" -> MidnightPalette
    "dawn" -> DawnPalette
    else -> EmeraldPalette
}

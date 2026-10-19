package com.rafiq.launcher.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.rafiq.launcher.R

val TajawalFamily = FontFamily(
    Font(R.font.tajawal_light, FontWeight.Light),
    Font(R.font.tajawal_regular, FontWeight.Normal),
    Font(R.font.tajawal_medium, FontWeight.Medium),
    Font(R.font.tajawal_bold, FontWeight.Bold),
    Font(R.font.tajawal_extrabold, FontWeight.ExtraBold)
)

val AmiriFamily = FontFamily(
    Font(R.font.amiri_regular, FontWeight.Normal),
    Font(R.font.amiri_bold, FontWeight.Bold)
)

val RafiqTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = AmiriFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 54.sp,
        lineHeight = 64.sp
    ),
    displayMedium = TextStyle(
        fontFamily = TajawalFamily,
        fontWeight = FontWeight.Light,
        fontSize = 68.sp,
        lineHeight = 72.sp
    ),
    displaySmall = TextStyle(
        fontFamily = TajawalFamily,
        fontWeight = FontWeight.Light,
        fontSize = 42.sp,
        lineHeight = 48.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = TajawalFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 30.sp,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = TajawalFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 25.sp,
        lineHeight = 34.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = TajawalFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 21.sp,
        lineHeight = 30.sp
    ),
    titleLarge = TextStyle(
        fontFamily = TajawalFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 19.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = TajawalFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    titleSmall = TextStyle(
        fontFamily = TajawalFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 21.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = TajawalFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 26.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = TajawalFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 23.sp
    ),
    bodySmall = TextStyle(
        fontFamily = TajawalFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = TajawalFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontFamily = TajawalFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 18.sp
    ),
    labelSmall = TextStyle(
        fontFamily = TajawalFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 16.sp
    )
)

object RafiqText {
    val Clock = TextStyle(
        fontFamily = TajawalFamily,
        fontWeight = FontWeight.Light,
        fontSize = 68.sp,
        lineHeight = 72.sp,
        letterSpacing = (-1).sp
    )
    val ClockCompact = TextStyle(
        fontFamily = TajawalFamily,
        fontWeight = FontWeight.Light,
        fontSize = 52.sp,
        lineHeight = 56.sp,
        letterSpacing = (-1).sp
    )
    val Quran = TextStyle(
        fontFamily = AmiriFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 25.sp,
        lineHeight = 48.sp
    )
    val QuranLarge = TextStyle(
        fontFamily = AmiriFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 29.sp,
        lineHeight = 56.sp
    )
    val QuranSmall = TextStyle(
        fontFamily = AmiriFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 21.sp,
        lineHeight = 40.sp
    )
    val LogoArabic = TextStyle(
        fontFamily = AmiriFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        lineHeight = 56.sp
    )
    val OrnamentTitle = TextStyle(
        fontFamily = AmiriFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 38.sp
    )
    val Timer = TextStyle(
        fontFamily = TajawalFamily,
        fontWeight = FontWeight.Light,
        fontSize = 58.sp,
        lineHeight = 64.sp
    )
    val Caption = TextStyle(
        fontFamily = TajawalFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 18.sp
    )
}

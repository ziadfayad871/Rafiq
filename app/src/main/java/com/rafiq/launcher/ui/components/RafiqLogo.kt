package com.rafiq.launcher.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rafiq.launcher.ui.theme.RafiqTheme
import com.rafiq.launcher.ui.theme.RafiqText

@Composable
fun RafiqMark(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp
) {
    val palette = RafiqTheme.colors
    val brush = Brush.verticalGradient(
        listOf(palette.goldBright, palette.gold, palette.goldMuted)
    )
    val softBrush = Brush.verticalGradient(
        listOf(
            palette.goldBright.copy(alpha = 0.65f),
            palette.gold.copy(alpha = 0.65f),
            palette.goldMuted.copy(alpha = 0.65f)
        )
    )
    val sideBrush = Brush.verticalGradient(
        listOf(
            palette.goldBright.copy(alpha = 0.75f),
            palette.gold.copy(alpha = 0.75f),
            palette.goldMuted.copy(alpha = 0.75f)
        )
    )
    Canvas(modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cx = w / 2f
        val baseY = h * 0.88f
        val archTop = h * 0.2f
        val stroke = Stroke(
            width = (w * 0.048f).coerceAtLeast(1f),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )

        val outer = Path().apply {
            moveTo(cx - w * 0.30f, baseY)
            lineTo(cx - w * 0.30f, h * 0.49f)
            cubicTo(
                cx - w * 0.30f, h * 0.35f,
                cx - w * 0.22f, h * 0.26f,
                cx, archTop
            )
            cubicTo(
                cx + w * 0.22f, h * 0.26f,
                cx + w * 0.30f, h * 0.35f,
                cx + w * 0.30f, h * 0.49f
            )
            lineTo(cx + w * 0.30f, baseY)
        }
        drawPath(outer, brush = brush, style = stroke)

        val inner = Path().apply {
            moveTo(cx - w * 0.19f, baseY)
            lineTo(cx - w * 0.19f, h * 0.51f)
            cubicTo(
                cx - w * 0.19f, h * 0.41f,
                cx - w * 0.13f, h * 0.34f,
                cx, h * 0.30f
            )
            cubicTo(
                cx + w * 0.13f, h * 0.34f,
                cx + w * 0.19f, h * 0.41f,
                cx + w * 0.19f, h * 0.51f
            )
            lineTo(cx + w * 0.19f, baseY)
        }
        drawPath(inner, brush = softBrush, style = stroke)

        val side = Path().apply {
            moveTo(cx - w * 0.38f, h * 0.39f)
            cubicTo(cx - w * 0.39f, h * 0.54f, cx - w * 0.36f, h * 0.7f, cx - w * 0.32f, h * 0.82f)
            moveTo(cx + w * 0.38f, h * 0.39f)
            cubicTo(cx + w * 0.39f, h * 0.54f, cx + w * 0.36f, h * 0.7f, cx + w * 0.32f, h * 0.82f)
        }
        drawPath(side, brush = sideBrush, style = stroke)

        drawLine(
            brush = brush,
            start = Offset(cx - w * 0.35f, baseY),
            end = Offset(cx + w * 0.35f, baseY),
            strokeWidth = (w * 0.045f).coerceAtLeast(1f),
            cap = StrokeCap.Round
        )

        val finial = Path().apply {
            moveTo(cx, h * 0.10f)
            lineTo(cx, h * 0.2f)
            moveTo(cx - w * 0.055f, h * 0.145f)
            lineTo(cx, h * 0.09f)
            lineTo(cx + w * 0.055f, h * 0.145f)
        }
        drawPath(finial, brush = brush, style = stroke)

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(palette.goldBright, palette.gold),
                center = Offset(cx, h * 0.55f),
                radius = w * 0.07f
            ),
            radius = w * 0.037f,
            center = Offset(cx, h * 0.55f)
        )
    }
}

@Composable
fun RafiqLogo(
    modifier: Modifier = Modifier,
    showTagline: Boolean = false,
    markSize: Dp = 62.dp
) {
    val palette = RafiqTheme.colors
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        RafiqMark(size = markSize)
        Spacer(Modifier.height(4.dp))
        Text(
            text = "رفيق",
            style = RafiqText.LogoArabic.copy(
                fontSize = if (showTagline) 52.sp else 42.sp,
                color = palette.gold,
                textAlign = TextAlign.Center
            )
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(2.dp))
            Text(
                text = "R A F I Q",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 3.sp,
                    color = palette.goldMuted
                )
            )
            Spacer(Modifier.width(2.dp))
        }
        if (showTagline) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = "شريكك في رحلتك نحو الأفضل",
                style = MaterialTheme.typography.bodyMedium,
                color = palette.textSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}


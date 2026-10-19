package com.rafiq.launcher.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.rafiq.launcher.ui.theme.RafiqTheme
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun RafiqBackground(
    modifier: Modifier = Modifier,
    pattern: Boolean = true,
    arch: Boolean = false
) {
    val palette = RafiqTheme.colors
    Canvas(modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    palette.backgroundSecondary,
                    palette.background,
                    palette.backgroundDeep
                )
            )
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(palette.mint.copy(alpha = 0.045f), Color.Transparent),
                center = Offset(size.width * 0.78f, size.height * 0.06f),
                radius = size.width * 0.72f
            ),
            radius = size.width * 0.72f,
            center = Offset(size.width * 0.78f, size.height * 0.06f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(palette.gold.copy(alpha = 0.035f), Color.Transparent),
                center = Offset(size.width * 0.12f, size.height * 0.58f),
                radius = size.width * 0.65f
            ),
            radius = size.width * 0.65f,
            center = Offset(size.width * 0.12f, size.height * 0.58f)
        )
        if (pattern) {
            drawIslamicPattern(
                color = palette.gold.copy(alpha = 0.035f),
                cell = 58.dp.toPx(),
                strokeWidth = 0.8.dp.toPx()
            )
        }
        if (arch) {
            drawMihrabWatermark(palette.gold.copy(alpha = 0.04f))
        }
    }
}

@Composable
fun RafiqScreen(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier.fillMaxSize()) {
        RafiqBackground()
        content()
    }
}

fun DrawScope.drawIslamicPattern(
    color: Color,
    cell: Float,
    strokeWidth: Float = 1f
) {
    val columns = (size.width / cell).toInt() + 2
    val rows = (size.height / cell).toInt() + 2
    val stroke = Stroke(width = strokeWidth)
    for (row in 0 until rows) {
        for (column in 0 until columns) {
            val centerX = column * cell + if (row % 2 == 0) 0f else cell / 2f
            val centerY = row * cell
            val outer = cell * 0.33f
            val inner = cell * 0.16f
            drawGeometricStar(
                center = Offset(centerX, centerY),
                outerRadius = outer,
                innerRadius = inner,
                color = color,
                stroke = stroke
            )
            drawCircle(
                color = color,
                radius = cell * 0.025f,
                center = Offset(centerX, centerY)
            )
        }
    }
}

private fun DrawScope.drawGeometricStar(
    center: Offset,
    outerRadius: Float,
    innerRadius: Float,
    color: Color,
    stroke: Stroke
) {
    val path = Path()
    for (i in 0 until 16) {
        val radius = if (i % 2 == 0) outerRadius else innerRadius
        val angle = Math.toRadians((i * 22.5) - 90.0)
        val point = Offset(
            center.x + (radius * cos(angle)).toFloat(),
            center.y + (radius * sin(angle)).toFloat()
        )
        if (i == 0) path.moveTo(point.x, point.y) else path.lineTo(point.x, point.y)
    }
    path.close()
    drawPath(path, color = color, style = stroke)
    drawCircle(color = color.copy(alpha = color.alpha * 0.65f), radius = innerRadius, center = center, style = stroke)
}

private fun DrawScope.drawMihrabWatermark(color: Color) {
    val path = Path()
    val width = min(size.width * 0.82f, size.height * 0.52f)
    val left = (size.width - width) / 2f
    val bottom = size.height * 0.95f
    val top = size.height * 0.18f
    path.moveTo(left, bottom)
    path.lineTo(left, top + width * 0.34f)
    path.cubicTo(
        left,
        top + width * 0.1f,
        left + width * 0.26f,
        top,
        left + width / 2f,
        top
    )
    path.cubicTo(
        left + width * 0.74f,
        top,
        left + width,
        top + width * 0.1f,
        left + width,
        top + width * 0.34f
    )
    path.lineTo(left + width, bottom)
    drawPath(path, color = color, style = Stroke(width = 1.3.dp.toPx()))
    rotate(degrees = 45f, pivot = Offset(size.width / 2f, top)) {
        drawLine(
            color = color.copy(alpha = color.alpha * 0.8f),
            start = Offset(size.width / 2f, top - 18.dp.toPx()),
            end = Offset(size.width / 2f, top + 18.dp.toPx()),
            strokeWidth = 1.dp.toPx()
        )
    }
}

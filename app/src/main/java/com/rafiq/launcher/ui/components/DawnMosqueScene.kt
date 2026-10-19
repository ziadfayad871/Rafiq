package com.rafiq.launcher.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.rafiq.launcher.ui.theme.RafiqTheme
import kotlin.math.sin

@Composable
fun DawnMosqueScene(modifier: Modifier = Modifier) {
    val palette = RafiqTheme.colors
    Canvas(modifier) {
        drawDawnSky()
        drawClouds()
        drawMountains()
        drawCity()
        drawMosque()
        drawWaterAndForeground(palette)
        drawRect(
            brush = Brush.verticalGradient(
                0f to Color(0xFF02100B).copy(alpha = 0.22f),
                0.18f to Color.Transparent,
                0.72f to Color.Transparent,
                1f to palette.background
            )
        )
    }
}

private fun DrawScope.drawDawnSky() {
    drawRect(
        brush = Brush.verticalGradient(
            0f to Color(0xFF031B17),
            0.25f to Color(0xFF0A322A),
            0.48f to Color(0xFF345E50),
            0.62f to Color(0xFF917151),
            0.72f to Color(0xFFD0A166)
        )
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFD98F).copy(alpha = 0.45f),
                Color(0xFFE1A45C).copy(alpha = 0.18f),
                Color.Transparent
            ),
            center = Offset(size.width * 0.53f, size.height * 0.48f),
            radius = size.width * 0.58f
        ),
        radius = size.width * 0.58f,
        center = Offset(size.width * 0.53f, size.height * 0.48f)
    )
    drawCircle(
        color = Color(0xFFFFE0A6).copy(alpha = 0.22f),
        radius = size.width * 0.045f,
        center = Offset(size.width * 0.54f, size.height * 0.53f)
    )
    for (i in 0..17) {
        val x = size.width * ((i * 0.137f + 0.061f) % 1f)
        val y = size.height * (0.05f + ((i * 0.071f) % 0.3f))
        drawCircle(
            color = Color(0xFFE8E1C8).copy(alpha = 0.16f + (i % 3) * 0.06f),
            radius = (0.5f + (i % 2)) * density,
            center = Offset(x, y)
        )
    }
}

private fun DrawScope.drawClouds() {
    val bands = listOf(
        0.34f to 0.028f,
        0.39f to 0.018f,
        0.46f to 0.022f,
        0.57f to 0.026f,
        0.63f to 0.035f
    )
    bands.forEachIndexed { index, (fraction, thickness) ->
        drawOval(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0xFFA08161).copy(alpha = 0.12f + index * 0.02f),
                    Color(0xFFC89A68).copy(alpha = 0.16f + index * 0.02f),
                    Color.Transparent
                )
            ),
            topLeft = Offset(size.width * (0.05f + index * 0.1f), size.height * fraction),
            size = Size(size.width * (0.55f + index * 0.08f), size.height * thickness)
        )
    }
}

private fun DrawScope.drawMountains() {
    val back = Path().apply {
        moveTo(0f, size.height * 0.66f)
        lineTo(size.width * 0.08f, size.height * 0.54f)
        lineTo(size.width * 0.15f, size.height * 0.59f)
        lineTo(size.width * 0.26f, size.height * 0.48f)
        lineTo(size.width * 0.38f, size.height * 0.59f)
        lineTo(size.width * 0.52f, size.height * 0.51f)
        lineTo(size.width * 0.66f, size.height * 0.60f)
        lineTo(size.width * 0.81f, size.height * 0.44f)
        lineTo(size.width * 0.93f, size.height * 0.55f)
        lineTo(size.width, size.height * 0.49f)
        lineTo(size.width, size.height * 0.7f)
        lineTo(0f, size.height * 0.7f)
        close()
    }
    drawPath(
        path = back,
        brush = Brush.verticalGradient(
            0f to Color(0xFF244F42),
            1f to Color(0xFF123329)
        )
    )
    val foreground = Path().apply {
        moveTo(0f, size.height * 0.69f)
        lineTo(size.width * 0.13f, size.height * 0.63f)
        lineTo(size.width * 0.22f, size.height * 0.67f)
        lineTo(size.width * 0.36f, size.height * 0.60f)
        lineTo(size.width * 0.5f, size.height * 0.67f)
        lineTo(size.width * 0.74f, size.height * 0.62f)
        lineTo(size.width * 0.9f, size.height * 0.68f)
        lineTo(size.width, size.height * 0.63f)
        lineTo(size.width, size.height * 0.76f)
        lineTo(0f, size.height * 0.76f)
        close()
    }
    drawPath(
        path = foreground,
        brush = Brush.verticalGradient(
            0f to Color(0xFF163B30),
            1f to Color(0xFF092219)
        )
    )
}

private fun DrawScope.drawCity() {
    val baseY = size.height * 0.75f
    val cityPath = Path().apply {
        moveTo(0f, baseY)
        lineTo(0f, size.height * 0.7f)
        var x = 0f
        val blockWidths = listOf(0.04f, 0.03f, 0.06f, 0.035f, 0.05f, 0.04f, 0.06f, 0.03f, 0.05f, 0.04f, 0.06f)
        val heights = listOf(0.035f, 0.055f, 0.04f, 0.065f, 0.045f, 0.05f, 0.04f, 0.07f, 0.05f, 0.045f, 0.055f)
        blockWidths.forEachIndexed { index, width ->
            val top = size.height * (0.69f - heights[index])
            x += size.width * width * 0.7f
            lineTo(x, top)
            lineTo(x + size.width * width * 0.55f, top)
            lineTo(x + size.width * width * 0.55f, baseY)
            x += size.width * width * 0.55f
        }
        lineTo(size.width, baseY)
        close()
    }
    drawPath(cityPath, color = Color(0xFF0C2D24))

    drawCircle(
        color = Color(0xFF0C2D24),
        radius = size.width * 0.025f,
        center = Offset(size.width * 0.77f, size.height * 0.645f)
    )
    drawRect(
        color = Color(0xFF0C2D24),
        topLeft = Offset(size.width * 0.752f, size.height * 0.64f),
        size = Size(size.width * 0.036f, size.height * 0.11f)
    )

    val random = listOf(0.06, 0.12, 0.19, 0.29, 0.63, 0.7, 0.76, 0.83, 0.9)
    random.forEachIndexed { index, x ->
        val y = size.height * (0.715f + (index % 3) * 0.007f)
        drawRect(
            color = Color(0xFFFFCA77).copy(alpha = 0.6f),
            topLeft = Offset((size.width * x).toFloat(), y),
            size = Size(1.7f * density, 1.2f * density)
        )
    }
}

private fun DrawScope.drawMosque() {
    val ground = size.height * 0.765f
    val center = size.width * 0.37f
    val dark = Color(0xFF092219)
    val darker = Color(0xFF061A13)
    val rim = Color(0xFFD9B476).copy(alpha = 0.8f)
    val window = Color(0xFFFFCA79)

    drawRect(
        color = dark,
        topLeft = Offset(size.width * 0.17f, ground - size.height * 0.09f),
        size = Size(size.width * 0.39f, size.height * 0.1f)
    )

    val mainDomeRadius = size.width * 0.105f
    val domeBase = ground - size.height * 0.085f
    val dome = Path().apply {
        moveTo(center - mainDomeRadius, domeBase)
        cubicTo(
            center - mainDomeRadius,
            domeBase - mainDomeRadius * 0.82f,
            center - mainDomeRadius * 0.74f,
            domeBase - mainDomeRadius * 1.22f,
            center,
            domeBase - mainDomeRadius * 1.28f
        )
        cubicTo(
            center + mainDomeRadius * 0.74f,
            domeBase - mainDomeRadius * 1.22f,
            center + mainDomeRadius,
            domeBase - mainDomeRadius * 0.82f,
            center + mainDomeRadius,
            domeBase
        )
        close()
    }
    drawPath(dome, color = darker)
    drawPath(
        path = dome,
        color = rim,
        style = Stroke(width = 0.9f * density)
    )

    drawRect(
        color = dark,
        topLeft = Offset(center - mainDomeRadius * 0.93f, domeBase),
        size = Size(mainDomeRadius * 1.86f, size.height * 0.035f)
    )
    drawLine(
        color = Color(0xFFC89B5B).copy(alpha = 0.55f),
        start = Offset(center, domeBase - mainDomeRadius * 1.28f),
        end = Offset(center, domeBase - mainDomeRadius * 1.48f),
        strokeWidth = 1.1f * density
    )
    drawArc(
        color = Color(0xFFE4C487).copy(alpha = 0.9f),
        startAngle = 200f,
        sweepAngle = 140f,
        useCenter = false,
        topLeft = Offset(center - 4.2f * density, domeBase - mainDomeRadius * 1.58f),
        size = Size(8.4f * density, 8.4f * density),
        style = Stroke(width = 1.1f * density)
    )

    val smallDomeR = size.width * 0.041f
    listOf(center - size.width * 0.125f, center + size.width * 0.125f).forEach { domeCenterX ->
        val smallBase = domeBase + size.height * 0.002f
        val smallDome = Path().apply {
            moveTo(domeCenterX - smallDomeR, smallBase)
            cubicTo(
                domeCenterX - smallDomeR,
                smallBase - smallDomeR * 1.2f,
                domeCenterX - smallDomeR * 0.55f,
                smallBase - smallDomeR * 1.5f,
                domeCenterX,
                smallBase - smallDomeR * 1.5f
            )
            cubicTo(
                domeCenterX + smallDomeR * 0.55f,
                smallBase - smallDomeR * 1.5f,
                domeCenterX + smallDomeR,
                smallBase - smallDomeR * 1.2f,
                domeCenterX + smallDomeR,
                smallBase
            )
            close()
        }
        drawPath(smallDome, color = darker)
        drawLine(
            color = rim,
            start = Offset(domeCenterX, smallBase - smallDomeR * 1.5f),
            end = Offset(domeCenterX, smallBase - smallDomeR * 1.85f),
            strokeWidth = 0.8f * density
        )
    }

    drawMinaret(size.width * 0.22f, ground, size.height * 0.31f, dark, rim, window)
    drawMinaret(size.width * 0.51f, ground, size.height * 0.25f, dark, rim, window)

    val arcadeLeft = size.width * 0.29f
    val arcadeWidth = size.width * 0.16f
    for (i in 0 until 4) {
        val archWidth = arcadeWidth / 4.4f
        val left = arcadeLeft + i * archWidth
        val top = ground - size.height * 0.055f
        val path = Path().apply {
            moveTo(left, ground)
            lineTo(left, top + archWidth * 0.5f)
            cubicTo(
                left,
                top,
                left + archWidth * 0.9f,
                top,
                left + archWidth * 0.9f,
                top + archWidth * 0.5f
            )
            lineTo(left + archWidth * 0.9f, ground)
        }
        drawPath(path, color = window.copy(alpha = 0.36f), style = Stroke(width = 0.8f * density))
    }
    drawRect(
        color = window.copy(alpha = 0.7f),
        topLeft = Offset(center - 1.3f * density, domeBase + size.height * 0.015f),
        size = Size(2.6f * density, 2.6f * density)
    )

    drawRect(
        color = darker,
        topLeft = Offset(0f, ground),
        size = Size(size.width, size.height * 0.02f)
    )
    for (i in 0..13) {
        drawCircle(
            color = window.copy(alpha = 0.85f),
            radius = 1.2f * density,
            center = Offset(
                size.width * (0.18f + i * 0.03f),
                ground - 1.5f * density
            )
        )
    }
}

private fun DrawScope.drawMinaret(
    x: Float,
    ground: Float,
    height: Float,
    body: Color,
    rim: Color,
    window: Color
) {
    val width = size.width * 0.022f
    drawRect(
        color = body,
        topLeft = Offset(x - width / 2f, ground - height),
        size = Size(width, height)
    )
    drawRect(
        color = body,
        topLeft = Offset(x - width * 1.25f, ground - height * 0.64f),
        size = Size(width * 2.5f, height * 0.035f)
    )
    drawRect(
        color = body,
        topLeft = Offset(x - width * 0.95f, ground - height * 0.9f),
        size = Size(width * 1.9f, height * 0.025f)
    )
    val cap = Path().apply {
        moveTo(x - width * 1.1f, ground - height)
        lineTo(x, ground - height - width * 2.4f)
        lineTo(x + width * 1.1f, ground - height)
        close()
    }
    drawPath(cap, color = body)
    drawLine(
        color = rim.copy(alpha = 0.65f),
        start = Offset(x, ground - height - width * 2.4f),
        end = Offset(x, ground - height - width * 4.1f),
        strokeWidth = 0.8f * density
    )
    drawArc(
        color = rim,
        startAngle = 205f,
        sweepAngle = 130f,
        useCenter = false,
        topLeft = Offset(x - 2.8f * density, ground - height - width * 5.5f),
        size = Size(5.6f * density, 5.6f * density),
        style = Stroke(width = 0.8f * density)
    )
    drawCircle(
        color = window.copy(alpha = 0.75f),
        radius = 1.1f * density,
        center = Offset(x, ground - height * 0.8f)
    )
    drawCircle(
        color = window.copy(alpha = 0.65f),
        radius = 1.1f * density,
        center = Offset(x, ground - height * 0.55f)
    )
    drawLine(
        color = rim.copy(alpha = 0.26f),
        start = Offset(x + width * 0.5f, ground - height * 0.95f),
        end = Offset(x + width * 0.5f, ground),
        strokeWidth = 0.7f * density
    )
}

private fun DrawScope.drawWaterAndForeground(palette: com.rafiq.launcher.ui.theme.RafiqPalette) {
    val waterTop = size.height * 0.765f
    drawRect(
        brush = Brush.verticalGradient(
            0f to Color(0xFF2E5A49),
            0.2f to Color(0xFF113C2F),
            0.75f to Color(0xFF041B14),
            1f to palette.background
        ),
        topLeft = Offset(0f, waterTop),
        size = Size(size.width, size.height - waterTop)
    )
    val lights = listOf(0.22f, 0.28f, 0.37f, 0.44f, 0.51f)
    lights.forEachIndexed { index, x ->
        val startY = waterTop + (index % 3) * 4f * density
        drawLine(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFFC97A).copy(alpha = 0.45f),
                    Color(0xFFFFC97A).copy(alpha = 0.02f)
                ),
                startY = startY,
                endY = startY + size.height * (0.04f + index * 0.006f)
            ),
            start = Offset(size.width * x, startY),
            end = Offset(size.width * x, startY + size.height * (0.04f + index * 0.006f)),
            strokeWidth = 1.2f * density
        )
        drawOval(
            color = Color(0xFFFFD68E).copy(alpha = 0.12f),
            topLeft = Offset(size.width * x - 8f * density, startY),
            size = Size(16f * density, size.height * 0.025f)
        )
    }
    for (i in 0..6) {
        val y = waterTop + size.height * 0.02f + i * size.height * 0.025f
        val shimmer = (sin(i * 1.7).toFloat() * 0.5f + 0.5f) * 0.08f
        drawLine(
            color = Color(0xFFD8C99E).copy(alpha = shimmer),
            start = Offset(size.width * (0.1f + i * 0.055f), y),
            end = Offset(size.width * (0.55f + i * 0.035f), y),
            strokeWidth = 0.8f * density
        )
    }
    val treeDark = Color(0xFF04130E)
    drawOval(
        color = treeDark,
        topLeft = Offset(-size.width * 0.1f, waterTop - size.height * 0.1f),
        size = Size(size.width * 0.28f, size.height * 0.17f)
    )
    drawOval(
        color = treeDark,
        topLeft = Offset(size.width * 0.86f, waterTop - size.height * 0.08f),
        size = Size(size.width * 0.25f, size.height * 0.15f)
    )
    drawCircle(
        color = treeDark,
        radius = size.width * 0.06f,
        center = Offset(size.width * 0.08f, waterTop - size.height * 0.09f)
    )
    drawCircle(
        color = treeDark,
        radius = size.width * 0.05f,
        center = Offset(size.width * 0.14f, waterTop - size.height * 0.12f)
    )
}

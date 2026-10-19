package com.rafiq.launcher.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object RafiqIcons {

    val Home: ImageVector by lazy {
        lineIcon("Home") {
            moveTo(3.2f, 10.7f)
            lineTo(12f, 3.5f)
            lineTo(20.8f, 10.7f)
            moveTo(5.5f, 9.1f)
            verticalLineTo(20.2f)
            horizontalLineTo(18.5f)
            verticalLineTo(9.1f)
            moveTo(9.6f, 20.2f)
            verticalLineTo(14.3f)
            horizontalLineTo(14.4f)
            verticalLineTo(20.2f)
        }
    }

    val Mosque: ImageVector by lazy {
        lineIcon("Mosque") {
            moveTo(12f, 2.7f)
            curveTo(10.1f, 4.7f, 8.2f, 6f, 8.2f, 9.1f)
            horizontalLineTo(15.8f)
            curveTo(15.8f, 6f, 13.9f, 4.7f, 12f, 2.7f)
            close()
            moveTo(4.2f, 20.8f)
            verticalLineTo(9.7f)
            curveTo(4.2f, 8.2f, 5f, 7.4f, 6f, 6.8f)
            moveTo(19.8f, 20.8f)
            verticalLineTo(9.7f)
            curveTo(19.8f, 8.2f, 19f, 7.4f, 18f, 6.8f)
            moveTo(7.8f, 20.8f)
            verticalLineTo(10.6f)
            horizontalLineTo(16.2f)
            verticalLineTo(20.8f)
            moveTo(10.3f, 20.8f)
            verticalLineTo(16.6f)
            curveTo(10.3f, 15.7f, 11.1f, 15f, 12f, 15f)
            curveTo(12.9f, 15f, 13.7f, 15.7f, 13.7f, 16.6f)
            verticalLineTo(20.8f)
            moveTo(3f, 20.8f)
            horizontalLineTo(21f)
        }
    }

    val BookOpen: ImageVector by lazy {
        lineIcon("BookOpen") {
            moveTo(3f, 5.3f)
            curveTo(5.6f, 4.1f, 9f, 4.2f, 12f, 6.1f)
            curveTo(15f, 4.2f, 18.4f, 4.1f, 21f, 5.3f)
            verticalLineTo(18.7f)
            curveTo(18.4f, 17.5f, 15f, 17.7f, 12f, 19.7f)
            curveTo(9f, 17.7f, 5.6f, 17.5f, 3f, 18.7f)
            close()
            moveTo(12f, 6.1f)
            verticalLineTo(19.7f)
        }
    }

    val QuranBook: ImageVector by lazy {
        lineIcon("QuranBook") {
            moveTo(4.2f, 4.3f)
            curveTo(6.3f, 3.4f, 9.2f, 3.6f, 12f, 5.3f)
            curveTo(14.8f, 3.6f, 17.7f, 3.4f, 19.8f, 4.3f)
            verticalLineTo(18.7f)
            curveTo(17.7f, 17.9f, 14.8f, 18.1f, 12f, 20f)
            curveTo(9.2f, 18.1f, 6.3f, 17.9f, 4.2f, 18.7f)
            close()
            moveTo(12f, 5.3f)
            verticalLineTo(20f)
            moveTo(12f, 8.4f)
            curveTo(10.7f, 8.4f, 9.7f, 9.3f, 9.5f, 10.5f)
            moveTo(12f, 13.3f)
            curveTo(13.6f, 12.2f, 15.4f, 10.8f, 15.2f, 9.5f)
            curveTo(15.1f, 8.7f, 14.4f, 8.2f, 13.7f, 8.4f)
        }
    }

    val PrayerBeads: ImageVector by lazy {
        lineIcon("PrayerBeads") {
            moveTo(12f, 2.7f)
            curveTo(7.2f, 2.7f, 3.4f, 6.5f, 3.4f, 11.2f)
            curveTo(3.4f, 14.6f, 5.5f, 17.4f, 8.6f, 18.7f)
            moveTo(15.4f, 18.7f)
            curveTo(18.4f, 17.3f, 20.6f, 14.5f, 20.6f, 11.2f)
            curveTo(20.6f, 6.5f, 16.8f, 2.7f, 12f, 2.7f)
            moveTo(9.2f, 20.4f)
            curveTo(10.3f, 19.5f, 11.1f, 19f, 12f, 19f)
            curveTo(12.9f, 19f, 13.7f, 19.5f, 14.8f, 20.4f)
            moveTo(12f, 20.2f)
            verticalLineTo(22.3f)
            moveTo(9.1f, 3.8f)
            curveTo(10.5f, 5.9f, 12f, 6.9f, 12f, 9.4f)
            curveTo(12f, 6.9f, 13.5f, 5.9f, 14.9f, 3.8f)
        }
    }

    val Tasbih: ImageVector = PrayerBeads

    val Focus: ImageVector by lazy {
        lineIcon("Focus") {
            moveTo(12f, 3f)
            curveTo(7f, 3f, 3f, 7f, 3f, 12f)
            curveTo(3f, 17f, 7f, 21f, 12f, 21f)
            curveTo(17f, 21f, 21f, 17f, 21f, 12f)
            curveTo(21f, 7f, 17f, 3f, 12f, 3f)
            close()
            moveTo(12f, 7.3f)
            curveTo(9.4f, 7.3f, 7.3f, 9.4f, 7.3f, 12f)
            curveTo(7.3f, 14.6f, 9.4f, 16.7f, 12f, 16.7f)
            curveTo(14.6f, 16.7f, 16.7f, 14.6f, 16.7f, 12f)
            curveTo(16.7f, 9.4f, 14.6f, 7.3f, 12f, 7.3f)
            close()
            moveTo(12f, 10.2f)
            curveTo(11f, 10.2f, 10.2f, 11f, 10.2f, 12f)
            curveTo(10.2f, 13f, 11f, 13.8f, 12f, 13.8f)
            curveTo(13f, 13.8f, 13.8f, 13f, 13.8f, 12f)
            curveTo(13.8f, 11f, 13f, 10.2f, 12f, 10.2f)
            close()
        }
    }

    val Tasks: ImageVector by lazy {
        lineIcon("Tasks") {
            moveTo(5f, 3.8f)
            horizontalLineTo(19f)
            curveTo(19.6f, 3.8f, 20f, 4.2f, 20f, 4.8f)
            verticalLineTo(19.2f)
            curveTo(20f, 19.8f, 19.6f, 20.2f, 19f, 20.2f)
            horizontalLineTo(5f)
            curveTo(4.4f, 20.2f, 4f, 19.8f, 4f, 19.2f)
            verticalLineTo(4.8f)
            curveTo(4f, 4.2f, 4.4f, 3.8f, 5f, 3.8f)
            close()
            moveTo(7.5f, 8.5f)
            lineTo(9.2f, 10.2f)
            lineTo(12f, 7.4f)
            moveTo(14.3f, 9f)
            horizontalLineTo(17f)
            moveTo(7.5f, 14.2f)
            lineTo(9.2f, 15.9f)
            lineTo(12f, 13.1f)
            moveTo(14.3f, 14.7f)
            horizontalLineTo(17f)
        }
    }

    val Sparkle: ImageVector by lazy {
        filledIcon("Sparkle") {
            moveTo(12f, 2.3f)
            lineTo(13.5f, 8.8f)
            lineTo(20f, 10.4f)
            lineTo(13.5f, 12f)
            lineTo(12f, 18.5f)
            lineTo(10.5f, 12f)
            lineTo(4f, 10.4f)
            lineTo(10.5f, 8.8f)
            close()
            moveTo(18.5f, 15.3f)
            lineTo(19.1f, 17.7f)
            lineTo(21.5f, 18.3f)
            lineTo(19.1f, 18.9f)
            lineTo(18.5f, 21.3f)
            lineTo(17.9f, 18.9f)
            lineTo(15.5f, 18.3f)
            lineTo(17.9f, 17.7f)
            close()
            moveTo(5.1f, 4.8f)
            lineTo(5.6f, 6.8f)
            lineTo(7.6f, 7.3f)
            lineTo(5.6f, 7.8f)
            lineTo(5.1f, 9.8f)
            lineTo(4.6f, 7.8f)
            lineTo(2.6f, 7.3f)
            lineTo(4.6f, 6.8f)
            close()
        }
    }

    val Sun: ImageVector by lazy {
        lineIcon("Sun") {
            moveTo(12f, 7.2f)
            curveTo(9.4f, 7.2f, 7.2f, 9.4f, 7.2f, 12f)
            curveTo(7.2f, 14.6f, 9.4f, 16.8f, 12f, 16.8f)
            curveTo(14.6f, 16.8f, 16.8f, 14.6f, 16.8f, 12f)
            curveTo(16.8f, 9.4f, 14.6f, 7.2f, 12f, 7.2f)
            close()
            moveTo(12f, 2.2f)
            verticalLineTo(4.3f)
            moveTo(12f, 19.7f)
            verticalLineTo(21.8f)
            moveTo(2.2f, 12f)
            horizontalLineTo(4.3f)
            moveTo(19.7f, 12f)
            horizontalLineTo(21.8f)
            moveTo(5.1f, 5.1f)
            lineTo(6.6f, 6.6f)
            moveTo(17.4f, 17.4f)
            lineTo(18.9f, 18.9f)
            moveTo(18.9f, 5.1f)
            lineTo(17.4f, 6.6f)
            moveTo(6.6f, 17.4f)
            lineTo(5.1f, 18.9f)
        }
    }

    val Moon: ImageVector by lazy {
        filledIcon("Moon") {
            moveTo(20.2f, 15.2f)
            curveTo(18.9f, 15.8f, 17.4f, 16.1f, 15.8f, 16.1f)
            curveTo(10.4f, 16.1f, 6f, 11.7f, 6f, 6.3f)
            curveTo(6f, 5.4f, 6.1f, 4.5f, 6.4f, 3.6f)
            curveTo(3.8f, 5.2f, 2f, 8.1f, 2f, 11.4f)
            curveTo(2f, 16.7f, 6.3f, 21f, 11.6f, 21f)
            curveTo(15f, 21f, 17.9f, 19.1f, 19.5f, 16.4f)
            close()
            moveTo(18.7f, 5.1f)
            lineTo(19.4f, 7.2f)
            lineTo(21.5f, 7.9f)
            lineTo(19.4f, 8.6f)
            lineTo(18.7f, 10.7f)
            lineTo(18f, 8.6f)
            lineTo(15.9f, 7.9f)
            lineTo(18f, 7.2f)
            close()
        }
    }

    val Bed: ImageVector by lazy {
        lineIcon("Bed") {
            moveTo(2.8f, 18.7f)
            verticalLineTo(7.2f)
            moveTo(4.6f, 14f)
            horizontalLineTo(20f)
            verticalLineTo(18.7f)
            moveTo(4.6f, 18.7f)
            verticalLineTo(14f)
            moveTo(4.6f, 11.6f)
            curveTo(4.6f, 10.3f, 5.7f, 9.3f, 7f, 9.3f)
            horizontalLineTo(10.4f)
            curveTo(11.7f, 9.3f, 12.8f, 10.3f, 12.8f, 11.6f)
            verticalLineTo(14f)
            moveTo(12.8f, 14f)
            verticalLineTo(9.9f)
            curveTo(12.8f, 8.9f, 13.6f, 8.1f, 14.6f, 8.1f)
            horizontalLineTo(18.2f)
            curveTo(19.2f, 8.1f, 20f, 8.9f, 20f, 9.9f)
            verticalLineTo(14f)
            moveTo(2.8f, 18.7f)
            horizontalLineTo(21.2f)
        }
    }

    val Mic: ImageVector by lazy {
        lineIcon("Mic") {
            moveTo(12f, 3f)
            curveTo(10.3f, 3f, 9f, 4.3f, 9f, 6f)
            verticalLineTo(12f)
            curveTo(9f, 13.7f, 10.3f, 15f, 12f, 15f)
            curveTo(13.7f, 15f, 15f, 13.7f, 15f, 12f)
            verticalLineTo(6f)
            curveTo(15f, 4.3f, 13.7f, 3f, 12f, 3f)
            close()
            moveTo(5.8f, 11.2f)
            curveTo(5.8f, 14.6f, 8.6f, 17.3f, 12f, 17.3f)
            curveTo(15.4f, 17.3f, 18.2f, 14.6f, 18.2f, 11.2f)
            moveTo(12f, 17.3f)
            verticalLineTo(21f)
            moveTo(9f, 21f)
            horizontalLineTo(15f)
        }
    }

    val Play: ImageVector by lazy {
        filledIcon("Play") {
            moveTo(8f, 5.3f)
            lineTo(19f, 12f)
            lineTo(8f, 18.7f)
            close()
        }
    }

    val Pause: ImageVector by lazy {
        filledIcon("Pause") {
            moveTo(7f, 5f)
            horizontalLineTo(9.7f)
            verticalLineTo(19f)
            horizontalLineTo(7f)
            close()
            moveTo(14.3f, 5f)
            horizontalLineTo(17f)
            verticalLineTo(19f)
            horizontalLineTo(14.3f)
            close()
        }
    }

    val Next: ImageVector by lazy {
        filledIcon("Next") {
            moveTo(4f, 5.5f)
            lineTo(11f, 12f)
            lineTo(4f, 18.5f)
            close()
            moveTo(12f, 5.5f)
            lineTo(19f, 12f)
            lineTo(12f, 18.5f)
            close()
        }
    }

    val Previous: ImageVector by lazy {
        filledIcon("Previous") {
            moveTo(20f, 5.5f)
            lineTo(13f, 12f)
            lineTo(20f, 18.5f)
            close()
            moveTo(12f, 5.5f)
            lineTo(5f, 12f)
            lineTo(12f, 18.5f)
            close()
        }
    }

    val Bookmark: ImageVector by lazy {
        lineIcon("Bookmark") {
            moveTo(6f, 3.7f)
            horizontalLineTo(18f)
            verticalLineTo(21f)
            lineTo(12f, 17.1f)
            lineTo(6f, 21f)
            close()
        }
    }

    val BookmarkFilled: ImageVector by lazy {
        filledIcon("BookmarkFilled") {
            moveTo(6f, 3.7f)
            horizontalLineTo(18f)
            verticalLineTo(21f)
            lineTo(12f, 17.1f)
            lineTo(6f, 21f)
            close()
        }
    }

    val Search: ImageVector by lazy {
        lineIcon("Search") {
            moveTo(10.7f, 3.7f)
            curveTo(6.8f, 3.7f, 3.7f, 6.8f, 3.7f, 10.7f)
            curveTo(3.7f, 14.6f, 6.8f, 17.7f, 10.7f, 17.7f)
            curveTo(14.6f, 17.7f, 17.7f, 14.6f, 17.7f, 10.7f)
            curveTo(17.7f, 6.8f, 14.6f, 3.7f, 10.7f, 3.7f)
            close()
            moveTo(15.5f, 15.5f)
            lineTo(20.3f, 20.3f)
        }
    }

    val ArrowLeft: ImageVector by lazy {
        lineIcon("ArrowLeft") {
            moveTo(19.5f, 12f)
            horizontalLineTo(4.5f)
            moveTo(10.4f, 5.8f)
            lineTo(4.3f, 12f)
            lineTo(10.4f, 18.2f)
        }
    }

    val ChevronLeft: ImageVector by lazy {
        lineIcon("ChevronLeft") {
            moveTo(14.8f, 5.3f)
            lineTo(8.2f, 12f)
            lineTo(14.8f, 18.7f)
        }
    }

    val ChevronDown: ImageVector by lazy {
        lineIcon("ChevronDown") {
            moveTo(5.3f, 9.2f)
            lineTo(12f, 15.8f)
            lineTo(18.7f, 9.2f)
        }
    }

    val Location: ImageVector by lazy {
        lineIcon("Location") {
            moveTo(12f, 21f)
            curveTo(16f, 16.8f, 19f, 13.1f, 19f, 9.3f)
            curveTo(19f, 5.4f, 15.9f, 3f, 12f, 3f)
            curveTo(8.1f, 3f, 5f, 5.4f, 5f, 9.3f)
            curveTo(5f, 13.1f, 8f, 16.8f, 12f, 21f)
            close()
            moveTo(12f, 7f)
            curveTo(10.6f, 7f, 9.5f, 8.1f, 9.5f, 9.5f)
            curveTo(9.5f, 10.9f, 10.6f, 12f, 12f, 12f)
            curveTo(13.4f, 12f, 14.5f, 10.9f, 14.5f, 9.5f)
            curveTo(14.5f, 8.1f, 13.4f, 7f, 12f, 7f)
            close()
        }
    }

    val Settings: ImageVector by lazy {
        lineIcon("Settings") {
            moveTo(12f, 8.2f)
            curveTo(9.9f, 8.2f, 8.2f, 9.9f, 8.2f, 12f)
            curveTo(8.2f, 14.1f, 9.9f, 15.8f, 12f, 15.8f)
            curveTo(14.1f, 15.8f, 15.8f, 14.1f, 15.8f, 12f)
            curveTo(15.8f, 9.9f, 14.1f, 8.2f, 12f, 8.2f)
            close()
            moveTo(19.4f, 13.4f)
            curveTo(19.5f, 12.9f, 19.5f, 12.4f, 19.4f, 11.9f)
            lineTo(21.2f, 10.5f)
            lineTo(19.4f, 7.3f)
            lineTo(17.2f, 8f)
            curveTo(16.5f, 7.5f, 15.8f, 7.1f, 15f, 6.9f)
            lineTo(14.7f, 4.5f)
            horizontalLineTo(9.3f)
            lineTo(9f, 6.9f)
            curveTo(8.2f, 7.1f, 7.5f, 7.5f, 6.8f, 8f)
            lineTo(4.6f, 7.3f)
            lineTo(2.8f, 10.5f)
            lineTo(4.6f, 11.9f)
            curveTo(4.5f, 12.4f, 4.5f, 12.8f, 4.6f, 13.3f)
            lineTo(2.8f, 14.7f)
            lineTo(4.6f, 17.9f)
            lineTo(6.8f, 17.2f)
            curveTo(7.5f, 17.7f, 8.2f, 18.1f, 9f, 18.3f)
            lineTo(9.3f, 20.7f)
            horizontalLineTo(14.7f)
            lineTo(15f, 18.3f)
            curveTo(15.8f, 18.1f, 16.6f, 17.7f, 17.2f, 17.2f)
            lineTo(19.4f, 17.9f)
            lineTo(21.2f, 14.7f)
            close()
        }
    }

    val Bell: ImageVector by lazy {
        lineIcon("Bell") {
            moveTo(6.2f, 9.3f)
            curveTo(6.2f, 6.1f, 8.8f, 3.5f, 12f, 3.5f)
            curveTo(15.2f, 3.5f, 17.8f, 6.1f, 17.8f, 9.3f)
            verticalLineTo(13.8f)
            lineTo(20f, 17.2f)
            horizontalLineTo(4f)
            lineTo(6.2f, 13.8f)
            close()
            moveTo(9.4f, 17.2f)
            curveTo(9.4f, 18.6f, 10.6f, 19.7f, 12f, 19.7f)
            curveTo(13.4f, 19.7f, 14.6f, 18.6f, 14.6f, 17.2f)
        }
    }

    val Palette: ImageVector by lazy {
        lineIcon("Palette") {
            moveTo(12f, 3f)
            curveTo(7f, 3f, 3f, 7f, 3f, 12f)
            curveTo(3f, 17f, 7f, 21f, 12f, 21f)
            horizontalLineTo(13.2f)
            curveTo(14.3f, 21f, 14.8f, 19.5f, 13.8f, 18.7f)
            curveTo(13.2f, 18.2f, 13.2f, 17.4f, 13.8f, 16.9f)
            horizontalLineTo(15.2f)
            curveTo(18.2f, 16.9f, 20.7f, 14.5f, 20.7f, 11.5f)
            curveTo(20.7f, 6.8f, 16.8f, 3f, 12f, 3f)
            close()
            moveTo(7.7f, 10.2f)
            curveTo(8.2f, 10.2f, 8.6f, 10.6f, 8.6f, 11.1f)
            curveTo(8.6f, 11.6f, 8.2f, 12f, 7.7f, 12f)
            curveTo(7.2f, 12f, 6.8f, 11.6f, 6.8f, 11.1f)
            curveTo(6.8f, 10.6f, 7.2f, 10.2f, 7.7f, 10.2f)
            close()
            moveTo(9.7f, 6.7f)
            curveTo(10.2f, 6.7f, 10.6f, 7.1f, 10.6f, 7.6f)
            curveTo(10.6f, 8.1f, 10.2f, 8.5f, 9.7f, 8.5f)
            curveTo(9.2f, 8.5f, 8.8f, 8.1f, 8.8f, 7.6f)
            curveTo(8.8f, 7.1f, 9.2f, 6.7f, 9.7f, 6.7f)
            close()
            moveTo(14.3f, 6.7f)
            curveTo(14.8f, 6.7f, 15.2f, 7.1f, 15.2f, 7.6f)
            curveTo(15.2f, 8.1f, 14.8f, 8.5f, 14.3f, 8.5f)
            curveTo(13.8f, 8.5f, 13.4f, 8.1f, 13.4f, 7.6f)
            curveTo(13.4f, 7.1f, 13.8f, 6.7f, 14.3f, 6.7f)
            close()
            moveTo(16.9f, 10.2f)
            curveTo(17.4f, 10.2f, 17.8f, 10.6f, 17.8f, 11.1f)
            curveTo(17.8f, 11.6f, 17.4f, 12f, 16.9f, 12f)
            curveTo(16.4f, 12f, 16f, 11.6f, 16f, 11.1f)
            curveTo(16f, 10.6f, 16.4f, 10.2f, 16.9f, 10.2f)
            close()
        }
    }

    val Volume: ImageVector by lazy {
        lineIcon("Volume") {
            moveTo(4f, 9.2f)
            horizontalLineTo(7.5f)
            lineTo(12.3f, 5.2f)
            verticalLineTo(18.8f)
            lineTo(7.5f, 14.8f)
            horizontalLineTo(4f)
            close()
            moveTo(15.5f, 8.5f)
            curveTo(17.4f, 10.5f, 17.4f, 13.5f, 15.5f, 15.5f)
            moveTo(18.2f, 5.8f)
            curveTo(21.6f, 9.2f, 21.6f, 14.8f, 18.2f, 18.2f)
        }
    }

    val Shield: ImageVector by lazy {
        lineIcon("Shield") {
            moveTo(12f, 2.8f)
            lineTo(20f, 6f)
            verticalLineTo(11.1f)
            curveTo(20f, 15.8f, 16.8f, 19.7f, 12f, 21.2f)
            curveTo(7.2f, 19.7f, 4f, 15.8f, 4f, 11.1f)
            verticalLineTo(6f)
            close()
            moveTo(9.2f, 11.6f)
            lineTo(11.2f, 13.6f)
            lineTo(15.1f, 9.7f)
        }
    }

    val Clock: ImageVector by lazy {
        lineIcon("Clock") {
            moveTo(12f, 3f)
            curveTo(7f, 3f, 3f, 7f, 3f, 12f)
            curveTo(3f, 17f, 7f, 21f, 12f, 21f)
            curveTo(17f, 21f, 21f, 17f, 21f, 12f)
            curveTo(21f, 7f, 17f, 3f, 12f, 3f)
            close()
            moveTo(12f, 7f)
            verticalLineTo(12.3f)
            lineTo(15.8f, 14.5f)
        }
    }

    val Info: ImageVector by lazy {
        lineIcon("Info") {
            moveTo(12f, 3f)
            curveTo(7f, 3f, 3f, 7f, 3f, 12f)
            curveTo(3f, 17f, 7f, 21f, 12f, 21f)
            curveTo(17f, 21f, 21f, 17f, 21f, 12f)
            curveTo(21f, 7f, 17f, 3f, 12f, 3f)
            close()
            moveTo(12f, 10.8f)
            verticalLineTo(16.6f)
            moveTo(12f, 7.3f)
            verticalLineTo(7.7f)
        }
    }

    val Person: ImageVector by lazy {
        lineIcon("Person") {
            moveTo(12f, 3.4f)
            curveTo(9.9f, 3.4f, 8.2f, 5.1f, 8.2f, 7.2f)
            curveTo(8.2f, 9.3f, 9.9f, 11f, 12f, 11f)
            curveTo(14.1f, 11f, 15.8f, 9.3f, 15.8f, 7.2f)
            curveTo(15.8f, 5.1f, 14.1f, 3.4f, 12f, 3.4f)
            close()
            moveTo(4.2f, 20.6f)
            curveTo(4.2f, 16.9f, 7.2f, 14.2f, 12f, 14.2f)
            curveTo(16.8f, 14.2f, 19.8f, 16.9f, 19.8f, 20.6f)
        }
    }

    val MenuLines: ImageVector by lazy {
        lineIcon("MenuLines") {
            moveTo(4f, 7f)
            horizontalLineTo(20f)
            moveTo(4f, 12f)
            horizontalLineTo(16f)
            moveTo(4f, 17f)
            horizontalLineTo(12f)
        }
    }

    val Plus: ImageVector by lazy {
        lineIcon("Plus") {
            moveTo(12f, 4.5f)
            verticalLineTo(19.5f)
            moveTo(4.5f, 12f)
            horizontalLineTo(19.5f)
        }
    }

    val Check: ImageVector by lazy {
        lineIcon("Check") {
            moveTo(4.5f, 12.4f)
            lineTo(9.5f, 17.3f)
            lineTo(19.5f, 6.7f)
        }
    }

    val Close: ImageVector by lazy {
        lineIcon("Close") {
            moveTo(5.5f, 5.5f)
            lineTo(18.5f, 18.5f)
            moveTo(18.5f, 5.5f)
            lineTo(5.5f, 18.5f)
        }
    }

    val Send: ImageVector by lazy {
        lineIcon("Send") {
            moveTo(3.5f, 4.2f)
            lineTo(21f, 12f)
            lineTo(3.5f, 19.8f)
            lineTo(6f, 12f)
            close()
            moveTo(6f, 12f)
            horizontalLineTo(21f)
        }
    }

    val Globe: ImageVector by lazy {
        lineIcon("Globe") {
            moveTo(12f, 3f)
            curveTo(7f, 3f, 3f, 7f, 3f, 12f)
            curveTo(3f, 17f, 7f, 21f, 12f, 21f)
            curveTo(17f, 21f, 21f, 17f, 21f, 12f)
            curveTo(21f, 7f, 17f, 3f, 12f, 3f)
            close()
            moveTo(3.5f, 9f)
            horizontalLineTo(20.5f)
            moveTo(3.5f, 15f)
            horizontalLineTo(20.5f)
            moveTo(12f, 3f)
            curveTo(9.8f, 5.2f, 8.6f, 8.4f, 8.6f, 12f)
            curveTo(8.6f, 15.6f, 9.8f, 18.8f, 12f, 21f)
            curveTo(14.2f, 18.8f, 15.4f, 15.6f, 15.4f, 12f)
            curveTo(15.4f, 8.4f, 14.2f, 5.2f, 12f, 3f)
            close()
        }
    }

    val Trash: ImageVector by lazy {
        lineIcon("Trash") {
            moveTo(4.5f, 6.5f)
            horizontalLineTo(19.5f)
            moveTo(9f, 3.5f)
            horizontalLineTo(15f)
            lineTo(15.8f, 6.5f)
            moveTo(6.5f, 6.5f)
            lineTo(7.5f, 20.5f)
            horizontalLineTo(16.5f)
            lineTo(17.5f, 6.5f)
            moveTo(10f, 10.5f)
            verticalLineTo(16.5f)
            moveTo(14f, 10.5f)
            verticalLineTo(16.5f)
        }
    }

    val Edit: ImageVector by lazy {
        lineIcon("Edit") {
            moveTo(4f, 20f)
            horizontalLineTo(8f)
            lineTo(19.2f, 8.8f)
            curveTo(19.9f, 8.1f, 19.9f, 7f, 19.2f, 6.3f)
            lineTo(17.7f, 4.8f)
            curveTo(17f, 4.1f, 15.9f, 4.1f, 15.2f, 4.8f)
            lineTo(4f, 16f)
            close()
            moveTo(13.8f, 6.2f)
            lineTo(17.8f, 10.2f)
        }
    }

    val Refresh: ImageVector by lazy {
        lineIcon("Refresh") {
            moveTo(20f, 11.5f)
            curveTo(19.5f, 7.1f, 16.1f, 4f, 12f, 4f)
            curveTo(8.6f, 4f, 5.7f, 6f, 4.4f, 8.8f)
            moveTo(4f, 4.5f)
            verticalLineTo(9f)
            horizontalLineTo(8.5f)
            moveTo(4f, 12.5f)
            curveTo(4.5f, 16.9f, 7.9f, 20f, 12f, 20f)
            curveTo(15.4f, 20f, 18.3f, 18f, 19.6f, 15.2f)
            moveTo(20f, 19.5f)
            verticalLineTo(15f)
            horizontalLineTo(15.5f)
        }
    }

    val Phone: ImageVector by lazy {
        filledIcon("Phone") {
            moveTo(7.1f, 3.2f)
            curveTo(6.4f, 2.5f, 5.2f, 2.6f, 4.5f, 3.4f)
            lineTo(3.1f, 4.8f)
            curveTo(2.1f, 5.8f, 1.8f, 7.3f, 2.3f, 8.7f)
            curveTo(4.6f, 15.3f, 8.7f, 19.4f, 15.3f, 21.7f)
            curveTo(16.7f, 22.2f, 18.2f, 21.9f, 19.2f, 20.9f)
            lineTo(20.6f, 19.5f)
            curveTo(21.4f, 18.8f, 21.5f, 17.6f, 20.8f, 16.9f)
            lineTo(17.8f, 14.1f)
            curveTo(17.1f, 13.5f, 16.1f, 13.5f, 15.4f, 14.1f)
            lineTo(14f, 15.3f)
            curveTo(11.6f, 14f, 10f, 12.4f, 8.7f, 10f)
            lineTo(9.9f, 8.6f)
            curveTo(10.5f, 7.9f, 10.5f, 6.9f, 9.9f, 6.2f)
            close()
        }
    }

    val Message: ImageVector by lazy {
        filledIcon("Message") {
            moveTo(4f, 4f)
            horizontalLineTo(20f)
            curveTo(20.8f, 4f, 21.5f, 4.7f, 21.5f, 5.5f)
            verticalLineTo(16f)
            curveTo(21.5f, 16.8f, 20.8f, 17.5f, 20f, 17.5f)
            horizontalLineTo(9f)
            lineTo(4.5f, 21f)
            verticalLineTo(17.5f)
            horizontalLineTo(3f)
            curveTo(2.2f, 17.5f, 1.5f, 16.8f, 1.5f, 16f)
            verticalLineTo(5.5f)
            curveTo(1.5f, 4.7f, 2.2f, 4f, 3f, 4f)
            close()
        }
    }

    val Browser: ImageVector by lazy {
        lineIcon("Browser") {
            moveTo(12f, 2.8f)
            curveTo(7f, 2.8f, 3f, 6.8f, 3f, 11.8f)
            curveTo(3f, 16.8f, 7f, 20.8f, 12f, 20.8f)
            curveTo(17f, 20.8f, 21f, 16.8f, 21f, 11.8f)
            curveTo(21f, 6.8f, 17f, 2.8f, 12f, 2.8f)
            close()
            moveTo(3.4f, 8.8f)
            horizontalLineTo(20.6f)
            moveTo(9f, 4f)
            curveTo(6.5f, 8f, 6.5f, 15.6f, 9f, 19.6f)
            moveTo(15f, 4f)
            curveTo(17.5f, 8f, 17.5f, 15.6f, 15f, 19.6f)
        }
    }

    val Camera: ImageVector by lazy {
        lineIcon("Camera") {
            moveTo(3f, 7.5f)
            horizontalLineTo(7f)
            lineTo(8.7f, 5f)
            horizontalLineTo(15.3f)
            lineTo(17f, 7.5f)
            horizontalLineTo(21f)
            verticalLineTo(19f)
            horizontalLineTo(3f)
            close()
            moveTo(12f, 9.2f)
            curveTo(9.9f, 9.2f, 8.2f, 10.9f, 8.2f, 13f)
            curveTo(8.2f, 15.1f, 9.9f, 16.8f, 12f, 16.8f)
            curveTo(14.1f, 16.8f, 15.8f, 15.1f, 15.8f, 13f)
            curveTo(15.8f, 10.9f, 14.1f, 9.2f, 12f, 9.2f)
            close()
        }
    }

    private fun lineIcon(name: String, pathBuilder: PathBuilder.() -> Unit): ImageVector =
        ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.65f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                pathBuilder = pathBuilder
            )
        }.build()

    private fun filledIcon(name: String, pathBuilder: PathBuilder.() -> Unit): ImageVector =
        ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                pathBuilder = pathBuilder
            )
        }.build()
}

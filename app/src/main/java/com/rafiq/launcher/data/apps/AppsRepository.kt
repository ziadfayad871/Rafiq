package com.rafiq.launcher.data.apps

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.Collator
import java.util.Locale

data class InstalledApp(
    val label: String,
    val packageName: String,
    val activityName: String,
    val icon: ImageBitmap?
)

enum class BuiltinDestination {
    QURAN, ADHKAR, PRAYER, TASKS, FOCUS, ASSISTANT, SETTINGS
}

data class BuiltinApp(
    val id: String,
    val label: String,
    val destination: BuiltinDestination
)

enum class DockKind { PHONE, MESSAGES, BROWSER, CAMERA }

data class DockApp(
    val kind: DockKind,
    val label: String,
    val packageName: String?,
    val icon: ImageBitmap?
)

class AppsRepository(private val context: Context) {

    private val packageManager: PackageManager = context.packageManager

    val builtinApps: List<BuiltinApp> = listOf(
        BuiltinApp("quran", "القرآن", BuiltinDestination.QURAN),
        BuiltinApp("adhkar", "الأذكار", BuiltinDestination.ADHKAR),
        BuiltinApp("prayer", "الصلاة", BuiltinDestination.PRAYER),
        BuiltinApp("tasks", "المهام", BuiltinDestination.TASKS),
        BuiltinApp("focus", "التركيز", BuiltinDestination.FOCUS),
        BuiltinApp("assistant", "مساعد رفيق", BuiltinDestination.ASSISTANT),
        BuiltinApp("settings", "الإعدادات", BuiltinDestination.SETTINGS)
    )

    suspend fun loadInstalledApps(): List<InstalledApp> = withContext(Dispatchers.IO) {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        @Suppress("DEPRECATION")
        val resolved = packageManager.queryIntentActivities(intent, 0)
        val collator = Collator.getInstance(Locale("ar", "EG"))
        val seen = mutableSetOf<String>()
        val apps = mutableListOf<InstalledApp>()
        for (info in resolved) {
            val activity = info.activityInfo ?: continue
            if (activity.packageName == context.packageName) continue
            if (!seen.add(activity.packageName)) continue
            val label = info.loadLabel(packageManager)?.toString()
                ?.takeIf { it.isNotBlank() }
                ?: activity.packageName
            val icon = runCatching {
                info.loadIcon(packageManager).toAppBitmap()
            }.getOrNull()
            apps += InstalledApp(
                label = label,
                packageName = activity.packageName,
                activityName = activity.name,
                icon = icon
            )
        }
        apps.sortedWith(compareBy(collator) { it.label })
    }

    suspend fun loadDockApps(): List<DockApp> = withContext(Dispatchers.IO) {
        listOf(
            resolveDockApp(
                DockKind.PHONE,
                Intent(Intent.ACTION_DIAL),
                "الهاتف"
            ),
            resolveDockApp(
                DockKind.MESSAGES,
                Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:")),
                "الرسائل"
            ),
            resolveDockApp(
                DockKind.BROWSER,
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")),
                "المتصفح"
            ),
            resolveDockApp(
                DockKind.CAMERA,
                Intent(MediaStore.ACTION_IMAGE_CAPTURE),
                "الكاميرا"
            )
        )
    }

    private fun resolveDockApp(kind: DockKind, intent: Intent, fallbackLabel: String): DockApp {
        @Suppress("DEPRECATION")
        val resolved = packageManager.resolveActivity(intent, 0)
        val activity = resolved?.activityInfo
        val icon = runCatching {
            resolved?.loadIcon(packageManager)?.toAppBitmap()
        }.getOrNull()
        val label = resolved?.loadLabel(packageManager)?.toString()
            ?.takeIf { it.isNotBlank() && it != "Android System" }
            ?: fallbackLabel
        return DockApp(
            kind = kind,
            label = label,
            packageName = activity?.packageName,
            icon = icon
        )
    }

    fun launchInstalled(app: InstalledApp): Result<Unit> = runCatching {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            component = ComponentName(app.packageName, app.activityName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        }
        context.startActivity(intent)
    }

    fun launchDock(kind: DockKind): Result<Unit> = runCatching {
        val intent = when (kind) {
            DockKind.PHONE -> Intent(Intent.ACTION_DIAL)
            DockKind.MESSAGES -> Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"))
            DockKind.BROWSER -> Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))
            DockKind.CAMERA -> Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun openAppSettings(): Result<Unit> = runCatching {
        val intent = Intent(
            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:${context.packageName}")
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun openLanguageSettings(): Result<Unit> = runCatching {
        context.startActivity(
            Intent("android.settings.APP_LOCALE_SETTINGS", Uri.parse("package:${context.packageName}"))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    fun openHomeSettings(): Result<Unit> = runCatching {
        context.startActivity(
            Intent(android.provider.Settings.ACTION_HOME_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    private fun Drawable.toAppBitmap(): ImageBitmap {
        val bitmap: Bitmap = toBitmap(width = 96, height = 96)
        return bitmap.asImageBitmap()
    }
}

fun normalizeArabicForSearch(value: String): String {
    val withoutDiacritics = value
        .replace(Regex("[\\u064B-\\u065F\\u0670\\u06D6-\\u06ED]"), "")
        .replace("ـ", "")
    return withoutDiacritics
        .replace('أ', 'ا')
        .replace('إ', 'ا')
        .replace('آ', 'ا')
        .replace('ى', 'ي')
        .replace('ة', 'ه')
        .lowercase(Locale.ROOT)
        .trim()
}

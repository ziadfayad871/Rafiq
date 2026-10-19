package com.rafiq.launcher.ui.screens.settings

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rafiq.launcher.BuildConfig
import com.rafiq.launcher.data.prayer.CalculationMethod
import com.rafiq.launcher.data.prayer.ManualCities
import com.rafiq.launcher.ui.AppViewModelProvider
import com.rafiq.launcher.ui.components.RafiqCard
import com.rafiq.launcher.ui.components.RafiqDialog
import com.rafiq.launcher.ui.components.RafiqIcons
import com.rafiq.launcher.ui.components.RafiqPrimaryButton
import com.rafiq.launcher.ui.components.RafiqSecondaryButton
import com.rafiq.launcher.ui.components.RafiqTopBar
import com.rafiq.launcher.ui.components.RoundIconButton
import com.rafiq.launcher.ui.components.TransientMessage
import com.rafiq.launcher.ui.theme.RafiqTheme
import com.rafiq.launcher.ui.theme.paletteFor

private enum class SettingsSection { MAIN, PRAYER, SOUND, PRIVACY, ABOUT, LANGUAGE }

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val palette = RafiqTheme.colors
    val context = LocalContext.current
    var section by remember { mutableStateOf(SettingsSection.MAIN) }
    var showNameDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showFocusDialog by remember { mutableStateOf(false) }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.setPrayerNotifications(granted)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (section) {
            SettingsSection.MAIN -> Column(modifier = Modifier.fillMaxSize()) {
                RafiqTopBar(title = "الإعدادات", subtitle = "رفيقك كما تحب")
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 18.dp)
                        .padding(bottom = 24.dp)
                ) {
                    ProfileCard(
                        name = preferences.displayName,
                        onEdit = { showNameDialog = true }
                    )
                    Spacer(Modifier.height(16.dp))
                    SettingsGroup {
                        SettingsRow(
                            icon = RafiqIcons.Palette,
                            title = "المظهر",
                            value = themeName(preferences.theme),
                            onClick = { showThemeDialog = true }
                        )
                        SettingsRow(
                            icon = RafiqIcons.Mosque,
                            title = "الصلاة والمواقيت",
                            value = CalculationMethod.fromId(preferences.calculationMethod).arabicName,
                            onClick = { section = SettingsSection.PRAYER }
                        )
                        SettingsRow(
                            icon = RafiqIcons.Volume,
                            title = "الأصوات والاهتزاز",
                            value = if (preferences.soundsEnabled) "مفعّلة" else "متوقفة",
                            onClick = { section = SettingsSection.SOUND }
                        )
                        SettingsRow(
                            icon = RafiqIcons.Focus,
                            title = "جلسة التركيز",
                            value = "${preferences.focusDurationMinutes} دقيقة",
                            onClick = { showFocusDialog = true }
                        )
                        SettingsRow(
                            icon = RafiqIcons.Globe,
                            title = "لغة الواجهة",
                            value = "العربية",
                            onClick = { section = SettingsSection.LANGUAGE }
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    SettingsGroup {
                        SettingsRow(
                            icon = RafiqIcons.Shield,
                            title = "الخصوصية والبيانات",
                            value = null,
                            onClick = { section = SettingsSection.PRIVACY }
                        )
                        SettingsRow(
                            icon = RafiqIcons.Info,
                            title = "حول رفيق",
                            value = "الإصدار ${BuildConfig.VERSION_NAME}",
                            onClick = { section = SettingsSection.ABOUT }
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    SettingsGroup {
                        SettingsRow(
                            icon = RafiqIcons.Home,
                            title = "تعيين رفيق كشاشة رئيسية",
                            value = null,
                            onClick = viewModel::openHomeSettings
                        )
                        SettingsRow(
                            icon = RafiqIcons.Settings,
                            title = "إعدادات التطبيق في النظام",
                            value = null,
                            onClick = viewModel::openAppSettings
                        )
                    }
                    Spacer(Modifier.height(18.dp))
                    Text(
                        text = "رفيق • شريكك في رحلتك نحو الأفضل",
                        style = MaterialTheme.typography.labelSmall.copy(color = palette.textDim),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            SettingsSection.PRAYER -> SectionScaffold(
                title = "الصلاة والمواقيت",
                onBack = { section = SettingsSection.MAIN }
            ) {
                ToggleRow(
                    icon = RafiqIcons.Bell,
                    title = "تنبيهات الصلاة",
                    subtitle = if (preferences.prayerNotifications) {
                        "سيصلك تنبيه عند دخول وقت كل صلاة"
                    } else {
                        "تنبيهات الصلاة متوقفة حاليًا"
                    },
                    checked = preferences.prayerNotifications,
                    onToggle = { enable ->
                        if (enable) {
                            notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            viewModel.setPrayerNotifications(false)
                        }
                    }
                )
                Spacer(Modifier.height(10.dp))
                RafiqSecondaryButton(
                    text = "إرسال تنبيه تجريبي",
                    icon = RafiqIcons.Bell,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = viewModel::showTestPrayerNotification
                )
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    Spacer(Modifier.height(10.dp))
                    SettingsGroup {
                        SettingsRow(
                            icon = RafiqIcons.Clock,
                            title = "إذن المنبّهات الدقيقة",
                            value = if (viewModel.canScheduleExactAlarms()) "مسموح" else "غير مسموح",
                            onClick = viewModel::openExactAlarmSettings
                        )
                    }
                }
                Spacer(Modifier.height(18.dp))
                Text(
                    text = "طريقة الحساب",
                    style = MaterialTheme.typography.titleMedium.copy(color = palette.textPrimary)
                )
                Spacer(Modifier.height(8.dp))
                RafiqCard(contentPadding = PaddingValues(vertical = 4.dp)) {
                    CalculationMethod.entries.forEach { method ->
                        val selected = method.id == preferences.calculationMethod
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setCalculationMethod(method) }
                                .padding(horizontal = 14.dp, vertical = 11.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = method.arabicName,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = if (selected) palette.gold else palette.textPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            if (selected) {
                                Icon(
                                    imageVector = RafiqIcons.Check,
                                    contentDescription = null,
                                    tint = palette.gold,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))
                Text(
                    text = "مدينة الحساب اليدوية",
                    style = MaterialTheme.typography.titleMedium.copy(color = palette.textPrimary)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "المدينة الحالية: ${preferences.locationLabel}",
                    style = MaterialTheme.typography.bodySmall.copy(color = palette.textDim)
                )
                Spacer(Modifier.height(8.dp))
                RafiqCard(contentPadding = PaddingValues(vertical = 4.dp)) {
                    ManualCities.all.forEach { city ->
                        val selected = city.name == preferences.locationLabel
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setCity(city) }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = city.name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = if (selected) palette.gold else palette.textPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = city.country,
                                style = MaterialTheme.typography.labelSmall.copy(color = palette.textDim)
                            )
                        }
                    }
                }
            }

            SettingsSection.SOUND -> SectionScaffold(
                title = "الأصوات والاهتزاز",
                onBack = { section = SettingsSection.MAIN }
            ) {
                ToggleRow(
                    icon = RafiqIcons.Volume,
                    title = "أصوات الواجهة",
                    subtitle = "نغمات هادئة عند التفاعل والإشعارات",
                    checked = preferences.soundsEnabled,
                    onToggle = viewModel::setSoundsEnabled
                )
                Spacer(Modifier.height(10.dp))
                ToggleRow(
                    icon = RafiqIcons.Sparkle,
                    title = "الاهتزاز اللمسي",
                    subtitle = "ردود لمسية خفيفة عند الأزرار والبطاقات",
                    checked = preferences.hapticsEnabled,
                    onToggle = viewModel::setHapticsEnabled
                )
            }

            SettingsSection.PRIVACY -> SectionScaffold(
                title = "الخصوصية والبيانات",
                onBack = { section = SettingsSection.MAIN }
            ) {
                Text(
                    text = "رفيق لا يجمع بيانات شخصية ولا يرسلها إلى خوادم خاصة. تعمل المواقيت والأذكار والمصحف داخل جهازك، ولا يُستخدم الإنترنت إلا لتشغيل التلاوة عند طلبها.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = palette.textSecondary)
                )
                Spacer(Modifier.height(16.dp))
                RafiqCard {
                    Text(
                        text = "البيانات المحفوظة محليًا",
                        style = MaterialTheme.typography.titleSmall.copy(color = palette.gold)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "• اسمك المفضل وإعدادات المظهر واللغة\n" +
                            "• مدينة وطريقة حساب المواقيت\n" +
                            "• مهامك اليومية وعلامات المصحف\n" +
                            "• موضع تلاوتك وآخر سورة قرأتها",
                        style = MaterialTheme.typography.bodySmall.copy(color = palette.textSecondary)
                    )
                }
                Spacer(Modifier.height(16.dp))
                RafiqSecondaryButton(
                    text = "إعادة ضبط البيانات الشخصية",
                    icon = RafiqIcons.Refresh,
                    contentColor = palette.danger,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = viewModel::resetPersonalData
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "سيؤدي هذا إلى حذف المهام والعلامات وإعداداتك الشخصية، مع الاحتفاظ بإعدادات النظام.",
                    style = MaterialTheme.typography.labelSmall.copy(color = palette.textDim)
                )
            }

            SettingsSection.LANGUAGE -> SectionScaffold(
                title = "لغة الواجهة",
                onBack = { section = SettingsSection.MAIN }
            ) {
                RafiqCard {
                    Text(
                        text = "العربية",
                        style = MaterialTheme.typography.titleMedium.copy(color = palette.gold)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "صُمم رفيق واجهةً عربية أصيلة، والمحتوى القرآني والأذكار بالعربية الفصحى. إن أردت لغة أخرى فستُضاف في تحديث قادم.",
                        style = MaterialTheme.typography.bodySmall.copy(color = palette.textSecondary)
                    )
                }
                Spacer(Modifier.height(14.dp))
                RafiqSecondaryButton(
                    text = "فتح إعدادات لغة النظام",
                    icon = RafiqIcons.Globe,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = viewModel::openLanguageSettings
                )
            }

            SettingsSection.ABOUT -> SectionScaffold(
                title = "حول رفيق",
                onBack = { section = SettingsSection.MAIN }
            ) {
                RafiqCard {
                    Text(
                        text = "رفيق — شريكك في رحلتك نحو الأفضل",
                        style = MaterialTheme.typography.titleSmall.copy(color = palette.gold)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "تطبيق إسلامي عربي يجمع المصحف والأذكار والمواقيت والتركيز والمهام، بتصميم هادئ يحترم خصوصيتك.",
                        style = MaterialTheme.typography.bodySmall.copy(color = palette.textSecondary)
                    )
                }
                Spacer(Modifier.height(12.dp))
                RafiqCard {
                    Text(
                        text = "المصادر",
                        style = MaterialTheme.typography.titleSmall.copy(color = palette.gold)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "• النص القرآني والترجمة: Quran.com API (النص العثماني + Saheeh International)\n" +
                            "• الأذكار: حصن المسلم ومصادر الصباح والمساء الموثقة\n" +
                            "• المواقيت: حسابات فلكية وفق طرق الهيئات المعتمدة\n" +
                            "• التلاوة: مشاري العفاسي عبر everyayah.com",
                        style = MaterialTheme.typography.bodySmall.copy(color = palette.textSecondary)
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "الإصدار ${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.labelSmall.copy(color = palette.textDim)
                )
            }
        }

        TransientMessage(
            message = state.message,
            onConsumed = viewModel::consumeMessage,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp)
        )
    }

    if (showNameDialog) {
        NameDialog(
            current = preferences.displayName,
            onDismiss = { showNameDialog = false },
            onSave = { name ->
                viewModel.setDisplayName(name)
                showNameDialog = false
            }
        )
    }

    if (showThemeDialog) {
        ThemeDialog(
            current = preferences.theme,
            onDismiss = { showThemeDialog = false },
            onSelect = { theme ->
                viewModel.setTheme(theme)
                showThemeDialog = false
            }
        )
    }

    if (showFocusDialog) {
        FocusDurationDialog(
            current = preferences.focusDurationMinutes,
            onDismiss = { showFocusDialog = false },
            onSelect = { minutes ->
                viewModel.setFocusDuration(minutes)
                showFocusDialog = false
            }
        )
    }
}

@Composable
private fun SectionScaffold(
    title: String,
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        RafiqTopBar(title = title, onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp)
                .padding(bottom = 24.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun ProfileCard(name: String, onEdit: () -> Unit) {
    val palette = RafiqTheme.colors
    RafiqCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(palette.surfaceHigh)
                    .border(1.dp, palette.border, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = RafiqIcons.Person,
                    contentDescription = null,
                    tint = palette.gold,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge.copy(color = palette.textPrimary)
                )
                Text(
                    text = "اضغط لتغيير الاسم المفضل",
                    style = MaterialTheme.typography.labelSmall.copy(color = palette.textDim)
                )
            }
            RoundIconButton(
                icon = RafiqIcons.Edit,
                contentDescription = "تعديل الاسم",
                size = 36.dp,
                onClick = onEdit
            )
        }
    }
}

@Composable
private fun SettingsGroup(content: @Composable () -> Unit) {
    val palette = RafiqTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(palette.surface.copy(alpha = 0.6f))
            .border(1.dp, palette.borderSoft, RoundedCornerShape(20.dp))
    ) {
        content()
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    value: String?,
    onClick: () -> Unit
) {
    val palette = RafiqTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = palette.goldMuted,
            modifier = Modifier.size(19.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(color = palette.textPrimary),
            modifier = Modifier.weight(1f)
        )
        if (value != null) {
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall.copy(color = palette.textDim)
            )
            Spacer(Modifier.width(6.dp))
        }
        Icon(
            imageVector = RafiqIcons.ChevronLeft,
            contentDescription = null,
            tint = palette.textDim,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun ToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val palette = RafiqTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(palette.surface.copy(alpha = 0.6f))
            .border(1.dp, palette.borderSoft, RoundedCornerShape(18.dp))
            .clickable { onToggle(!checked) }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = palette.goldMuted,
            modifier = Modifier.size(19.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(color = palette.textPrimary)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(color = palette.textDim)
            )
        }
        Box(
            modifier = Modifier
                .size(width = 44.dp, height = 24.dp)
                .clip(CircleShape)
                .background(if (checked) palette.gold.copy(alpha = 0.35f) else palette.surfaceHigh)
                .border(1.dp, if (checked) palette.gold else palette.borderSoft, CircleShape)
                .padding(2.dp),
            contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(if (checked) palette.goldBright else palette.textDim)
            )
        }
    }
}

@Composable
private fun NameDialog(
    current: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    val palette = RafiqTheme.colors
    var name by remember { mutableStateOf(current) }
    RafiqDialog(title = "الاسم المفضل", onDismiss = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(palette.surface.copy(alpha = 0.7f))
                .border(1.dp, palette.borderSoft, RoundedCornerShape(14.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            BasicTextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = palette.textPrimary),
                cursorBrush = SolidColor(palette.gold),
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(Modifier.height(16.dp))
        RafiqPrimaryButton(
            text = "حفظ",
            icon = RafiqIcons.Check,
            enabled = name.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            onClick = { onSave(name) }
        )
    }
}

@Composable
private fun ThemeDialog(
    current: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    val palette = RafiqTheme.colors
    val themes = listOf(
        Triple("emerald", "الزمردي", paletteFor("emerald")),
        Triple("midnight", "الليلي", paletteFor("midnight")),
        Triple("dawn", "الفجر", paletteFor("dawn"))
    )
    RafiqDialog(title = "مظهر رفيق", onDismiss = onDismiss) {
        themes.forEach { (id, name, themePalette) ->
            val selected = id == current
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (selected) palette.surfaceHigh.copy(alpha = 0.9f) else Color.Transparent
                    )
                    .clickable { onSelect(id) }
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(themePalette.backgroundSecondary)
                        .border(2.dp, themePalette.gold, CircleShape)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (selected) palette.gold else palette.textPrimary
                    ),
                    modifier = Modifier.weight(1f)
                )
                if (selected) {
                    Icon(
                        imageVector = RafiqIcons.Check,
                        contentDescription = null,
                        tint = palette.gold,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FocusDurationDialog(
    current: Int,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit
) {
    RafiqDialog(title = "مدة جلسة التركيز", onDismiss = onDismiss) {
        Column {
            listOf(15, 25, 45, 60, 90).forEach { minutes ->
                val selected = minutes == current
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(minutes) }
                        .padding(vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$minutes دقيقة",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (selected) RafiqTheme.colors.gold else RafiqTheme.colors.textPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    if (selected) {
                        Icon(
                            imageVector = RafiqIcons.Check,
                            contentDescription = null,
                            tint = RafiqTheme.colors.gold,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun themeName(theme: String): String = when (theme) {
    "midnight" -> "الليلي"
    "dawn" -> "الفجر"
    else -> "الزمردي"
}

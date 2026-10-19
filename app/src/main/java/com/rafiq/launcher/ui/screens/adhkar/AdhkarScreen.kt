package com.rafiq.launcher.ui.screens.adhkar

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rafiq.launcher.data.adhkar.AdhkarCategory
import com.rafiq.launcher.data.adhkar.AdhkarItem
import com.rafiq.launcher.ui.AppViewModelProvider
import com.rafiq.launcher.ui.components.ErrorOrnament
import com.rafiq.launcher.ui.components.LoadingOrnament
import com.rafiq.launcher.ui.components.RafiqCard
import com.rafiq.launcher.ui.components.RafiqDialog
import com.rafiq.launcher.ui.components.RafiqIcons
import com.rafiq.launcher.ui.components.RafiqTopBar
import com.rafiq.launcher.ui.components.RoundIconButton
import com.rafiq.launcher.ui.components.SegmentedPill
import com.rafiq.launcher.ui.theme.RafiqText
import com.rafiq.launcher.ui.theme.RafiqTheme

@Composable
fun AdhkarScreen(
    viewModel: AdhkarViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val palette = RafiqTheme.colors
    var showSources by remember { mutableStateOf(false) }
    var quickTab by remember { mutableStateOf(0) }

    val category = state.selectedCategory
    if (category != null) {
        BackHandler { viewModel.closeCategory() }
        AdhkarReader(
            category = category,
            progress = state.progress,
            onIncrement = viewModel::increment,
            onResetItem = viewModel::resetItem,
            onResetCategory = { viewModel.resetCategory(category) },
            onBack = viewModel::closeCategory
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        RafiqTopBar(
            title = "الأذكار",
            subtitle = "حصّن يومك بذكر الله",
            actions = {
                RoundIconButton(
                    icon = RafiqIcons.Info,
                    contentDescription = "المصادر",
                    size = 38.dp,
                    onClick = { showSources = true }
                )
            }
        )
        when {
            state.isLoading -> LoadingOrnament(label = "جارٍ تحميل الأذكار")
            state.error != null -> ErrorOrnament(
                message = state.error ?: "",
                onRetry = viewModel::retry
            )
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 18.dp)
                ) {
                    SegmentedPill(
                        options = listOf("أذكار الصباح", "أذكار المساء"),
                        selectedIndex = quickTab,
                        onSelected = { index ->
                            quickTab = index
                            viewModel.openCategory(if (index == 0) "morning" else "evening")
                        }
                    )
                    Spacer(Modifier.height(16.dp))
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 20.dp)
                    ) {
                        items(state.data?.categories ?: emptyList(), key = { it.id }) { item ->
                            CategoryCard(
                                category = item,
                                onClick = { viewModel.openCategory(item.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSources) {
        RafiqDialog(title = "مصادر الأذكار", onDismiss = { showSources = false }) {
            Text(
                text = state.data?.source ?: "أذكار الصباح والمساء من حصن المسلم ومصادر موثقة.",
                style = MaterialTheme.typography.bodyMedium.copy(color = palette.textSecondary)
            )
        }
    }
}

@Composable
private fun CategoryCard(category: AdhkarCategory, onClick: () -> Unit) {
    val palette = RafiqTheme.colors
    RafiqCard(onClick = onClick, contentPadding = PaddingValues(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(palette.surfaceHigh.copy(alpha = 0.85f))
                    .border(1.dp, palette.border, RoundedCornerShape(15.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryIcon(category.icon),
                    contentDescription = category.title,
                    tint = palette.gold,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.title,
                    style = MaterialTheme.typography.titleMedium.copy(color = palette.textPrimary)
                )
                Text(
                    text = category.subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(color = palette.textDim),
                    maxLines = 2
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${category.items.size}",
                    style = MaterialTheme.typography.titleLarge.copy(color = palette.gold)
                )
                Text(
                    text = "ذكرًا",
                    style = MaterialTheme.typography.labelSmall.copy(color = palette.textDim)
                )
            }
        }
    }
}

@Composable
private fun AdhkarReader(
    category: AdhkarCategory,
    progress: Map<String, Int>,
    onIncrement: (AdhkarItem) -> Unit,
    onResetItem: (AdhkarItem) -> Unit,
    onResetCategory: () -> Unit,
    onBack: () -> Unit
) {
    val palette = RafiqTheme.colors
    val completed = category.items.count { item -> (progress[item.id] ?: 0) >= item.count }
    Column(modifier = Modifier.fillMaxSize()) {
        RafiqTopBar(
            title = category.title,
            subtitle = "أتممت $completed من ${category.items.size}",
            onBack = onBack,
            actions = {
                RoundIconButton(
                    icon = RafiqIcons.Refresh,
                    contentDescription = "إعادة العدّ",
                    size = 38.dp,
                    onClick = onResetCategory
                )
            }
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 6.dp, bottom = 24.dp)
        ) {
            items(category.items, key = { it.id }) { item ->
                DhikrCard(
                    item = item,
                    current = (progress[item.id] ?: 0).coerceAtMost(item.count),
                    onIncrement = { onIncrement(item) },
                    onReset = { onResetItem(item) }
                )
            }
        }
    }
}

@Composable
private fun DhikrCard(
    item: AdhkarItem,
    current: Int,
    onIncrement: () -> Unit,
    onReset: () -> Unit
) {
    val palette = RafiqTheme.colors
    val done = current >= item.count
    RafiqCard(
        onClick = onIncrement,
        contentPadding = PaddingValues(16.dp),
        borderColor = if (done) palette.gold.copy(alpha = 0.55f) else palette.borderSoft
    ) {
        if (item.title != null) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.labelMedium.copy(color = palette.goldMuted),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(6.dp))
        }
        Text(
            text = item.text,
            style = RafiqText.QuranSmall.copy(
                fontSize = 20.sp,
                lineHeight = 38.sp,
                color = palette.textPrimary
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        if (item.benefit.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = item.benefit,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = palette.mint,
                    fontStyle = FontStyle.Italic
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        if (done) palette.gold.copy(alpha = 0.16f) else palette.surfaceHigh
                    )
                    .border(1.dp, if (done) palette.gold else palette.borderSoft, CircleShape)
                    .clickable(onClick = onIncrement),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$current/${item.count}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = if (done) palette.goldBright else palette.textSecondary
                    )
                )
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                if (item.countText.isNotBlank()) {
                    Text(
                        text = item.countText,
                        style = MaterialTheme.typography.labelSmall.copy(color = palette.textDim)
                    )
                }
                Text(
                    text = item.source,
                    style = MaterialTheme.typography.labelSmall.copy(color = palette.textDim),
                    maxLines = 2
                )
            }
            RoundIconButton(
                icon = RafiqIcons.Refresh,
                contentDescription = "تصفير",
                size = 34.dp,
                tint = palette.textDim,
                onClick = onReset
            )
        }
    }
}

private fun categoryIcon(icon: String): ImageVector = when (icon) {
    "sun" -> RafiqIcons.Sun
    "moon" -> RafiqIcons.Moon
    "bed" -> RafiqIcons.Bed
    else -> RafiqIcons.Sparkle
}

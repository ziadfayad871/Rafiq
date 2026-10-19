package com.rafiq.launcher.ui.screens.quran

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rafiq.launcher.ui.AppViewModelProvider
import com.rafiq.launcher.ui.components.ErrorOrnament
import com.rafiq.launcher.ui.components.LoadingOrnament
import com.rafiq.launcher.ui.components.OrnamentalDivider
import com.rafiq.launcher.ui.components.RafiqBottomSheet
import com.rafiq.launcher.ui.components.RafiqDialog
import com.rafiq.launcher.ui.components.RafiqIcons
import com.rafiq.launcher.ui.components.RafiqTopBar
import com.rafiq.launcher.ui.components.RoundIconButton
import com.rafiq.launcher.ui.components.SegmentedPill
import com.rafiq.launcher.ui.components.TransientMessage
import com.rafiq.launcher.ui.theme.RafiqText
import com.rafiq.launcher.ui.theme.RafiqTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun QuranScreen(
    viewModel: QuranViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val palette = RafiqTheme.colors
    val listState = rememberLazyListState()
    var showIndexSheet by remember { mutableStateOf(false) }
    var showBookmarksSheet by remember { mutableStateOf(false) }
    var showSourcesDialog by remember { mutableStateOf(false) }

    val scrollRequest = state.scrollRequest
    if (scrollRequest != null) {
        LaunchedEffect(state.surahNumber, scrollRequest) {
            val index = state.surah?.verses?.indexOfFirst { it.number == scrollRequest } ?: 0
            if (index >= 0) {
                listState.animateScrollToItem(index + 1)
            }
            viewModel.consumeScrollRequest()
        }
    }

    LaunchedEffect(state.surahNumber) {
        snapshotFlow { listState.firstVisibleItemIndex }.collectLatest { first ->
            if (first > 0) {
                val verse = state.surah?.verses?.getOrNull(first - 1) ?: return@collectLatest
                viewModel.onAyahVisible(state.surahNumber, verse.number)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            RafiqTopBar(
                title = "القرآن الكريم",
                subtitle = state.surah?.let { "${it.name} • ${it.revelation}" },
                actions = {
                    RoundIconButton(
                        icon = RafiqIcons.MenuLines,
                        contentDescription = "فهرس السور",
                        size = 38.dp,
                        onClick = { showIndexSheet = true }
                    )
                    RoundIconButton(
                        icon = RafiqIcons.Bookmark,
                        contentDescription = "العلامات",
                        size = 38.dp,
                        onClick = { showBookmarksSheet = true }
                    )
                    RoundIconButton(
                        icon = RafiqIcons.Info,
                        contentDescription = "المصادر",
                        size = 38.dp,
                        onClick = { showSourcesDialog = true }
                    )
                }
            )
            when {
                state.isLoading -> LoadingOrnament(label = "جارٍ فتح المصحف")
                state.error != null -> ErrorOrnament(
                    message = state.error ?: "",
                    onRetry = viewModel::retry
                )
                state.surah != null -> {
                    val surah = state.surah!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        SegmentedPill(
                            options = listOf("المصحف", "الترجمة"),
                            selectedIndex = state.mode,
                            onSelected = viewModel::setMode
                        )
                        Spacer(Modifier.height(12.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "سورة ${surah.name}",
                                style = RafiqText.OrnamentTitle.copy(color = palette.gold)
                            )
                            Text(
                                text = "${surah.versesCount} آية • ${surah.revelation}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = palette.textDim
                                )
                            )
                            if (surah.bismillahBefore && surah.number != 9) {
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                    style = RafiqText.QuranSmall.copy(color = palette.goldMuted)
                                )
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        OrnamentalDivider()
                        Spacer(Modifier.height(6.dp))
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(surah.verses, key = { it.number }) { verse ->
                                val bookmarked = viewModel.isBookmarked(surah.number, verse.number)
                                AyahRow(
                                    number = verse.number,
                                    text = verse.text,
                                    translation = if (state.showTranslation) verse.translation else null,
                                    isBookmarked = bookmarked,
                                    isPlaying = state.playingAyah == verse.number &&
                                        state.audioPhase != AudioPhase.IDLE,
                                    onBookmark = { viewModel.toggleBookmark(surah.number, verse.number) },
                                    onPlay = { viewModel.playAyah(verse.number) },
                                    onSelect = { viewModel.onAyahVisible(surah.number, verse.number) }
                                )
                                Spacer(Modifier.height(4.dp))
                            }
                        }
                        AudioBar(
                            phase = state.audioPhase,
                            playingAyah = state.playingAyah,
                            onToggle = viewModel::togglePlayPause,
                            onNext = viewModel::skipNext,
                            onPrevious = viewModel::skipPrevious
                        )
                    }
                }
            }
        }

        TransientMessage(
            message = state.message ?: state.audioMessage,
            onConsumed = {
                viewModel.consumeMessage()
                viewModel.consumeAudioMessage()
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 110.dp)
        )
    }

    if (showIndexSheet) {
        RafiqBottomSheet(onDismiss = { showIndexSheet = false }) {
            Text(
                text = "فهرس السور",
                style = MaterialTheme.typography.titleLarge,
                color = palette.gold
            )
            Spacer(Modifier.height(4.dp))
            LazyColumn(modifier = Modifier.height(430.dp)) {
                items(state.data?.surahs ?: emptyList(), key = { it.number }) { surah ->
                    val selected = surah.number == state.surahNumber
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (selected) palette.surfaceHigh.copy(alpha = 0.9f)
                                else Color.Transparent
                            )
                            .clickable {
                                viewModel.selectSurah(surah.number)
                                showIndexSheet = false
                            }
                            .padding(horizontal = 12.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(palette.surfaceHigh)
                                .border(1.dp, palette.borderSoft, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = surah.number.toString(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (selected) palette.gold else palette.textSecondary
                                )
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = surah.name,
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = if (selected) palette.gold else palette.textPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${surah.versesCount} آية",
                            style = MaterialTheme.typography.labelSmall.copy(color = palette.textDim)
                        )
                    }
                }
            }
        }
    }

    if (showBookmarksSheet) {
        RafiqBottomSheet(onDismiss = { showBookmarksSheet = false }) {
            Text(
                text = "علاماتي المرجعية",
                style = MaterialTheme.typography.titleLarge,
                color = palette.gold
            )
            Spacer(Modifier.height(10.dp))
            if (state.bookmarks.isEmpty()) {
                Text(
                    text = "لم تحفظ أي علامة بعد. اضغط على أيقونة العلامة بجانب الآية لحفظها هنا.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = palette.textSecondary),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                )
            } else {
                LazyColumn(modifier = Modifier.height(420.dp)) {
                    items(state.bookmarks, key = { it.id }) { bookmark ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    viewModel.openBookmark(bookmark)
                                    showBookmarksSheet = false
                                }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${bookmark.surahName} • الآية ${bookmark.ayahNumber}",
                                    style = MaterialTheme.typography.titleSmall.copy(color = palette.gold)
                                )
                                Text(
                                    text = bookmark.snippet,
                                    style = RafiqText.QuranSmall.copy(
                                        fontSize = 17.sp,
                                        lineHeight = 30.sp,
                                        color = palette.textSecondary
                                    ),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            RoundIconButton(
                                icon = RafiqIcons.Trash,
                                contentDescription = "حذف",
                                size = 34.dp,
                                onClick = { viewModel.deleteBookmark(bookmark) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSourcesDialog) {
        RafiqDialog(title = "مصادر النص", onDismiss = { showSourcesDialog = false }) {
            Text(
                text = "النص القرآني: النص العثماني الموثق عبر Quran.com API.",
                style = MaterialTheme.typography.bodyMedium.copy(color = palette.textSecondary)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "الترجمة: Saheeh International، وتظهر عند اختيار تبويب الترجمة.",
                style = MaterialTheme.typography.bodyMedium.copy(color = palette.textSecondary)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "التلاوة: الشيخ مشاري العفاسي — everyayah.com.",
                style = MaterialTheme.typography.bodyMedium.copy(color = palette.textSecondary)
            )
        }
    }
}

@Composable
private fun AyahRow(
    number: Int,
    text: String,
    translation: String?,
    isBookmarked: Boolean,
    isPlaying: Boolean,
    onBookmark: () -> Unit,
    onPlay: () -> Unit,
    onSelect: () -> Unit
) {
    val palette = RafiqTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isPlaying) palette.surfaceHigh.copy(alpha = 0.55f) else Color.Transparent
            )
            .clickable(onClick = onSelect)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            style = RafiqText.Quran.copy(color = palette.textPrimary),
            textAlign = TextAlign.Justify
        )
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .border(1.dp, palette.borderSoft, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number.toString(),
                    style = MaterialTheme.typography.labelSmall.copy(color = palette.goldMuted)
                )
            }
            Spacer(Modifier.width(8.dp))
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                RoundIconButton(
                    icon = RafiqIcons.Play,
                    contentDescription = "تشغيل الآية",
                    size = 32.dp,
                    onClick = onPlay
                )
                RoundIconButton(
                    icon = if (isBookmarked) RafiqIcons.BookmarkFilled else RafiqIcons.Bookmark,
                    contentDescription = "علامة",
                    size = 32.dp,
                    tint = if (isBookmarked) palette.goldBright else palette.goldMuted,
                    onClick = onBookmark
                )
            }
        }
        if (translation != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = translation,
                style = MaterialTheme.typography.bodyMedium.copy(color = palette.textSecondary),
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
private fun AudioBar(
    phase: AudioPhase,
    playingAyah: Int?,
    onToggle: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    val palette = RafiqTheme.colors
    Column {
        OrnamentalDivider(color = palette.borderSoft)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "تلاوة الشيخ مشاري العفاسي",
                    style = MaterialTheme.typography.labelMedium.copy(color = palette.textPrimary)
                )
                Text(
                    text = when (phase) {
                        AudioPhase.IDLE -> "اختر آية للاستماع"
                        AudioPhase.PREPARING -> "جارٍ تجهيز التلاوة..."
                        AudioPhase.PLAYING -> "تُتلى الآن الآية ${playingAyah ?: ""}"
                        AudioPhase.PAUSED -> "تلاوة متوقفة مؤقتًا"
                    },
                    style = MaterialTheme.typography.labelSmall.copy(color = palette.textDim)
                )
            }
            RoundIconButton(
                icon = RafiqIcons.Previous,
                contentDescription = "السابق",
                size = 38.dp,
                onClick = onPrevious
            )
            Spacer(Modifier.width(8.dp))
            RoundIconButton(
                icon = if (phase == AudioPhase.PLAYING) RafiqIcons.Pause else RafiqIcons.Play,
                contentDescription = "تشغيل",
                size = 46.dp,
                filled = true,
                onClick = onToggle
            )
            Spacer(Modifier.width(8.dp))
            RoundIconButton(
                icon = RafiqIcons.Next,
                contentDescription = "التالي",
                size = 38.dp,
                onClick = onNext
            )
        }
    }
}

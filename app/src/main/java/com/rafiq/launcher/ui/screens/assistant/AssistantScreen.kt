package com.rafiq.launcher.ui.screens.assistant

import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rafiq.launcher.ui.AppViewModelProvider
import com.rafiq.launcher.ui.components.RafiqCard
import com.rafiq.launcher.ui.components.RafiqIcons
import com.rafiq.launcher.ui.components.RafiqMark
import com.rafiq.launcher.ui.components.RafiqTopBar
import com.rafiq.launcher.ui.components.RoundIconButton
import com.rafiq.launcher.ui.components.SimpleOrnamentRing
import com.rafiq.launcher.ui.theme.RafiqTheme
import java.util.Locale

@Composable
fun AssistantScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: AssistantViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val palette = RafiqTheme.colors
    val context = LocalContext.current
    val listState = rememberLazyListState()
    var input by remember { mutableStateOf("") }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(Unit) {
        var engine: TextToSpeech? = null
        engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                runCatching {
                    engine?.language = Locale("ar")
                }
            }
        }
        tts = engine
        onDispose {
            runCatching { engine?.stop() }
            runCatching { engine?.shutdown() }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigation.collect { route -> onNavigate(route) }
    }

    LaunchedEffect(state.messages.size, state.isThinking) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val text = result.data
            ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            ?.firstOrNull()
        if (!text.isNullOrBlank()) {
            viewModel.send(text)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        RafiqTopBar(
            title = "مساعد رفيق",
            subtitle = "إجابات موثوقة من مصادر محفوظة",
            onBack = onBack,
            actions = {
                if (state.messages.isNotEmpty()) {
                    RoundIconButton(
                        icon = RafiqIcons.Trash,
                        contentDescription = "محادثة جديدة",
                        size = 38.dp,
                        onClick = viewModel::clearConversation
                    )
                }
            }
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            if (state.messages.isEmpty()) {
                AssistantHero(
                    suggestions = state.suggestions,
                    onSuggestion = viewModel::send,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 12.dp)
                ) {
                    items(state.messages, key = { it.id }) { message ->
                        MessageBubble(
                            message = message,
                            onSpeak = {
                                tts?.speak(
                                    message.text,
                                    TextToSpeech.QUEUE_FLUSH,
                                    null,
                                    "rafiq-${message.id}"
                                )
                            }
                        )
                    }
                    if (state.isThinking) {
                        item(key = "thinking") {
                            ThinkingIndicator()
                        }
                    }
                }
            }
        }
        InputBar(
            value = input,
            onValueChange = { input = it },
            onSend = {
                viewModel.send(input)
                input = ""
            },
            onMic = {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                    )
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar")
                    putExtra(RecognizerIntent.EXTRA_PROMPT, "تحدّث مع رفيق")
                }
                runCatching { speechLauncher.launch(intent) }
            }
        )
    }
}

@Composable
private fun AssistantHero(
    suggestions: List<String>,
    onSuggestion: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = RafiqTheme.colors
    Column(
        modifier = modifier.padding(bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            SimpleOrnamentRing(modifier = Modifier.size(150.dp), color = palette.gold.copy(alpha = 0.5f))
            RafiqMark(size = 78.dp)
        }
        Spacer(Modifier.height(18.dp))
        Text(
            text = "كيف أساعدك اليوم؟",
            style = MaterialTheme.typography.titleLarge.copy(color = palette.textPrimary)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "أسألني عن الصلاة أو القرآن أو الأذكار، أو اطلب فتح قسم أو إضافة مهمة.",
            style = MaterialTheme.typography.bodySmall.copy(color = palette.textDim),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(Modifier.height(20.dp))
        suggestions.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowItems.forEach { suggestion ->
                    RafiqCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSuggestion(suggestion) },
                        contentPadding = PaddingValues(14.dp)
                    ) {
                        Text(
                            text = suggestion,
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = palette.goldMuted
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                if (rowItems.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage, onSpeak: () -> Unit) {
    val palette = RafiqTheme.colors
    val alignment = if (message.fromUser) Alignment.CenterEnd else Alignment.CenterStart
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (message.fromUser) 48.dp else 0.dp,
                end = if (message.fromUser) 0.dp else 48.dp
            ),
        contentAlignment = alignment
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(
                    if (message.fromUser) palette.gold.copy(alpha = 0.16f)
                    else palette.surfaceHigh.copy(alpha = 0.9f)
                )
                .border(
                    1.dp,
                    if (message.fromUser) palette.gold.copy(alpha = 0.4f) else palette.borderSoft,
                    RoundedCornerShape(18.dp)
                )
                .padding(horizontal = 14.dp, vertical = 11.dp)
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = palette.textPrimary
                )
            )
            if (!message.fromUser && !message.source.isNullOrBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "المصدر: ${message.source}",
                    style = MaterialTheme.typography.labelSmall.copy(color = palette.goldMuted)
                )
            }
            if (!message.fromUser) {
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = RafiqIcons.Volume,
                        contentDescription = "استماع",
                        tint = palette.textDim,
                        modifier = Modifier
                            .size(16.dp)
                            .clickable(onClick = onSpeak)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = "استماع",
                        style = MaterialTheme.typography.labelSmall.copy(color = palette.textDim),
                        modifier = Modifier.clickable(onClick = onSpeak)
                    )
                }
            }
        }
    }
}

@Composable
private fun ThinkingIndicator() {
    val palette = RafiqTheme.colors
    val transition = rememberInfiniteTransition(label = "thinking")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 48.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = "رفيق يفكّر...",
            style = MaterialTheme.typography.labelMedium.copy(color = palette.textDim),
            modifier = Modifier.alpha(alpha)
        )
    }
}

@Composable
private fun InputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onMic: () -> Unit
) {
    val palette = RafiqTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(palette.surface.copy(alpha = 0.8f))
                .border(1.dp, palette.borderSoft, RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                if (value.isEmpty()) {
                    Text(
                        text = "اكتب سؤالك لرفيق...",
                        style = MaterialTheme.typography.bodyMedium.copy(color = palette.textDim)
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = palette.textPrimary),
                    cursorBrush = SolidColor(palette.gold),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Icon(
                imageVector = RafiqIcons.Mic,
                contentDescription = "تحدث",
                tint = palette.mint,
                modifier = Modifier
                    .size(20.dp)
                    .clickable(onClick = onMic)
            )
        }
        Spacer(Modifier.width(8.dp))
        RoundIconButton(
            icon = RafiqIcons.Send,
            contentDescription = "إرسال",
            size = 48.dp,
            filled = value.isNotBlank(),
            onClick = { if (value.isNotBlank()) onSend() }
        )
    }
}

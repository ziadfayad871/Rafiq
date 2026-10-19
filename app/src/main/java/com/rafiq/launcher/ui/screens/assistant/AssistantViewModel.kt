package com.rafiq.launcher.ui.screens.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafiq.launcher.AppContainer
import com.rafiq.launcher.data.apps.normalizeArabicForSearch
import com.rafiq.launcher.data.prayer.CalculationMethod
import com.rafiq.launcher.data.prayer.PrayerCalculator
import com.rafiq.launcher.data.preferences.UserPreferences
import com.rafiq.launcher.util.formatCountdown
import com.rafiq.launcher.util.formatTime12Hour
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.atomic.AtomicLong

data class ChatMessage(
    val id: Long = 0L,
    val fromUser: Boolean,
    val text: String,
    val source: String? = null
)

data class AssistantUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isThinking: Boolean = false,
    val suggestions: List<String> = listOf(
        "افتح القرآن",
        "ما هي الصلاة القادمة؟",
        "ابدأ جلسة تركيز ٢٥ دقيقة",
        "ذكرني بورد القرآن الساعة 8"
    )
)

class AssistantViewModel(private val container: AppContainer) : ViewModel() {

    private val _uiState = MutableStateFlow(AssistantUiState())
    val uiState: StateFlow<AssistantUiState> = _uiState.asStateFlow()

    private val _navigation = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val navigation: SharedFlow<String> = _navigation.asSharedFlow()

    private val idGenerator = AtomicLong(1L)
    private var prefs: UserPreferences = UserPreferences()

    private data class AnswerEntry(
        val keywords: List<String>,
        val answer: String,
        val source: String
    )

    private var answers: List<AnswerEntry> = emptyList()
    private var fallback =
        "لا أملك إجابة موثوقة لهذا السؤال ضمن المصادر المحفوظة داخل رفيق، ولا أنسب إلى الله أو رسوله ﷺ ما لم يثبت. اسألني عن الصلاة أو القرآن أو الأذكار."

    init {
        viewModelScope.launch {
            container.preferencesRepository.preferences.collect { prefs = it }
        }
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    container.appContext.assets.open("assistant_answers.json")
                        .bufferedReader(Charsets.UTF_8)
                        .use { it.readText() }
                }
            }.onSuccess { raw -> parseAnswers(raw) }
        }
    }

    private fun parseAnswers(raw: String) {
        runCatching {
            val root = JSONObject(raw)
            val suggestionsArray = root.optJSONArray("suggestions")
            if (suggestionsArray != null) {
                val suggestions = (0 until suggestionsArray.length())
                    .mapNotNull { suggestionsArray.optString(it).takeIf { text -> text.isNotBlank() } }
                if (suggestions.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(suggestions = suggestions)
                }
            }
            val entries = root.optJSONArray("answers") ?: return@runCatching
            answers = (0 until entries.length()).mapNotNull { index ->
                val entry = entries.optJSONObject(index) ?: return@mapNotNull null
                val keywordsArray = entry.optJSONArray("keywords") ?: return@mapNotNull null
                val keywords = (0 until keywordsArray.length())
                    .mapNotNull { keywordsArray.optString(it).takeIf { text -> text.isNotBlank() } }
                AnswerEntry(
                    keywords = keywords,
                    answer = entry.optString("answer"),
                    source = entry.optString("source")
                )
            }
            fallback = root.optString("fallback", fallback)
        }
    }

    fun send(rawInput: String) {
        val input = rawInput.trim()
        if (input.isEmpty()) return
        append(ChatMessage(id = idGenerator.getAndIncrement(), fromUser = true, text = input))
        _uiState.value = _uiState.value.copy(isThinking = true)
        viewModelScope.launch {
            delay(420)
            val reply = buildReply(input)
            _uiState.value = _uiState.value.copy(isThinking = false)
            append(
                ChatMessage(
                    id = idGenerator.getAndIncrement(),
                    fromUser = false,
                    text = reply.text,
                    source = reply.source
                )
            )
            reply.route?.let { _navigation.tryEmit(it) }
        }
    }

    private fun append(message: ChatMessage) {
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + message
        )
    }

    fun clearConversation() {
        _uiState.value = _uiState.value.copy(messages = emptyList())
    }

    private data class Reply(val text: String, val source: String? = null, val route: String? = null)

    private fun buildReply(input: String): Reply {
        val normalized = normalizeArabicForSearch(input)

        openIntent(normalized)?.let { return it }
        prayerIntent(normalized)?.let { return it }
        ayahIntent(normalized)?.let { return it }
        focusIntent(normalized)?.let { return it }
        taskIntent(normalized)?.let { return it }

        val match = answers.firstOrNull { entry ->
            entry.keywords.any { keyword -> normalized.contains(normalizeArabicForSearch(keyword)) }
        }
        return if (match != null) {
            Reply(match.answer, match.source)
        } else {
            Reply(fallback)
        }
    }

    private fun openIntent(normalized: String): Reply? {
        if (!normalized.contains("افتح") && !normalized.contains("شغل")) return null
        val destinations = listOf(
            Triple(listOf("القران", "المصحف"), "quran", "المصحف"),
            Triple(listOf("الاذكار", "اذكار"), "adhkar", "الأذكار"),
            Triple(listOf("الصلاه", "المواقيت", "مواقيت"), "prayer", "مواقيت الصلاة"),
            Triple(listOf("المهام", "مهامي"), "tasks", "المهام"),
            Triple(listOf("التركيز"), "focus", "التركيز"),
            Triple(listOf("الاعدادات", "الاعداد"), "settings", "الإعدادات"),
            Triple(listOf("التطبيقات", "التطبيقا"), "apps", "التطبيقات")
        )
        for ((keywords, route, label) in destinations) {
            if (keywords.any { normalized.contains(it) }) {
                return Reply("فتحت لك $label.", route = route)
            }
        }
        return null
    }

    private fun prayerIntent(normalized: String): Reply? {
        val asksNext = normalized.contains("الصلاة القادمة") ||
            normalized.contains("الصلاه القادمه") ||
            normalized.contains("مواقيت") ||
            normalized.contains("متى الصلاة") ||
            (normalized.contains("وقت") && normalized.contains("الصلاه"))
        if (!asksNext) return null
        val zone = runCatching { ZoneId.of(prefs.timeZoneId) }.getOrDefault(ZoneId.systemDefault())
        val now = LocalDateTime.now(zone)
        val method = CalculationMethod.fromId(prefs.calculationMethod)
        val today = runCatching {
            PrayerCalculator.calculate(
                now.toLocalDate(), prefs.latitude.toDouble(), prefs.longitude.toDouble(), zone, method
            )
        }.getOrNull() ?: return null
        val tomorrow = runCatching {
            PrayerCalculator.calculate(
                now.toLocalDate().plusDays(1), prefs.latitude.toDouble(), prefs.longitude.toDouble(), zone, method
            )
        }.getOrNull() ?: return null
        val info = PrayerCalculator.nextPrayer(today, now, tomorrow)
        val timeLabel = formatTime12Hour(info.prayer.time)
        val remaining = formatCountdown(info.remaining)
        val notificationHint = if (prefs.prayerNotifications) {
            "وتنبيهات الصلاة مفعّلة."
        } else {
            "ويمكنك تفعيل تنبيهات الصلاة من قسم الصلاة."
        }
        return Reply(
            text = "الصلاة القادمة هي ${info.prayer.name} الساعة $timeLabel، $remaining. " +
                "الحساب حسب ${prefs.locationLabel}. $notificationHint",
            source = "حساب فلكي حسب الإحداثيات والطريقة المختارة (${method.arabicName})."
        )
    }

    private fun ayahIntent(normalized: String): Reply? {
        if (!normalized.contains("ايه الكرسي") && !normalized.contains("اية الكرسي") &&
            !normalized.contains("آية الكرسي")
        ) {
            return null
        }
        val data = container.quranRepository.cachedData() ?: return Reply(
            text = "يمكنك قراءة آية الكرسي من سورة البقرة داخل المصحف في رفيق (الآية 255).",
            source = "سورة البقرة، الآية 255."
        )
        val surah = data.surahs.getOrNull(1)
        val verse = surah?.verses?.getOrNull(254)
        return if (verse != null) {
            Reply(verse.text, "سورة البقرة، الآية 255 — النص العثماني الموثق.")
        } else {
            Reply("يمكنك قراءة آية الكرسي من سورة البقرة داخل المصحف (الآية 255).")
        }
    }

    private fun focusIntent(normalized: String): Reply? {
        if (!normalized.contains("جلسة تركيز") && !normalized.contains("جلسه تركيز") &&
            !normalized.contains("ابدأ التركيز") && !normalized.contains("شغل التركيز")
        ) {
            return null
        }
        val minutes = parseMinutes(normalized) ?: prefs.focusDurationMinutes
        viewModelScope.launch {
            container.preferencesRepository.setFocusDuration(minutes)
        }
        return Reply(
            text = "جهّزت لك جلسة تركيز لمدة $minutes دقيقة، وفتحت شاشة التركيز. اضغط ابدأ متى شئت.",
            route = "focus"
        )
    }

    private fun taskIntent(normalized: String): Reply? {
        val reminder = normalized.contains("ذكرني")
        val addTask = normalized.contains("اضف مهمه") || normalized.contains("اضف مهمة") ||
            normalized.contains("ذكرني")
        if (!reminder && !addTask) return null

        val keyword = if (reminder) "ذكرني" else "اضف مهمه"
        var remainder = normalized.substringAfter(keyword)
        remainder = remainder.trim().removePrefix("ب").trim()

        val timeIndex = remainder.indexOf("الساعه")
        var title = remainder
        var timeLabel: String? = null
        if (timeIndex >= 0) {
            title = remainder.substring(0, timeIndex).trim()
            val timePart = normalizeDigits(remainder.substring(timeIndex + "الساعه".length)).trim()
            timeLabel = parseTimeLabel(timePart)
        }
        title = title.trim(' ', '.', '،', '!', '؟', '?')
        if (title.isEmpty()) {
            return Reply("اكتب لي عنوان المهمة، مثل: «ذكرني بمراجعة الدرس الساعة 8».")
        }
        viewModelScope.launch {
            runCatching { container.tasksRepository.add(title, timeLabel) }
        }
        val timeText = timeLabel?.let { " الساعة $it" } ?: ""
        return Reply("أضفت مهمة: «$title»$timeText إلى قائمة مهام اليوم.")
    }

    private fun parseMinutes(normalized: String): Int? {
        val digits = normalizeDigits(normalized)
        Regex("(\\d{1,3})\\s*(دقيق|دقيقه)").find(digits)?.let { match ->
            return match.groupValues[1].toIntOrNull()?.coerceIn(5, 180)
        }
        Regex("(\\d{1,2})\\s*(ساع|ساعه)").find(digits)?.let { match ->
            return match.groupValues[1].toIntOrNull()?.times(60)?.coerceIn(5, 180)
        }
        return null
    }

    private fun parseTimeLabel(value: String): String? {
        val cleaned = value.trim()
        if (cleaned.isEmpty()) return null
        Regex("(\\d{1,2})(?::(\\d{2}))?").find(cleaned)?.let { match ->
            val hour = match.groupValues[1].toIntOrNull() ?: return null
            val minute = match.groupValues[2].toIntOrNull() ?: 0
            if (hour !in 0..23 || minute !in 0..59) return null
            return "%02d:%02d".format(hour, minute)
        }
        return cleaned.take(12)
    }

    private fun normalizeDigits(value: String): String = value.map { char ->
        if (char in '\u0660'..'\u0669') {
            ('0' + (char - '\u0660'))
        } else if (char in '\u06F0'..'\u06F9') {
            ('0' + (char - '\u06F0'))
        } else {
            char
        }
    }.joinToString("")
}

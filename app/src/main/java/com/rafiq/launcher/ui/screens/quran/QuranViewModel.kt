package com.rafiq.launcher.ui.screens.quran

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafiq.launcher.AppContainer
import com.rafiq.launcher.data.local.QuranBookmarkEntity
import com.rafiq.launcher.data.preferences.UserPreferences
import com.rafiq.launcher.data.quran.QuranData
import com.rafiq.launcher.data.quran.QuranSurah
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

enum class AudioPhase { IDLE, PREPARING, PLAYING, PAUSED }

data class QuranUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val data: QuranData? = null,
    val surahNumber: Int = 1,
    val mode: Int = 0,
    val bookmarks: List<QuranBookmarkEntity> = emptyList(),
    val currentAyah: Int = 1,
    val scrollRequest: Int? = null,
    val audioPhase: AudioPhase = AudioPhase.IDLE,
    val playingAyah: Int? = null,
    val audioMessage: String? = null,
    val message: String? = null
) {
    val surah: QuranSurah?
        get() = data?.surahs?.getOrNull(surahNumber - 1)

    val showTranslation: Boolean get() = mode == 1
}

class QuranViewModel(private val container: AppContainer) : ViewModel() {

    private val _uiState = MutableStateFlow(QuranUiState())
    val uiState: StateFlow<QuranUiState> = _uiState.asStateFlow()

    private var prefs: UserPreferences = UserPreferences()
    private var player: MediaPlayer? = null
    private var lastSavedAt = 0L

    init {
        viewModelScope.launch {
            container.preferencesRepository.preferences.collect { prefs = it }
        }
        viewModelScope.launch {
            container.bookmarksRepository.bookmarks.collect { bookmarks ->
                _uiState.value = _uiState.value.copy(bookmarks = bookmarks)
            }
        }
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            runCatching { container.quranRepository.load() }
                .onSuccess { data ->
                    val initial = container.preferencesRepository.current()
                    val initialSurah = initial.quranSurah.coerceIn(1, data.surahs.size)
                    val initialAyah = if (initialSurah == initial.quranSurah) {
                        initial.quranAyah
                    } else 1
                    _uiState.value = _uiState.value.copy(
                        data = data,
                        isLoading = false,
                        surahNumber = initialSurah,
                        currentAyah = initialAyah,
                        scrollRequest = initialAyah
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "تعذّر تحميل المصحف من ملفات التطبيق"
                    )
                }
        }
    }

    fun retry() = load()

    fun setMode(index: Int) {
        _uiState.value = _uiState.value.copy(mode = index.coerceIn(0, 1))
    }

    fun selectSurah(number: Int) {
        val data = _uiState.value.data ?: return
        val surahNumber = number.coerceIn(1, data.surahs.size)
        val savedAyah = if (prefs.quranSurah == surahNumber) prefs.quranAyah else 1
        stopAudio()
        _uiState.value = _uiState.value.copy(
            surahNumber = surahNumber,
            currentAyah = savedAyah,
            scrollRequest = savedAyah
        )
        savePosition(surahNumber, savedAyah, immediate = true)
    }

    fun consumeScrollRequest() {
        _uiState.value = _uiState.value.copy(scrollRequest = null)
    }

    fun onAyahVisible(surahNumber: Int, ayah: Int) {
        if (_uiState.value.surahNumber != surahNumber) return
        if (_uiState.value.currentAyah != ayah) {
            _uiState.value = _uiState.value.copy(currentAyah = ayah)
        }
        savePosition(surahNumber, ayah)
    }

    private fun savePosition(surah: Int, ayah: Int, immediate: Boolean = false) {
        val now = System.currentTimeMillis()
        if (!immediate && now - lastSavedAt < 2_000) return
        lastSavedAt = now
        viewModelScope.launch {
            runCatching { container.preferencesRepository.setQuranPosition(surah, ayah) }
        }
    }

    fun isBookmarked(surah: Int, ayah: Int): Boolean =
        _uiState.value.bookmarks.any { it.surahNumber == surah && it.ayahNumber == ayah }

    fun toggleBookmark(surahNumber: Int, ayah: Int) {
        val surah = _uiState.value.data?.surahs?.getOrNull(surahNumber - 1) ?: return
        val verse = surah.verses.getOrNull(ayah - 1) ?: return
        viewModelScope.launch {
            val exists = isBookmarked(surahNumber, ayah)
            runCatching {
                container.bookmarksRepository.toggle(
                    surah = surahNumber,
                    ayah = ayah,
                    surahName = surah.name,
                    snippet = verse.text,
                    exists = exists
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(message = "تعذّر حفظ العلامة")
            }
        }
    }

    fun openBookmark(bookmark: QuranBookmarkEntity) {
        stopAudio()
        _uiState.value = _uiState.value.copy(
            surahNumber = bookmark.surahNumber,
            currentAyah = bookmark.ayahNumber,
            scrollRequest = bookmark.ayahNumber
        )
    }

    fun deleteBookmark(bookmark: QuranBookmarkEntity) {
        viewModelScope.launch {
            runCatching { container.bookmarksRepository.delete(bookmark) }
        }
    }

    fun togglePlayPause() {
        when (_uiState.value.audioPhase) {
            AudioPhase.PLAYING -> {
                player?.let {
                    runCatching { it.pause() }
                    _uiState.value = _uiState.value.copy(audioPhase = AudioPhase.PAUSED)
                }
            }
            AudioPhase.PAUSED -> {
                player?.let {
                    runCatching { it.start() }
                    _uiState.value = _uiState.value.copy(audioPhase = AudioPhase.PLAYING)
                }
            }
            AudioPhase.IDLE, AudioPhase.PREPARING -> {
                val state = _uiState.value
                if (state.audioPhase == AudioPhase.PREPARING) return
                playAyah(state.currentAyah)
            }
        }
    }

    fun playAyah(ayah: Int) {
        val state = _uiState.value
        val surah = state.surah ?: return
        val target = ayah.coerceIn(1, surah.verses.size)
        stopPlayerOnly()
        _uiState.value = _uiState.value.copy(
            audioPhase = AudioPhase.PREPARING,
            playingAyah = target,
            currentAyah = target
        )
        val url = "https://everyayah.com/data/Alafasy_128kbps/%03d%03d.mp3".format(
            surah.number,
            surah.verses[target - 1].number
        )
        runCatching {
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )
            mediaPlayer.setDataSource(url)
            mediaPlayer.setOnPreparedListener {
                if (_uiState.value.playingAyah != target) {
                    runCatching { it.release() }
                    return@setOnPreparedListener
                }
                it.start()
                _uiState.value = _uiState.value.copy(audioPhase = AudioPhase.PLAYING)
            }
            mediaPlayer.setOnCompletionListener { completed ->
                runCatching { completed.release() }
                player = null
                advanceAfterCompletion(surah.number, target)
            }
            mediaPlayer.setOnErrorListener { failed, _, _ ->
                runCatching { failed.release() }
                player = null
                _uiState.value = _uiState.value.copy(
                    audioPhase = AudioPhase.IDLE,
                    playingAyah = null,
                    audioMessage = "تعذّر تشغيل التلاوة، تحقق من اتصال الإنترنت"
                )
                true
            }
            player = mediaPlayer
            mediaPlayer.prepareAsync()
        }.onFailure {
            _uiState.value = _uiState.value.copy(
                audioPhase = AudioPhase.IDLE,
                playingAyah = null,
                audioMessage = "تعذّر تشغيل التلاوة، تحقق من اتصال الإنترنت"
            )
        }
    }

    private fun advanceAfterCompletion(surahNumber: Int, finishedAyah: Int) {
        val surah = _uiState.value.data?.surahs?.getOrNull(surahNumber - 1)
        if (surah == null || finishedAyah >= surah.verses.size) {
            _uiState.value = _uiState.value.copy(
                audioPhase = AudioPhase.IDLE,
                playingAyah = null
            )
            return
        }
        playAyah(finishedAyah + 1)
    }

    fun skipNext() {
        val state = _uiState.value
        val surah = state.surah ?: return
        val from = state.playingAyah ?: state.currentAyah
        if (from < surah.verses.size) playAyah(from + 1)
    }

    fun skipPrevious() {
        val state = _uiState.value
        val from = state.playingAyah ?: state.currentAyah
        if (from > 1) playAyah(from - 1)
    }

    fun stopAudio() {
        stopPlayerOnly()
        _uiState.value = _uiState.value.copy(
            audioPhase = AudioPhase.IDLE,
            playingAyah = null
        )
    }

    private fun stopPlayerOnly() {
        player?.let {
            runCatching { it.stop() }
            runCatching { it.release() }
        }
        player = null
    }

    fun consumeAudioMessage() {
        _uiState.value = _uiState.value.copy(audioMessage = null)
    }

    fun consumeMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }

    override fun onCleared() {
        stopPlayerOnly()
        super.onCleared()
    }
}

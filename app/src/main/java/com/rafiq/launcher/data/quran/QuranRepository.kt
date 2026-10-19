package com.rafiq.launcher.data.quran

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class QuranVerse(
    val number: Int,
    val text: String,
    val translation: String
)

data class QuranSurah(
    val number: Int,
    val name: String,
    val englishName: String,
    val revelation: String,
    val versesCount: Int,
    val bismillahBefore: Boolean,
    val verses: List<QuranVerse>
)

data class QuranData(
    val source: String,
    val translationSource: String,
    val surahs: List<QuranSurah>
)

class QuranRepository(private val context: Context) {
    private val mutex = Mutex()
    @Volatile
    private var cached: QuranData? = null

    suspend fun load(): QuranData {
        cached?.let { return it }
        return mutex.withLock {
            cached ?: withContext(Dispatchers.IO) {
                parse(loadAsset()).also { cached = it }
            }
        }
    }

    fun cachedData(): QuranData? = cached

    private fun loadAsset(): String =
        context.assets.open("quran.json").bufferedReader(Charsets.UTF_8).use { it.readText() }

    private fun parse(raw: String): QuranData {
        val root = JSONObject(raw)
        val surahsArray = root.getJSONArray("surahs")
        val surahs = ArrayList<QuranSurah>(surahsArray.length())
        for (i in 0 until surahsArray.length()) {
            val surahObject = surahsArray.getJSONObject(i)
            val versesArray = surahObject.getJSONArray("verses")
            val verses = ArrayList<QuranVerse>(versesArray.length())
            for (v in 0 until versesArray.length()) {
                val verse = versesArray.getJSONObject(v)
                verses += QuranVerse(
                    number = verse.getInt("number"),
                    text = verse.getString("text"),
                    translation = verse.optString("translation", "")
                )
            }
            surahs += QuranSurah(
                number = surahObject.getInt("number"),
                name = surahObject.getString("name"),
                englishName = surahObject.optString("englishName", ""),
                revelation = surahObject.optString("revelation", "مكية"),
                versesCount = surahObject.optInt("versesCount", verses.size),
                bismillahBefore = surahObject.optBoolean("bismillahBefore", true),
                verses = verses
            )
        }
        return QuranData(
            source = root.optString("source", ""),
            translationSource = root.optString("translationSource", ""),
            surahs = surahs
        )
    }
}

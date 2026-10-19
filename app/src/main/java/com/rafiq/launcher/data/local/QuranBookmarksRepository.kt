package com.rafiq.launcher.data.local

import kotlinx.coroutines.flow.Flow

class QuranBookmarksRepository(private val dao: QuranBookmarkDao) {
    val bookmarks: Flow<List<QuranBookmarkEntity>> = dao.observeAll()

    fun isBookmarked(surah: Int, ayah: Int): Flow<Boolean> = dao.observeIsBookmarked(surah, ayah)

    suspend fun toggle(surah: Int, ayah: Int, surahName: String, snippet: String, exists: Boolean) {
        if (exists) {
            dao.deleteAt(surah, ayah)
        } else {
            dao.insert(
                QuranBookmarkEntity(
                    surahNumber = surah,
                    ayahNumber = ayah,
                    surahName = surahName,
                    snippet = snippet.take(120)
                )
            )
        }
    }

    suspend fun delete(bookmark: QuranBookmarkEntity) = dao.delete(bookmark)

    suspend fun clearAll() = dao.clear()
}

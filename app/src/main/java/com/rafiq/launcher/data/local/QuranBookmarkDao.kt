package com.rafiq.launcher.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuranBookmarkDao {
    @Query("SELECT * FROM quran_bookmarks ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<QuranBookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM quran_bookmarks WHERE surahNumber = :surah AND ayahNumber = :ayah)")
    fun observeIsBookmarked(surah: Int, ayah: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(bookmark: QuranBookmarkEntity): Long

    @Query("DELETE FROM quran_bookmarks WHERE surahNumber = :surah AND ayahNumber = :ayah")
    suspend fun deleteAt(surah: Int, ayah: Int)

    @Delete
    suspend fun delete(bookmark: QuranBookmarkEntity)

    @Query("DELETE FROM quran_bookmarks")
    suspend fun clear()
}

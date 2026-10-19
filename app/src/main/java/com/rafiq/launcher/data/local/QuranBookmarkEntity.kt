package com.rafiq.launcher.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "quran_bookmarks",
    indices = [Index(value = ["surahNumber", "ayahNumber"], unique = true)]
)
data class QuranBookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val surahNumber: Int,
    val ayahNumber: Int,
    val surahName: String,
    val snippet: String,
    val createdAt: Long = System.currentTimeMillis()
)

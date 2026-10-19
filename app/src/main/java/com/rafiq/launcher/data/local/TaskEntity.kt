package com.rafiq.launcher.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val timeLabel: String? = null,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val sortOrder: Int = 0
)

package com.rafiq.launcher.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM daily_tasks ORDER BY isCompleted ASC, sortOrder ASC, createdAt ASC")
    fun observeAll(): Flow<List<TaskEntity>>

    @Query("SELECT COUNT(*) FROM daily_tasks")
    suspend fun count(): Int

    @Insert
    suspend fun insert(task: TaskEntity): Long

    @Insert
    suspend fun insertAll(tasks: List<TaskEntity>)

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("DELETE FROM daily_tasks")
    suspend fun clear()
}

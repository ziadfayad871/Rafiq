package com.rafiq.launcher.data.local

import kotlinx.coroutines.flow.Flow

class TasksRepository(private val dao: TaskDao) {
    val tasks: Flow<List<TaskEntity>> = dao.observeAll()

    suspend fun add(title: String, timeLabel: String?) {
        val clean = title.trim()
        if (clean.isEmpty()) return
        dao.insert(
            TaskEntity(
                title = clean,
                timeLabel = timeLabel?.trim()?.takeIf { it.isNotEmpty() },
                sortOrder = dao.count()
            )
        )
    }

    suspend fun toggle(task: TaskEntity) {
        dao.update(task.copy(isCompleted = !task.isCompleted))
    }

    suspend fun delete(task: TaskEntity) {
        dao.delete(task)
    }

    suspend fun edit(task: TaskEntity, title: String, timeLabel: String?) {
        val clean = title.trim()
        if (clean.isEmpty()) return
        dao.update(
            task.copy(
                title = clean,
                timeLabel = timeLabel?.trim()?.takeIf { it.isNotEmpty() }
            )
        )
    }

    suspend fun clearAll() = dao.clear()

    suspend fun seedDefaults() {
        if (dao.count() > 0) return
        dao.insertAll(
            listOf(
                TaskEntity(title = "قراءة ٥ صفحات من القرآن", timeLabel = "بعد الفجر", sortOrder = 0),
                TaskEntity(title = "مراجعة مادة البرمجة", timeLabel = "١٠:٠٠", sortOrder = 1),
                TaskEntity(title = "ممارسة الرياضة", timeLabel = "١٦:٠٠", sortOrder = 2),
                TaskEntity(title = "مراجعة ملاحظات اليوم", timeLabel = "٢٠:٠٠", sortOrder = 3),
                TaskEntity(title = "النوم مبكرًا", timeLabel = "٢٢:٣٠", sortOrder = 4)
            )
        )
    }
}

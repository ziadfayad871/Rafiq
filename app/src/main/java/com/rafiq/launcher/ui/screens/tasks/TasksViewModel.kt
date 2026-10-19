package com.rafiq.launcher.ui.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafiq.launcher.data.local.TaskEntity
import com.rafiq.launcher.data.local.TasksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TasksUiState(
    val message: String? = null
)

class TasksViewModel(private val repository: TasksRepository) : ViewModel() {

    val tasks: StateFlow<List<TaskEntity>> = repository.tasks.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    fun add(title: String, timeLabel: String?) {
        if (title.isBlank()) return
        viewModelScope.launch {
            runCatching { repository.add(title, timeLabel) }
            _uiState.value = _uiState.value.copy(message = "أُضيفت المهمة")
        }
    }

    fun toggle(task: TaskEntity) {
        viewModelScope.launch {
            runCatching { repository.toggle(task) }
        }
    }

    fun edit(task: TaskEntity, title: String, timeLabel: String?) {
        viewModelScope.launch {
            runCatching { repository.edit(task, title, timeLabel) }
        }
    }

    fun delete(task: TaskEntity) {
        viewModelScope.launch {
            runCatching { repository.delete(task) }
            _uiState.value = _uiState.value.copy(message = "حُذفت المهمة")
        }
    }

    fun addFromAssistant(title: String, timeLabel: String?) {
        viewModelScope.launch {
            runCatching { repository.add(title, timeLabel) }
        }
    }

    fun consumeMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}

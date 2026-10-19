package com.rafiq.launcher.ui.screens.adhkar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafiq.launcher.data.adhkar.AdhkarCategory
import com.rafiq.launcher.data.adhkar.AdhkarData
import com.rafiq.launcher.data.adhkar.AdhkarItem
import com.rafiq.launcher.data.adhkar.AdhkarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdhkarUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val data: AdhkarData? = null,
    val selectedCategoryId: String? = null,
    val progress: Map<String, Int> = emptyMap()
) {
    val selectedCategory: AdhkarCategory?
        get() = data?.categories?.firstOrNull { it.id == selectedCategoryId }
}

class AdhkarViewModel(private val repository: AdhkarRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AdhkarUiState())
    val uiState: StateFlow<AdhkarUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            runCatching { repository.load() }
                .onSuccess { data ->
                    _uiState.value = _uiState.value.copy(data = data, isLoading = false)
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "تعذّر تحميل الأذكار من ملفات التطبيق"
                    )
                }
        }
    }

    fun retry() = load()

    fun openCategory(categoryId: String) {
        _uiState.value = _uiState.value.copy(selectedCategoryId = categoryId)
    }

    fun closeCategory() {
        _uiState.value = _uiState.value.copy(selectedCategoryId = null)
    }

    fun openCategoryByTitle(title: String) {
        val category = _uiState.value.data?.categories?.firstOrNull { it.title == title } ?: return
        openCategory(category.id)
    }

    fun increment(item: AdhkarItem) {
        val current = _uiState.value.progress[item.id] ?: 0
        if (current >= item.count) return
        _uiState.value = _uiState.value.copy(
            progress = _uiState.value.progress + (item.id to current + 1)
        )
    }

    fun resetItem(item: AdhkarItem) {
        _uiState.value = _uiState.value.copy(
            progress = _uiState.value.progress - item.id
        )
    }

    fun resetCategory(category: AdhkarCategory) {
        val updated = _uiState.value.progress.toMutableMap()
        category.items.forEach { updated.remove(it.id) }
        _uiState.value = _uiState.value.copy(progress = updated)
    }
}

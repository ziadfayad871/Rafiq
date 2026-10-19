package com.rafiq.launcher.ui.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rafiq.launcher.data.local.TaskEntity
import com.rafiq.launcher.ui.AppViewModelProvider
import com.rafiq.launcher.ui.components.EmptyState
import com.rafiq.launcher.ui.components.FloatingGoldButton
import com.rafiq.launcher.ui.components.RafiqCard
import com.rafiq.launcher.ui.components.RafiqDialog
import com.rafiq.launcher.ui.components.RafiqIcons
import com.rafiq.launcher.ui.components.RafiqPrimaryButton
import com.rafiq.launcher.ui.components.RafiqTopBar
import com.rafiq.launcher.ui.components.RoundIconButton
import com.rafiq.launcher.ui.components.TransientMessage
import com.rafiq.launcher.ui.theme.RafiqTheme
import com.rafiq.launcher.util.formatGregorianDate

@Composable
fun TasksScreen(
    onBack: () -> Unit,
    viewModel: TasksViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val palette = RafiqTheme.colors
    var editorTarget by remember { mutableStateOf<TaskEntity?>(null) }
    var showEditor by remember { mutableStateOf(false) }

    val completed = tasks.count { it.isCompleted }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            RafiqTopBar(
                title = "مهام اليوم",
                subtitle = formatGregorianDate(),
                onBack = onBack,
                actions = {
                    RoundIconButton(
                        icon = RafiqIcons.Plus,
                        contentDescription = "مهمة جديدة",
                        size = 38.dp,
                        onClick = {
                            editorTarget = null
                            showEditor = true
                        }
                    )
                }
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(Modifier.height(8.dp))
                if (tasks.isNotEmpty()) {
                    Text(
                        text = "أنجزت $completed من ${tasks.size}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = palette.textSecondary)
                    )
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(palette.borderSoft.copy(alpha = 0.5f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(
                                    if (tasks.isEmpty()) 0f else completed.toFloat() / tasks.size
                                )
                                .height(4.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(palette.goldMuted, palette.goldBright)
                                    )
                                )
                        )
                    }
                    Spacer(Modifier.height(14.dp))
                }
                if (tasks.isEmpty()) {
                    EmptyState(
                        icon = RafiqIcons.Tasks,
                        title = "لا مهام بعد",
                        body = "أضف مهامك اليومية ونظّم يومك بهدوء.",
                        action = {
                            RafiqPrimaryButton(
                                text = "إضافة مهمة",
                                icon = RafiqIcons.Plus,
                                onClick = {
                                    editorTarget = null
                                    showEditor = true
                                }
                            )
                        }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 90.dp)
                    ) {
                        items(tasks, key = { it.id }) { task ->
                            TaskRow(
                                task = task,
                                onToggle = { viewModel.toggle(task) },
                                onEdit = {
                                    editorTarget = task
                                    showEditor = true
                                },
                                onDelete = { viewModel.delete(task) }
                            )
                        }
                    }
                }
            }
        }
        FloatingGoldButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            icon = RafiqIcons.Plus,
            onClick = {
                editorTarget = null
                showEditor = true
            }
        )
        TransientMessage(
            message = state.message,
            onConsumed = viewModel::consumeMessage,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp)
        )
    }

    if (showEditor) {
        TaskEditorDialog(
            task = editorTarget,
            onDismiss = { showEditor = false },
            onSave = { title, time ->
                val target = editorTarget
                if (target == null) {
                    viewModel.add(title, time)
                } else {
                    viewModel.edit(target, title, time)
                }
                showEditor = false
            }
        )
    }
}

@Composable
private fun TaskRow(
    task: TaskEntity,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val palette = RafiqTheme.colors
    RafiqCard(contentPadding = PaddingValues(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .border(
                        1.dp,
                        if (task.isCompleted) palette.gold else palette.border,
                        CircleShape
                    )
                    .background(
                        if (task.isCompleted) palette.gold.copy(alpha = 0.18f) else palette.surfaceHigh
                    )
                    .clickable(onClick = onToggle),
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(
                        imageVector = RafiqIcons.Check,
                        contentDescription = null,
                        tint = palette.goldBright,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = if (task.isCompleted) palette.textDim else palette.textPrimary,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                    )
                )
                if (task.timeLabel != null) {
                    Text(
                        text = task.timeLabel,
                        style = MaterialTheme.typography.labelSmall.copy(color = palette.goldMuted)
                    )
                }
            }
            RoundIconButton(
                icon = RafiqIcons.Edit,
                contentDescription = "تعديل",
                size = 32.dp,
                tint = palette.textDim,
                onClick = onEdit
            )
            Spacer(Modifier.width(4.dp))
            RoundIconButton(
                icon = RafiqIcons.Trash,
                contentDescription = "حذف",
                size = 32.dp,
                tint = palette.danger,
                onClick = onDelete
            )
        }
    }
}

@Composable
private fun TaskEditorDialog(
    task: TaskEntity?,
    onDismiss: () -> Unit,
    onSave: (String, String?) -> Unit
) {
    val palette = RafiqTheme.colors
    var title by remember(task) { mutableStateOf(task?.title ?: "") }
    var time by remember(task) { mutableStateOf(task?.timeLabel ?: "") }

    RafiqDialog(
        title = if (task == null) "مهمة جديدة" else "تعديل المهمة",
        onDismiss = onDismiss
    ) {
        FieldLabel("عنوان المهمة")
        DialogField(
            value = title,
            placeholder = "مثال: مراجعة الدرس",
            onValueChange = { title = it }
        )
        Spacer(Modifier.height(12.dp))
        FieldLabel("الوقت (اختياري)")
        DialogField(
            value = time,
            placeholder = "مثال: 10:00 أو بعد الفجر",
            onValueChange = { time = it }
        )
        Spacer(Modifier.height(18.dp))
        RafiqPrimaryButton(
            text = if (task == null) "إضافة" else "حفظ التعديل",
            icon = RafiqIcons.Check,
            enabled = title.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            onClick = { onSave(title, time.ifBlank { null }) }
        )
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium.copy(color = RafiqTheme.colors.textDim),
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
private fun DialogField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    val palette = RafiqTheme.colors
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(palette.surface.copy(alpha = 0.7f))
            .border(1.dp, palette.borderSoft, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium.copy(color = palette.textDim)
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = palette.textPrimary),
            cursorBrush = SolidColor(palette.gold),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

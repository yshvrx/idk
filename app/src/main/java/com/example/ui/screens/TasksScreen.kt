package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Priority
import com.example.data.model.SubTask
import com.example.data.model.TaskEntity
import com.example.data.model.TaskFilter
import com.example.ui.components.CopyrightFooter
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.AccentRed
import com.example.ui.theme.TagAmberBg
import com.example.ui.theme.TagAmberText
import com.example.ui.theme.TagBlueBg
import com.example.ui.theme.TagBlueText
import com.example.ui.theme.TagGreenBg
import com.example.ui.theme.TagGreenText
import com.example.ui.theme.TagPurpleBg
import com.example.ui.theme.TagPurpleText
import com.example.ui.theme.TagRedBg
import com.example.ui.theme.TagRedText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class TaskSortBy {
    DATE,
    PRIORITY,
    CREATION
}

@Composable
fun TasksScreen(
    tasks: List<TaskEntity>,
    activeFilter: TaskFilter,
    searchQuery: String,
    selectedList: String,
    onFilterChange: (TaskFilter) -> Unit,
    onSearchChange: (String) -> Unit,
    onListChange: (String) -> Unit,
    onToggleTask: (TaskEntity) -> Unit,
    onAddTask: (String, String, Long?, Priority, String, List<SubTask>) -> Unit,
    onUpdateTask: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var fastAddText by remember { mutableStateOf("") }
    var taskToEdit by remember { mutableStateOf<TaskEntity?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var currentSort by remember { mutableStateOf(TaskSortBy.DATE) }

    val sortedTasks = remember(tasks, currentSort) {
        when (currentSort) {
            TaskSortBy.DATE -> tasks.sortedBy { it.dueDateEpochMillis ?: Long.MAX_VALUE }
            TaskSortBy.PRIORITY -> tasks.sortedByDescending { it.priority.ordinal }
            TaskSortBy.CREATION -> tasks.sortedByDescending { it.createdAt }
        }
    }

    val surfaceColor = MaterialTheme.colorScheme.surface
    val outlineColor = MaterialTheme.colorScheme.outline
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    val lists = listOf("All", "Personal", "Work", "Ideas")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
            .testTag("tasks_screen")
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title and Sort
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Reminders & Tasks",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.SemiBold,
                color = onSurface
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        currentSort = when (currentSort) {
                            TaskSortBy.DATE -> TaskSortBy.PRIORITY
                            TaskSortBy.PRIORITY -> TaskSortBy.CREATION
                            TaskSortBy.CREATION -> TaskSortBy.DATE
                        }
                    },
                    modifier = Modifier.testTag("tasks_sort_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Sort,
                        contentDescription = "Sort Tasks",
                        tint = onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Apple Reminders Smart View Pills: Today, Upcoming, Completed, All
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                Pair(TaskFilter.ALL, "All"),
                Pair(TaskFilter.TODAY, "Today"),
                Pair(TaskFilter.UPCOMING, "Upcoming"),
                Pair(TaskFilter.COMPLETED, "Completed")
            ).forEach { (filter, label) ->
                val isSelected = activeFilter == filter
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) onSurface else surfaceColor)
                        .border(1.dp, if (isSelected) onSurface else outlineColor, RoundedCornerShape(10.dp))
                        .clickable { onFilterChange(filter) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.surface else onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar & Custom List Chips
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search tasks or descriptions...", style = MaterialTheme.typography.bodyMedium) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = onSurfaceVariant) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear search", tint = onSurfaceVariant)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = surfaceColor,
                unfocusedContainerColor = surfaceColor,
                focusedBorderColor = onSurface,
                unfocusedBorderColor = outlineColor
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("tasks_search_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Lists / Projects filter chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(lists) { listTag ->
                val selected = selectedList.equals(listTag, ignoreCase = true)
                val (chipBg, chipText) = when (listTag.lowercase()) {
                    "work" -> Pair(TagBlueBg, TagBlueText)
                    "ideas" -> Pair(TagAmberBg, TagAmberText)
                    "personal" -> Pair(TagPurpleBg, TagPurpleText)
                    else -> Pair(MaterialTheme.colorScheme.surfaceVariant, onSurface)
                }
                FilterChip(
                    selected = selected,
                    onClick = { onListChange(listTag) },
                    label = { Text(listTag, style = MaterialTheme.typography.labelMedium) },
                    shape = RoundedCornerShape(20.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = chipBg,
                        selectedLabelColor = chipText,
                        containerColor = surfaceColor,
                        labelColor = onSurfaceVariant
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selected,
                        borderColor = if (selected) chipText else outlineColor
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Fast-Add Input Row (Apple Reminders style)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = fastAddText,
                onValueChange = { fastAddText = it },
                placeholder = { Text("Add task... (Press Enter)", style = MaterialTheme.typography.bodyMedium) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (fastAddText.isNotBlank()) {
                            onAddTask(
                                fastAddText.trim(),
                                "",
                                System.currentTimeMillis() + 86400000L,
                                Priority.LOW,
                                if (selectedList == "All") "Personal" else selectedList,
                                emptyList()
                            )
                            fastAddText = ""
                        }
                    }
                ),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = surfaceColor,
                    unfocusedContainerColor = surfaceColor,
                    focusedBorderColor = onSurface,
                    unfocusedBorderColor = outlineColor
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("tasks_fast_add_input")
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(onSurface)
                    .testTag("tasks_open_detailed_add_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Detailed Add",
                    tint = MaterialTheme.colorScheme.surface
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tasks List
        if (sortedTasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No tasks found in this view.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("tasks_lazy_list")
            ) {
                items(sortedTasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        onToggle = { onToggleTask(task) },
                        onEdit = { taskToEdit = task }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    CopyrightFooter()
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // Task Creation Dialog
    if (showCreateDialog) {
        TaskEditDialog(
            task = null,
            defaultList = if (selectedList == "All") "Personal" else selectedList,
            onDismiss = { showCreateDialog = false },
            onSave = { title, desc, dueDate, priority, listTag, subtasks ->
                onAddTask(title, desc, dueDate, priority, listTag, subtasks)
                showCreateDialog = false
            },
            onDelete = null
        )
    }

    // Task Edit Dialog
    taskToEdit?.let { task ->
        TaskEditDialog(
            task = task,
            defaultList = task.listName,
            onDismiss = { taskToEdit = null },
            onSave = { title, desc, dueDate, priority, listTag, subtasks ->
                onUpdateTask(
                    task.copy(
                        title = title,
                        description = desc,
                        dueDateEpochMillis = dueDate,
                        priority = priority,
                        listName = listTag,
                        subtasksJson = TaskEntity.serializeSubtasks(subtasks)
                    )
                )
                taskToEdit = null
            },
            onDelete = {
                onDeleteTask(task)
                taskToEdit = null
            }
        )
    }
}

@Composable
fun TaskCard(
    task: TaskEntity,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val outlineColor = MaterialTheme.colorScheme.outline
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    val subtasks = remember(task.subtasksJson) { task.getSubtasks() }
    val completedSubtasks = subtasks.count { it.isCompleted }

    val dateFormat = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
    val formattedDueDate = task.dueDateEpochMillis?.let { dateFormat.format(Date(it)) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(surfaceColor)
            .border(1.dp, outlineColor, RoundedCornerShape(14.dp))
            .clickable { onEdit() }
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Apple Reminders Minimal Check Circle
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(22.dp)
                .clip(CircleShape)
                .border(
                    1.5.dp,
                    if (task.isCompleted) AccentGreen else onSurfaceVariant.copy(alpha = 0.5f),
                    CircleShape
                )
                .background(if (task.isCompleted) AccentGreen else Color.Transparent)
                .clickable { onToggle() },
            contentAlignment = Alignment.Center
        ) {
            if (task.isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Completed",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (task.isCompleted) onSurfaceVariant else onSurface,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
            )

            if (task.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Metadata Row: Due Date, Subtask Count, List badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                formattedDueDate?.let { dateStr ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = dateStr,
                            style = MaterialTheme.typography.labelSmall,
                            color = onSurfaceVariant
                        )
                    }
                }

                if (subtasks.isNotEmpty()) {
                    Text(
                        text = "$completedSubtasks/${subtasks.size} subtasks",
                        style = MaterialTheme.typography.labelSmall,
                        color = onSurfaceVariant
                    )
                }

                val (badgeBg, badgeText) = when (task.listName.lowercase()) {
                    "work" -> Pair(TagBlueBg, TagBlueText)
                    "ideas" -> Pair(TagAmberBg, TagAmberText)
                    else -> Pair(TagPurpleBg, TagPurpleText)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeBg)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = task.listName,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        fontWeight = FontWeight.SemiBold,
                        color = badgeText
                    )
                }
            }
        }

        // Priority indicator pill
        when (task.priority) {
            Priority.HIGH -> {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(TagRedBg)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "HIGH",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        fontWeight = FontWeight.Bold,
                        color = TagRedText
                    )
                }
            }
            Priority.MEDIUM -> {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(TagAmberBg)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "MED",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        fontWeight = FontWeight.Bold,
                        color = TagAmberText
                    )
                }
            }
            Priority.LOW -> {}
        }
    }
}

@Composable
fun TaskEditDialog(
    task: TaskEntity?,
    defaultList: String,
    onDismiss: () -> Unit,
    onSave: (String, String, Long?, Priority, String, List<SubTask>) -> Unit,
    onDelete: (() -> Unit)?
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(task?.title ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    var priority by remember { mutableStateOf(task?.priority ?: Priority.LOW) }
    var listName by remember { mutableStateOf(task?.listName ?: defaultList) }
    var dueDateMillis by remember { mutableStateOf(task?.dueDateEpochMillis) }
    var subtasks by remember { mutableStateOf(task?.getSubtasks() ?: emptyList()) }
    var newSubtaskText by remember { mutableStateOf("") }

    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (task == null) "New Task" else "Edit Task",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Task title") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Notes or description") },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Priority Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(Priority.LOW, Priority.MEDIUM, Priority.HIGH).forEach { p ->
                        val isSelected = priority == p
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { priority = p }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = p.name,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Due Date Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (dueDateMillis != null) "Due: ${dateFormat.format(Date(dueDateMillis!!))}" else "No due date set",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(
                        onClick = {
                            val cal = Calendar.getInstance()
                            if (dueDateMillis != null) cal.timeInMillis = dueDateMillis!!
                            DatePickerDialog(
                                context,
                                { _, y, m, d ->
                                    val newCal = Calendar.getInstance().apply {
                                        set(y, m, d, 18, 0, 0)
                                    }
                                    dueDateMillis = newCal.timeInMillis
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Set Date")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Subtasks Section
                Text(
                    text = "SUBTASKS (${subtasks.size})",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))

                subtasks.forEachIndexed { index, sub ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                                .background(if (sub.isCompleted) AccentGreen else Color.Transparent)
                                .clickable {
                                    val updated = subtasks.toMutableList()
                                    updated[index] = sub.copy(isCompleted = !sub.isCompleted)
                                    subtasks = updated
                                }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = sub.title,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            textDecoration = if (sub.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                subtasks = subtasks.filterIndexed { i, _ -> i != index }
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Delete", modifier = Modifier.size(14.dp))
                        }
                    }
                }

                // Add Subtask row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newSubtaskText,
                        onValueChange = { newSubtaskText = it },
                        placeholder = { Text("Add subtask...") },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newSubtaskText.isNotBlank()) {
                                subtasks = subtasks + SubTask(
                                    id = System.currentTimeMillis().toString(),
                                    title = newSubtaskText.trim(),
                                    isCompleted = false
                                )
                                newSubtaskText = ""
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onSurface,
                            contentColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Text("Add")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions: Delete & Save
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (onDelete != null) {
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.testTag("task_delete_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Task",
                                tint = AccentRed
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Row {
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (title.isNotBlank()) {
                                    onSave(title.trim(), description.trim(), dueDateMillis, priority, listName, subtasks)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.onSurface,
                                contentColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("task_save_button")
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}

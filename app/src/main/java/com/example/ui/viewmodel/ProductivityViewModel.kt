package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.PreferencesManager
import com.example.data.model.CalendarEventEntity
import com.example.data.model.NoteEntity
import com.example.data.model.Priority
import com.example.data.model.SubTask
import com.example.data.model.TaskEntity
import com.example.data.model.TaskFilter
import com.example.data.repository.ProductivityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class CalendarViewMode {
    MONTH,
    WEEK,
    DAY
}

class ProductivityViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val preferencesManager = PreferencesManager(application)
    val repository = ProductivityRepository(
        taskDao = database.taskDao(),
        eventDao = database.eventDao(),
        noteDao = database.noteDao(),
        preferencesManager = preferencesManager
    )

    val userName: StateFlow<String> = preferencesManager.userName
    val isOnboarded: StateFlow<Boolean> = preferencesManager.isOnboarded
    val themeMode: StateFlow<String> = preferencesManager.themeMode

    // Tasks State
    val allTasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val taskFilter = MutableStateFlow(TaskFilter.ALL)
    val taskSearchQuery = MutableStateFlow("")
    val selectedTaskList = MutableStateFlow("All")

    val celebrateTrigger = MutableStateFlow(0L)

    val filteredTasks: StateFlow<List<TaskEntity>> = combine(
        allTasks,
        taskFilter,
        taskSearchQuery,
        selectedTaskList
    ) { tasks, filter, query, listTag ->
        val calendar = Calendar.getInstance()
        val startOfToday = calendar.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val endOfToday = startOfToday + 86400000L

        tasks.filter { task ->
            // Search query filter
            val matchesQuery = query.isBlank() ||
                task.title.contains(query, ignoreCase = true) ||
                task.description.contains(query, ignoreCase = true)

            // Category list filter
            val matchesList = listTag == "All" || task.listName.equals(listTag, ignoreCase = true)

            // Smart Status Filter
            val matchesFilter = when (filter) {
                TaskFilter.ALL -> !task.isCompleted
                TaskFilter.TODAY -> {
                    !task.isCompleted && (task.dueDateEpochMillis == null ||
                        (task.dueDateEpochMillis in startOfToday..endOfToday) ||
                        task.dueDateEpochMillis < startOfToday)
                }
                TaskFilter.UPCOMING -> {
                    !task.isCompleted && (task.dueDateEpochMillis != null && task.dueDateEpochMillis > endOfToday)
                }
                TaskFilter.COMPLETED -> task.isCompleted
            }

            matchesQuery && matchesList && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Calendar State
    val allEvents: StateFlow<List<CalendarEventEntity>> = repository.allEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedDateMillis = MutableStateFlow(System.currentTimeMillis())
    val calendarViewMode = MutableStateFlow(CalendarViewMode.MONTH)

    // Notes State
    val activeNotes: StateFlow<List<NoteEntity>> = repository.activeNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val noteSearchQuery = MutableStateFlow("")

    val filteredNotes: StateFlow<List<NoteEntity>> = combine(
        activeNotes,
        noteSearchQuery
    ) { notes, query ->
        if (query.isBlank()) {
            notes
        } else {
            notes.filter { note ->
                note.title.contains(query, ignoreCase = true) ||
                    note.content.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.seedStarterContentIfNeeded()
        }
    }

    // Dynamic greeting based on time of day
    fun getGreeting(name: String): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val displayName = if (name.isNotBlank()) name else "there"
        return when (hour) {
            in 5..11 -> "Good morning, $displayName."
            in 12..16 -> "Good afternoon, $displayName."
            in 17..21 -> "Good evening, $displayName."
            else -> "Welcome back, $displayName."
        }
    }

    fun getFormattedCurrentDate(): String {
        val sdf = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
        return sdf.format(Date())
    }

    // User Profile Actions
    fun completeOnboarding(name: String) {
        preferencesManager.completeOnboarding(name)
    }

    fun updateUserName(name: String) {
        preferencesManager.setUserName(name)
    }

    fun setThemeMode(mode: String) {
        preferencesManager.setThemeMode(mode)
    }

    // Task Actions
    fun addTask(
        title: String,
        description: String = "",
        dueDateMillis: Long? = null,
        priority: Priority = Priority.LOW,
        listName: String = "Personal",
        subtasks: List<SubTask> = emptyList()
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val task = TaskEntity(
                title = title.trim(),
                description = description.trim(),
                isCompleted = false,
                dueDateEpochMillis = dueDateMillis,
                priority = priority,
                listName = listName,
                subtasksJson = TaskEntity.serializeSubtasks(subtasks),
                createdAt = System.currentTimeMillis()
            )
            repository.insertTask(task)
        }
    }

    fun toggleTaskCompleted(task: TaskEntity) {
        viewModelScope.launch {
            val updated = task.copy(
                isCompleted = !task.isCompleted,
                completedAt = if (!task.isCompleted) System.currentTimeMillis() else null
            )
            repository.updateTask(updated)
            if (!task.isCompleted) {
                // Trigger pixel smiley celebratory reaction!
                celebrateTrigger.value = System.currentTimeMillis()
            }
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // Event Actions
    fun addEvent(
        title: String,
        description: String = "",
        startMillis: Long,
        endMillis: Long,
        isAllDay: Boolean = false,
        colorTag: String = "DEFAULT",
        recurrence: String = "NONE"
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val event = CalendarEventEntity(
                title = title.trim(),
                description = description.trim(),
                startEpochMillis = startMillis,
                endEpochMillis = endMillis,
                isAllDay = isAllDay,
                colorTag = colorTag,
                recurrence = recurrence,
                createdAt = System.currentTimeMillis()
            )
            repository.insertEvent(event)
        }
    }

    fun updateEvent(event: CalendarEventEntity) {
        viewModelScope.launch {
            repository.updateEvent(event)
        }
    }

    fun deleteEvent(event: CalendarEventEntity) {
        viewModelScope.launch {
            repository.deleteEvent(event)
        }
    }

    // Note Actions
    fun saveNote(
        id: Long = 0,
        title: String,
        content: String,
        isPinned: Boolean = false,
        folder: String = "Notes"
    ) {
        if (title.isBlank() && content.isBlank()) return
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val note = NoteEntity(
                id = id,
                title = if (title.isBlank()) "Untitled Note" else title.trim(),
                content = content,
                isPinned = isPinned,
                isArchived = false,
                folder = folder,
                createdAt = if (id == 0L) now else now,
                updatedAt = now
            )
            if (id == 0L) {
                repository.insertNote(note)
            } else {
                repository.updateNote(note)
            }
        }
    }

    fun toggleNotePinned(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isPinned = !note.isPinned, updatedAt = System.currentTimeMillis()))
        }
    }

    fun archiveNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isArchived = true, updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteNotePermanently(note: NoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    // Backup & Restore
    fun exportBackup(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val json = repository.exportDataAsJson()
            onResult(json)
        }
    }

    fun importBackup(jsonString: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.importDataFromJson(jsonString)
            onResult(success)
        }
    }
}

package com.example.data.repository

import com.example.data.local.EventDao
import com.example.data.local.NoteDao
import com.example.data.local.PreferencesManager
import com.example.data.local.TaskDao
import com.example.data.model.CalendarEventEntity
import com.example.data.model.NoteEntity
import com.example.data.model.Priority
import com.example.data.model.SubTask
import com.example.data.model.TaskEntity
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

class ProductivityRepository(
    private val taskDao: TaskDao,
    private val eventDao: EventDao,
    private val noteDao: NoteDao,
    val preferencesManager: PreferencesManager
) {
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
    val allEvents: Flow<List<CalendarEventEntity>> = eventDao.getAllEvents()
    val activeNotes: Flow<List<NoteEntity>> = noteDao.getActiveNotes()
    val archivedNotes: Flow<List<NoteEntity>> = noteDao.getArchivedNotes()

    suspend fun insertTask(task: TaskEntity): Long = taskDao.insertTask(task)
    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)
    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)
    suspend fun deleteTaskById(id: Long) = taskDao.deleteTaskById(id)

    suspend fun insertEvent(event: CalendarEventEntity): Long = eventDao.insertEvent(event)
    suspend fun updateEvent(event: CalendarEventEntity) = eventDao.updateEvent(event)
    suspend fun deleteEvent(event: CalendarEventEntity) = eventDao.deleteEvent(event)
    suspend fun deleteEventById(id: Long) = eventDao.deleteEventById(id)

    suspend fun insertNote(note: NoteEntity): Long = noteDao.insertNote(note)
    suspend fun updateNote(note: NoteEntity) = noteDao.updateNote(note)
    suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)
    suspend fun deleteNoteById(id: Long) = noteDao.deleteNoteById(id)

    suspend fun seedStarterContentIfNeeded() {
        if (preferencesManager.isInitialSeedDone()) return

        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance().apply { timeInMillis = now }

        // Today end of day for due date
        val todayDue = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 17)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.timeInMillis

        // Tomorrow due date
        val tomorrowDue = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 11)
            set(Calendar.MINUTE, 30)
        }.timeInMillis

        // Starter Tasks
        val starterTasks = listOf(
            TaskEntity(
                title = "Welcome to your minimalist productivity space",
                description = "Tap on this task to view subtasks, set priorities, or add notes.",
                isCompleted = false,
                dueDateEpochMillis = todayDue,
                priority = Priority.HIGH,
                listName = "Personal",
                subtasksJson = TaskEntity.serializeSubtasks(
                    listOf(
                        SubTask(id = "1", title = "Explore the Dashboard and Pixel Smiley", isCompleted = true),
                        SubTask(id = "2", title = "Check off a task to make the smiley bounce!", isCompleted = false),
                        SubTask(id = "3", title = "Try the quick-add input at the bottom", isCompleted = false)
                    )
                )
            ),
            TaskEntity(
                title = "Design review & system sprint",
                description = "Verify typography, contrast, and subtle monochrome micro-interactions.",
                isCompleted = false,
                dueDateEpochMillis = todayDue,
                priority = Priority.MEDIUM,
                listName = "Work",
                subtasksJson = "[]"
            ),
            TaskEntity(
                title = "Notion Calendar planning & agenda setup",
                description = "Schedule upcoming milestones and focus blocks.",
                isCompleted = false,
                dueDateEpochMillis = tomorrowDue,
                priority = Priority.LOW,
                listName = "Work",
                subtasksJson = "[]"
            ),
            TaskEntity(
                title = "Drink water & take a 5-minute breather",
                description = "Stay refreshed and focused throughout the day.",
                isCompleted = true,
                dueDateEpochMillis = now - 3600000,
                priority = Priority.LOW,
                listName = "Personal",
                completedAt = now - 1800000
            )
        )
        taskDao.insertTasks(starterTasks)

        // Starter Events (Today and Tomorrow)
        val eventStart1 = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 10)
            set(Calendar.MINUTE, 0)
        }.timeInMillis
        val eventEnd1 = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 11)
            set(Calendar.MINUTE, 0)
        }.timeInMillis

        val eventStart2 = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 14)
            set(Calendar.MINUTE, 30)
        }.timeInMillis
        val eventEnd2 = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 15)
            set(Calendar.MINUTE, 30)
        }.timeInMillis

        val starterEvents = listOf(
            CalendarEventEntity(
                title = "Focus Block: Deep Work & Writing",
                description = "Distraction-free focus time. Silence notifications and execute.",
                startEpochMillis = eventStart1,
                endEpochMillis = eventEnd1,
                isAllDay = false,
                colorTag = "FOCUS"
            ),
            CalendarEventEntity(
                title = "Weekly Review & Project Sync",
                description = "Review completed tasks, upcoming deadlines, and archive notes.",
                startEpochMillis = eventStart2,
                endEpochMillis = eventEnd2,
                isAllDay = false,
                colorTag = "WORK"
            )
        )
        eventDao.insertEvents(starterEvents)

        // Starter Notes (Apple Notes style)
        val starterNotes = listOf(
            NoteEntity(
                title = "Welcome to Minimal Notes",
                content = """# Minimalist Productivity

Welcome to your serene writing and thinking sanctuary. Notes are automatically saved as you type, distraction-free.

### Key Capabilities:
- Pinned notes stay at the top of your list
- Instant search across all titles and content
- Markdown-style headings and formatting
- Local, private storage with zero tracking

> "Simplicity is about subtracting the obvious and adding the meaningful."

### Quick Checklist:
- [x] Onboarding completed
- [ ] Write your first personal idea
- [ ] Enjoy the retro pixel smiley companion!""",
                isPinned = true,
                folder = "Personal",
                createdAt = now,
                updatedAt = now
            ),
            NoteEntity(
                title = "Productivity Principles",
                content = """## Core Workflows
1. Capture everything immediately into the Fast-Add input.
2. Structure your day in the Calendar view.
3. Keep your active list short: 3 High-Priority tasks per day.
4. Reflect each evening and mark completed tasks.""",
                isPinned = false,
                folder = "Work",
                createdAt = now - 86400000,
                updatedAt = now - 43200000
            )
        )
        noteDao.insertNotes(starterNotes)

        preferencesManager.setInitialSeedDone()
    }

    suspend fun exportDataAsJson(): String {
        val root = JSONObject()
        val tasks = taskDao.getAllTasksSnapshot()
        val events = eventDao.getAllEventsSnapshot()
        val notes = noteDao.getAllNotesSnapshot()

        val tasksArray = JSONArray()
        tasks.forEach { t ->
            val obj = JSONObject().apply {
                put("id", t.id)
                put("title", t.title)
                put("description", t.description)
                put("isCompleted", t.isCompleted)
                put("dueDateEpochMillis", t.dueDateEpochMillis ?: JSONObject.NULL)
                put("priority", t.priority.name)
                put("listName", t.listName)
                put("subtasksJson", t.subtasksJson)
                put("createdAt", t.createdAt)
                put("completedAt", t.completedAt ?: JSONObject.NULL)
            }
            tasksArray.put(obj)
        }
        root.put("tasks", tasksArray)

        val eventsArray = JSONArray()
        events.forEach { e ->
            val obj = JSONObject().apply {
                put("id", e.id)
                put("title", e.title)
                put("description", e.description)
                put("startEpochMillis", e.startEpochMillis)
                put("endEpochMillis", e.endEpochMillis)
                put("isAllDay", e.isAllDay)
                put("colorTag", e.colorTag)
                put("recurrence", e.recurrence)
                put("createdAt", e.createdAt)
            }
            eventsArray.put(obj)
        }
        root.put("events", eventsArray)

        val notesArray = JSONArray()
        notes.forEach { n ->
            val obj = JSONObject().apply {
                put("id", n.id)
                put("title", n.title)
                put("content", n.content)
                put("isPinned", n.isPinned)
                put("isArchived", n.isArchived)
                put("folder", n.folder)
                put("createdAt", n.createdAt)
                put("updatedAt", n.updatedAt)
            }
            notesArray.put(obj)
        }
        root.put("notes", notesArray)
        root.put("userName", preferencesManager.userName.value)
        root.put("exportedAt", System.currentTimeMillis())

        return root.toString(2)
    }

    suspend fun importDataFromJson(jsonString: String): Boolean {
        return try {
            val root = JSONObject(jsonString)
            if (root.has("userName")) {
                val name = root.getString("userName")
                if (name.isNotBlank()) preferencesManager.setUserName(name)
            }

            if (root.has("tasks")) {
                val array = root.getJSONArray("tasks")
                val tasks = mutableListOf<TaskEntity>()
                for (i in 0 until array.length()) {
                    val o = array.getJSONObject(i)
                    tasks.add(
                        TaskEntity(
                            id = 0, // new autoincrement ID
                            title = o.getString("title"),
                            description = o.optString("description", ""),
                            isCompleted = o.optBoolean("isCompleted", false),
                            dueDateEpochMillis = if (o.isNull("dueDateEpochMillis")) null else o.optLong("dueDateEpochMillis"),
                            priority = try { Priority.valueOf(o.optString("priority", "LOW")) } catch (_: Exception) { Priority.LOW },
                            listName = o.optString("listName", "Personal"),
                            subtasksJson = o.optString("subtasksJson", "[]"),
                            createdAt = o.optLong("createdAt", System.currentTimeMillis()),
                            completedAt = if (o.isNull("completedAt")) null else o.optLong("completedAt")
                        )
                    )
                }
                if (tasks.isNotEmpty()) taskDao.insertTasks(tasks)
            }

            if (root.has("events")) {
                val array = root.getJSONArray("events")
                val events = mutableListOf<CalendarEventEntity>()
                for (i in 0 until array.length()) {
                    val o = array.getJSONObject(i)
                    events.add(
                        CalendarEventEntity(
                            id = 0,
                            title = o.getString("title"),
                            description = o.optString("description", ""),
                            startEpochMillis = o.getLong("startEpochMillis"),
                            endEpochMillis = o.getLong("endEpochMillis"),
                            isAllDay = o.optBoolean("isAllDay", false),
                            colorTag = o.optString("colorTag", "DEFAULT"),
                            recurrence = o.optString("recurrence", "NONE"),
                            createdAt = o.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
                if (events.isNotEmpty()) eventDao.insertEvents(events)
            }

            if (root.has("notes")) {
                val array = root.getJSONArray("notes")
                val notes = mutableListOf<NoteEntity>()
                for (i in 0 until array.length()) {
                    val o = array.getJSONObject(i)
                    notes.add(
                        NoteEntity(
                            id = 0,
                            title = o.getString("title"),
                            content = o.getString("content"),
                            isPinned = o.optBoolean("isPinned", false),
                            isArchived = o.optBoolean("isArchived", false),
                            folder = o.optString("folder", "Notes"),
                            createdAt = o.optLong("createdAt", System.currentTimeMillis()),
                            updatedAt = o.optLong("updatedAt", System.currentTimeMillis())
                        )
                    )
                }
                if (notes.isNotEmpty()) noteDao.insertNotes(notes)
            }
            true
        } catch (_: Exception) {
            false
        }
    }
}

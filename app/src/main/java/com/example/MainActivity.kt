package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.NoteEntity
import com.example.ui.screens.AboutDialog
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.NotesScreen
import com.example.ui.screens.SettingsDialog
import com.example.ui.screens.TasksScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ProductivityViewModel

enum class AppTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    DASHBOARD("Today", Icons.Filled.GridView, Icons.Outlined.GridView),
    TASKS("Tasks", Icons.Filled.CheckCircle, Icons.Outlined.CheckCircle),
    CALENDAR("Calendar", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    NOTES("Notes", Icons.Filled.Description, Icons.Outlined.Description)
}

class MainActivity : ComponentActivity() {
    private val viewModel: ProductivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val systemDark = isSystemInDarkTheme()
            val isDark = when (themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> systemDark
            }

            MyApplicationTheme(darkTheme = isDark) {
                val isOnboarded by viewModel.isOnboarded.collectAsStateWithLifecycle()

                if (!isOnboarded) {
                    WelcomeScreen(
                        onContinue = { name ->
                            viewModel.completeOnboarding(name)
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .navigationBarsPadding()
                    )
                } else {
                    MainProductivityApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MainProductivityApp(viewModel: ProductivityViewModel) {
    var currentTab by remember { mutableStateOf(AppTab.DASHBOARD) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var selectedNoteToOpen by remember { mutableStateOf<NoteEntity?>(null) }

    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    val allTasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val filteredTasks by viewModel.filteredTasks.collectAsStateWithLifecycle()
    val activeFilter by viewModel.taskFilter.collectAsStateWithLifecycle()
    val taskSearch by viewModel.taskSearchQuery.collectAsStateWithLifecycle()
    val selectedList by viewModel.selectedTaskList.collectAsStateWithLifecycle()
    val celebrateTrigger by viewModel.celebrateTrigger.collectAsStateWithLifecycle()

    val allEvents by viewModel.allEvents.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDateMillis.collectAsStateWithLifecycle()
    val calendarViewMode by viewModel.calendarViewMode.collectAsStateWithLifecycle()

    val activeNotes by viewModel.activeNotes.collectAsStateWithLifecycle()
    val filteredNotes by viewModel.filteredNotes.collectAsStateWithLifecycle()
    val noteSearch by viewModel.noteSearchQuery.collectAsStateWithLifecycle()

    // Back handler: Return to Dashboard if on a sub-tab
    BackHandler(enabled = currentTab != AppTab.DASHBOARD) {
        currentTab = AppTab.DASHBOARD
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val isTabletLandscape = maxWidth > 680.dp

        if (isTabletLandscape) {
            // Desktop & Tablet Navigation Rail (Slim left sidebar)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    header = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (userName.isNotBlank()) userName.take(1).uppercase() else "P",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = userName.ifBlank { "User" },
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    modifier = Modifier.border(width = 1.dp, color = MaterialTheme.colorScheme.outline)
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    AppTab.entries.forEach { tab ->
                        val selected = currentTab == tab
                        NavigationRailItem(
                            selected = selected,
                            onClick = {
                                if (tab != AppTab.NOTES) selectedNoteToOpen = null
                                currentTab = tab
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.title
                                )
                            },
                            label = { Text(tab.title, style = MaterialTheme.typography.labelSmall) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.surface,
                                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                                indicatorColor = MaterialTheme.colorScheme.onSurface,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = { showSettingsDialog = true },
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Content area
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    ContentForTab(
                        currentTab = currentTab,
                        viewModel = viewModel,
                        userName = userName,
                        allTasks = allTasks,
                        filteredTasks = filteredTasks,
                        activeFilter = activeFilter,
                        taskSearch = taskSearch,
                        selectedList = selectedList,
                        celebrateTrigger = celebrateTrigger,
                        allEvents = allEvents,
                        selectedDate = selectedDate,
                        calendarViewMode = calendarViewMode,
                        activeNotes = activeNotes,
                        filteredNotes = filteredNotes,
                        noteSearch = noteSearch,
                        selectedNoteToOpen = selectedNoteToOpen,
                        onSwitchTab = { tab -> currentTab = tab },
                        onOpenNote = { note ->
                            selectedNoteToOpen = note
                            currentTab = AppTab.NOTES
                        },
                        onOpenSettings = { showSettingsDialog = true },
                        onOpenAbout = { showAboutDialog = true }
                    )
                }
            }
        } else {
            // Mobile Layout with Standard Bottom Navigation Bar
            Scaffold(
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 0.dp,
                        modifier = Modifier
                            .border(width = 1.dp, color = MaterialTheme.colorScheme.outline)
                            .navigationBarsPadding()
                            .testTag("app_bottom_nav_bar")
                    ) {
                        AppTab.entries.forEach { tab ->
                            val selected = currentTab == tab
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    if (tab != AppTab.NOTES) selectedNoteToOpen = null
                                    currentTab = tab
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.title
                                    )
                                },
                                label = {
                                    Text(
                                        text = tab.title,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.surface,
                                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                                    indicatorColor = MaterialTheme.colorScheme.onSurface,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    ContentForTab(
                        currentTab = currentTab,
                        viewModel = viewModel,
                        userName = userName,
                        allTasks = allTasks,
                        filteredTasks = filteredTasks,
                        activeFilter = activeFilter,
                        taskSearch = taskSearch,
                        selectedList = selectedList,
                        celebrateTrigger = celebrateTrigger,
                        allEvents = allEvents,
                        selectedDate = selectedDate,
                        calendarViewMode = calendarViewMode,
                        activeNotes = activeNotes,
                        filteredNotes = filteredNotes,
                        noteSearch = noteSearch,
                        selectedNoteToOpen = selectedNoteToOpen,
                        onSwitchTab = { tab -> currentTab = tab },
                        onOpenNote = { note ->
                            selectedNoteToOpen = note
                            currentTab = AppTab.NOTES
                        },
                        onOpenSettings = { showSettingsDialog = true },
                        onOpenAbout = { showAboutDialog = true }
                    )
                }
            }
        }
    }

    if (showSettingsDialog) {
        SettingsDialog(
            currentName = userName,
            currentTheme = themeMode,
            onSaveName = { viewModel.updateUserName(it) },
            onSelectTheme = { viewModel.setThemeMode(it) },
            onExportBackup = { onComplete -> viewModel.exportBackup(onComplete) },
            onImportBackup = { json, onResult -> viewModel.importBackup(json, onResult) },
            onOpenAbout = { showAboutDialog = true },
            onDismiss = { showSettingsDialog = false }
        )
    }

    if (showAboutDialog) {
        AboutDialog(onDismiss = { showAboutDialog = false })
    }
}

@Composable
fun ContentForTab(
    currentTab: AppTab,
    viewModel: ProductivityViewModel,
    userName: String,
    allTasks: List<com.example.data.model.TaskEntity>,
    filteredTasks: List<com.example.data.model.TaskEntity>,
    activeFilter: com.example.data.model.TaskFilter,
    taskSearch: String,
    selectedList: String,
    celebrateTrigger: Long,
    allEvents: List<com.example.data.model.CalendarEventEntity>,
    selectedDate: Long,
    calendarViewMode: com.example.ui.viewmodel.CalendarViewMode,
    activeNotes: List<com.example.data.model.NoteEntity>,
    filteredNotes: List<com.example.data.model.NoteEntity>,
    noteSearch: String,
    selectedNoteToOpen: NoteEntity?,
    onSwitchTab: (AppTab) -> Unit,
    onOpenNote: (NoteEntity) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAbout: () -> Unit
) {
    when (currentTab) {
        AppTab.DASHBOARD -> {
            val todayTasks = remember(allTasks) {
                allTasks.filter { !it.isCompleted }
            }
            DashboardScreen(
                greeting = viewModel.getGreeting(userName),
                currentDate = viewModel.getFormattedCurrentDate(),
                todayTasks = todayTasks,
                upcomingTasks = allTasks.filter { !it.isCompleted },
                recentEvents = allEvents,
                recentNotes = activeNotes,
                celebrateTrigger = celebrateTrigger,
                onToggleTask = { viewModel.toggleTaskCompleted(it) },
                onQuickAddTask = { title ->
                    viewModel.addTask(
                        title = title,
                        dueDateMillis = System.currentTimeMillis() + 86400000L
                    )
                },
                onNavigateToTasks = { onSwitchTab(AppTab.TASKS) },
                onNavigateToCalendar = { onSwitchTab(AppTab.CALENDAR) },
                onNavigateToNotes = { onSwitchTab(AppTab.NOTES) },
                onOpenNote = onOpenNote,
                onOpenSettings = onOpenSettings,
                onOpenAbout = onOpenAbout
            )
        }

        AppTab.TASKS -> {
            TasksScreen(
                tasks = filteredTasks,
                activeFilter = activeFilter,
                searchQuery = taskSearch,
                selectedList = selectedList,
                onFilterChange = { viewModel.taskFilter.value = it },
                onSearchChange = { viewModel.taskSearchQuery.value = it },
                onListChange = { viewModel.selectedTaskList.value = it },
                onToggleTask = { viewModel.toggleTaskCompleted(it) },
                onAddTask = { title, desc, due, prio, listTag, subs ->
                    viewModel.addTask(title, desc, due, prio, listTag, subs)
                },
                onUpdateTask = { viewModel.updateTask(it) },
                onDeleteTask = { viewModel.deleteTask(it) }
            )
        }

        AppTab.CALENDAR -> {
            CalendarScreen(
                events = allEvents,
                tasks = allTasks,
                selectedDateMillis = selectedDate,
                viewMode = calendarViewMode,
                onDateSelected = { viewModel.selectedDateMillis.value = it },
                onViewModeChanged = { viewModel.calendarViewMode.value = it },
                onAddEvent = { title, desc, start, end, isAllDay ->
                    viewModel.addEvent(title, desc, start, end, isAllDay)
                },
                onUpdateEvent = { viewModel.updateEvent(it) },
                onDeleteEvent = { viewModel.deleteEvent(it) }
            )
        }

        AppTab.NOTES -> {
            NotesScreen(
                notes = filteredNotes,
                searchQuery = noteSearch,
                onSearchChange = { viewModel.noteSearchQuery.value = it },
                onSaveNote = { id, title, content, isPinned, folder ->
                    viewModel.saveNote(id, title, content, isPinned, folder)
                },
                onTogglePin = { viewModel.toggleNotePinned(it) },
                onDeleteNote = { viewModel.deleteNotePermanently(it) },
                selectedNoteToOpen = selectedNoteToOpen
            )
        }
    }
}

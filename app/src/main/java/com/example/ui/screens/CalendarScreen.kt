package com.example.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CalendarEventEntity
import com.example.data.model.TaskEntity
import com.example.ui.components.CopyrightFooter
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.AccentRed
import com.example.ui.theme.TagGreenBg
import com.example.ui.theme.TagPurpleBg
import com.example.ui.viewmodel.CalendarViewMode
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CalendarScreen(
    events: List<CalendarEventEntity>,
    tasks: List<TaskEntity>,
    selectedDateMillis: Long,
    viewMode: CalendarViewMode,
    onDateSelected: (Long) -> Unit,
    onViewModeChanged: (CalendarViewMode) -> Unit,
    onAddEvent: (String, String, Long, Long, Boolean) -> Unit,
    onUpdateEvent: (CalendarEventEntity) -> Unit,
    onDeleteEvent: (CalendarEventEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentMonthCalendar by remember {
        mutableStateOf(Calendar.getInstance().apply { timeInMillis = selectedDateMillis })
    }

    var showCreateDialog by remember { mutableStateOf(false) }
    var eventToEdit by remember { mutableStateOf<CalendarEventEntity?>(null) }

    val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    val dayHeaderFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())

    val surfaceColor = MaterialTheme.colorScheme.surface
    val outlineColor = MaterialTheme.colorScheme.outline
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    // Calculate days for the month view
    val daysInMonth = remember(currentMonthCalendar.get(Calendar.MONTH), currentMonthCalendar.get(Calendar.YEAR)) {
        val cal = currentMonthCalendar.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1=Sun, 2=Mon...
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        val daysList = mutableListOf<CalendarDay?>()
        // Fill offset days
        for (i in 1 until firstDayOfWeek) {
            daysList.add(null)
        }
        for (day in 1..maxDays) {
            val dayCal = cal.clone() as Calendar
            dayCal.set(Calendar.DAY_OF_MONTH, day)
            daysList.add(
                CalendarDay(
                    dayOfMonth = day,
                    epochMillis = dayCal.timeInMillis
                )
            )
        }
        daysList
    }

    // Filter events and tasks for the currently selected date
    val selectedCal = Calendar.getInstance().apply { timeInMillis = selectedDateMillis }
    val selYear = selectedCal.get(Calendar.YEAR)
    val selDayOfYear = selectedCal.get(Calendar.DAY_OF_YEAR)

    val dateEvents = events.filter {
        val c = Calendar.getInstance().apply { timeInMillis = it.startEpochMillis }
        c.get(Calendar.YEAR) == selYear && c.get(Calendar.DAY_OF_YEAR) == selDayOfYear
    }

    val dateTasks = tasks.filter {
        it.dueDateEpochMillis?.let { due ->
            val c = Calendar.getInstance().apply { timeInMillis = due }
            c.get(Calendar.YEAR) == selYear && c.get(Calendar.DAY_OF_YEAR) == selDayOfYear
        } ?: false
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
            .testTag("calendar_screen")
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title and "Today" button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Calendar",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.SemiBold,
                color = onSurface
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Today Button
                Button(
                    onClick = {
                        val today = System.currentTimeMillis()
                        currentMonthCalendar = Calendar.getInstance().apply { timeInMillis = today }
                        onDateSelected(today)
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = surfaceColor,
                        contentColor = onSurface
                    ),
                    modifier = Modifier
                        .border(1.dp, outlineColor, RoundedCornerShape(10.dp))
                        .testTag("calendar_today_button")
                ) {
                    Text("Today", style = MaterialTheme.typography.labelMedium)
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { showCreateDialog = true },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(onSurface)
                        .testTag("calendar_add_event_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Event",
                        tint = MaterialTheme.colorScheme.surface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // View Mode Switcher: Month / Week / Day
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                Pair(CalendarViewMode.MONTH, "Month"),
                Pair(CalendarViewMode.WEEK, "Week"),
                Pair(CalendarViewMode.DAY, "Day")
            ).forEach { (mode, label) ->
                val isSelected = viewMode == mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) onSurface else surfaceColor)
                        .border(1.dp, if (isSelected) onSurface else outlineColor, RoundedCornerShape(10.dp))
                        .clickable { onViewModeChanged(mode) }
                        .padding(vertical = 8.dp),
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

        Spacer(modifier = Modifier.height(14.dp))

        // Month Navigation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = monthYearFormat.format(currentMonthCalendar.time),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = onSurface
            )

            Row {
                IconButton(
                    onClick = {
                        val cal = currentMonthCalendar.clone() as Calendar
                        cal.add(Calendar.MONTH, -1)
                        currentMonthCalendar = cal
                    }
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Prev Month", tint = onSurfaceVariant)
                }
                IconButton(
                    onClick = {
                        val cal = currentMonthCalendar.clone() as Calendar
                        cal.add(Calendar.MONTH, 1)
                        currentMonthCalendar = cal
                    }
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month", tint = onSurfaceVariant)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Clean Days-of-Week Row
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach { dayName ->
                Text(
                    text = dayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Notion-style Calendar Grid
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(surfaceColor)
                .border(1.dp, outlineColor, RoundedCornerShape(14.dp))
                .padding(10.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            ) {
                items(daysInMonth) { day ->
                    if (day == null) {
                        Box(modifier = Modifier.aspectRatio(1f))
                    } else {
                        val isSelected = isSameDay(day.epochMillis, selectedDateMillis)
                        val isToday = isSameDay(day.epochMillis, System.currentTimeMillis())

                        // Check if this date has events or tasks
                        val hasEvents = events.any { isSameDay(it.startEpochMillis, day.epochMillis) }
                        val hasTasks = tasks.any { t -> t.dueDateEpochMillis?.let { isSameDay(it, day.epochMillis) } ?: false }

                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when {
                                        isSelected -> onSurface
                                        isToday -> MaterialTheme.colorScheme.surfaceVariant
                                        else -> Color.Transparent
                                    }
                                )
                                .clickable { onDateSelected(day.epochMillis) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = day.dayOfMonth.toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.surface else onSurface
                                )
                                if (hasEvents || hasTasks) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                        if (hasEvents) {
                                            Box(
                                                modifier = Modifier
                                                    .size(4.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isSelected) MaterialTheme.colorScheme.surface else AccentPurple)
                                            )
                                        }
                                        if (hasTasks) {
                                            Box(
                                                modifier = Modifier
                                                    .size(4.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isSelected) MaterialTheme.colorScheme.surface else AccentGreen)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selected Date Agenda Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = dayHeaderFormat.format(Date(selectedDateMillis)),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = onSurface
            )
            Text(
                text = "${dateEvents.size} events, ${dateTasks.size} tasks",
                style = MaterialTheme.typography.labelSmall,
                color = onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Agenda Items
        if (dateEvents.isEmpty() && dateTasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No events or scheduled tasks on this date.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Scheduled Events
                items(dateEvents, key = { "evt_${it.id}" }) { event ->
                    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                    val timeText = "${timeFormat.format(Date(event.startEpochMillis))} – ${timeFormat.format(Date(event.endEpochMillis))}"

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(surfaceColor)
                            .border(1.dp, outlineColor, RoundedCornerShape(12.dp))
                            .clickable { eventToEdit = event }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp, 36.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(AccentPurple)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = event.title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = onSurface
                            )
                            Text(
                                text = timeText,
                                style = MaterialTheme.typography.labelSmall,
                                color = onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Scheduled Tasks due today
                items(dateTasks, key = { "task_cal_${it.id}" }) { task ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(surfaceColor)
                            .border(1.dp, outlineColor, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp, 36.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(AccentGreen)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "[Task] ${task.title}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = onSurface
                            )
                            Text(
                                text = "Due on this date",
                                style = MaterialTheme.typography.labelSmall,
                                color = onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    CopyrightFooter()
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // Event Create Dialog
    if (showCreateDialog) {
        EventEditDialog(
            initialDateMillis = selectedDateMillis,
            event = null,
            onDismiss = { showCreateDialog = false },
            onSave = { title, desc, start, end, isAllDay ->
                onAddEvent(title, desc, start, end, isAllDay)
                showCreateDialog = false
            },
            onDelete = null
        )
    }

    // Event Edit Dialog
    eventToEdit?.let { event ->
        EventEditDialog(
            initialDateMillis = event.startEpochMillis,
            event = event,
            onDismiss = { eventToEdit = null },
            onSave = { title, desc, start, end, isAllDay ->
                onUpdateEvent(
                    event.copy(
                        title = title,
                        description = desc,
                        startEpochMillis = start,
                        endEpochMillis = end,
                        isAllDay = isAllDay
                    )
                )
                eventToEdit = null
            },
            onDelete = {
                onDeleteEvent(event)
                eventToEdit = null
            }
        )
    }
}

data class CalendarDay(
    val dayOfMonth: Int,
    val epochMillis: Long
)

fun isSameDay(t1: Long, t2: Long): Boolean {
    val c1 = Calendar.getInstance().apply { timeInMillis = t1 }
    val c2 = Calendar.getInstance().apply { timeInMillis = t2 }
    return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
        c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
}

@Composable
fun EventEditDialog(
    initialDateMillis: Long,
    event: CalendarEventEntity?,
    onDismiss: () -> Unit,
    onSave: (String, String, Long, Long, Boolean) -> Unit,
    onDelete: (() -> Unit)?
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(event?.title ?: "") }
    var description by remember { mutableStateOf(event?.description ?: "") }
    var isAllDay by remember { mutableStateOf(event?.isAllDay ?: false) }

    var startMillis by remember {
        mutableLongStateOf(
            event?.startEpochMillis ?: Calendar.getInstance().apply {
                timeInMillis = initialDateMillis
                set(Calendar.HOUR_OF_DAY, 10)
                set(Calendar.MINUTE, 0)
            }.timeInMillis
        )
    }

    var endMillis by remember {
        mutableLongStateOf(
            event?.endEpochMillis ?: Calendar.getInstance().apply {
                timeInMillis = initialDateMillis
                set(Calendar.HOUR_OF_DAY, 11)
                set(Calendar.MINUTE, 0)
            }.timeInMillis
        )
    }

    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
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
            Column(modifier = Modifier.padding(22.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (event == null) "New Event" else "Edit Event",
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
                    placeholder = { Text("Event title") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Event details or location") },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Start Time Picker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Start: ${dateFormat.format(Date(startMillis))} at ${timeFormat.format(Date(startMillis))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(
                        onClick = {
                            val cal = Calendar.getInstance().apply { timeInMillis = startMillis }
                            TimePickerDialog(
                                context,
                                { _, h, m ->
                                    val newCal = Calendar.getInstance().apply {
                                        timeInMillis = startMillis
                                        set(Calendar.HOUR_OF_DAY, h)
                                        set(Calendar.MINUTE, m)
                                    }
                                    startMillis = newCal.timeInMillis
                                    if (endMillis <= startMillis) {
                                        endMillis = startMillis + 3600000L
                                    }
                                },
                                cal.get(Calendar.HOUR_OF_DAY),
                                cal.get(Calendar.MINUTE),
                                false
                            ).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Pick Time")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // End Time Picker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "End: ${timeFormat.format(Date(endMillis))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(
                        onClick = {
                            val cal = Calendar.getInstance().apply { timeInMillis = endMillis }
                            TimePickerDialog(
                                context,
                                { _, h, m ->
                                    val newCal = Calendar.getInstance().apply {
                                        timeInMillis = endMillis
                                        set(Calendar.HOUR_OF_DAY, h)
                                        set(Calendar.MINUTE, m)
                                    }
                                    endMillis = newCal.timeInMillis
                                },
                                cal.get(Calendar.HOUR_OF_DAY),
                                cal.get(Calendar.MINUTE),
                                false
                            ).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Pick Time")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (onDelete != null) {
                        IconButton(onClick = onDelete) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = AccentRed)
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
                                    onSave(title.trim(), description.trim(), startMillis, endMillis, isAllDay)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.onSurface,
                                contentColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}

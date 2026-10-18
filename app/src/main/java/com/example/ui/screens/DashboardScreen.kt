package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CalendarEventEntity
import com.example.data.model.NoteEntity
import com.example.data.model.Priority
import com.example.data.model.TaskEntity
import com.example.ui.components.CopyrightFooter
import com.example.ui.components.PixelSmileyCard
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentPink
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.AccentRed
import com.example.ui.theme.AccentYellow
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

@Composable
fun DashboardScreen(
    greeting: String,
    currentDate: String,
    todayTasks: List<TaskEntity>,
    upcomingTasks: List<TaskEntity>,
    recentEvents: List<CalendarEventEntity>,
    recentNotes: List<NoteEntity>,
    celebrateTrigger: Long,
    onToggleTask: (TaskEntity) -> Unit,
    onQuickAddTask: (String) -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToNotes: () -> Unit,
    onOpenNote: (NoteEntity) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var quickTaskText by remember { mutableStateOf("") }

    val surfaceColor = MaterialTheme.colorScheme.surface
    val outlineColor = MaterialTheme.colorScheme.outline
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
            .testTag("dashboard_screen")
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            // Top Header: Greeting, Date & Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(AccentGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentDate,
                            style = MaterialTheme.typography.labelMedium,
                            color = onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = onSurface,
                        letterSpacing = (-0.5).sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onOpenAbout,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(TagPurpleBg)
                            .border(1.dp, TagPurpleText.copy(alpha = 0.2f), CircleShape)
                            .testTag("dashboard_about_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "About",
                            tint = TagPurpleText,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(surfaceColor)
                            .border(1.dp, outlineColor, CircleShape)
                            .testTag("dashboard_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Signature Feature: Interactive Pixelated Smiley Card
            PixelSmileyCard(celebrateTrigger = celebrateTrigger)

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Add Task Input
            OutlinedTextField(
                value = quickTaskText,
                onValueChange = { quickTaskText = it },
                placeholder = {
                    Text(
                        text = "Quick add a task for today...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add task",
                        tint = AccentBlue
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (quickTaskText.isNotBlank()) {
                            onQuickAddTask(quickTaskText.trim())
                            quickTaskText = ""
                        }
                    }
                ),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = surfaceColor,
                    unfocusedContainerColor = surfaceColor,
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = outlineColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_quick_add_input")
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Section: Today's Tasks
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Today's Tasks",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(TagBlueBg)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${todayTasks.size}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = TagBlueText
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onNavigateToTasks() }
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "View all",
                        style = MaterialTheme.typography.labelMedium,
                        color = AccentBlue,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = AccentBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        // Today Tasks list
        if (todayTasks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(surfaceColor)
                        .border(1.dp, outlineColor, RoundedCornerShape(14.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "All clear for today. Enjoy the calm.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = onSurfaceVariant
                    )
                }
            }
        } else {
            items(todayTasks.take(5), key = { "today_${it.id}" }) { task ->
                TaskRowItem(
                    task = task,
                    onToggle = { onToggleTask(task) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Section: Notion Calendar Preview
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Calendar & Agenda",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(TagPurpleBg)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${recentEvents.size}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = TagPurpleText
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onNavigateToCalendar() }
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Open calendar",
                        style = MaterialTheme.typography.labelMedium,
                        color = AccentPurple,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = AccentPurple
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            if (recentEvents.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(surfaceColor)
                        .border(1.dp, outlineColor, RoundedCornerShape(14.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No upcoming events scheduled.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = onSurfaceVariant
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(surfaceColor)
                        .border(1.dp, outlineColor, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    recentEvents.take(3).forEachIndexed { idx, event ->
                        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                        val timeRange = "${timeFormat.format(Date(event.startEpochMillis))} - ${timeFormat.format(Date(event.endEpochMillis))}"
                        val stripColor = when (idx % 3) {
                            0 -> AccentBlue
                            1 -> AccentPurple
                            else -> AccentGreen
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(4.dp, 32.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(stripColor)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = event.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = onSurface
                                )
                                Text(
                                    text = timeRange,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Section: Recent Notes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Recent Notes",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(TagAmberBg)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${recentNotes.size}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = TagAmberText
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onNavigateToNotes() }
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "All notes",
                        style = MaterialTheme.typography.labelMedium,
                        color = AccentOrange,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = AccentOrange
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Recent Notes Horizontal Carousel with colorful top strip
        if (recentNotes.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(surfaceColor)
                        .border(1.dp, outlineColor, RoundedCornerShape(14.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No notes written yet. Jot down an idea.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = onSurfaceVariant
                    )
                }
            }
        } else {
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(recentNotes.take(4), key = { "note_${it.id}" }) { note ->
                        val noteColor = if (note.isPinned) AccentYellow else AccentBlue
                        Box(
                            modifier = Modifier
                                .width(190.dp)
                                .height(125.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(surfaceColor)
                                .border(1.dp, outlineColor, RoundedCornerShape(14.dp))
                                .clickable { onOpenNote(note) }
                        ) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                // Top accent line
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(3.dp)
                                        .background(noteColor)
                                )
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = note.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = onSurface,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (note.isPinned) {
                                            Icon(
                                                imageVector = Icons.Default.PushPin,
                                                contentDescription = "Pinned",
                                                modifier = Modifier.size(14.dp),
                                                tint = AccentOrange
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = note.content,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = onSurfaceVariant,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Mandatory Copyright Footer
        item {
            Spacer(modifier = Modifier.height(36.dp))
            CopyrightFooter()
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun TaskRowItem(
    task: TaskEntity,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val outlineColor = MaterialTheme.colorScheme.outline
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    val (tagBg, tagText) = when (task.listName.lowercase()) {
        "work" -> Pair(TagBlueBg, TagBlueText)
        "ideas" -> Pair(TagAmberBg, TagAmberText)
        else -> Pair(TagPurpleBg, TagPurpleText)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(surfaceColor)
            .border(1.dp, outlineColor, RoundedCornerShape(12.dp))
            .clickable { onToggle() }
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .testTag("task_item_${task.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Apple Reminders Check Circle with vibrant completion green
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .border(
                    1.5.dp,
                    if (task.isCompleted) AccentGreen else onSurfaceVariant.copy(alpha = 0.5f),
                    CircleShape
                )
                .background(if (task.isCompleted) AccentGreen else Color.Transparent),
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
                color = if (task.isCompleted) onSurfaceVariant else onSurface,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (task.description.isNotBlank()) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = onSurfaceVariant.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Colorful List Badge Tag
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(tagBg)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = task.listName,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                fontWeight = FontWeight.SemiBold,
                color = tagText
            )
        }

        // Priority tag dot if High or Medium
        if (task.priority == Priority.HIGH) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(AccentRed)
            )
        } else if (task.priority == Priority.MEDIUM) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(AccentOrange)
            )
        }
    }
}

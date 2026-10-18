package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NoteEntity
import com.example.ui.components.CopyrightFooter
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentOrange
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
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotesScreen(
    notes: List<NoteEntity>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSaveNote: (Long, String, String, Boolean, String) -> Unit,
    onTogglePin: (NoteEntity) -> Unit,
    onDeleteNote: (NoteEntity) -> Unit,
    selectedNoteToOpen: NoteEntity? = null,
    modifier: Modifier = Modifier
) {
    var activeNoteId by remember { mutableStateOf<Long?>(selectedNoteToOpen?.id) }
    var isCreatingNew by remember { mutableStateOf(false) }

    LaunchedEffect(selectedNoteToOpen) {
        if (selectedNoteToOpen != null) {
            activeNoteId = selectedNoteToOpen.id
            isCreatingNew = false
        }
    }

    val activeNote = remember(activeNoteId, notes) {
        notes.find { it.id == activeNoteId }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("notes_screen")
    ) {
        val isTabletLandscape = maxWidth > 680.dp

        if (isTabletLandscape) {
            // Dual Pane Side-by-Side layout
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .width(320.dp)
                        .fillMaxHeight()
                        .border(width = 1.dp, color = MaterialTheme.colorScheme.outline)
                ) {
                    NotesListView(
                        notes = notes,
                        searchQuery = searchQuery,
                        selectedNoteId = activeNoteId,
                        onSearchChange = onSearchChange,
                        onSelectNote = {
                            activeNoteId = it.id
                            isCreatingNew = false
                        },
                        onNewNote = {
                            activeNoteId = null
                            isCreatingNew = true
                        }
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    if (isCreatingNew) {
                        NoteEditorView(
                            note = null,
                            onSave = { title, content, isPinned, folder ->
                                onSaveNote(0L, title, content, isPinned, folder)
                                isCreatingNew = false
                            },
                            onDelete = null,
                            onBack = { isCreatingNew = false }
                        )
                    } else if (activeNote != null) {
                        NoteEditorView(
                            note = activeNote,
                            onSave = { title, content, isPinned, folder ->
                                onSaveNote(activeNote.id, title, content, isPinned, folder)
                            },
                            onDelete = {
                                onDeleteNote(activeNote)
                                activeNoteId = null
                            },
                            onBack = { activeNoteId = null }
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Select a note to read or create a new one.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        } else {
            // Mobile Single-Pane Navigation with BackHandler
            if (isCreatingNew) {
                BackHandler { isCreatingNew = false }
                NoteEditorView(
                    note = null,
                    onSave = { title, content, isPinned, folder ->
                        onSaveNote(0L, title, content, isPinned, folder)
                        isCreatingNew = false
                    },
                    onDelete = null,
                    onBack = { isCreatingNew = false }
                )
            } else if (activeNote != null) {
                BackHandler { activeNoteId = null }
                NoteEditorView(
                    note = activeNote,
                    onSave = { title, content, isPinned, folder ->
                        onSaveNote(activeNote.id, title, content, isPinned, folder)
                    },
                    onDelete = {
                        onDeleteNote(activeNote)
                        activeNoteId = null
                    },
                    onBack = { activeNoteId = null }
                )
            } else {
                NotesListView(
                    notes = notes,
                    searchQuery = searchQuery,
                    selectedNoteId = null,
                    onSearchChange = onSearchChange,
                    onSelectNote = {
                        activeNoteId = it.id
                    },
                    onNewNote = {
                        isCreatingNew = true
                    }
                )
            }
        }
    }
}

@Composable
fun NotesListView(
    notes: List<NoteEntity>,
    searchQuery: String,
    selectedNoteId: Long?,
    onSearchChange: (String) -> Unit,
    onSelectNote: (NoteEntity) -> Unit,
    onNewNote: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pinnedNotes = remember(notes) { notes.filter { it.isPinned } }
    val regularNotes = remember(notes) { notes.filter { !it.isPinned } }

    val surfaceColor = MaterialTheme.colorScheme.surface
    val outlineColor = MaterialTheme.colorScheme.outline
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title and Create Note
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Notes",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.SemiBold,
                color = onSurface
            )

            IconButton(
                onClick = onNewNote,
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(onSurface)
                    .testTag("notes_new_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Note",
                    tint = MaterialTheme.colorScheme.surface
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search all notes...", style = MaterialTheme.typography.bodyMedium) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = onSurfaceVariant) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = onSurfaceVariant)
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
                .testTag("notes_search_input")
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (notes.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No notes yet. Tap + to create one.",
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
                if (pinnedNotes.isNotEmpty()) {
                    item {
                        Text(
                            text = "PINNED",
                            style = MaterialTheme.typography.labelSmall,
                            color = onSurfaceVariant,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    items(pinnedNotes, key = { "pinned_${it.id}" }) { note ->
                        NoteCardItem(
                            note = note,
                            isSelected = note.id == selectedNoteId,
                            onClick = { onSelectNote(note) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    item { Spacer(modifier = Modifier.height(10.dp)) }
                }

                if (regularNotes.isNotEmpty()) {
                    if (pinnedNotes.isNotEmpty()) {
                        item {
                            Text(
                                text = "NOTES",
                                style = MaterialTheme.typography.labelSmall,
                                color = onSurfaceVariant,
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                    items(regularNotes, key = { "note_${it.id}" }) { note ->
                        NoteCardItem(
                            note = note,
                            isSelected = note.id == selectedNoteId,
                            onClick = { onSelectNote(note) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    CopyrightFooter()
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun NoteCardItem(
    note: NoteEntity,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(note.updatedAt))

    val surfaceColor = MaterialTheme.colorScheme.surface
    val outlineColor = MaterialTheme.colorScheme.outline
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.surfaceVariant else surfaceColor)
            .border(
                1.dp,
                if (isSelected) onSurface else outlineColor,
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(14.dp)
            .testTag("note_item_${note.id}")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (note.isPinned) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(TagAmberBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = "Pinned",
                                modifier = Modifier.size(12.dp),
                                tint = AccentOrange
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "PINNED",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                fontWeight = FontWeight.Bold,
                                color = TagAmberText
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = note.content.ifBlank { "Empty note" },
                style = MaterialTheme.typography.bodySmall,
                color = onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = formattedDate,
                style = MaterialTheme.typography.labelSmall,
                color = onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

/**
 * Spacious Apple Notes Writing Canvas with auto-saving as user types
 */
@Composable
fun NoteEditorView(
    note: NoteEntity?,
    onSave: (String, String, Boolean, String) -> Unit,
    onDelete: (() -> Unit)?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember(note?.id) { mutableStateOf(note?.title ?: "") }
    var content by remember(note?.id) { mutableStateOf(note?.content ?: "") }
    var isPinned by remember(note?.id) { mutableStateOf(note?.isPinned ?: false) }
    var lastSavedText by remember { mutableStateOf("All changes saved") }

    val dateFormat = SimpleDateFormat("MMMM d, yyyy 'at' h:mm a", Locale.getDefault())

    // Live auto-save on typing with 500ms debounce
    LaunchedEffect(title, content, isPinned) {
        if (title.isNotBlank() || content.isNotBlank()) {
            delay(500)
            onSave(title, content, isPinned, "Notes")
            lastSavedText = "Saved at " + SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        }
    }

    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 22.dp)
            .testTag("note_editor_view")
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top Navigation Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("note_editor_back_button")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = onSurfaceVariant
                )
            }

            Text(
                text = lastSavedText,
                style = MaterialTheme.typography.labelSmall,
                color = onSurfaceVariant.copy(alpha = 0.6f)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { isPinned = !isPinned }) {
                    Icon(
                        imageVector = if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                        contentDescription = "Pin Note",
                        tint = if (isPinned) onSurface else onSurfaceVariant
                    )
                }

                if (onDelete != null) {
                    IconButton(onClick = onDelete, modifier = Modifier.testTag("note_delete_button")) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = AccentRed
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Formatting Quick-Bar (Notion / Apple style)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                FormatChip(
                    icon = Icons.Default.Title,
                    label = "H1",
                    onClick = { content += "\n# " },
                    containerColor = TagPurpleBg,
                    iconColor = TagPurpleText,
                    textColor = TagPurpleText
                )
            }
            item {
                FormatChip(
                    icon = Icons.Default.Title,
                    label = "H2",
                    onClick = { content += "\n## " },
                    containerColor = TagBlueBg,
                    iconColor = TagBlueText,
                    textColor = TagBlueText
                )
            }
            item {
                FormatChip(
                    icon = Icons.Default.Checklist,
                    label = "Checklist",
                    onClick = { content += "\n- [ ] " },
                    containerColor = TagGreenBg,
                    iconColor = TagGreenText,
                    textColor = TagGreenText
                )
            }
            item {
                FormatChip(
                    icon = Icons.AutoMirrored.Filled.FormatListBulleted,
                    label = "Bullet",
                    onClick = { content += "\n- " },
                    containerColor = TagAmberBg,
                    iconColor = TagAmberText,
                    textColor = TagAmberText
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Note Title Input
        BasicTextField(
            value = title,
            onValueChange = { title = it },
            textStyle = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = onSurface
            ),
            cursorBrush = SolidColor(onSurface),
            decorationBox = { innerTextField ->
                if (title.isEmpty()) {
                    Text(
                        text = "Title",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = onSurfaceVariant.copy(alpha = 0.4f)
                        )
                    )
                }
                innerTextField()
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("note_title_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Last edited timestamp
        val editTimestamp = note?.updatedAt ?: System.currentTimeMillis()
        Text(
            text = dateFormat.format(Date(editTimestamp)),
            style = MaterialTheme.typography.labelSmall,
            color = onSurfaceVariant.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Note Body Canvas
        BasicTextField(
            value = content,
            onValueChange = { content = it },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                lineHeight = 24.sp,
                color = onSurface
            ),
            cursorBrush = SolidColor(onSurface),
            decorationBox = { innerTextField ->
                if (content.isEmpty()) {
                    Text(
                        text = "Start typing your thoughts, checklists, or notes...",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 24.sp,
                            color = onSurfaceVariant.copy(alpha = 0.4f)
                        )
                    )
                }
                innerTextField()
            },
            modifier = Modifier
                .fillMaxSize()
                .testTag("note_content_input")
        )
    }
}

@Composable
fun FormatChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    iconColor: Color = MaterialTheme.colorScheme.onSurface,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calendar_events")
data class CalendarEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val startEpochMillis: Long,
    val endEpochMillis: Long,
    val isAllDay: Boolean = false,
    val colorTag: String = "DEFAULT",
    val recurrence: String = "NONE", // NONE, DAILY, WEEKLY, MONTHLY
    val createdAt: Long = System.currentTimeMillis()
)

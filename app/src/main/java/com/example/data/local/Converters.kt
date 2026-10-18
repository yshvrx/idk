package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.Priority

class Converters {
    @TypeConverter
    fun fromPriority(priority: Priority?): String {
        return priority?.name ?: Priority.LOW.name
    }

    @TypeConverter
    fun toPriority(value: String?): Priority {
        return try {
            if (value != null) Priority.valueOf(value) else Priority.LOW
        } catch (_: Exception) {
            Priority.LOW
        }
    }
}

package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONArray
import org.json.JSONObject

enum class Priority {
    LOW,
    MEDIUM,
    HIGH
}

data class SubTask(
    val id: String,
    val title: String,
    val isCompleted: Boolean = false
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("title", title)
        put("isCompleted", isCompleted)
    }

    companion object {
        fun fromJson(obj: JSONObject): SubTask = SubTask(
            id = obj.optString("id", System.currentTimeMillis().toString()),
            title = obj.optString("title", ""),
            isCompleted = obj.optBoolean("isCompleted", false)
        )
    }
}

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val dueDateEpochMillis: Long? = null,
    val priority: Priority = Priority.LOW,
    val listName: String = "Personal",
    val subtasksJson: String = "[]",
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
) {
    fun getSubtasks(): List<SubTask> {
        val list = mutableListOf<SubTask>()
        try {
            val array = JSONArray(subtasksJson)
            for (i in 0 until array.length()) {
                list.add(SubTask.fromJson(array.getJSONObject(i)))
            }
        } catch (_: Exception) { }
        return list
    }

    companion object {
        fun serializeSubtasks(subtasks: List<SubTask>): String {
            val array = JSONArray()
            subtasks.forEach { array.put(it.toJson()) }
            return array.toString()
        }
    }
}

enum class TaskFilter {
    ALL,
    TODAY,
    UPCOMING,
    COMPLETED
}

package com.todo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Task entity class representing a to-do item in the database.
 *
 * @property id Unique identifier (auto-generated)
 * @property title Task title
 * @property description Detailed description of the task
 * @property category Task category (Work, Personal, Shopping, Health, Finance, Other)
 * @property priority Task priority level (High, Medium, Low)
 * @property dueDate Optional deadline for the task
 * @property isCompleted Whether the task is completed
 * @property createdAt Timestamp when task was created
 * @property updatedAt Timestamp when task was last updated
 */
@Entity(tableName = "task")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val category: String = TaskCategory.OTHER.value,
    val priority: String = TaskPriority.MEDIUM.value,
    val dueDate: String? = null,
    val isCompleted: Boolean = false,
    val createdAt: String = getCurrentTimestamp(),
    val updatedAt: String = getCurrentTimestamp()
)

/**
 * Enum class for task categories.
 */
enum class TaskCategory(val value: String) {
    WORK("Work"),
    PERSONAL("Personal"),
    SHOPPING("Shopping"),
    HEALTH("Health"),
    FINANCE("Finance"),
    OTHER("Other");

    companion object {
        fun fromValue(value: String): TaskCategory {
            return values().find { it.value == value } ?: OTHER
        }
    }
}

/**
 * Enum class for task priority levels.
 */
enum class TaskPriority(val value: String) {
    HIGH("High"),
    MEDIUM("Medium"),
    LOW("Low");

    companion object {
        fun fromValue(value: String): TaskPriority {
            return values().find { it.value == value } ?: MEDIUM
        }
    }
}

/**
 * Data class for task statistics.
 */
data class TaskStats(
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val pendingTasks: Int = 0,
    val completionPercentage: Float = 0f,
    val tasksByCategory: Map<String, Int> = emptyMap(),
    val tasksByPriority: Map<String, Int> = emptyMap()
) {
    val completionPercentageString: String
        get() = String.format("%.1f%%", completionPercentage)
}

/**
 * Get current timestamp in ISO 8601 format.
 */
fun getCurrentTimestamp(): String {
    return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
}

/**
 * Format timestamp string to readable format.
 */
fun formatTimestamp(timestamp: String): String {
    return try {
        val dateTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
    } catch (e: Exception) {
        timestamp
    }
}

/**
 * Check if task is overdue.
 */
fun Task.isOverdue(): Boolean {
    return dueDate != null && !isCompleted && try {
        val dueDateParsed = LocalDateTime.parse(dueDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        LocalDateTime.now().isAfter(dueDateParsed)
    } catch (e: Exception) {
        false
    }
}

/**
 * Get days until due date.
 */
fun Task.daysUntilDue(): Long {
    return dueDate?.let {
        try {
            val dueDateParsed = LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            java.time.temporal.ChronoUnit.DAYS.between(LocalDateTime.now(), dueDateParsed)
        } catch (e: Exception) {
            -1
        }
    } ?: -1
}

package de.hhn.myapplication.database.dataclass

/**
 * Data class representing a ToDo task.
 */
data class ToDoDataClass (
        val id: Int = 0,
        val name: String,
        val priority: String,
        val endDate: String,
        val description: String,
        val status: Int
)
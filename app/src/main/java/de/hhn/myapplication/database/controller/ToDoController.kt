package de.hhn.myapplication.database.controller

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.util.Log
import de.hhn.myapplication.database.DbHelper
import de.hhn.myapplication.database.dataclass.ToDoDataClass

/**
 * Controller class to manage CRUD operations for ToDo tasks in the database.
 */
class ToDoController(context: Context) {
    private val dbHelper = DbHelper(context)

    /**
     * Inserts a new ToDo task into the database.
     */
    fun insertToDo(task: ToDoDataClass): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            val priorityId = getPriorityId( task.priority)
            if (priorityId == -1) {
                Log.e("ToDoController", "Invalid priority: ${task.priority}")
                return false
            }

            val values = ContentValues().apply {
                put("name", task.name)
                put("priority", priorityId)
                put("endDate", task.endDate)
                put("description", task.description)
                put("status", task.status)
            }

            val result = db.insert("tasks", null, values)
            result != -1L
        }
        catch (e: Exception) {
            Log.e("ToDoController", "Insert failed", e)
            false
        } finally {
            db.close()
        }
    }

    /**
     * Updates an existing ToDo task in the database.
     */
    fun updateToDo(task: ToDoDataClass): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            val priorityId = getPriorityId(task.priority)
            if (priorityId == -1) {
                Log.e("ToDoController", "Invalid priority: ${task.priority}")
                return false
            }

            val values = ContentValues().apply {
                put("name", task.name)
                put("priority", priorityId)
                put("endDate", task.endDate)
                put("description", task.description)
                put("status", task.status)
            }

            val rowsUpdated = db.update(
                "tasks",
                values,
                "id = ?",
                arrayOf(task.id.toString())
            )

            Log.d("ToDoController", "Rows updated: $rowsUpdated for ToDo ID: ${task.id}")
            rowsUpdated > 0
        }
        catch (e: Exception) {
            Log.e("ToDoController", "Update failed", e)
            false
        }
        finally {
            db.close()
        }
    }

    /**
     * Toggles the status of a ToDo task between open (0) and closed (1).
     */
    fun toggleToDo(task: ToDoDataClass): Boolean {
        val db = dbHelper.writableDatabase
        val toggle = if (task.status == 1) 0 else 1
        return try {
            val priorityId = getPriorityId( task.priority)

            if (priorityId == -1) {
                Log.e("ToDoController", "Invalid priority: ${task.priority}")
                return false
            }
            val values = ContentValues().apply {
                put("name", task.name)
                put("priority", priorityId)
                put("endDate", task.endDate)
                put("description", task.description)
                put("status",toggle)
            }
            val result = db.update("tasks", values, "id = ?", arrayOf(task.id.toString()))
            Log.d("ToDoController", "Update result: $result, Student ID: ${task.id}")
            result > 0
        }
        catch (e: Exception) {
            Log.e("ToDoController", "Toggle failed", e)
            false
        }
        finally {
            db.close()
        }
    }

    /**
     * Deletes a ToDo task from the database.
     */
    fun deleteToDo(taskId: Int): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            val result = db.delete("tasks", "id = ?", arrayOf(taskId.toString()))
            result > 0
        }
        catch (e: Exception) {
            Log.e("ToDoController", "Delete failed", e)
            false
        }
        finally {
            db.close()
        }
    }

    /**
     * Retrieves all open ToDo tasks (status = 0) from the database.
     */
    fun getAllToDosOpen(): List<ToDoDataClass> {
        val db = dbHelper.readableDatabase
        val toDosOpen = mutableListOf<ToDoDataClass>()
        val cursor = db.rawQuery("SELECT t.*, p.level AS priority FROM tasks t INNER JOIN priorities p ON t.priority = p.id WHERE t.status = ? ORDER BY t.endDate, t.priority",
            arrayOf("0"))
        try {
            if (cursor.moveToFirst()) {
                do {
                    val task = ToDoDataClass(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        priority = cursor.getString(cursor.getColumnIndexOrThrow("priority")),
                        endDate = cursor.getString(cursor.getColumnIndexOrThrow("endDate")),
                        description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        status = cursor.getInt(cursor.getColumnIndexOrThrow("status")),
                    )
                    toDosOpen.add(task)
                } while (cursor.moveToNext())
            }
        }
        catch (e: Exception) {
            Log.e("ToDoController", "Fetching todos failed", e)
        }
        finally {
            cursor.close()
            db.close()
        }
        return toDosOpen
    }

    /**
     * Retrieves all closed ToDo tasks (status = 1) from the database.
     */
    fun getAllToDoClose(): List<ToDoDataClass> {
        val db = dbHelper.readableDatabase
        val toDosClose = mutableListOf<ToDoDataClass>()
        val cursor = db.rawQuery("SELECT t.*, p.level AS priority  FROM tasks t INNER JOIN priorities p ON t.priority = p.id WHERE t.status = ? ORDER BY t.endDate, t.priority",
            arrayOf("1"))
        try {
            if (cursor.moveToFirst()) {
                do {
                    val task = ToDoDataClass(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        priority = cursor.getString(cursor.getColumnIndexOrThrow("priority")),
                        endDate = cursor.getString(cursor.getColumnIndexOrThrow("endDate")),
                        description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        status = cursor.getInt(cursor.getColumnIndexOrThrow("status")),
                    )
                    toDosClose.add(task)
                } while (cursor.moveToNext())
            }
        }
        catch (e: Exception) {
            Log.e("ToDoController", "Fetching todos failed", e)
        }
        finally {
            cursor.close()
            db.close()
        }
        return toDosClose
    }

    /**
     * Retrieves the ID for a given priority level from the database.
     */
    @SuppressLint("Range")
    fun getPriorityId(priority: String): Int {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id FROM priorities WHERE level = ?", arrayOf(priority))
        var priorityId = -1

        if (cursor.moveToFirst()) {
            priorityId = cursor.getInt(cursor.getColumnIndex("id"))
        }
        cursor.close()
        Log.d("ToDoControllerId", priorityId.toString())
        return priorityId
    }
}

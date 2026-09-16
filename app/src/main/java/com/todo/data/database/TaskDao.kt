package com.todo.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.todo.data.model.Task

/**
 * Data Access Object (DAO) for Task entity.
 * Provides database operations for tasks.
 */
@Dao
interface TaskDao {

    /**
     * Insert a new task into the database.
     *
     * @param task The task to insert
     * @return The row ID of the inserted task
     */
    @Insert
    suspend fun insertTask(task: Task): Long

    /**
     * Update an existing task in the database.
     *
     * @param task The task to update
     */
    @Update
    suspend fun updateTask(task: Task)

    /**
     * Delete a task from the database.
     *
     * @param task The task to delete
     */
    @Delete
    suspend fun deleteTask(task: Task)

    /**
     * Get a task by its ID.
     *
     * @param id The task ID
     * @return LiveData containing the task, or null if not found
     */
    @Query("SELECT * FROM task WHERE id = :id")
    fun getTaskById(id: Int): LiveData<Task?>

    /**
     * Get all tasks ordered by creation date (newest first).
     *
     * @return LiveData containing list of all tasks
     */
    @Query("SELECT * FROM task ORDER BY createdAt DESC")
    fun getAllTasks(): LiveData<List<Task>>

    /**
     * Get all incomplete tasks ordered by due date.
     *
     * @return LiveData containing list of incomplete tasks
     */
    @Query("SELECT * FROM task WHERE isCompleted = 0 ORDER BY dueDate ASC, createdAt DESC")
    fun getIncompleteTasks(): LiveData<List<Task>>

    /**
     * Get all completed tasks.
     *
     * @return LiveData containing list of completed tasks
     */
    @Query("SELECT * FROM task WHERE isCompleted = 1 ORDER BY updatedAt DESC")
    fun getCompletedTasks(): LiveData<List<Task>>

    /**
     * Get tasks by category.
     *
     * @param category The task category
     * @return LiveData containing list of tasks in the category
     */
    @Query("SELECT * FROM task WHERE category = :category ORDER BY createdAt DESC")
    fun getTasksByCategory(category: String): LiveData<List<Task>>

    /**
     * Get tasks by priority.
     *
     * @param priority The task priority
     * @return LiveData containing list of tasks with the priority
     */
    @Query("SELECT * FROM task WHERE priority = :priority ORDER BY createdAt DESC")
    fun getTasksByPriority(priority: String): LiveData<List<Task>>

    /**
     * Get tasks by completion status.
     *
     * @param isCompleted Whether tasks should be completed or not
     * @return LiveData containing filtered tasks
     */
    @Query("SELECT * FROM task WHERE isCompleted = :isCompleted ORDER BY createdAt DESC")
    fun getTasksByCompletionStatus(isCompleted: Boolean): LiveData<List<Task>>

    /**
     * Search tasks by title or description.
     *
     * @param query The search query
     * @return LiveData containing matching tasks
     */
    @Query("""
        SELECT * FROM task 
        WHERE title LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    fun searchTasks(query: String): LiveData<List<Task>>

    /**
     * Search tasks by title, description, and category.
     *
     * @param query The search query
     * @param category The category to filter by
     * @return LiveData containing matching tasks
     */
    @Query("""
        SELECT * FROM task 
        WHERE (title LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%')
        AND category = :category
        ORDER BY createdAt DESC
    """)
    fun searchTasksByCategory(query: String, category: String): LiveData<List<Task>>

    /**
     * Get total task count.
     *
     * @return Total number of tasks
     */
    @Query("SELECT COUNT(*) FROM task")
    suspend fun getTotalTaskCount(): Int

    /**
     * Get completed task count.
     *
     * @return Number of completed tasks
     */
    @Query("SELECT COUNT(*) FROM task WHERE isCompleted = 1")
    suspend fun getCompletedTaskCount(): Int

    /**
     * Get pending task count.
     *
     * @return Number of incomplete tasks
     */
    @Query("SELECT COUNT(*) FROM task WHERE isCompleted = 0")
    suspend fun getPendingTaskCount(): Int

    /**
     * Get task count by category.
     *
     * @param category The category to count
     * @return Number of tasks in the category
     */
    @Query("SELECT COUNT(*) FROM task WHERE category = :category")
    suspend fun getTaskCountByCategory(category: String): Int

    /**
     * Get task count by priority.
     *
     * @param priority The priority to count
     * @return Number of tasks with the priority
     */
    @Query("SELECT COUNT(*) FROM task WHERE priority = :priority")
    suspend fun getTaskCountByPriority(priority: String): Int

    /**
     * Get all unique categories.
     *
     * @return List of unique category values
     */
    @Query("SELECT DISTINCT category FROM task ORDER BY category ASC")
    fun getAllCategories(): LiveData<List<String>>

    /**
     * Get all unique priorities.
     *
     * @return List of unique priority values
     */
    @Query("SELECT DISTINCT priority FROM task ORDER BY priority ASC")
    fun getAllPriorities(): LiveData<List<String>>

    /**
     * Mark a task as complete.
     *
     * @param id The task ID
     */
    @Query("UPDATE task SET isCompleted = 1, updatedAt = datetime('now') WHERE id = :id")
    suspend fun markTaskComplete(id: Int)

    /**
     * Mark a task as incomplete.
     *
     * @param id The task ID
     */
    @Query("UPDATE task SET isCompleted = 0, updatedAt = datetime('now') WHERE id = :id")
    suspend fun markTaskIncomplete(id: Int)

    /**
     * Delete all completed tasks.
     */
    @Query("DELETE FROM task WHERE isCompleted = 1")
    suspend fun deleteCompletedTasks()

    /**
     * Delete all tasks.
     */
    @Query("DELETE FROM task")
    suspend fun deleteAllTasks()

    /**
     * Get tasks due soon (within 7 days).
     *
     * @return LiveData containing tasks due soon
     */
    @Query("""
        SELECT * FROM task 
        WHERE isCompleted = 0 
        AND dueDate IS NOT NULL 
        AND dueDate <= datetime('now', '+7 days')
        ORDER BY dueDate ASC
    """)
    fun getTasksDueSoon(): LiveData<List<Task>>
}

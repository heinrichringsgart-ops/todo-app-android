package com.todo.data.repository

import androidx.lifecycle.LiveData
import com.todo.data.database.TaskDao
import com.todo.data.model.Task
import com.todo.data.model.TaskStats

/**
 * Repository for managing Task data operations.
 * Provides a clean API for data access to the rest of the application.
 *
 * @property taskDao The DAO for task database operations
 */
class TaskRepository(private val taskDao: TaskDao) {

    // ============ Insert Operations ============

    /**
     * Insert a new task into the database.
     *
     * @param task The task to insert
     */
    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    // ============ Update Operations ============

    /**
     * Update an existing task in the database.
     *
     * @param task The task to update
     */
    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    /**
     * Mark a task as complete.
     *
     * @param taskId The ID of the task to mark complete
     */
    suspend fun markTaskComplete(taskId: Int) {
        taskDao.markTaskComplete(taskId)
    }

    /**
     * Mark a task as incomplete.
     *
     * @param taskId The ID of the task to mark incomplete
     */
    suspend fun markTaskIncomplete(taskId: Int) {
        taskDao.markTaskIncomplete(taskId)
    }

    // ============ Delete Operations ============

    /**
     * Delete a task from the database.
     *
     * @param task The task to delete
     */
    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    /**
     * Delete all completed tasks.
     */
    suspend fun deleteCompletedTasks() {
        taskDao.deleteCompletedTasks()
    }

    /**
     * Delete all tasks from the database.
     */
    suspend fun deleteAllTasks() {
        taskDao.deleteAllTasks()
    }

    // ============ Read Operations - Single Task ============

    /**
     * Get a task by its ID.
     *
     * @param taskId The task ID
     * @return LiveData containing the task, or null if not found
     */
    fun getTaskById(taskId: Int): LiveData<Task?> {
        return taskDao.getTaskById(taskId)
    }

    // ============ Read Operations - All Tasks ============

    /**
     * Get all tasks.
     *
     * @return LiveData containing list of all tasks
     */
    fun getAllTasks(): LiveData<List<Task>> {
        return taskDao.getAllTasks()
    }

    /**
     * Get all incomplete tasks.
     *
     * @return LiveData containing list of incomplete tasks
     */
    fun getIncompleteTasks(): LiveData<List<Task>> {
        return taskDao.getIncompleteTasks()
    }

    /**
     * Get all completed tasks.
     *
     * @return LiveData containing list of completed tasks
     */
    fun getCompletedTasks(): LiveData<List<Task>> {
        return taskDao.getCompletedTasks()
    }

    // ============ Read Operations - Filtered ============

    /**
     * Get tasks by category.
     *
     * @param category The category to filter by
     * @return LiveData containing tasks in the category
     */
    fun getTasksByCategory(category: String): LiveData<List<Task>> {
        return taskDao.getTasksByCategory(category)
    }

    /**
     * Get tasks by priority.
     *
     * @param priority The priority to filter by
     * @return LiveData containing tasks with the priority
     */
    fun getTasksByPriority(priority: String): LiveData<List<Task>> {
        return taskDao.getTasksByPriority(priority)
    }

    /**
     * Get tasks by completion status.
     *
     * @param isCompleted Whether tasks should be completed or not
     * @return LiveData containing filtered tasks
     */
    fun getTasksByCompletionStatus(isCompleted: Boolean): LiveData<List<Task>> {
        return taskDao.getTasksByCompletionStatus(isCompleted)
    }

    /**
     * Get tasks due soon (within 7 days).
     *
     * @return LiveData containing tasks due soon
     */
    fun getTasksDueSoon(): LiveData<List<Task>> {
        return taskDao.getTasksDueSoon()
    }

    // ============ Read Operations - Search ============

    /**
     * Search tasks by title or description.
     *
     * @param query The search query
     * @return LiveData containing matching tasks
     */
    fun searchTasks(query: String): LiveData<List<Task>> {
        return if (query.isEmpty()) {
            getAllTasks()
        } else {
            taskDao.searchTasks(query)
        }
    }

    /**
     * Search tasks by title, description, and category.
     *
     * @param query The search query
     * @param category The category to filter by
     * @return LiveData containing matching tasks
     */
    fun searchTasksByCategory(query: String, category: String): LiveData<List<Task>> {
        return if (query.isEmpty()) {
            getTasksByCategory(category)
        } else {
            taskDao.searchTasksByCategory(query, category)
        }
    }

    // ============ Read Operations - Categories & Priorities ============

    /**
     * Get all unique categories used in tasks.
     *
     * @return LiveData containing list of categories
     */
    fun getAllCategories(): LiveData<List<String>> {
        return taskDao.getAllCategories()
    }

    /**
     * Get all unique priorities used in tasks.
     *
     * @return LiveData containing list of priorities
     */
    fun getAllPriorities(): LiveData<List<String>> {
        return taskDao.getAllPriorities()
    }

    // ============ Statistics Operations ============

    /**
     * Get task statistics.
     * Note: This returns a LiveData that must be observed to get updates.
     * For a single synchronous call, use getTaskStatsSync().
     *
     * @return LiveData containing task statistics
     */
    fun getTaskStats(): LiveData<TaskStats> {
        return object : LiveData<TaskStats>() {
            init {
                observeForever {
                    // Keep data fresh
                }
            }

            override fun onActive() {
                super.onActive()
                postValue(computeTaskStats())
            }
        }
    }

    /**
     * Compute task statistics synchronously.
     * This is a suspend function and should be called from a coroutine.
     *
     * @return TaskStats object containing current statistics
     */
    suspend fun getTaskStatsSync(): TaskStats {
        return computeTaskStats()
    }

    /**
     * Compute task statistics.
     * This is an internal suspend function.
     */
    private suspend fun computeTaskStats(): TaskStats {
        val totalTasks = taskDao.getTotalTaskCount()
        val completedTasks = taskDao.getCompletedTaskCount()
        val pendingTasks = taskDao.getPendingTaskCount()

        val completionPercentage = if (totalTasks > 0) {
            (completedTasks.toFloat() / totalTasks) * 100
        } else {
            0f
        }

        // Get task counts by category
        val categories = listOf("Work", "Personal", "Shopping", "Health", "Finance", "Other")
        val tasksByCategory = mutableMapOf<String, Int>()
        categories.forEach { category ->
            tasksByCategory[category] = taskDao.getTaskCountByCategory(category)
        }

        // Get task counts by priority
        val priorities = listOf("High", "Medium", "Low")
        val tasksByPriority = mutableMapOf<String, Int>()
        priorities.forEach { priority ->
            tasksByPriority[priority] = taskDao.getTaskCountByPriority(priority)
        }

        return TaskStats(
            totalTasks = totalTasks,
            completedTasks = completedTasks,
            pendingTasks = pendingTasks,
            completionPercentage = completionPercentage,
            tasksByCategory = tasksByCategory,
            tasksByPriority = tasksByPriority
        )
    }
}

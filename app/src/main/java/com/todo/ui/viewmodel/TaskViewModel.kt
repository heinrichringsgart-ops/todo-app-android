package com.todo.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.todo.data.database.TaskDatabase
import com.todo.data.model.Task
import com.todo.data.model.TaskStats
import com.todo.data.repository.TaskRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for managing task-related UI state and business logic.
 * Handles all task operations and provides LiveData for UI observation.
 *
 * @property application The application context
 */
class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val taskRepository: TaskRepository
    private val taskDatabase: TaskDatabase = TaskDatabase.getDatabase(application)

    init {
        val taskDao = taskDatabase.taskDao()
        taskRepository = TaskRepository(taskDao)
    }

    // ============ UI State ============

    private val _uiState = MutableLiveData<TaskUIState>(TaskUIState.Idle)
    val uiState: LiveData<TaskUIState> = _uiState

    private val _selectedTask = MutableLiveData<Task?>(null)
    val selectedTask: LiveData<Task?> = _selectedTask

    private val _searchQuery = MutableLiveData<String>("")
    val searchQuery: LiveData<String> = _searchQuery

    private val _selectedCategory = MutableLiveData<String?>(null)
    val selectedCategory: LiveData<String?> = _selectedCategory

    private val _filterByCompletion = MutableLiveData<Boolean?>(null)
    val filterByCompletion: LiveData<Boolean?> = _filterByCompletion

    // ============ Task Data ============

    val allTasks: LiveData<List<Task>> = taskRepository.getAllTasks()
    val incompleteTasks: LiveData<List<Task>> = taskRepository.getIncompleteTasks()
    val completedTasks: LiveData<List<Task>> = taskRepository.getCompletedTasks()
    val tasksDueSoon: LiveData<List<Task>> = taskRepository.getTasksDueSoon()
    val allCategories: LiveData<List<String>> = taskRepository.getAllCategories()
    val allPriorities: LiveData<List<String>> = taskRepository.getAllPriorities()

    // ============ Insert/Create Operations ============

    /**
     * Create a new task.
     *
     * @param title Task title
     * @param description Task description
     * @param category Task category
     * @param priority Task priority
     * @param dueDate Optional due date
     */
    fun createTask(
        title: String,
        description: String = "",
        category: String,
        priority: String,
        dueDate: String? = null
    ) {
        if (title.isBlank()) {
            _uiState.value = TaskUIState.Error("Title cannot be empty")
            return
        }

        val task = Task(
            title = title,
            description = description,
            category = category,
            priority = priority,
            dueDate = dueDate
        )

        viewModelScope.launch {
            try {
                _uiState.value = TaskUIState.Loading
                taskRepository.insertTask(task)
                _uiState.value = TaskUIState.Success("Task created successfully")
            } catch (e: Exception) {
                _uiState.value = TaskUIState.Error("Failed to create task: ${e.message}")
            }
        }
    }

    // ============ Update Operations ============

    /**
     * Update an existing task.
     *
     * @param task The task to update
     */
    fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                _uiState.value = TaskUIState.Loading
                taskRepository.updateTask(task)
                _uiState.value = TaskUIState.Success("Task updated successfully")
            } catch (e: Exception) {
                _uiState.value = TaskUIState.Error("Failed to update task: ${e.message}")
            }
        }
    }

    /**
     * Toggle task completion status.
     *
     * @param taskId The task ID
     * @param isCompleted The new completion status
     */
    fun toggleTaskCompletion(taskId: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            try {
                if (isCompleted) {
                    taskRepository.markTaskComplete(taskId)
                } else {
                    taskRepository.markTaskIncomplete(taskId)
                }
                _uiState.value = TaskUIState.Success("Task status updated")
            } catch (e: Exception) {
                _uiState.value = TaskUIState.Error("Failed to update task: ${e.message}")
            }
        }
    }

    // ============ Delete Operations ============

    /**
     * Delete a task.
     *
     * @param task The task to delete
     */
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            try {
                _uiState.value = TaskUIState.Loading
                taskRepository.deleteTask(task)
                _uiState.value = TaskUIState.Success("Task deleted successfully")
                _selectedTask.value = null
            } catch (e: Exception) {
                _uiState.value = TaskUIState.Error("Failed to delete task: ${e.message}")
            }
        }
    }

    /**
     * Delete all completed tasks.
     */
    fun deleteCompletedTasks() {
        viewModelScope.launch {
            try {
                _uiState.value = TaskUIState.Loading
                taskRepository.deleteCompletedTasks()
                _uiState.value = TaskUIState.Success("Completed tasks deleted")
            } catch (e: Exception) {
                _uiState.value = TaskUIState.Error("Failed to delete tasks: ${e.message}")
            }
        }
    }

    /**
     * Delete all tasks.
     */
    fun deleteAllTasks() {
        viewModelScope.launch {
            try {
                _uiState.value = TaskUIState.Loading
                taskRepository.deleteAllTasks()
                _uiState.value = TaskUIState.Success("All tasks deleted")
                _selectedTask.value = null
            } catch (e: Exception) {
                _uiState.value = TaskUIState.Error("Failed to delete all tasks: ${e.message}")
            }
        }
    }

    // ============ Selection/Navigation ============

    /**
     * Select a task for viewing/editing.
     *
     * @param task The task to select
     */
    fun selectTask(task: Task) {
        _selectedTask.value = task
    }

    /**
     * Clear the selected task.
     */
    fun clearSelectedTask() {
        _selectedTask.value = null
    }

    // ============ Search & Filter ============

    /**
     * Set the search query.
     *
     * @param query The search query
     */
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * Get filtered tasks based on current search and filter criteria.
     *
     * @return LiveData containing filtered tasks
     */
    fun getFilteredTasks(): LiveData<List<Task>> {
        val query = searchQuery.value ?: ""
        val category = selectedCategory.value
        val isCompleted = filterByCompletion.value

        return when {
            // Search with category filter
            query.isNotEmpty() && category != null -> {
                taskRepository.searchTasksByCategory(query, category)
            }
            // Search only
            query.isNotEmpty() -> {
                taskRepository.searchTasks(query)
            }
            // Category filter only
            category != null -> {
                taskRepository.getTasksByCategory(category)
            }
            // Completion filter only
            isCompleted != null -> {
                taskRepository.getTasksByCompletionStatus(isCompleted)
            }
            // No filters
            else -> {
                allTasks
            }
        }
    }

    /**
     * Set category filter.
     *
     * @param category The category to filter by, or null to clear
     */
    fun setSelectedCategory(category: String?) {
        _selectedCategory.value = category
    }

    /**
     * Set completion filter.
     *
     * @param isCompleted Whether to show completed (true), incomplete (false), or all (null)
     */
    fun setCompletionFilter(isCompleted: Boolean?) {
        _filterByCompletion.value = isCompleted
    }

    /**
     * Clear all filters.
     */
    fun clearFilters() {
        _searchQuery.value = ""
        _selectedCategory.value = null
        _filterByCompletion.value = null
    }

    // ============ Statistics ============

    /**
     * Get task statistics.
     *
     * @return LiveData containing task statistics
     */
    fun getTaskStats(): LiveData<TaskStats> {
        val stats = MutableLiveData<TaskStats>()
        viewModelScope.launch {
            try {
                stats.value = taskRepository.getTaskStatsSync()
            } catch (e: Exception) {
                stats.value = TaskStats()
            }
        }
        return stats
    }

    // ============ UI State Management ============

    /**
     * Clear the current UI state message.
     */
    fun clearUIState() {
        _uiState.value = TaskUIState.Idle
    }

    /**
     * Get the current UI state.
     */
    fun getCurrentUIState(): TaskUIState {
        return uiState.value ?: TaskUIState.Idle
    }
}

/**
 * Sealed class representing the UI state of the application.
 */
sealed class TaskUIState {
    object Idle : TaskUIState()
    object Loading : TaskUIState()
    data class Success(val message: String) : TaskUIState()
    data class Error(val message: String) : TaskUIState()
}

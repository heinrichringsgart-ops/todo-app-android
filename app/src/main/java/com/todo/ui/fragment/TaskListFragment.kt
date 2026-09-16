package com.todo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.todo.R
import com.todo.databinding.FragmentTaskListBinding
import com.todo.data.model.Task
import com.todo.ui.adapter.TaskAdapter
import com.todo.ui.viewmodel.TaskViewModel
import com.todo.ui.viewmodel.TaskUIState

/**
 * Fragment for displaying the list of tasks.
 * Handles task list display, filtering, searching, and user interactions.
 */
class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    /**
     * Set up the RecyclerView with adapter and layout manager.
     */
    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onTaskClick = { task -> handleTaskClick(task) },
            onTaskLongClick = { task -> handleTaskLongClick(task) },
            onTaskCheckClick = { task -> handleTaskCheckClick(task) }
        )

        binding.tasksRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskAdapter
        }

        // Set up swipe-to-delete functionality
        setupSwipeToDelete()
    }

    /**
     * Set up swipe-to-delete functionality using ItemTouchHelper.
     */
    private fun setupSwipeToDelete() {
        val swipeCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val task = taskAdapter.currentList[position]
                viewModel.deleteTask(task)
                showToast("Task deleted")
            }
        }

        ItemTouchHelper(swipeCallback).attachToRecyclerView(binding.tasksRecyclerView)
    }

    /**
     * Set up observers for LiveData from the ViewModel.
     */
    private fun setupObservers() {
        // Observe filtered tasks
        viewModel.getFilteredTasks().observe(viewLifecycleOwner) { tasks ->
            taskAdapter.submitList(tasks)
            updateEmptyStateVisibility(tasks)
        }

        // Observe UI state
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            handleUIState(state)
        }

        // Observe categories for filter
        viewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            // Update category filter UI if needed
        }
    }

    /**
     * Set up event listeners for UI interactions.
     */
    private fun setupListeners() {
        binding.createTaskButton.setOnClickListener {
            // Navigate to create task screen
            // fragmentManager?.beginTransaction()?.replace(...)
            showToast("Create new task")
        }

        binding.searchEditText.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setSearchQuery(newText ?: "")
                return true
            }
        })

        binding.filterCompleteButton.setOnClickListener {
            viewModel.setCompletionFilter(false) // Show incomplete tasks
            showToast("Showing incomplete tasks")
        }

        binding.filterPendingButton.setOnClickListener {
            viewModel.setCompletionFilter(true) // Show completed tasks
            showToast("Showing completed tasks")
        }

        binding.filterAllButton.setOnClickListener {
            viewModel.clearFilters()
            showToast("Showing all tasks")
        }

        binding.clearFiltersButton.setOnClickListener {
            viewModel.clearFilters()
        }
    }

    /**
     * Handle task click events.
     */
    private fun handleTaskClick(task: Task) {
        viewModel.selectTask(task)
        // Navigate to task detail fragment
        // fragmentManager?.beginTransaction()?.replace(...)
        showToast("Opening task: ${task.title}")
    }

    /**
     * Handle task long click events.
     */
    private fun handleTaskLongClick(task: Task): Boolean {
        // Show context menu or options
        showToast("Long pressed: ${task.title}")
        return true
    }

    /**
     * Handle task checkbox click events.
     */
    private fun handleTaskCheckClick(task: Task) {
        viewModel.toggleTaskCompletion(task.id, !task.isCompleted)
    }

    /**
     * Handle UI state changes.
     */
    private fun handleUIState(state: TaskUIState) {
        when (state) {
            is TaskUIState.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
            }
            is TaskUIState.Success -> {
                binding.progressBar.visibility = View.GONE
                showToast(state.message)
                viewModel.clearUIState()
            }
            is TaskUIState.Error -> {
                binding.progressBar.visibility = View.GONE
                showToast("Error: ${state.message}")
                viewModel.clearUIState()
            }
            is TaskUIState.Idle -> {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    /**
     * Update empty state visibility based on task list.
     */
    private fun updateEmptyStateVisibility(tasks: List<Task>) {
        if (tasks.isEmpty()) {
            binding.emptyStateView.visibility = View.VISIBLE
            binding.tasksRecyclerView.visibility = View.GONE
        } else {
            binding.emptyStateView.visibility = View.GONE
            binding.tasksRecyclerView.visibility = View.VISIBLE
        }
    }

    /**
     * Show a toast message.
     */
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): TaskListFragment = TaskListFragment()
    }
}

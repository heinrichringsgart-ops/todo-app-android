package com.todo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.todo.databinding.TaskListItemBinding
import com.todo.data.model.Task

/**
 * RecyclerView adapter for displaying tasks in a list.
 * Handles task display, user interactions, and list updates.
 *
 * @param onTaskClick Callback when a task is clicked
 * @param onTaskLongClick Callback when a task is long clicked
 * @param onTaskCheckClick Callback when a task checkbox is clicked
 */
class TaskAdapter(
    private val onTaskClick: (Task) -> Unit,
    private val onTaskLongClick: (Task) -> Boolean,
    private val onTaskCheckClick: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = TaskListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)
    }

    /**
     * ViewHolder for task list items.
     */
    inner class TaskViewHolder(private val binding: TaskListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onTaskClick(getItem(position))
                }
            }

            binding.root.setOnLongClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onTaskLongClick(getItem(position))
                } else {
                    false
                }
            }

            binding.taskCheckbox.setOnCheckedChangeListener { _, _ ->
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onTaskCheckClick(getItem(position))
                }
            }
        }

        fun bind(task: Task) {
            binding.apply {
                taskTitle.text = task.title
                taskDescription.text = task.description.ifEmpty { "No description" }
                taskCategory.text = task.category
                taskPriority.text = task.priority
                taskCheckbox.isChecked = task.isCompleted
                taskDueDate.text = task.dueDate ?: "No due date"

                // Set visual style based on completion status
                if (task.isCompleted) {
                    taskTitle.alpha = 0.5f
                    taskDescription.alpha = 0.5f
                } else {
                    taskTitle.alpha = 1.0f
                    taskDescription.alpha = 1.0f
                }

                // Set priority color
                val priorityColor = when (task.priority) {
                    "High" -> binding.root.context.getColor(android.R.color.holo_red_light)
                    "Medium" -> binding.root.context.getColor(android.R.color.holo_orange_light)
                    "Low" -> binding.root.context.getColor(android.R.color.holo_green_light)
                    else -> binding.root.context.getColor(android.R.color.darker_gray)
                }
                taskPriority.setTextColor(priorityColor)
            }
        }
    }

    /**
     * DiffUtil callback for efficient list updates.
     */
    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}

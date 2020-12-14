package com.pomodorotime.task.tasklist.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.pomodorotime.core.bases.BaseMultiSelectorAdapter
import com.pomodorotime.task.databinding.RowTaskViewBinding

class TaskListAdapter(onItemSelector: OnItemClick<TaskListItem>) :
    BaseMultiSelectorAdapter<TaskListItem, TaskViewHolder>(
        onItemSelector,
        DIFF_CALLBACK
    ) {

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.onSelected(isItemSelected(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = RowTaskViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return TaskViewHolder(binding)
    }

    override fun submitList(list: List<TaskListItem>?) {
        clearSelection()
        super.submitList(list)
    }

    companion object {
        private val DIFF_CALLBACK = object :
            DiffUtil.ItemCallback<TaskListItem>() {

            override fun areItemsTheSame(oldTask: TaskListItem, newTask: TaskListItem) =
                oldTask.id == newTask.id

            override fun areContentsTheSame(oldTask: TaskListItem, newTask: TaskListItem) =
                oldTask == newTask
        }

    }

    override fun isViewHolderSelectable(itemViewType: Int): Boolean =
        itemViewType == TaskListItem.LIST_ITEM
}
package com.pomodorotime.task.tasklist.list

import android.text.format.DateFormat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.pomodorotime.core.BaseViewHolder
import com.pomodorotime.task.R
import com.pomodorotime.task.databinding.RowTaskViewBinding

class TaskViewHolder(private val binding: RowTaskViewBinding) :
    BaseViewHolder<TaskListItem>(binding) {

    override fun bind(data: TaskListItem) {
        with(binding) {
            tvTaskName.text = data.name
            tvTaskDate.text = DateFormat.getDateFormat(itemView.context).format(data.creationDate)
            tvEstPommodoros.text = data.estimatedPomodoros
            ivTaskCheck.isVisible = data.completed
        }
    }

    fun onSelected(selected: Boolean) {

        with(binding.container) {
            background = ContextCompat.getDrawable(
                context,
                if (selected) R.drawable.row_background else R.drawable.list_ripple_background
            )
        }
    }
}
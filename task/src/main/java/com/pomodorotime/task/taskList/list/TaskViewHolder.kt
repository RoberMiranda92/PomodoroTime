package com.pomodorotime.task.taskList.list

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.pomodorotime.core.BaseViewHolder
import com.pomodorotime.task.R
import com.pomodorotime.task.databinding.RowTaskViewBinding
import java.text.DateFormat
import java.util.*

class TaskViewHolder(private val binding: RowTaskViewBinding) :
    BaseViewHolder<TaskListItem>(binding) {

    override fun bind(data: TaskListItem) {
        with(binding) {
            tvTaskName.text = data.name
            val f: DateFormat = DateFormat.getDateTimeInstance(
                DateFormat.SHORT,
                DateFormat.SHORT,
                Locale.getDefault()
            )
            tvTaskDate.text = f.format(data.creationDate)
            tvEstPommodoros.text = data.extimatedPomodoros
            ivTaskCheck.isVisible = true
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
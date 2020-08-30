package com.pomodorotime.task.taskList.list

import com.pomodorotime.core.ListItem
import java.util.*

data class TaskListItem(
    val id: Int, val name: String, val creationDate: Date, val extimatedPomodoros: String
) : ListItem {

    override fun getType(): Int =
        LIST_ITEM

    companion object {
        const val LIST_ITEM = 1
    }
}
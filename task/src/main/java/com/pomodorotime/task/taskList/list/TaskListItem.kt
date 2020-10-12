package com.pomodorotime.task.tasklist.list

import com.pomodorotime.core.ListItem
import java.util.*

data class TaskListItem(
    val id: Long,
    val name: String,
    val creationDate: Date,
    val estimatedPomodoros: String,
    val completed: Boolean
) : ListItem {

    override fun getType(): Int =
        LIST_ITEM

    companion object {
        const val LIST_ITEM = 1
    }
}
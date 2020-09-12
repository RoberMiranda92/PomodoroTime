package com.pomodorotime.task.tasklist

import com.pomodorotime.data.task.TaskEntity
import com.pomodorotime.task.tasklist.list.TaskListItem

fun fromModelToView(tasks: List<TaskEntity>): List<TaskListItem> {
    return tasks.map { TaskListItem(it.id ?: -1, it.name, it.creationDate,
        it.estimatedPomodoros.toString()) }
}
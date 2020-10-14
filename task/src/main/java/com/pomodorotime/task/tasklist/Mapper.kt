package com.pomodorotime.task.tasklist

import com.pomodorotime.domain.models.Task
import com.pomodorotime.task.tasklist.list.TaskListItem

fun fromModelToView(tasks: List<Task>): List<TaskListItem> {
    return tasks.map {
        TaskListItem(
            it.id ?: -1, it.name, it.creationDate,
            it.estimatedPomodoros.toString(), it.completed
        )
    }
}
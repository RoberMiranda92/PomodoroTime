package com.pomodorotime.task.tasklist

import com.pomodorotime.task.tasklist.list.TaskListItem

sealed class TaskListScreenState {
    object Initial : TaskListScreenState()
    object Loading : TaskListScreenState()
    data class DataLoaded(val taskList: List<TaskListItem>) : TaskListScreenState()
    object EmptyState : TaskListScreenState()
    data class Error(val error: String) : TaskListScreenState()
    object Editing : TaskListScreenState()
}
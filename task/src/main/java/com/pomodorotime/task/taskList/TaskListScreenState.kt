package com.pomodorotime.task.taskList

import com.pomodorotime.data.ErrorResponse
import com.pomodorotime.task.taskList.list.TaskListItem

sealed class TaskListScreenState {
    object Loading : TaskListScreenState()
    data class DataLoaded(val taskList:List<TaskListItem>) : TaskListScreenState()
    object EmptyState : TaskListScreenState()
    data class Error(val error: ErrorResponse) : TaskListScreenState()
    object Editing : TaskListScreenState()
    object NavigateToCreateTask : TaskListScreenState()

}
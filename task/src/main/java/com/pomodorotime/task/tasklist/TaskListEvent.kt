package com.pomodorotime.task.tasklist

import com.pomodorotime.task.tasklist.list.TaskListItem

sealed class TaskListEvent {
    object Load : TaskListEvent()
    object EditTaskList : TaskListEvent()
    object EditTaskListFinished : TaskListEvent()
    object AddTaskPressed : TaskListEvent()
    data class DeleteTaskElementsPressed(val list: List<TaskListItem>) : TaskListEvent()
}
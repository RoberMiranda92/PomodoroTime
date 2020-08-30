package com.pomodorotime.task.taskList.list

sealed class TaskListEvent {
    object Load : TaskListEvent()
    object EditTaskList : TaskListEvent()
    object EditTaskListFinished : TaskListEvent()
    object AddTaskPressed : TaskListEvent()
    data class DeleteTaskElementsPressed(val list: List<TaskListItem>) : TaskListEvent()
}
package com.pomodorotime.task.create

sealed class CreateTaskEvent {
    data class EditingTask(val name: String, val estimatedPomodoros: Int) : CreateTaskEvent()
    object SaveTask : CreateTaskEvent()
}
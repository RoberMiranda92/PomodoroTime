package com.pomodorotime.task.create

sealed class CreateTaskEvent {
    data class EdittingTask(val name: String, val estimatedPomodoros: Int) : CreateTaskEvent()
    object SaveTask : CreateTaskEvent()
}
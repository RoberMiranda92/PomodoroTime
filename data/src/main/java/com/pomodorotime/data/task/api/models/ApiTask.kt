package com.pomodorotime.data.task.api.models

import java.util.Date

data class ApiTask(
    val id: Long,
    val name: String,
    val creationDate: Date,
    val donePomodoros: Int,
    val estimatedPomodoros: Int,
    val shortBreaks: Int,
    val longBreaks: Int,
    val completed: Boolean
)
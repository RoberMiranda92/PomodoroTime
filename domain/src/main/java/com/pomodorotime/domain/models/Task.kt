package com.pomodorotime.domain.task.models

import java.util.Date

data class Task (
    var id: Long? = null,
    val name: String,
    val creationDate: Date,
    val donePomodoros: Int,
    val estimatedPomodoros: Int,
    val shortBreaks: Int,
    val longBreaks: Int,
    val completed: Boolean
)
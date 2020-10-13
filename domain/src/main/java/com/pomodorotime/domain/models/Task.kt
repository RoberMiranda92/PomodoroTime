package com.pomodorotime.domain.models

import com.pomodorotime.data.task.TaskDataModel
import java.util.*

data class Task(
    var id: Long? = null,
    val name: String,
    val creationDate: Date,
    val donePomodoros: Int,
    val estimatedPomodoros: Int,
    val shortBreaks: Int,
    val longBreaks: Int,
    val completed: Boolean
)

fun TaskDataModel.toDomainModel() =
    Task(
        id,
        name,
        creationDate,
        donePomodoros,
        estimatedPomodoros,
        shortBreaks,
        longBreaks,
        completed
    )
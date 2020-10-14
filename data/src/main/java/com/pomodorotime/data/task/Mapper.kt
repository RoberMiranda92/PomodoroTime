package com.pomodorotime.data.task

import com.pomodorotime.data.task.api.models.ApiTask
import com.pomodorotime.data.task.dataBase.TaskEntity
import com.pomodorotime.domain.models.Task

fun Task.toApiTaskModel() =
    ApiTask(
        id,
        name,
        creationDate,
        donePomodoros,
        estimatedPomodoros,
        shortBreaks,
        longBreaks,
        completed
    )

fun Task.toDataModel() =
    TaskEntity(
        id,
        name,
        creationDate,
        donePomodoros,
        estimatedPomodoros,
        shortBreaks,
        longBreaks,
        completed
    )

fun ApiTask.toDomainModel() =
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

fun TaskEntity.toDomainModel() =
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
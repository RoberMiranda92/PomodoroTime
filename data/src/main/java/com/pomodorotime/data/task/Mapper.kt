package com.pomodorotime.data.task

import com.pomodorotime.data.task.api.models.ApiTask
import com.pomodorotime.data.task.dataBase.TaskEntity

fun ApiTask.toDataModel() =
    TaskDataModel(
        id,
        name,
        creationDate,
        donePomodoros,
        estimatedPomodoros,
        shortBreaks,
        longBreaks,
        completed
    )

fun TaskDataModel.toApiTaskModel() =
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

fun TaskEntity.toDataModel() =
    TaskDataModel(
        id,
        name,
        creationDate,
        donePomodoros,
        estimatedPomodoros,
        shortBreaks,
        longBreaks,
        completed
    )

fun TaskDataModel.toEntityModel() =
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
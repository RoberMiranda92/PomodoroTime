package com.pomodorotime.data.task.api

import com.pomodorotime.data.task.api.models.ApiTask

interface ITaskApi {

    fun getAllTask()

    fun insetTask(task: ApiTask)

    fun updateTask(task: ApiTask)

    fun deleteTask(id: Int)
}
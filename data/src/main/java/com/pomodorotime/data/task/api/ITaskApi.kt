package com.pomodorotime.data.task.api

import com.pomodorotime.data.task.api.models.ApiTask

interface ITaskApi {

    suspend fun getAllTask()

    suspend fun insetTask(userId: String, task: ApiTask)

    suspend fun updateTask(userId: String, task: ApiTask)

    suspend fun deleteTask(userId: String, id: Long)
}
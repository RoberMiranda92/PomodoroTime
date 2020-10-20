package com.pomodorotime.data.task.datasource.remote

import com.pomodorotime.data.task.api.models.ApiTask

interface ITaskRemoteDataSource {

    suspend fun insetTask(userId: String, task: ApiTask)
    suspend fun deleteTask(userId: String, taskId: Long)
}
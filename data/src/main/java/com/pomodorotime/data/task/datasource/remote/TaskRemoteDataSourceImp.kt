package com.pomodorotime.data.task.datasource.remote

import com.pomodorotime.data.task.api.ITaskApi
import com.pomodorotime.data.task.api.models.ApiTask

class TaskRemoteDataSourceImp(private val api: ITaskApi) : ITaskRemoteDataSource {

    override suspend fun insetTask(userId: String, task: ApiTask) =
        api.insetTask(userId, task)

    override suspend fun deleteTask(userId: String, taskId: Long) =
        api.deleteTask(userId, taskId)
}
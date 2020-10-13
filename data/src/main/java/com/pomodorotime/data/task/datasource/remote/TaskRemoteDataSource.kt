package com.pomodorotime.data.task.datasource.remote

import com.pomodorotime.data.task.api.FirebaseTaskApi
import com.pomodorotime.data.task.api.ITaskApi
import com.pomodorotime.data.task.api.models.ApiTask

class TaskRemoteDataSource(private val api: ITaskApi) {

    suspend fun insetTask(userId: String, task: ApiTask) =
        api.insetTask(userId, task)

    suspend fun deleteTask(userId: String, taskId: Long) =
        api.deleteTask(userId, taskId)

    companion object {
        fun getNewInstance(): TaskRemoteDataSource {
            return TaskRemoteDataSource(FirebaseTaskApi())
        }
    }
}
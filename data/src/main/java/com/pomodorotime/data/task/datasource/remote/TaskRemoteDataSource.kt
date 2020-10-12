package com.pomodorotime.data.task.datasource.remote

import com.pomodorotime.data.task.api.FirebaseTaskApi
import com.pomodorotime.data.task.api.ITaskApi

class TaskRemoteDataSource(private val api: ITaskApi) {


    companion object {
        fun getNewInstance(): TaskRemoteDataSource {
            return TaskRemoteDataSource(FirebaseTaskApi())
        }
    }
}
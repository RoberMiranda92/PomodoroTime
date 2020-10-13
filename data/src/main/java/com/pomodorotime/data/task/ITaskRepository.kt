package com.pomodorotime.data.task

import com.pomodorotime.data.ResultWrapper
import kotlinx.coroutines.flow.Flow

interface ITaskRepository {

    fun getAllTasks(): Flow<ResultWrapper<List<TaskDataModel>>>

    suspend fun insetTask(task: TaskDataModel): ResultWrapper<Long>

    suspend fun getTaskById(id: Int): ResultWrapper<TaskDataModel>

    suspend fun deleteTasks(idList: List<Long>): ResultWrapper<Unit>

    suspend fun insetTaskRemote(task: TaskDataModel): ResultWrapper<Unit>
}
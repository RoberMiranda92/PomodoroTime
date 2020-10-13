package com.pomodorotime.data.task

import kotlinx.coroutines.flow.Flow

interface ITaskRepository {

    fun getAllTasks(): Flow<List<TaskDataModel>>

    suspend fun insetTask(task: TaskDataModel): Long

    suspend fun getTaskById(id: Long): TaskDataModel

    suspend fun deleteTasks(idList: List<Long>)

    suspend fun insetTaskRemote(task: TaskDataModel)
}
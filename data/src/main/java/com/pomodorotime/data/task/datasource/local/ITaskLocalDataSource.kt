package com.pomodorotime.data.task.datasource.local

import com.pomodorotime.data.task.dataBase.TaskEntity
import kotlinx.coroutines.flow.Flow

interface ITaskLocalDataSource {

    fun getAllTasks(): Flow<List<TaskEntity>>

    suspend fun insetTask(entity: TaskEntity): Long

    suspend fun getTaskById(id: Long): TaskEntity

    suspend fun deleteTasks(idList: List<Long>)
}
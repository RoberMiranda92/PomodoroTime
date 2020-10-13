package com.pomodorotime.data.task.dataBase

import kotlinx.coroutines.flow.Flow

interface IDataBase {

    fun getAllTask(): Flow<List<TaskEntity>>

    suspend fun getTaskById(id: Long): TaskEntity

    suspend fun insert(taskList: List<TaskEntity>)

    suspend fun insert(task: TaskEntity): Long

    suspend fun delete(task: TaskEntity)

    suspend fun deleteTaskList(list: List<Long>)
}
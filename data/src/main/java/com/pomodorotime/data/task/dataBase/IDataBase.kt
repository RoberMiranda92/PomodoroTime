package com.pomodorotime.data.task.dataBase

import kotlinx.coroutines.flow.Flow

interface IDataBase {

    fun getAllTask(): Flow<List<TaskEntity>>

    suspend fun getTaskById(id: Int): TaskEntity

    suspend fun insert(taskList: List<TaskEntity>)

    suspend fun insert(task: TaskEntity)

    suspend fun delete(task: TaskEntity)

    suspend fun deleteTaskList(list: List<Int>)
}
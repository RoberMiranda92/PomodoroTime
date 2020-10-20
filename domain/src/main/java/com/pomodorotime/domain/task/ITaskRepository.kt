package com.pomodorotime.domain.task

import com.pomodorotime.domain.models.Task
import kotlinx.coroutines.flow.Flow

interface ITaskRepository {

    fun getAllTasks(): Flow<List<Task>>

    suspend fun insetTask(task: Task): Long

    suspend fun getTaskById(id: Long): Task

    suspend fun deleteTasks(idList: List<Long>)

}
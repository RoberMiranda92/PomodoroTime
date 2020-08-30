package com.pomodorotime.data.task

import android.content.Context
import com.pomodorotime.data.BaseRepository
import com.pomodorotime.data.ResultWrapper
import kotlinx.coroutines.Dispatchers

class TaskRepository private constructor(private val taskDao: TaskDao) : BaseRepository() {

    fun getAllTasks() = taskDao.getAllTask()

    suspend fun insetTask(entity: TaskEntity): ResultWrapper<Unit> {
        return safeApiCall(Dispatchers.IO) { taskDao.insert(entity) }
    }

    suspend fun getTaskById(id: Int): ResultWrapper<TaskEntity> {
        return safeApiCall(Dispatchers.IO) { taskDao.getTaskById(id) }
    }

    suspend fun deleteTasks(idList: List<Int>): ResultWrapper<Unit> =
        safeApiCall(Dispatchers.IO) { taskDao.deleteTaskList(idList) }

    companion object {

        fun getNewInstance(context: Context): TaskRepository {
            return TaskRepository(TaskDataBase.getInstance(context).taskDao())
        }
    }

}
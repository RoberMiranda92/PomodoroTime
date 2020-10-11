package com.pomodorotime.data.task

import android.content.Context
import com.pomodorotime.data.BaseRepository
import com.pomodorotime.data.ResultWrapper
import com.pomodorotime.data.task.datasource.local.TaskLocalDataSource
import com.pomodorotime.data.task.datasource.remote.TaskRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

@ExperimentalCoroutinesApi
class TaskRepository private constructor(
    private val localDataSource: TaskLocalDataSource,
    private val remoteDataSource: TaskRemoteDataSource
) : BaseRepository() {

    fun getAllTasks(): Flow<ResultWrapper<List<TaskDataModel>>> {
        return safeFlowCall(Dispatchers.IO, localDataSource.getAllTasks().transform
        { it.map { it.toDataModel() } })
    }

    suspend fun insetTask(task: TaskDataModel): ResultWrapper<Unit> {
        return safeApiCall(Dispatchers.IO) { localDataSource.insetTask(task.toEntityModel()) }
    }

    suspend fun getTaskById(id: Int): ResultWrapper<TaskDataModel> {
        return safeApiCall(Dispatchers.IO) { localDataSource.getTaskById(id).toDataModel() }
    }

    suspend fun deleteTasks(idList: List<Int>): ResultWrapper<Unit> =
        safeApiCall(Dispatchers.IO) { localDataSource.deleteTasks(idList) }

    companion object {

        fun getNewInstance(context: Context): TaskRepository {
            return TaskRepository(
                TaskLocalDataSource.getNewInstance(context),
                TaskRemoteDataSource.getNewInstance()
            )
        }
    }

}
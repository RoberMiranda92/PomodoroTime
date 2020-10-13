package com.pomodorotime.data.task

import android.content.Context
import com.pomodorotime.data.BaseRepository
import com.pomodorotime.data.ResultWrapper
import com.pomodorotime.data.task.datasource.local.TaskLocalDataSource
import com.pomodorotime.data.task.datasource.remote.TaskRemoteDataSource
import com.pomodorotime.data.user.UserLocalDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

@ExperimentalCoroutinesApi
class TaskRepository private constructor(
    private val localDataSource: TaskLocalDataSource,
    private val remoteDataSource: TaskRemoteDataSource,
    private val userLocalDataSource: UserLocalDataSource
) : BaseRepository(), ITaskRepository {

    override fun getAllTasks(): Flow<ResultWrapper<List<TaskDataModel>>> {
        return safeFlowCall(Dispatchers.IO, localDataSource.getAllTasks().transform
        { emit(it.map { it.toDataModel() }) })
    }

    override suspend fun insetTask(task: TaskDataModel): ResultWrapper<Long> {
        return safeApiCall(Dispatchers.IO) { localDataSource.insetTask(task.toEntityModel()) }
    }

    override suspend fun getTaskById(id: Int): ResultWrapper<TaskDataModel> {
        return safeApiCall(Dispatchers.IO) { localDataSource.getTaskById(id).toDataModel() }
    }

    override suspend fun deleteTasks(idList: List<Long>): ResultWrapper<Unit> =
        safeApiCall(Dispatchers.IO) { localDataSource.deleteTasks(idList) }

    override suspend fun insetTaskRemote(task: TaskDataModel): ResultWrapper<Unit> {
        val userId: String = userLocalDataSource.getUserId()
        return safeApiCall(Dispatchers.IO) {
            remoteDataSource.insetTask(
                userId,
                task.toApiTaskModel()
            )
        }
    }

    companion object {

        fun getNewInstance(context: Context): TaskRepository {
            return TaskRepository(
                TaskLocalDataSource.getNewInstance(context),
                TaskRemoteDataSource.getNewInstance(),
                UserLocalDataSource
            )
        }
    }

}
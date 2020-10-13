package com.pomodorotime.data.task

import android.content.Context
import com.pomodorotime.data.task.datasource.local.TaskLocalDataSource
import com.pomodorotime.data.task.datasource.remote.TaskRemoteDataSource
import com.pomodorotime.data.user.UserLocalDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

@ExperimentalCoroutinesApi
class TaskRepository private constructor(
    private val localDataSource: TaskLocalDataSource,
    private val remoteDataSource: TaskRemoteDataSource,
    private val userLocalDataSource: UserLocalDataSource
) : ITaskRepository {

    override fun getAllTasks(): Flow<List<TaskDataModel>> {
        return localDataSource.getAllTasks().transform { emit(it.map { it.toDataModel() }) }
    }

    override suspend fun insetTask(task: TaskDataModel): Long {
        return localDataSource.insetTask(task.toEntityModel())
    }

    override suspend fun getTaskById(id: Long): TaskDataModel {
        return localDataSource.getTaskById(id).toDataModel()
    }

    override suspend fun deleteTasks(idList: List<Long>) =
        localDataSource.deleteTasks(idList)

    override suspend fun insetTaskRemote(task: TaskDataModel) {
        val userId: String = userLocalDataSource.getUserId()
        return remoteDataSource.insetTask(
            userId,
            task.toApiTaskModel()
        )
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
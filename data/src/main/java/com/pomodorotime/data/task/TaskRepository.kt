package com.pomodorotime.data.task

import com.pomodorotime.data.sync.ISyncManager
import com.pomodorotime.data.sync.SyncTypes
import com.pomodorotime.data.task.datasource.local.ITaskLocalDataSource
import com.pomodorotime.domain.models.Task
import com.pomodorotime.domain.task.ITaskRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

@ExperimentalCoroutinesApi
class TaskRepository constructor(
    private val localDataSource: ITaskLocalDataSource,
    private val synchronizer: ISyncManager
) : ITaskRepository {

    override fun getAllTasks(): Flow<List<Task>> {
        return localDataSource.getAllTasks().transform { emit(it.map { it.toDomainModel() }) }
    }

    override suspend fun insetTask(task: Task): Long {
        val result = localDataSource.insetTask(task.toDataModel())
        task.id = result
        synchronizer.performSyncInsertion(task.toApiTaskModel())
        return result
    }

    override suspend fun getTaskById(id: Long): Task {
        return localDataSource.getTaskById(id).toDomainModel()
    }

    override suspend fun deleteTasks(idList: List<Long>) {
        localDataSource.deleteTasks(idList)
        idList.forEach { synchronizer.performSyncDeletion(it) }

    }

}
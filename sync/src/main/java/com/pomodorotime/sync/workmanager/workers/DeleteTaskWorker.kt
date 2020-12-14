package com.pomodorotime.sync.workmanager.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.pomodorotime.core.session.ISessionManager
import com.pomodorotime.data.sync.ISyncErrorHandler
import com.pomodorotime.data.sync.SyncError
import com.pomodorotime.data.task.datasource.remote.ITaskRemoteDataSource
import com.pomodorotime.data.user.IUserLocalDataSource

class DeleteTaskWorker(
    context: Context,
    params: WorkerParameters,
    private val userDataSource: IUserLocalDataSource,
    private val taskDataSource: ITaskRemoteDataSource,
    private val errorHandler: ISyncErrorHandler,
    private val sessionManager: ISessionManager
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        val taskId = dataToTaskId(inputData)

        try {
            taskDataSource.deleteTask(userDataSource.getToken(), taskId)
            return Result.success()
        } catch (ex: Exception) {
            return when (errorHandler.getSyncError(ex)) {
                is SyncError.DataBaseError -> {
                    Result.retry()
                }
                is SyncError.InvalidUser -> {
                    sessionManager.onLogout()
                    userDataSource.clearToken()
                    Result.retry()
                }

                is SyncError.NetworkError -> {
                    Result.retry()
                }

                is SyncError.GenericError -> {
                    Result.retry()
                }
            }
        }

    }

    private fun dataToTaskId(inputData: Data): Long =
        inputData.getLong(TASK_ID_ARGS, -1L)

    companion object {
        const val TASK_ID_ARGS = "task.id.args"
    }
}
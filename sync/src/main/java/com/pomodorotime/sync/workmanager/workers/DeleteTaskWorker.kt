package com.pomodorotime.sync.workmanager.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.pomodorotime.data.sync.ISyncErrorHandler
import com.pomodorotime.data.task.datasource.remote.ITaskRemoteDataSource
import com.pomodorotime.data.user.IUserLocalDataSource

class DeleteTaskWorker(
    context: Context,
    params: WorkerParameters,
    private val userDataSource: IUserLocalDataSource,
    private val taskDataSource: ITaskRemoteDataSource,
    private val errorHandler: ISyncErrorHandler
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        val taskId = dataToTaskId(inputData)

        return try {
            taskDataSource.deleteTask(userDataSource.getUserId(), taskId)
            Result.success()
        } catch (ex: Exception) {
            Log.e("InsertTaskWorker", errorHandler.getSyncError(ex).toString())
            Result.retry()
        }

    }

    private fun dataToTaskId(inputData: Data): Long =
        inputData.getLong(TASK_ID_ARGS, -1L)

    companion object {
        const val TASK_ID_ARGS = "task.id.args"
    }
}
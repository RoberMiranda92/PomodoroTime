package com.pomodorotime.sync.workmanager.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.pomodorotime.core.session.ISessionManager
import com.pomodorotime.data.sync.ISyncErrorHandler
import com.pomodorotime.data.sync.SyncError
import com.pomodorotime.data.task.api.models.ApiTask
import com.pomodorotime.data.task.datasource.remote.ITaskRemoteDataSource
import com.pomodorotime.data.user.IUserLocalDataSource
import java.util.*

class InsertTaskWorker(
    context: Context,
    params: WorkerParameters,
    private val userDataSource: IUserLocalDataSource,
    private val taskDataSource: ITaskRemoteDataSource,
    private val errorHandler: ISyncErrorHandler,
    private val sessionManager: ISessionManager
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        val task = dataToTaskModel(inputData)

        try {
            taskDataSource.insetTask(userDataSource.getToken(), task)
            return Result.success()
        } catch (ex: Exception) {
            return when (errorHandler.getSyncError(ex)) {
                is SyncError.DataBaseError -> {
                    sessionManager.onLogout()
                    userDataSource.clearToken()
                    Result.retry()
                }
                is SyncError.InvalidUser -> {
                    sessionManager.onLogout()
                    userDataSource.clearToken()
                    Result.retry()
                }

                is SyncError.NetworkError -> {
                    sessionManager.onLogout()
                    userDataSource.clearToken()
                    Result.retry()
                }

                is SyncError.GenericError -> {
                    sessionManager.onLogout()
                    userDataSource.clearToken()
                    Result.retry()
                }
            }
        }

    }

    private fun dataToTaskModel(inputData: Data): ApiTask =
        ApiTask(
            inputData.getLong(TASK_ID_ARGS, -1L),
            inputData.getString(TASK_NAME_ARGS) ?: "",
            Date(inputData.getLong(TASK_CREATION_DATE_ARGS, -1L)),
            inputData.getInt(TASK_DONE_POMODOROS_ARGS, -1),
            inputData.getInt(TASK_ESTIMATED_POMODOROS_ARGS, -1),
            inputData.getInt(TASK_SHORT_BREAKS_ARGS, -1),
            inputData.getInt(TASK_LONG_BREAKS_ARGS, -1),
            inputData.getBoolean(TASK_COMPLETED_ARGS, false)
        )

    companion object {
        const val TASK_ID_ARGS = "task.id.args"
        const val TASK_NAME_ARGS = "task.name.args"
        const val TASK_CREATION_DATE_ARGS = "task.creation.args"
        const val TASK_DONE_POMODOROS_ARGS = "task.done_pomodoros.args"
        const val TASK_ESTIMATED_POMODOROS_ARGS = "task.estimated_pomodoros.args"
        const val TASK_SHORT_BREAKS_ARGS = "task.short_breaks.args"
        const val TASK_LONG_BREAKS_ARGS = "task.long_breaks.args"
        const val TASK_COMPLETED_ARGS = "task.completed.args"
    }
}
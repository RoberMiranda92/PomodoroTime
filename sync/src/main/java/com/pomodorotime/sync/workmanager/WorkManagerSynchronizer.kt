package com.pomodorotime.sync.workmanager

import android.content.Context
import androidx.work.*
import com.pomodorotime.data.sync.ISyncErrorHandler
import com.pomodorotime.data.sync.SyncTypes
import com.pomodorotime.data.task.api.models.ApiTask
import com.pomodorotime.data.task.datasource.remote.ITaskRemoteDataSource
import com.pomodorotime.data.user.IUserLocalDataSource
import com.pomodorotime.sync.di.Synchronizer
import com.pomodorotime.sync.workmanager.workers.DeleteTaskWorker
import com.pomodorotime.sync.workmanager.workers.InsertTaskWorker
import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.logging.ErrorManager

class WorkManagerSynchronizer(
    context: Context,
    userLocalDataSource: IUserLocalDataSource,
    taskRemoteDataSource: ITaskRemoteDataSource,
    errorHandler: ISyncErrorHandler
) : Synchronizer {

    private var workManager: WorkManager

    init {
        WorkManager.initialize(
            context,
            WorkManagerConfiguration(
                userLocalDataSource,
                taskRemoteDataSource,
                errorHandler
            ).workManagerConfiguration
        )
        workManager = WorkManager.getInstance(context)
    }

    override fun performSync(task: ApiTask, type: SyncTypes) {

        val requestBuilder = OneTimeWorkRequest.Builder(getWorkByType(type))

        requestBuilder.setInputData(buildData(task))
        requestBuilder.setConstraints(getGeneralConstraints())

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            requestBuilder.setBackoffCriteria(
                BackoffPolicy.LINEAR,
                Duration.ofMillis(OneTimeWorkRequest.MIN_BACKOFF_MILLIS)
            )
        } else {
            requestBuilder.setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
        }

        workManager.enqueue(requestBuilder.build())
    }

    override fun performSync(taskId: Long, type: SyncTypes) {

        val requestBuilder = OneTimeWorkRequest.Builder(getWorkByType(type))

        requestBuilder.setInputData(buildDataFromId(taskId))
        requestBuilder.setConstraints(getGeneralConstraints())

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            requestBuilder.setBackoffCriteria(
                BackoffPolicy.LINEAR,
                Duration.ofMillis(OneTimeWorkRequest.MIN_BACKOFF_MILLIS)
            )
        } else {
            requestBuilder.setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
        }

        workManager.enqueue(requestBuilder.build())
    }

    private fun buildData(task: ApiTask): Data {

        val data = Data.Builder()

        data.putLong(InsertTaskWorker.TASK_ID_ARGS, task.id ?: -1L)
        data.putString(InsertTaskWorker.TASK_NAME_ARGS, task.name)
        data.putLong(InsertTaskWorker.TASK_CREATION_DATE_ARGS, task.creationDate.time)
        data.putInt(InsertTaskWorker.TASK_DONE_POMODOROS_ARGS, task.donePomodoros)
        data.putInt(InsertTaskWorker.TASK_ESTIMATED_POMODOROS_ARGS, task.estimatedPomodoros)
        data.putInt(InsertTaskWorker.TASK_SHORT_BREAKS_ARGS, task.shortBreaks)
        data.putInt(InsertTaskWorker.TASK_LONG_BREAKS_ARGS, task.longBreaks)
        data.putBoolean(InsertTaskWorker.TASK_COMPLETED_ARGS, task.completed)

        return data.build()
    }

    private fun buildDataFromId(taskId: Long): Data {
        val data = Data.Builder()
        data.putLong(DeleteTaskWorker.TASK_ID_ARGS, taskId)
        return data.build()
    }

    private fun getGeneralConstraints(): Constraints {
        val constrains = Constraints.Builder()
        constrains.setRequiredNetworkType(NetworkType.CONNECTED)
        return constrains.build()
    }

    private fun <T : ListenableWorker> getWorkByType(type: SyncTypes): Class<T> {
        return when (type) {
            SyncTypes.INSERT -> InsertTaskWorker::class.java
            SyncTypes.DELETE -> DeleteTaskWorker::class.java
            else -> InsertTaskWorker::class.java
        } as Class<T>

    }
}
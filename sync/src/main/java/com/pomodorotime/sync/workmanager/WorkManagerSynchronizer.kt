package com.pomodorotime.sync.workmanager

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.pomodorotime.domain.models.Task
import com.pomodorotime.domain.task.ITaskRepository
import com.pomodorotime.sync.SyncTypes
import com.pomodorotime.sync.di.Synchronizer
import com.pomodorotime.sync.workmanager.workers.InsertTaskWorker
import java.time.Duration
import java.util.concurrent.TimeUnit

class WorkManagerSynchronizer(
    context: Context,
    repository: ITaskRepository
) : Synchronizer {

    private var workManager: WorkManager

    init {
        WorkManager.initialize(
            context,
            WorkManagerConfiguration(repository).workManagerConfiguration
        )
        workManager = WorkManager.getInstance(context)
    }

    override fun performSync(task: Task, type: SyncTypes) {

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

    private fun buildData(task: Task): Data {

        val data = Data.Builder()

        data.putLong(InsertTaskWorker.TASK_ID_ARGS, task.id ?: -1L)
        data.putString(InsertTaskWorker.TASK_NAME_ARGS, task.name)
        data.putString(InsertTaskWorker.TASK_CREATION_DATE_ARGS, task.creationDate.toString())
        data.putInt(InsertTaskWorker.TASK_DONE_POMODOROS_ARGS, task.donePomodoros)
        data.putInt(InsertTaskWorker.TASK_ESTIMATED_POMODOROS_ARGS, task.estimatedPomodoros)
        data.putInt(InsertTaskWorker.TASK_SHORT_BREAKS_ARGS, task.shortBreaks)
        data.putInt(InsertTaskWorker.TASK_LONG_BREAKS_ARGS, task.longBreaks)
        data.putBoolean(InsertTaskWorker.TASK_COMPLETED_ARGS, task.completed)

        return data.build()
    }

    private fun getGeneralConstraints(): Constraints {
        val constrains = Constraints.Builder()
        constrains.setRequiredNetworkType(NetworkType.CONNECTED)
        return constrains.build()
    }

    //TODO
    private fun getWorkByType(type: SyncTypes): Class<InsertTaskWorker> {
        return when (type) {
            SyncTypes.INSERT -> InsertTaskWorker::class.java
            else -> InsertTaskWorker::class.java
        }

    }
}
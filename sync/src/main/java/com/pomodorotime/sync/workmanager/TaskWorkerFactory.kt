package com.pomodorotime.sync.workmanager

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.pomodorotime.domain.task.ITaskRepository
import com.pomodorotime.sync.workmanager.workers.InsertTaskWorker

class TaskWorkerFactory(
    private val repository: com.pomodorotime.domain.task.ITaskRepository
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            InsertTaskWorker::class.java.name -> InsertTaskWorker(
                appContext,
                workerParameters, repository
            )
            else ->
                null
        }
    }
}
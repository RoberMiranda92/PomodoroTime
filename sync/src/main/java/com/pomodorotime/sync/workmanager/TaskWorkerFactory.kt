package com.pomodorotime.sync.workmanager

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.pomodorotime.data.task.datasource.remote.ITaskRemoteDataSource
import com.pomodorotime.data.user.IUserLocalDataSource
import com.pomodorotime.sync.workmanager.workers.DeleteTaskWorker
import com.pomodorotime.sync.workmanager.workers.InsertTaskWorker

class TaskWorkerFactory(
    private val userDataSource: IUserLocalDataSource,
    private val dataSource: ITaskRemoteDataSource
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            InsertTaskWorker::class.java.name -> InsertTaskWorker(
                appContext,
                workerParameters, userDataSource, dataSource
            )
            DeleteTaskWorker::class.java.name -> DeleteTaskWorker(
                appContext,
                workerParameters, userDataSource, dataSource
            )
            else ->
                null
        }
    }
}
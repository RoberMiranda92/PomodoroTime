package com.pomodorotime.sync.workmanager

import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import com.pomodorotime.data.task.datasource.remote.ITaskRemoteDataSource
import com.pomodorotime.data.user.IUserLocalDataSource

class WorkManagerConfiguration(
    private val userDataSource: IUserLocalDataSource,
    private val dataSource: ITaskRemoteDataSource
) : Configuration.Provider {

    override fun getWorkManagerConfiguration(): Configuration {
        val myWorkerFactory = DelegatingWorkerFactory()
        myWorkerFactory.addFactory(TaskWorkerFactory(userDataSource, dataSource))

        return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .setWorkerFactory(myWorkerFactory)
            .build()
    }
}
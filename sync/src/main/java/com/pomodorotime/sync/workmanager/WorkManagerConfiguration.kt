package com.pomodorotime.sync.workmanager

import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import com.pomodorotime.domain.task.ITaskRepository

class WorkManagerConfiguration(
    private val repository: com.pomodorotime.domain.task.ITaskRepository
) : Configuration.Provider {

    override fun getWorkManagerConfiguration(): Configuration {
        val myWorkerFactory = DelegatingWorkerFactory()
        myWorkerFactory.addFactory(TaskWorkerFactory(repository))

        return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .setWorkerFactory(myWorkerFactory)
            .build()
    }
}
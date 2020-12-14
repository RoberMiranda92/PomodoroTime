package com.pomodorotime.sync

import android.content.Context
import com.pomodorotime.core.session.ISessionManager
import com.pomodorotime.data.sync.ISyncErrorHandler
import com.pomodorotime.data.sync.ISyncManager
import com.pomodorotime.data.sync.SyncTypes
import com.pomodorotime.data.task.api.models.ApiTask
import com.pomodorotime.data.task.datasource.remote.ITaskRemoteDataSource
import com.pomodorotime.data.user.IUserLocalDataSource
import com.pomodorotime.sync.di.Synchronizer
import com.pomodorotime.sync.workmanager.WorkManagerSynchronizer

class SyncManager(
    context: Context,
    userLocalDataSourceImpl: IUserLocalDataSource,
    taskRemoteDataSource: ITaskRemoteDataSource,
    errorHandler: ISyncErrorHandler,
    sessionManager: ISessionManager
) : ISyncManager {

    private var synchronizer: Synchronizer =
        WorkManagerSynchronizer(
            context,
            userLocalDataSourceImpl,
            taskRemoteDataSource,
            errorHandler,
            sessionManager
        )

    override fun performSyncInsertion(task: ApiTask) {
        synchronizer.performSync(task, SyncTypes.INSERT)
    }

    override fun performSyncDeletion(taskId: Long) {
        synchronizer.performSync(taskId, SyncTypes.DELETE)
    }

    override fun clear() {
        synchronizer.clear()
    }

}
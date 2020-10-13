package com.pomodorotime.sync

import android.content.Context
import com.pomodorotime.data.task.ITaskRepository
import com.pomodorotime.data.task.TaskDataModel
import com.pomodorotime.sync.di.Synchronizer
import com.pomodorotime.sync.workmanager.WorkManagerSynchronizer

class SyncManager(
    context: Context,
    repository: ITaskRepository
) {

    private var synchronizer: Synchronizer = WorkManagerSynchronizer(context, repository)

    fun performSync(task: TaskDataModel, type: SyncTypes) {
        synchronizer.performSync(task, type)
    }

}
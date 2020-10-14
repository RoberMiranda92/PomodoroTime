package com.pomodorotime.sync

import android.content.Context
import com.pomodorotime.domain.models.Task
import com.pomodorotime.domain.task.ITaskRepository
import com.pomodorotime.sync.di.Synchronizer
import com.pomodorotime.sync.workmanager.WorkManagerSynchronizer

class SyncManager(
    context: Context,
    repository: ITaskRepository
) {

    private var synchronizer: Synchronizer = WorkManagerSynchronizer(context, repository)

    fun performSync(task: Task, type: SyncTypes) {
        synchronizer.performSync(task, type)
    }

}
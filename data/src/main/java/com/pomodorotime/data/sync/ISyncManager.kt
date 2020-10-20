package com.pomodorotime.data.sync

import com.pomodorotime.data.task.api.models.ApiTask

interface ISyncManager {

    fun performSyncInsertion(task: ApiTask)

    fun performSyncDeletion(taskId: Long)

}
package com.pomodorotime.sync.di

import com.pomodorotime.data.sync.SyncTypes
import com.pomodorotime.data.task.api.models.ApiTask

interface Synchronizer {

    fun performSync(task: ApiTask, type: SyncTypes)

    fun performSync(taskId: Long, type: SyncTypes)

    fun clear()
}
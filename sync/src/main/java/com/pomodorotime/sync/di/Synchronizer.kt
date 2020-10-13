package com.pomodorotime.sync.di

import com.pomodorotime.data.task.TaskDataModel
import com.pomodorotime.sync.SyncTypes

interface Synchronizer {

    fun performSync(task: TaskDataModel, type: SyncTypes)
}
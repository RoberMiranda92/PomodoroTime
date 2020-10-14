package com.pomodorotime.sync.di

import com.pomodorotime.domain.models.Task
import com.pomodorotime.sync.SyncTypes

interface Synchronizer {

    fun performSync(task: Task, type: SyncTypes)
}
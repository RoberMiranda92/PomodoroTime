package com.pomodorotime.data.sync

interface ISyncErrorHandler {
    fun getSyncError(throwable: Throwable): SyncError
}
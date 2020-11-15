package com.pomodorotime.data.sync


sealed class SyncError {
    data class GenericError(val code: Int? = null, val message: String) : SyncError()
    object NetworkError : SyncError()
    data class DataBaseError(val code: Int? = null, val message: String) : SyncError()
}
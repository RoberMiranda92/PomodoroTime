package com.pomodorotime.domain.models

sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T) : ResultWrapper<T>()
    data class Error(val error: ErrorEntity) : ResultWrapper<Nothing>()
}
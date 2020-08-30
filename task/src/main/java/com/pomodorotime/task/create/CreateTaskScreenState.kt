package com.pomodorotime.task.create

import com.pomodorotime.data.ErrorResponse

sealed class CreateTaskScreenState {
    object Initial : CreateTaskScreenState()
    object InvalidName : CreateTaskScreenState()
    object Loading : CreateTaskScreenState()
    data class Error(val error: ErrorResponse) : CreateTaskScreenState()
    object Success : CreateTaskScreenState()
}

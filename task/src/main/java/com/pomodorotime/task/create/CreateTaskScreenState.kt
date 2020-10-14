package com.pomodorotime.task.create

sealed class CreateTaskScreenState {
    data class Initial(val name: String = "", val estimated: Int = 0) : CreateTaskScreenState()
    object InvalidName : CreateTaskScreenState()
    object Loading : CreateTaskScreenState()
    object Success : CreateTaskScreenState()
}

package com.pomodorotime.task

interface TaskNavigator {

    fun navigateOnToCreateTask()

    fun navigateOnToTimer(id: Long, name: String?)

    fun onBack()

}
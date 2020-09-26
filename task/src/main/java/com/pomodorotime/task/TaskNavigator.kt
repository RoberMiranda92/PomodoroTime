package com.pomodorotime.task

interface TaskNavigator {

    fun navigateOnToCreateTask()

    fun navigateOnToTimer(id: Int, name: String?)

    fun onBack()

}
package com.pomodorotime

import androidx.navigation.NavController
import com.pomodorotime.login.LoginFragmentDirections.Companion.actionLoginFragmentToTaskList
import com.pomodorotime.login.LoginNavigator
import com.pomodorotime.task.TaskNavigator
import com.pomodorotime.task.taskList.TaskListFragmentDirections.Companion.actionTaskListFragmentToCreateTask
import com.pomodorotime.task.taskList.TaskListFragmentDirections.Companion.actionTaskListFragmentToTimer

class RouteNavigator : LoginNavigator, TaskNavigator {

    private var navController: NavController? = null

    override fun navigateOnLoginSuccess() {
        navController?.navigate(actionLoginFragmentToTaskList())
    }

    override fun navigateOnToCreateTask() {
        navController?.navigate(actionTaskListFragmentToCreateTask())
    }

    override fun navigateOnToTimer(id: Int) {
        navController?.navigate(actionTaskListFragmentToTimer(id))
    }

    fun bind(navController: NavController) {
        this.navController = navController
    }

    fun unbind() {
        navController = null
    }

    fun onBack() {
        navController?.popBackStack()
    }


}
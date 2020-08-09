package com.pomodorotime

import androidx.navigation.NavController
import com.pomodorotime.login.LoginFragmentDirections.Companion.actionLoginFragmentToTaskList
import com.pomodorotime.login.LoginNavigator

class RouteNavigator : LoginNavigator {

    private var navController: NavController? = null

    override fun navigateOnLoginSuccess() {
        navController?.navigate(actionLoginFragmentToTaskList())
    }

    fun bind(navController: NavController) {
        this.navController = navController
    }

    fun unbind() {
        navController = null
    }
}
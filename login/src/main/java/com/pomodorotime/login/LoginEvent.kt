package com.pomodorotime.login

sealed class LoginEvent {

    data class LoginTyping(val user: String, val password: String, val confirmPassword: String) :
        LoginEvent()
    object MainButtonPress : LoginEvent()
    object SecondaryButtonPress : LoginEvent()

}
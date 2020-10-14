package com.pomodorotime.login


sealed class LoginScreenState {

    object SignIn : LoginScreenState()
    object SignUp : LoginScreenState()
    object Loading : LoginScreenState()
}
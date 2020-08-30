package com.pomodorotime.login


sealed class LoginScreenState {

    object SignIn : LoginScreenState()
    object SignUp : LoginScreenState()
    object Loading : LoginScreenState()
    data class Error(val error: String) : LoginScreenState()
    data class EmailError(val error: String) : LoginScreenState()
    data class PasswordError(val error: String) : LoginScreenState()
    object Success : LoginScreenState()
}
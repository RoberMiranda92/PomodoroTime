package com.pomodorotime.domain.models

sealed class ErrorEntity {
    data class GenericError(val code: Int? = null, val message: String) : ErrorEntity()
    data class UserEmailError(val message: String) : ErrorEntity()
    data class UserPasswordError(val message: String) : ErrorEntity()

    object NetworkError : ErrorEntity()
}
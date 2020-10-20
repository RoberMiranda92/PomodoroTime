package com.pomodorotime.data.user

interface IUserLocalDataSource {

    fun getUserId(): String

    fun getEmail(): String
}
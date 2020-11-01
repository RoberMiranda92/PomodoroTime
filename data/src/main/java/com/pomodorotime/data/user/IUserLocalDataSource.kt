package com.pomodorotime.data.user

import com.pomodorotime.data.login.api.models.ApiUser

interface IUserLocalDataSource {

    fun setUser(user: ApiUser)

    fun getUserId(): String

    fun getEmail(): String

    fun clear()

    fun saveToken(token: String)
}
package com.pomodorotime.data.user

import com.pomodorotime.data.login.api.models.ApiUser

interface IUserLocalDataSource {

    fun setUser(user: ApiUser)

    fun getEmail(): String

    fun clear()

    suspend fun saveToken(token: String)

    suspend fun getToken(): String
}
package com.pomodorotime.data.user

interface IUserLocalDataSource {

    suspend fun clearToken()

    suspend fun saveToken(token: String)

    suspend fun getToken(): String
}
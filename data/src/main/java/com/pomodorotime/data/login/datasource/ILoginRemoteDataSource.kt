package com.pomodorotime.data.login.datasource

import com.pomodorotime.data.login.api.models.ApiUser

interface ILoginRemoteDataSource {

    suspend fun signIn(email: String, password: String): ApiUser

    suspend fun signUp(email: String, password: String): ApiUser
}
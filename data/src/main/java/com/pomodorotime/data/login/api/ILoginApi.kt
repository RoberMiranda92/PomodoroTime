package com.pomodorotime.data.login.api

import com.pomodorotime.data.login.api.models.ApiUser

interface ILoginApi {
    suspend fun signIn(email: String, password: String): ApiUser

    suspend fun signUp(email: String, password: String): ApiUser
}
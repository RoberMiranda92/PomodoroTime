package com.pomodorotime.data.login.repository

import com.pomodorotime.data.ApiUser
import com.pomodorotime.data.ResultWrapper

interface RemoteLoginRepository {

    suspend fun signIn(email: String, password: String): ResultWrapper<ApiUser>

    suspend fun signUp(email: String, password: String): ResultWrapper<ApiUser>
}
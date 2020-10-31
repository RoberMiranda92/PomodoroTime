package com.pomodorotime.data.login.datasource

import com.pomodorotime.data.login.api.ILoginApi
import com.pomodorotime.data.login.api.models.ApiUser

class LoginRemoteDataSourceImpl(private val api: ILoginApi): ILoginRemoteDataSource {

    override suspend fun signIn(email: String, password: String): ApiUser {
        return api.signIn(email, password)
    }

    override suspend fun signUp(email: String, password: String): ApiUser {
        return api.signUp(email, password)
    }
}
package com.pomodorotime.data.login.datasource

import com.pomodorotime.data.login.api.FirebaseLoginApi
import com.pomodorotime.data.login.api.ILoginApi
import com.pomodorotime.data.login.api.models.ApiUser

class LoginRemoteDataSource(private val api: ILoginApi) {

    suspend fun signIn(email: String, password: String): ApiUser {
        return api.signIn(email, password)
    }

    suspend fun signUp(email: String, password: String): ApiUser {
        return api.signUp(email, password)
    }

    companion object {

        fun getNewInstance(): LoginRemoteDataSource {
            return LoginRemoteDataSource(FirebaseLoginApi())
        }
    }
}
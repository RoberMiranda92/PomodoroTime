package com.pomodorotime.domain.login

import com.pomodorotime.domain.models.User


interface ILoginRepository {

    suspend fun signIn(email: String, password: String): User

    suspend fun signUp(email: String, password: String): User

    suspend fun saveUserToken(token: String)

    suspend fun getUserToken(): String
}
package com.robertomiranda.data.login.api

import com.robertomiranda.data.ApiUser

interface ILoginApi {
    suspend fun signIn(email: String, password: String): ApiUser

    suspend fun signUp(email: String, password: String): ApiUser
}
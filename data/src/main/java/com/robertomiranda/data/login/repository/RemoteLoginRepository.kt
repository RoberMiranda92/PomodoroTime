package com.robertomiranda.data.login.repository

import com.robertomiranda.data.ApiUser
import com.robertomiranda.data.ResultWrapper

interface RemoteLoginRepository {

    suspend fun signIn(email: String, password: String): ResultWrapper<ApiUser>

    suspend fun singUp(email: String, password: String): ResultWrapper<ApiUser>
}
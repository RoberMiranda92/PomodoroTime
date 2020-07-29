package com.robertomiranda.data.login.repository

import com.robertomiranda.data.ApiUser
import com.robertomiranda.data.BaseRepository
import com.robertomiranda.data.ResultWrapper
import com.robertomiranda.data.login.api.FirebaseLoginApi
import com.robertomiranda.data.login.api.ILoginApi
import kotlinx.coroutines.Dispatchers

class LoginRepository(private val api: ILoginApi) : BaseRepository(), RemoteLoginRepository {

    override suspend fun signIn(email: String, password: String): ResultWrapper<ApiUser> {
        return safeApiCall(Dispatchers.IO) {
            api.signIn(email, password)
        }
    }

    override suspend fun singUp(email: String, password: String): ResultWrapper<ApiUser> {
        return safeApiCall(Dispatchers.IO) {
            api.signUp(email, password)
        }
    }

    companion object {
        fun getNewInstance(): LoginRepository {
            return LoginRepository(FirebaseLoginApi())
        }
    }
}
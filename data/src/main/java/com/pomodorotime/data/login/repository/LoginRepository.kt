package com.pomodorotime.data.login.repository

import com.pomodorotime.data.ApiUser
import com.pomodorotime.data.BaseRepository
import com.pomodorotime.data.login.api.FirebaseLoginApi
import com.pomodorotime.data.login.api.ILoginApi
import com.pomodorotimemiranda.data.ResultWrapper
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
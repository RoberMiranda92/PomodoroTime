package com.pomodorotime.data.user

import com.pomodorotime.data.login.api.models.ApiUser
import com.pomodorotime.data.preferences.ISharedPreferences

class UserLocalDataSourceImp(
    private val sharedPreferences: ISharedPreferences
) : IUserLocalDataSource {

    private var user: ApiUser? = null

    override fun setUser(user: ApiUser) {
        this.user = user
    }

    override fun getUserId() = user?.id ?: ""

    override fun getEmail() = user?.email ?: ""

    override fun clear() {
        this.user = null
    }

    override suspend fun saveToken(token: String) {
        sharedPreferences.putString(USER_TOKEN, token)
    }

    override suspend fun getToken(): String {
        return sharedPreferences.getString(USER_TOKEN)
    }

    companion object {
        const val USER_TOKEN = "pref.user.token"
    }
}
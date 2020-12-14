package com.pomodorotime.data.user

import com.pomodorotime.data.preferences.ISharedPreferences

class UserLocalDataSourceImp(
    private val sharedPreferences: ISharedPreferences
) : IUserLocalDataSource {

    override suspend fun saveToken(token: String) {
        sharedPreferences.putString(USER_TOKEN, token)
    }

    override suspend fun getToken(): String {
        return sharedPreferences.getString(USER_TOKEN)
    }

    override suspend fun clearToken() {
        sharedPreferences.removeKey(USER_TOKEN)
    }

    companion object {
        const val USER_TOKEN = "pref.user.token"
    }
}
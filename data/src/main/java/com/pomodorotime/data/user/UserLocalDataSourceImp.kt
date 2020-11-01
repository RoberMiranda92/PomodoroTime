package com.pomodorotime.data.user

import com.pomodorotime.data.login.api.models.ApiUser

object UserLocalDataSourceImp : IUserLocalDataSource {

    private var user: ApiUser? = null

    override fun setUser(user: ApiUser) {
        this.user = user
    }

    override fun getUserId() = user?.id ?: ""

    override fun getEmail() = user?.email ?: ""

    override fun clear() {
        this.user = null
    }

    override fun saveToken(token: String) {

    }
}
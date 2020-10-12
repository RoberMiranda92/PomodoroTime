package com.pomodorotime.data.user

import com.pomodorotime.data.login.api.models.ApiUser

object UserLocalDataSource {

    var user: ApiUser? = null

    fun getUserId() = user?.id ?: ""

    fun getEmail() = user?.email ?: ""
}
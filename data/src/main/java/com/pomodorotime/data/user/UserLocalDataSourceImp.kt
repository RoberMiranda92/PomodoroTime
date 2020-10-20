package com.pomodorotime.data.user

import com.pomodorotime.data.login.api.models.ApiUser

object UserLocalDataSourceImp: IUserLocalDataSource {

    var user: ApiUser? = null

    override fun getUserId() = user?.id ?: ""

    override fun getEmail() = user?.email ?: ""
}
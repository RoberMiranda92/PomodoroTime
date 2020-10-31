package com.pomodorotime.data.login

import com.pomodorotime.data.login.api.models.ApiUser
import com.pomodorotime.domain.models.User

fun User.toApiModel() =
    ApiUser(
        email,
        id,
        token
    )

fun ApiUser.toDomainModel() =
    User(
        email,
        id,
        token
    )
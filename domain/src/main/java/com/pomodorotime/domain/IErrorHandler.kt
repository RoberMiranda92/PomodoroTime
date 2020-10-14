package com.pomodorotime.domain

import com.pomodorotime.domain.models.ErrorEntity

interface IErrorHandler {

    fun getError(throwable: Throwable): ErrorEntity
}
package com.pomodorotime.domain

import androidx.annotation.CallSuper
import com.pomodorotime.data.ErrorResponse
import com.pomodorotime.data.ResultWrapper
import java.io.IOException

abstract class UseCase<out R> {

    @CallSuper
    protected open fun <R> manageException(throwable: Throwable): ResultWrapper<R> {
        return when (throwable) {
            is IOException -> ResultWrapper.NetworkError
            else -> {
                ResultWrapper.GenericError(
                    null,
                    ErrorResponse(message = throwable.message ?: "")
                )
            }
        }
    }
}
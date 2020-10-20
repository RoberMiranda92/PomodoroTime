package com.pomodorotime.data

import com.google.firebase.auth.FirebaseAuthException
import com.pomodorotime.domain.IErrorHandler
import com.pomodorotime.domain.models.ErrorEntity
import okio.IOException
import retrofit2.HttpException

open class ErrorHandlerImpl : IErrorHandler {

//    private fun convertErrorBody(throwable: HttpException): ErrorResponse {
//        return try {
//            val string = throwable.response()?.errorBody()?.string()
//            return Gson().fromJson(string, ErrorResponse::class.java)
//
//        } catch (exception: Exception) {
//            ErrorResponse(message = throwable.message ?: "")
//        }
//    }

    override fun getError(throwable: Throwable): ErrorEntity {
        return when (throwable) {
            is IOException -> ErrorEntity.NetworkError
            is HttpException -> {
                val code = throwable.code()
                val message = throwable.message ?: ""
                ErrorEntity.GenericError(code, message)
            }
            is FirebaseAuthException -> {
                loginErrorCodeMapper(throwable)
            }
            else -> {
                ErrorEntity.GenericError(
                    null,
                    throwable.message ?: ""
                )
            }
        }
    }

    private fun loginErrorCodeMapper(throwable: FirebaseAuthException): ErrorEntity {
        return when (throwable.errorCode) {
            "ERROR_OPERATION_NOT_ALLOWED" -> ErrorEntity.GenericError(
                message = throwable.message ?: ""
            )
            "ERROR_WEAK_PASSWORD" -> ErrorEntity.UserPasswordError(throwable.message ?: "")
            "ERROR_INVALID_EMAIL" -> ErrorEntity.UserEmailError(throwable.message ?: "")
            "ERROR_WRONG_PASSWORD" -> ErrorEntity.UserPasswordError(throwable.message ?: "")
            "ERROR_EMAIL_ALREADY_IN_USE" -> ErrorEntity.UserEmailError(throwable.message ?: "")
            "ERROR_INVALID_CREDENTIAL" -> ErrorEntity.UserEmailError(throwable.message ?: "")
            "ERROR_USER_NOT_FOUND" -> ErrorEntity.UserEmailError(throwable.message ?: "")
            "ERROR_USER_DISABLED" -> ErrorEntity.UserEmailError(throwable.message ?: "")
            "ERROR_TOO_MANY_REQUESTS" -> ErrorEntity.GenericError(message = throwable.message ?: "")
            else -> ErrorEntity.GenericError(message = throwable.message ?: "")
        }
    }
}
package com.pomodorotime.data

import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.pomodorotime.data.sync.ISyncErrorHandler
import com.pomodorotime.data.sync.SyncError
import com.pomodorotime.domain.IErrorHandler
import com.pomodorotime.domain.models.ErrorEntity
import okio.IOException
import retrofit2.HttpException

class ErrorHandlerImpl : IErrorHandler, ISyncErrorHandler {

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

    override fun getSyncError(throwable: Throwable): SyncError {
        return when (throwable) {
            is IOException -> SyncError.NetworkError
            is DatabaseException -> dataBaseErrorCodeMapper(throwable)
            else -> {
                SyncError.GenericError(
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

    private fun dataBaseErrorCodeMapper(throwable: DatabaseException): SyncError {
        val error = DatabaseError.fromException(throwable)
        when (error.code) {
            DatabaseError.DATA_STALE,
            DatabaseError.OPERATION_FAILED,
            DatabaseError.WRITE_CANCELED,
            DatabaseError.UNKNOWN_ERROR,
            DatabaseError.MAX_RETRIES,
            DatabaseError.OVERRIDDEN_BY_SET,
            DatabaseError.UNAVAILABLE -> {
                SyncError.DataBaseError(error.code, throwable.message ?: "")
            }
            DatabaseError.PERMISSION_DENIED,
            DatabaseError.EXPIRED_TOKEN,
            DatabaseError.INVALID_TOKEN,
            DatabaseError.USER_CODE_EXCEPTION -> {
                SyncError.InvalidUser(error.code, throwable.message ?: "")
            }

            DatabaseError.NETWORK_ERROR -> {
                SyncError.NetworkError
            }
        }
        return SyncError.NetworkError
    }
}
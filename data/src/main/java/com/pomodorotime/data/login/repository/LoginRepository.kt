package com.pomodorotime.data.login.repository

import com.google.firebase.auth.FirebaseAuthException
import com.pomodorotime.data.BaseRepository
import com.pomodorotime.data.ErrorResponse
import com.pomodorotime.data.ResultWrapper
import com.pomodorotime.data.login.api.models.ApiUser
import com.pomodorotime.data.login.datasource.LoginRemoteDataSource
import com.pomodorotime.data.user.UserLocalDataSource
import kotlinx.coroutines.Dispatchers

class LoginRepository(
    private val remoteDataSource: LoginRemoteDataSource,
    private val userLocalDataSource: UserLocalDataSource
) : BaseRepository(),
    RemoteLoginRepository {

    override suspend fun signIn(email: String, password: String): ResultWrapper<ApiUser> {
        val result = safeApiCall(Dispatchers.IO) {
            remoteDataSource.signIn(email, password)
        }
        if (result is ResultWrapper.Success) {
            saveUser(result.value)
        }
        return result
    }

    override suspend fun signUp(email: String, password: String): ResultWrapper<ApiUser> {
        val result = safeApiCall(Dispatchers.IO) {
            remoteDataSource.signUp(email, password)
        }
        if (result is ResultWrapper.Success) {
            saveUser(result.value)
        }
        return result
    }

    private fun saveUser(value: ApiUser) {
        userLocalDataSource.user = value
    }

    override fun <T> manageException(throwable: Throwable): ResultWrapper<T> {
        return when (throwable) {
            is FirebaseAuthException -> {
                val code = loginErrorCodeMapper(throwable)
                ResultWrapper.GenericError(
                    code = code,
                    error = ErrorResponse(code = code, message = throwable.message ?: "")
                )
            }
            else -> super.manageException(throwable)

        }
    }

    private fun loginErrorCodeMapper(throwable: FirebaseAuthException): Int {
        return when (throwable.errorCode) {
            "ERROR_OPERATION_NOT_ALLOWED" -> ERROR_OPERATION_NOT_ALLOWED
            "ERROR_WEAK_PASSWORD" -> ERROR_WEAK_PASSWORD
            "ERROR_INVALID_EMAIL" -> ERROR_INVALID_EMAIL
            "ERROR_WRONG_PASSWORD" -> ERROR_WRONG_PASSWORD
            "ERROR_EMAIL_ALREADY_IN_USE" -> ERROR_EMAIL_ALREADY_IN_USE
            "ERROR_INVALID_CREDENTIAL" -> ERROR_INVALID_CREDENTIAL
            "ERROR_USER_NOT_FOUND" -> ERROR_USER_NOT_FOUND
            "ERROR_USER_DISABLED" -> ERROR_USER_DISABLED
            "ERROR_TOO_MANY_REQUESTS" -> ERROR_TOO_MANY_REQUESTS
            else -> -1
        }
    }

    companion object {
        const val ERROR_INVALID_EMAIL = 100
        const val ERROR_WRONG_PASSWORD = 101
        const val ERROR_OPERATION_NOT_ALLOWED = 102
        const val ERROR_WEAK_PASSWORD = 103
        const val ERROR_EMAIL_ALREADY_IN_USE = 104
        const val ERROR_INVALID_CREDENTIAL = 105
        const val ERROR_USER_NOT_FOUND = 106
        const val ERROR_USER_DISABLED = 107
        const val ERROR_TOO_MANY_REQUESTS = 108

        fun getNewInstance(): LoginRepository {
            return LoginRepository(
                LoginRemoteDataSource.getNewInstance(),
                UserLocalDataSource
            )
        }
    }
}
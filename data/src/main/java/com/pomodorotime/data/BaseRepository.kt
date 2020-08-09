package com.pomodorotime.data

import androidx.annotation.CallSuper
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okio.IOException
import retrofit2.HttpException

open class BaseRepository {

    suspend fun <T> safeApiCall(
        dispatcher: CoroutineDispatcher, apiCall: suspend () -> T
    ): ResultWrapper<T> {
        return withContext(dispatcher) {
            encapsulateResult(apiCall)
        }
    }

    private suspend fun <T> encapsulateResult(
        apiCall: suspend () -> T
    ): ResultWrapper<T> {
        return try {
            ResultWrapper.Success(
                apiCall.invoke()
            )
        } catch (throwable: Throwable) {
            manageException(throwable)
        }
    }

    @CallSuper
   open protected fun <T> manageException(throwable: Throwable): ResultWrapper<T> {
        return when (throwable) {
            is IOException -> ResultWrapper.NetworkError
            is HttpException -> {
                val code = throwable.code()
                val errorResponse = convertErrorBody(throwable)
                ResultWrapper.GenericError(code, errorResponse)
            }
            else -> {
                ResultWrapper.GenericError(
                    null,
                    ErrorResponse(message = throwable.message ?: "")
                )
            }
        }
    }

    private fun convertErrorBody(throwable: HttpException): ErrorResponse {
        return try {
            val string = throwable.response()?.errorBody()?.string()
            return Gson().fromJson(string, ErrorResponse::class.java)

        } catch (exception: Exception) {
            ErrorResponse(message = throwable.message ?: "")
        }
    }
}
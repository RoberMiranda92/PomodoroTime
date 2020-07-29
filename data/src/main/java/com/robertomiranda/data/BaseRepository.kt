package com.robertomiranda.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.IOException

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
            when (throwable) {
                is IOException -> ResultWrapper.NetworkError
//                is HttpException -> {
//                    val code = throwable.code()
//                    val errorResponse = convertErrorBody(throwable)
//                    ResultWrapper.GenericError(code, errorResponse)
//                }
                else -> {
                    ResultWrapper.GenericError(
                        null,
                        ErrorResponse(message = throwable.message ?: "")
                    )
                }
            }
        }
    }

//    private fun convertErrorBody(throwable: HttpException): ErrorResponse? {
//        return try {
//            val string = throwable.response()?.errorBody()?.string()
//            return Gson().fromJson(string, ErrorResponse::class.java)
//
//        } catch (exception: Exception) {
//            null
//        }
//    }
}
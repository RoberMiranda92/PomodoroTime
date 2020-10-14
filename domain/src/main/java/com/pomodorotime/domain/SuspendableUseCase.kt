package com.pomodorotime.domain

import com.pomodorotime.domain.models.ResultWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

abstract class SuspendableUseCase<in P, out R>(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val errorHandler: IErrorHandler
) {

    suspend operator fun invoke(parameters: P): ResultWrapper<R> {
        return withContext(coroutineDispatcher) {
            try {
                ResultWrapper.Success(execute(parameters))
            } catch (throwable: Throwable) {
                ResultWrapper.Error(errorHandler.getError(throwable))
            }
        }
    }

    protected abstract suspend fun execute(parameters: P): R
}
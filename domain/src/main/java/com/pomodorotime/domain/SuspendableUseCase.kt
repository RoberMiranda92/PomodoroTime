package com.pomodorotime.domain

import com.pomodorotime.data.ResultWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

abstract class SuspendableUseCase<in P, out R>(private val coroutineDispatcher: CoroutineDispatcher) :
    UseCase<R>() {

    suspend operator fun invoke(parameters: P): ResultWrapper<R> {
        return withContext(coroutineDispatcher) {
            try {
                ResultWrapper.Success(execute(parameters))
            } catch (throwable: Throwable) {
                manageException(throwable)
            }
        }
    }

    protected abstract suspend fun execute(parameters: P): R
}
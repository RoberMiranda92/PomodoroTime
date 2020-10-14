package com.pomodorotime.domain

import com.pomodorotime.domain.models.ResultWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

abstract class FlowUseCase<in P, out R>(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val errorHandler: IErrorHandler
) {

    operator fun invoke(parameters: P): Flow<ResultWrapper<R>> {
        return flow {
            try {
                execute(parameters).collect {
                    emit(ResultWrapper.Success(it))
                }
            } catch (throwable: Throwable) {
                emit(ResultWrapper.Error(errorHandler.getError(throwable)))
            }
        }.flowOn(coroutineDispatcher)
    }

    protected abstract fun execute(parameters: P): Flow<R>
}
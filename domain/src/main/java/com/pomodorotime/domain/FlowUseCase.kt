package com.pomodorotime.domain

import com.pomodorotime.data.ResultWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

abstract class FlowUseCase<in P, out R>(
    private val coroutineDispatcher: CoroutineDispatcher
) : UseCase<R>() {

    operator fun invoke(parameters: P): Flow<ResultWrapper<R>> {
        return flow<ResultWrapper<R>> {
            try {
                execute(parameters).collect {
                    emit(ResultWrapper.Success(it))
                }
            } catch (throwable: Throwable) {
                emit(manageException(throwable))
            }
        }.flowOn(coroutineDispatcher)
    }

    protected abstract fun execute(parameters: P): Flow<R>
}
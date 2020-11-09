package com.pomodorotime.domain.login.usecases

import com.pomodorotime.domain.IErrorHandler
import com.pomodorotime.domain.SuspendableUseCase
import com.pomodorotime.domain.login.ILoginRepository
import kotlinx.coroutines.CoroutineDispatcher

class IsUserLoggedUseCase(
    private val repository: ILoginRepository,
    coroutineDispatcher: CoroutineDispatcher,
    errorHandler: IErrorHandler
) : SuspendableUseCase<IsUserLoggedUseCase.IsUserLoggedParams, Boolean>(
    coroutineDispatcher,
    errorHandler
) {

    object IsUserLoggedParams

    override suspend fun execute(parameters: IsUserLoggedParams): Boolean {
        return repository.getUserToken().isNotEmpty()
    }
}
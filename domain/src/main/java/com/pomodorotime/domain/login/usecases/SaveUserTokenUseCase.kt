package com.pomodorotime.domain.login.usecases

import com.pomodorotime.domain.IErrorHandler
import com.pomodorotime.domain.SuspendableUseCase
import com.pomodorotime.domain.login.ILoginRepository
import kotlinx.coroutines.CoroutineDispatcher

class SaveUserTokenUseCase(
    private val repository: ILoginRepository,
    coroutineDispatcher: CoroutineDispatcher,
    errorHandler: IErrorHandler
) : SuspendableUseCase<SaveUserTokenUseCase.SaveUserTokenParams, Unit>(coroutineDispatcher, errorHandler) {

    data class SaveUserTokenParams(val token: String)

    override suspend fun execute(parameters: SaveUserTokenParams): Unit =
        repository.saveUserToken(parameters.token)
}
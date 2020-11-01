package com.pomodorotime.domain.login.usecases

import com.pomodorotime.domain.IErrorHandler
import com.pomodorotime.domain.SuspendableUseCase
import com.pomodorotime.domain.login.ILoginRepository
import com.pomodorotime.domain.models.User
import kotlinx.coroutines.CoroutineDispatcher

class SigUpUseCase(
    private val repository: ILoginRepository,
    coroutineDispatcher: CoroutineDispatcher,
    errorHandler: IErrorHandler
) : SuspendableUseCase<SigUpUseCase.SignUpParams, User>(coroutineDispatcher, errorHandler) {

    data class SignUpParams(val email: String, val password: String)

    override suspend fun execute(parameters: SignUpParams): User =
        repository.signUp(parameters.email, parameters.password)
}
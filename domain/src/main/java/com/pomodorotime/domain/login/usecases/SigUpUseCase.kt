package com.pomodorotime.domain.login.usecases

import com.pomodorotime.domain.IErrorHandler
import com.pomodorotime.domain.SuspendableUseCase
import com.pomodorotime.domain.login.ILoginRepository
import com.pomodorotime.domain.models.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class SigUpUseCase(
    private val repository: ILoginRepository,
    errorHandler: IErrorHandler,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SuspendableUseCase<SigUpUseCase.SignUpParams, User>(coroutineDispatcher, errorHandler) {

    data class SignUpParams(val email: String, val password: String)

    override suspend fun execute(parameters: SignUpParams): User =
        repository.signUp(parameters.email, parameters.password)
}
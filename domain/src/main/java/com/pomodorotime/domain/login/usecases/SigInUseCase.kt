package com.pomodorotime.domain.login.usecases


import com.pomodorotime.domain.IErrorHandler
import com.pomodorotime.domain.SuspendableUseCase
import com.pomodorotime.domain.login.ILoginRepository
import com.pomodorotime.domain.models.User
import kotlinx.coroutines.CoroutineDispatcher

class SigInUseCase(
    private val repository: ILoginRepository,
    coroutineDispatcher: CoroutineDispatcher,
    errorHandler: IErrorHandler
) : SuspendableUseCase<SigInUseCase.SignInParams, User>(coroutineDispatcher, errorHandler) {

    data class SignInParams(val email: String, val password: String)

    override suspend fun execute(parameters: SignInParams): User =
        repository.signIn(parameters.email, parameters.password)
}
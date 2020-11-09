package com.pomodorotime.domain.di

import com.pomodorotime.domain.login.usecases.IsUserLoggedUseCase
import com.pomodorotime.domain.login.usecases.SaveUserTokenUseCase
import com.pomodorotime.domain.login.usecases.SigInUseCase
import com.pomodorotime.domain.login.usecases.SigUpUseCase
import com.pomodorotime.domain.task.usecases.CreateTaskUseCase
import com.pomodorotime.domain.task.usecases.DeleteTaskUseCase
import com.pomodorotime.domain.task.usecases.GetAllTaskUseCase
import com.pomodorotime.domain.timer.GetTaskByIdUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

val domainModule = module {

    factory { SigUpUseCase(get(), get(), get()) }
    factory { SigInUseCase(get(), get(), get()) }
    factory { SaveUserTokenUseCase(get(), get(), get()) }
    factory { IsUserLoggedUseCase(get(), get(), get()) }

    factory { GetAllTaskUseCase(get(), get(), get()) }
    factory { CreateTaskUseCase(get(), get(), get()) }
    factory { DeleteTaskUseCase(get(), get(), get()) }

    factory { GetTaskByIdUseCase(get(), get(), get()) }

    single<CoroutineDispatcher> { Dispatchers.IO }
}
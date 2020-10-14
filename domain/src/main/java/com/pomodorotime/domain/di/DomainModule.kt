package com.pomodorotime.domain.di

import com.pomodorotime.domain.login.usecases.SigInUseCase
import com.pomodorotime.domain.login.usecases.SigUpUseCase
import com.pomodorotime.domain.task.usecases.CreateTaskUseCase
import com.pomodorotime.domain.task.usecases.DeleteTaskUseCase
import com.pomodorotime.domain.task.usecases.GetAllTaskUseCase
import com.pomodorotime.domain.timer.GetTaskByIdUseCase
import org.koin.dsl.module

val domainModule = module {

    factory { SigUpUseCase(get(), errorHandler = get()) }
    factory { SigInUseCase(get(), errorHandler = get()) }

    factory { GetAllTaskUseCase(get(), errorHandler = get()) }
    factory { CreateTaskUseCase(get(), errorHandler = get()) }
    factory { DeleteTaskUseCase(get(), errorHandler = get()) }

    factory { GetTaskByIdUseCase(get(), errorHandler = get()) }
}
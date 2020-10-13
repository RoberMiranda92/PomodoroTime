package com.pomodorotime.domain.di

import com.pomodorotime.domain.task.CreateTaskUseCase
import com.pomodorotime.domain.task.DeleteTaskUseCase
import com.pomodorotime.domain.task.GetAllTaskUseCase
import com.pomodorotime.domain.timer.GetTaskById
import org.koin.dsl.module

val domainModule = module {

    factory { GetAllTaskUseCase(get()) }
    factory { CreateTaskUseCase(get()) }
    factory { DeleteTaskUseCase(get()) }
    factory { GetTaskById(get()) }

}
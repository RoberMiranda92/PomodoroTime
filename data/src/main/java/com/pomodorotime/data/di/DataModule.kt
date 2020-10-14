package com.pomodorotime.data.di

import com.pomodorotime.data.ErrorHandlerImpl
import com.pomodorotime.data.login.repository.LoginRepository
import com.pomodorotime.data.task.TaskRepository
import com.pomodorotime.domain.IErrorHandler
import com.pomodorotime.domain.login.ILoginRepository
import com.pomodorotime.domain.task.ITaskRepository
import org.koin.dsl.module

val dataModule = module {

    single<ILoginRepository> { LoginRepository.getNewInstance() }

    single<ITaskRepository> { TaskRepository.getNewInstance(get()) }

    single<IErrorHandler> { ErrorHandlerImpl.getNewInstance() }

}
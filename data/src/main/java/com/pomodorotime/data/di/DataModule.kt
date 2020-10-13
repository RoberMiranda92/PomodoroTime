package com.pomodorotime.data.di

import com.pomodorotime.data.login.repository.LoginRepository
import com.pomodorotime.data.task.ITaskRepository
import com.pomodorotime.data.task.TaskRepository
import org.koin.dsl.module

val dataModule = module {

    single { LoginRepository.getNewInstance() }

    single<ITaskRepository> { TaskRepository.getNewInstance(get()) }

}
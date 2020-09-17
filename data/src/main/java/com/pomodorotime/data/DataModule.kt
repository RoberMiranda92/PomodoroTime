package com.pomodorotime.data

import com.pomodorotime.data.login.repository.LoginRepository
import com.pomodorotime.data.task.TaskRepository
import org.koin.dsl.module

val dataModule = module {

    single { LoginRepository.getNewInstance() }

    single { TaskRepository.getNewInstance(get()) }

}
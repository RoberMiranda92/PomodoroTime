package com.pomodorotime.login


import com.pomodorotime.data.login.repository.LoginRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val loginModule = module {

    viewModel {
        LoginViewModel(get())
    }

    single { LoginRepository.getNewInstance() }
}
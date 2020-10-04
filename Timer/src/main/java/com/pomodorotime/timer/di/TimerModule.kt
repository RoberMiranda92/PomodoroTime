package com.pomodorotime.timer.di

import com.pomodorotime.timer.TimerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val timerModule = module {

    viewModel { TimerViewModel(get()) }
}
package com.pomodorotime.timer

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val timerModule = module {

    viewModel { TimerViewModel(get()) }
}
package com.pomodorotime.core.di

import com.pomodorotime.core.logger.PomodoroLogger
import org.koin.dsl.module

val coreModule = module {

    single<PomodoroLogger> { PomodoroLogger() }
}
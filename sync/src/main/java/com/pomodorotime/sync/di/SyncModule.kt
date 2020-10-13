package com.pomodorotime.sync.di

import com.pomodorotime.sync.SyncManager
import org.koin.dsl.module

val syncModule = module {

    single { SyncManager(get(), get()) }
}
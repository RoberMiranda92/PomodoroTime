package com.pomodorotime.sync.di

import com.pomodorotime.data.sync.ISyncManager
import com.pomodorotime.sync.SyncManager
import org.koin.dsl.module

val syncModule = module {

    single<ISyncManager> { SyncManager(get(), get(), get()) }
}
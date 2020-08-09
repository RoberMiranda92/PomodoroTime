package com.pomodorotime

import android.app.Application
import com.pomodorotime.koin.appModule
import com.pomodorotime.login.loginModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class PomodoroApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            // Koin Android logger
            androidLogger()
            //inject Android context
            androidContext(this@PomodoroApp)
            // use modules
            modules(appModule, loginModule)
        }
    }
}
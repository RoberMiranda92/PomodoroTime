package com.pomodorotime

import android.app.Application
import com.pomodorotime.core.di.coreModule
import com.pomodorotime.data.di.dataModule
import com.pomodorotime.domain.di.domainModule
import com.pomodorotime.koin.appModule
import com.pomodorotime.login.loginModule
import com.pomodorotime.sync.di.syncModule
import com.pomodorotime.task.taskModule
import com.pomodorotime.timer.di.timerModule
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
            modules(
                appModule,
                coreModule,
                syncModule,
                domainModule,
                dataModule,
                loginModule,
                taskModule,
                timerModule
            )
        }
    }
}
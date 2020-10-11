package com.pomodorotimer.logger

import timber.log.Timber

class TimberLogger : Logger {

    init {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun v(msg: String) {
        Timber.v(msg)
    }

    override fun d(msg: String) {
        Timber.d(msg)
    }

    override fun e(msg: String) {
        Timber.e(msg)
    }
}
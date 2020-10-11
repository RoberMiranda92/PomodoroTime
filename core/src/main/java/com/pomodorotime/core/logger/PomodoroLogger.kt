package com.pomodorotime.core.logger

import com.pomodorotimer.logger.Logger
import com.pomodorotimer.logger.TimberLogger

class PomodoroLogger {

    private var logger: Logger = TimberLogger()

    fun logVerbose(msg: String) {
        logger.v(msg)
    }

    fun logDebug(msg: String) {
        logger.d(msg)
    }

    fun logError(msg: String) {
        logger.e(msg)
    }
}
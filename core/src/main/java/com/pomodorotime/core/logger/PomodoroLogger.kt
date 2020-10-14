package com.pomodorotime.core.logger

import com.pomodorotime.logger.Logger
import com.pomodorotime.logger.TimberLogger

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
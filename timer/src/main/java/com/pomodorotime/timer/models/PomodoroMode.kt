package com.pomodorotime.timer.models

import androidx.annotation.IntDef
import com.pomodorotime.timer.models.PomodoroMode.Companion.LONG_BREAK
import com.pomodorotime.timer.models.PomodoroMode.Companion.POMODORO
import com.pomodorotime.timer.models.PomodoroMode.Companion.SHORT_BREAK

@Target(
    AnnotationTarget.CLASS, AnnotationTarget.TYPE,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.VALUE_PARAMETER
)
@IntDef(POMODORO, SHORT_BREAK, LONG_BREAK)
@Retention(AnnotationRetention.SOURCE)
annotation class PomodoroMode {

    companion object {
        const val POMODORO = 0
        const val SHORT_BREAK = 1
        const val LONG_BREAK = 2

    }
}
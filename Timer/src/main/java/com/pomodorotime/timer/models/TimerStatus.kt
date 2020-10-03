package com.pomodorotime.timer

import androidx.annotation.IntDef
import com.pomodorotime.timer.TimerStatus.Companion.PAUSE
import com.pomodorotime.timer.TimerStatus.Companion.PLAY

@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.VALUE_PARAMETER
)
@IntDef(PLAY, PAUSE)
@Retention(AnnotationRetention.SOURCE)
annotation class TimerStatus {

    companion object {
        const val PLAY = 0
        const val PAUSE = 1
    }
}
package com.pomodorotime.timer

sealed class TimerScreenState {
    object Loading : TimerScreenState()
    class DataLoaded(
        val taskDetail: TimeDetail, val time: Long, @TimerStatus val status: Int,
        @PomodoroMode val mode: Int
    ) : TimerScreenState()

    class Error(val error: String) : TimerScreenState()
    object CounterStart : TimerScreenState()
}
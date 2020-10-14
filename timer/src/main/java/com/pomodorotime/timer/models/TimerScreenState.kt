package com.pomodorotime.timer.models

sealed class TimerScreenState {
    object Initial : TimerScreenState()
    object Loading : TimerScreenState()
    class DataLoaded(
        val taskDetail: TimeDetail,
        val time: Long,
        val progress: Float,
        @TimerStatus
        val status: Int,
        @PomodoroMode val mode: Int
    ) : TimerScreenState()

    class Error(val error: String) : TimerScreenState()
    object CounterStart : TimerScreenState()
}
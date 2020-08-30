package com.pomodorotime.timer

sealed class TimerScreenState {
    object Loading : TimerScreenState()
    class DataLoaded(val taskDetail: TimeDetail) : TimerScreenState()
    class Error(val error: String) : TimerScreenState()
}
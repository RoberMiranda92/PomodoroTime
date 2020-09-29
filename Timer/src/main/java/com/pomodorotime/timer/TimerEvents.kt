package com.pomodorotime.timer

sealed class TimerEvents {
    data class LoadData(val id: Int) : TimerEvents()
    object OnPlayStopButtonClicked: TimerEvents()
}
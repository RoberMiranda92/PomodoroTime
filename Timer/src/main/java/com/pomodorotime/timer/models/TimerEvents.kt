package com.pomodorotime.timer.models

sealed class TimerEvents {
    data class LoadData(val id: Long) : TimerEvents()
    object OnPlayStopButtonClicked: TimerEvents()
    object OnBackPressed: TimerEvents()
}
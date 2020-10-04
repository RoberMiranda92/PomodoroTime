package com.pomodorotime.timer.models

sealed class TimerEvents {
    data class LoadData(val id: Int) : TimerEvents()
    object OnPlayStopButtonClicked: TimerEvents()
    object OnBackPressed: TimerEvents()
}
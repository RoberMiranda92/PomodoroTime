package com.pomodorotime

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pomodorotime.core.Event
import com.pomodorotime.core.logger.PomodoroLogger
import com.pomodorotime.core.session.ISessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainViewModel(
    private val sessionManager: ISessionManager,
    private val logger: PomodoroLogger
) : ViewModel() {

    val navigation: LiveData<Event<ISessionManager.SessionEvent>>
        get() = _navigation
    private val _navigation: MutableLiveData<Event<ISessionManager.SessionEvent>> =
        MutableLiveData()

    fun onCreate() {
        sessionManager
            .subscribeToSessionEvents()
            .onEach {
                logger.logDebug(it.toString())
                _navigation.value = Event(it)
            }
            .flowOn(Dispatchers.Main)
            .launchIn(viewModelScope)
    }
}
package com.pomodorotime.core.session

import kotlinx.coroutines.flow.Flow

interface ISessionManager {

    suspend fun onLogin()

    suspend fun onLogout()

    fun subscribeToSessionEvents(): Flow<SessionEvent>

    enum class SessionEvent {
        LOGIN,
        LOGOUT
    }
}
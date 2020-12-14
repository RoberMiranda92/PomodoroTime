package com.pomodorotime.data.session

import com.pomodorotime.core.session.ISessionManager
import com.pomodorotime.data.user.IUserLocalDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged

@ExperimentalCoroutinesApi
class SessionManager(private val userLocalDataSourceImp: IUserLocalDataSource) : ISessionManager {

    private var sessionEvent = BroadcastChannel<ISessionManager.SessionEvent>(Channel.BUFFERED)

    override suspend fun onLogin() {
        sessionEvent.send(ISessionManager.SessionEvent.LOGIN)
    }

    override suspend fun onLogout() {
        sessionEvent.send(ISessionManager.SessionEvent.LOGOUT)
        userLocalDataSourceImp.clearToken()
    }

    override fun subscribeToSessionEvents(): Flow<ISessionManager.SessionEvent> =
        sessionEvent.asFlow().distinctUntilChanged()
}
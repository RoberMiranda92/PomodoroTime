package com.pomodorotime.data.login.repository

import com.pomodorotime.data.login.api.models.ApiUser
import com.pomodorotime.data.login.datasource.ILoginRemoteDataSource
import com.pomodorotime.data.login.toDomainModel
import com.pomodorotime.data.user.IUserLocalDataSource
import com.pomodorotime.domain.login.ILoginRepository
import com.pomodorotime.domain.models.User

class LoginRepository(
    private val remoteDataSource: ILoginRemoteDataSource,
    private val userLocalDataSource: IUserLocalDataSource
) : ILoginRepository {

    override suspend fun signIn(email: String, password: String): User {
        val result = remoteDataSource.signIn(email, password)
        saveUser(result)
        return result.toDomainModel()
    }

    override suspend fun signUp(email: String, password: String): User {
        val result = remoteDataSource.signUp(email, password)
        saveUser(result)
        return result.toDomainModel()
    }

    private fun saveUser(value: ApiUser) {
        userLocalDataSource.setUser(value)
    }
}
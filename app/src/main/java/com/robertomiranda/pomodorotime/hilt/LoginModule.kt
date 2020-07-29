package com.robertomiranda.pomodorotime.hilt

import com.robertomiranda.data.login.repository.LoginRepository
import com.robertomiranda.data.login.repository.RemoteLoginRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
object LoginModule {

    @Provides
    fun providesLoginRepository(
    ): RemoteLoginRepository {
        return LoginRepository.getNewInstance()
    }
}
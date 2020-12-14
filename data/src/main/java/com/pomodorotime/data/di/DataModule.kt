package com.pomodorotime.data.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.pomodorotime.core.session.ISessionManager
import com.pomodorotime.data.ErrorHandlerImpl
import com.pomodorotime.data.login.api.FirebaseLoginApi
import com.pomodorotime.data.login.api.ILoginApi
import com.pomodorotime.data.login.datasource.ILoginRemoteDataSource
import com.pomodorotime.data.login.datasource.LoginRemoteDataSourceImpl
import com.pomodorotime.data.login.repository.LoginRepository
import com.pomodorotime.data.preferences.EncryptedSharedPreferencesImpl
import com.pomodorotime.data.preferences.ISharedPreferences
import com.pomodorotime.data.preferences.SharedPreferencesImpl
import com.pomodorotime.data.session.SessionManager
import com.pomodorotime.data.sync.ISyncErrorHandler
import com.pomodorotime.data.task.TaskRepository
import com.pomodorotime.data.task.api.FirebaseTaskApi
import com.pomodorotime.data.task.api.ITaskApi
import com.pomodorotime.data.task.dataBase.IDataBase
import com.pomodorotime.data.task.dataBase.TaskDataBase
import com.pomodorotime.data.task.datasource.local.ITaskLocalDataSource
import com.pomodorotime.data.task.datasource.local.TaskLocalDataSourceImpl
import com.pomodorotime.data.task.datasource.remote.ITaskRemoteDataSource
import com.pomodorotime.data.task.datasource.remote.TaskRemoteDataSourceImp
import com.pomodorotime.data.user.IUserLocalDataSource
import com.pomodorotime.data.user.UserLocalDataSourceImp
import com.pomodorotime.domain.IErrorHandler
import com.pomodorotime.domain.login.ILoginRepository
import com.pomodorotime.domain.task.ITaskRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataModule = module {

    single<ILoginRepository> { LoginRepository(get(), get(), get()) }

    single<ITaskRepository> { TaskRepository(get(), get()) }

    single<IErrorHandler> { ErrorHandlerImpl() }

    single<ISyncErrorHandler> { ErrorHandlerImpl() }

    single<ITaskRemoteDataSource> { TaskRemoteDataSourceImp(get()) }

    single<ITaskLocalDataSource> { TaskLocalDataSourceImpl(get()) }

    single<ILoginRemoteDataSource> { LoginRemoteDataSourceImpl(get()) }

    single<IUserLocalDataSource> { UserLocalDataSourceImp(get(named("encrypted"))) }

    single<ISharedPreferences>(named("encrypted")) { EncryptedSharedPreferencesImpl(get()) }

    single<ISharedPreferences>((named("normal"))) { SharedPreferencesImpl(get()) }

    single<IDataBase> { TaskDataBase.getInstance(get()).taskDao() }

    single<ITaskApi> { FirebaseTaskApi(Firebase.database.reference) }

    single<ILoginApi> { FirebaseLoginApi(FirebaseAuth.getInstance()) }

    single<ISessionManager> { SessionManager(get()) }

}
package com.pomodorotime.koin

import com.pomodorotime.MainViewModel
import com.pomodorotime.RouteNavigator
import com.pomodorotime.login.LoginNavigator
import com.pomodorotime.task.TaskNavigator
import com.pomodorotime.timer.TimeNavigator
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val NAVIGATOR_NAME = "navigator"
val appModule = module {

    single(named(NAVIGATOR_NAME)) { RouteNavigator() }

    single<LoginNavigator> { get<RouteNavigator>(named(NAVIGATOR_NAME)) }

    single<TaskNavigator> { get<RouteNavigator>(named(NAVIGATOR_NAME)) }

    single<TimeNavigator> { get<RouteNavigator>(named(NAVIGATOR_NAME)) }

    viewModel { MainViewModel(get(), get()) }

}
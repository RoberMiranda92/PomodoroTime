package com.pomodorotime.koin

import com.pomodorotime.RouteNavigator
import com.pomodorotime.login.LoginNavigator
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val NAVIGATOR_NAME = "navigator"
val appModule = module {

    single(named(NAVIGATOR_NAME)) { RouteNavigator() }

    single<LoginNavigator> { get<RouteNavigator>(named(NAVIGATOR_NAME)) }
}
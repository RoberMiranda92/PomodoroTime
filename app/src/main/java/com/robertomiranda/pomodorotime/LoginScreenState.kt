package com.robertomiranda.pomodorotime

import androidx.annotation.IntDef

@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@IntDef(LoginScreenState.INITIAL, LoginScreenState.LOADING, LoginScreenState.ERROR)
@Retention(AnnotationRetention.SOURCE)
annotation class LoginScreenState {

    companion object {
        const val INITIAL = 0
        const val LOADING = 1
        const val ERROR = 2
    }
}
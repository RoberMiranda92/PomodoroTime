package com.pomodorotime.login

import androidx.annotation.IntDef

@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@IntDef(
    LoginMode.SIGN_IN,
    LoginMode.SIGN_UP
)
@Retention(AnnotationRetention.SOURCE)
annotation class LoginMode {

    companion object {
        const val SIGN_IN = 0
        const val SIGN_UP = 1
    }
}
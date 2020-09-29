package com.pomodorotime.login

import androidx.annotation.IntDef
import com.pomodorotime.login.LoginMode.Companion.SIGN_IN
import com.pomodorotime.login.LoginMode.Companion.SIGN_UP

@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@IntDef(SIGN_IN, SIGN_UP)
@Retention(AnnotationRetention.SOURCE)
annotation class LoginMode {

    companion object {
        const val SIGN_IN = 0
        const val SIGN_UP = 1
    }
}
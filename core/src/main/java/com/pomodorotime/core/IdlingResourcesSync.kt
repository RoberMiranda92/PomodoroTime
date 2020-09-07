package com.pomodorotime.core

import androidx.test.espresso.IdlingResource

interface IdlingResourcesSync {

    fun increment()

    fun decrement()

    fun onFinish(block: ()->Unit )

    fun getIdlingResource(): IdlingResource
}
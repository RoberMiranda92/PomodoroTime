package com.pomodorotime.core

import android.util.Log
import androidx.test.espresso.idling.CountingIdlingResource

object IdlingResourceWrapper : IdelingResoucesSync {

    override fun increment() {
    }

    override fun decrement() {
    }

    override fun onFinish(block: () -> Unit) {
    }

    override fun getIdlingResource(): IdlingResource {
    }
}
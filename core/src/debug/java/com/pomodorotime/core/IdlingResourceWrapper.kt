package com.pomodorotime.core

import android.util.Log
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.idling.CountingIdlingResource

object IdlingResourceWrapper : IdlingResourcesSync {

    private val idlingResource: CountingIdlingResource =
        CountingIdlingResource("IdlingResourceWrapper")

    init {
        idlingResource.registerIdleTransitionCallback {
            Log.d("IdlingResourceWrapper", "registerIdleTransitionCallback")
        }
    }

    override fun increment() {
        Log.d("IdlingResourceWrapper", "increment")
        idlingResource.increment()
    }

    override fun decrement() {
        Log.d("IdlingResourceWrapper", "decrement")
        idlingResource.decrement()
    }

    override fun onFinish(block: () -> Unit) {
        idlingResource.registerIdleTransitionCallback { block.invoke() }
    }

    override fun getIdlingResource(): IdlingResource = idlingResource
}
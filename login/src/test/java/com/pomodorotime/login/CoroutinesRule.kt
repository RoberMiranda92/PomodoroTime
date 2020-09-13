package com.pomodorotime.login

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.*
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class CoroutinesRule: TestWatcher() {

    private val testCoroutineDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
    private val testCoroutineScope: TestCoroutineScope = TestCoroutineScope()

    override fun starting(description: Description?) {
        Dispatchers.setMain(testCoroutineDispatcher)
        super.starting(description)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        testCoroutineDispatcher.cleanupTestCoroutines()
    }

    fun runBlockingTest(block: suspend TestCoroutineScope.() -> Unit) {
        testCoroutineScope.runBlockingTest(block)
    }
}
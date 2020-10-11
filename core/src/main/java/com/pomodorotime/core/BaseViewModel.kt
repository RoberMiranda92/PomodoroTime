package com.pomodorotime.core

import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pomodorotime.core.logger.PomodoroLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

@ExperimentalCoroutinesApi
abstract class BaseViewModel<in Event, State>(
    private val idlingResourceWrapper: IdlingResourcesSync? = null
) : ViewModel(), KoinComponent {

    private val logger: PomodoroLogger by inject()

    protected val _screenState: MutableLiveData<com.pomodorotime.core.Event<State>> =
        MutableLiveData(Event(initialState()))

    val screenState: LiveData<com.pomodorotime.core.Event<State>>
        get() = _screenState

    private val _netWorkError: MutableLiveData<Boolean> = MutableLiveData(false)
    val networkError: LiveData<Boolean>
        get() = _netWorkError

    @CallSuper
    protected open fun onNetworkError() {
        logger.logError("NetWorkError")
        _netWorkError.postValue(true)
    }

    protected fun executeCoroutine(block: suspend CoroutineScope.() -> Unit) {
        idlingResourceWrapper?.increment()

        val job = viewModelScope.launch(Dispatchers.Main, block = block)
        job.invokeOnCompletion { idlingResourceWrapper?.decrement() }
    }

    protected fun subscribeFlow(flow: Flow<Any>) {
        flow.onStart {
            idlingResourceWrapper?.increment()
        }.onCompletion {
            idlingResourceWrapper?.decrement()
        }.flowOn(Dispatchers.Main)
            .launchIn(viewModelScope)
    }

    abstract fun initialState(): State

    @CallSuper
    open fun postEvent(event: Event){
        logger.logDebug("New event detected  ${event.toString()}")
    }
}
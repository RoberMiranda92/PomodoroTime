package com.pomodorotime.core

import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
abstract class BaseViewModel<in Event, out State>(
    private val idlingResourceWrapper: IdlingResourcesSync? = null
) : ViewModel() {

    private val _netWorkError: MutableLiveData<Boolean> = MutableLiveData(false)
    val networkError: LiveData<Boolean>
        get() = _netWorkError

    @CallSuper
    protected open fun onNetworkError() {
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

    abstract fun postEvent(event: Event)
}
package com.pomodorotime.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BaseViewModel<in Event,out State> : ViewModel() {

    private val _netWorkError: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val networkError: LiveData<Boolean>
        get() = _netWorkError

    protected fun onNetworkError() {
        _netWorkError.postValue(true)
    }

    protected fun executeCoroutine(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(Dispatchers.Main, block = block)
    }

    abstract fun postEvent(event:Event)
}
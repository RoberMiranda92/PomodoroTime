package com.pomodorotime.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

    private val _netWorkError: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val networkError: LiveData<Boolean>
        get() = _netWorkError


    protected fun onNetworkError() {
        _netWorkError.postValue(true)
    }
}
package com.pomodorotime.login

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.pomodorotime.core.BaseViewModel
import com.pomodorotime.data.ApiUser
import com.pomodorotime.data.login.repository.RemoteLoginRepository
import com.pomodorotimemiranda.data.ErrorResponse
import com.pomodorotimemiranda.data.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel @ViewModelInject constructor(
    private val repository: RemoteLoginRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val _screenState: MutableLiveData<@LoginScreenState Int> =
        MutableLiveData(LoginScreenState.INITIAL)
    val screenState: LiveData<@LoginScreenState Int>
        get() = _screenState

    val loginMode: MutableLiveData<@LoginMode Int> =
        MutableLiveData(LoginMode.SIGN_IN)


    private val _emailLiveData: MutableLiveData<String> = MutableLiveData("")
    private val _passwordLiveData: MutableLiveData<String> = MutableLiveData("")
    private val _confirmePasswordLiveData: MutableLiveData<String> = MutableLiveData("")


    fun onEmailSet(email: String) {
        _emailLiveData.value = email
    }

    fun onPasswordSet(password: String) {
        _passwordLiveData.value = password
    }

    fun onConfirmPasswordSet(confirmPassword: String) {
        _confirmePasswordLiveData.value = confirmPassword
    }

    fun startSign() {
        when (loginMode.value) {
            LoginMode.SIGN_IN -> startSignIn()
            LoginMode.SIGN_UP -> startSignUp()
            else -> startSignIn()
        }
    }

    fun toogleMode() {
        loginMode.value = when (loginMode.value) {
            LoginMode.SIGN_IN -> LoginMode.SIGN_UP
            LoginMode.SIGN_UP -> LoginMode.SIGN_IN
            else -> LoginMode.SIGN_IN
        }
    }

    private fun startSignIn() {

        _screenState.value =
            LoginScreenState.LOADING

        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.singUp(_emailLiveData.value!!, _passwordLiveData.value!!)

            when (result) {
                is ResultWrapper.Success<ApiUser> -> onSignInSuccess(result.value)
                is ResultWrapper.GenericError -> onError(result.error)
                is ResultWrapper.NetworkError -> onNetworkError();
            }
        }
    }

    private fun startSignUp() {

        _screenState.value =
            LoginScreenState.LOADING

        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.singUp(_emailLiveData.value!!, _passwordLiveData.value!!)

            when (result) {
                is ResultWrapper.Success<ApiUser> -> onSignInSuccess(result.value)
                is ResultWrapper.GenericError -> onError(result.error)
                is ResultWrapper.NetworkError -> onNetworkError();
            }
        }
    }

    private fun onSignInSuccess(user: ApiUser) {
        _screenState.postValue(LoginScreenState.SUCCESS)
    }

    private fun onNetworkError() {
        TODO("Not yet implemented")
    }

    private fun onError(result: ErrorResponse) {

    }
}
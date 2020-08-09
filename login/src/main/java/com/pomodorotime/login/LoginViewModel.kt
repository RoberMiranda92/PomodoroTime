package com.pomodorotime.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pomodorotime.core.BaseViewModel
import com.pomodorotime.data.ApiUser
import com.pomodorotime.data.ErrorResponse
import com.pomodorotime.data.ResultWrapper
import com.pomodorotime.data.login.repository.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel constructor(
    private val repository: LoginRepository
) : BaseViewModel() {

    private val _screenState: MutableLiveData<@LoginScreenState Int> =
        MutableLiveData(LoginScreenState.INITIAL)
    val screenState: LiveData<@LoginScreenState Int>
        get() = _screenState

    val loginMode: MutableLiveData<@LoginMode Int> =
        MutableLiveData(LoginMode.SIGN_IN)

    //Error
    private val _invalidEmailError: MutableLiveData<String> = MutableLiveData()
    val invalidEmailError: LiveData<String>
        get() = _invalidEmailError

    private val _invalidPasswordError: MutableLiveData<String> = MutableLiveData()
    val invalidPasswordError: LiveData<String>
        get() = _invalidPasswordError

    private val _loginError: MutableLiveData<String> = MutableLiveData()
    val loginError: LiveData<String>
        get() = _loginError

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
            val result = repository.signIn(_emailLiveData.value!!, _passwordLiveData.value!!)

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


    private fun onError(error: ErrorResponse) {
        _screenState.postValue(LoginScreenState.INITIAL)

        when (error.code) {
            LoginRepository.ERROR_INVALID_EMAIL -> _invalidEmailError.postValue(error.message)
            LoginRepository.ERROR_WRONG_PASSWORD -> _invalidPasswordError.postValue(error.message)
            LoginRepository.ERROR_OPERATION_NOT_ALLOWED -> _loginError.postValue(error.message)
            LoginRepository.ERROR_WEAK_PASSWORD -> _invalidPasswordError.postValue(error.message)
            LoginRepository.ERROR_EMAIL_ALREADY_IN_USE -> _invalidEmailError.postValue(error.message)
            LoginRepository.ERROR_INVALID_CREDENTIAL -> _invalidEmailError.postValue(error.message)
            LoginRepository.ERROR_USER_NOT_FOUND -> _invalidEmailError.postValue(error.message)
            LoginRepository.ERROR_USER_DISABLED -> _loginError.postValue(error.message)
            LoginRepository.ERROR_TOO_MANY_REQUESTS -> _loginError.postValue(error.message)
            else -> _loginError.postValue(error.message)
        }

    }
}
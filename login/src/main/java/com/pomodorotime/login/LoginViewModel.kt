package com.pomodorotime.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pomodorotime.core.BaseViewModel
import com.pomodorotime.core.Event
import com.pomodorotime.data.ApiUser
import com.pomodorotime.data.ErrorResponse
import com.pomodorotime.data.ResultWrapper
import com.pomodorotime.data.login.repository.LoginRepository

class LoginViewModel constructor(
    private val repository: LoginRepository
) : BaseViewModel<LoginEvent, LoginScreenState>() {

    private val _screenState: MutableLiveData<Event<LoginScreenState>> =
        MutableLiveData(Event(LoginScreenState.SignIn))
    val screenState: LiveData<Event<LoginScreenState>>
        get() = _screenState

    private val _loginMode: MutableLiveData<@LoginMode Int> = MutableLiveData(LoginMode.SIGN_IN)
    private val _emailLiveData: MutableLiveData<String> = MutableLiveData("")
    private val _passwordLiveData: MutableLiveData<String> = MutableLiveData("")
    private val _confirmPasswordLiveData: MutableLiveData<String> = MutableLiveData("")

    override fun postEvent(event: LoginEvent) {
        when (event) {

            is LoginEvent.LoginTyping -> {
                onEmailSet(event.user)
                onPasswordSet(event.password)
                onConfirmPasswordSet(event.confirmPassword)
            }
            is LoginEvent.MainButtonPress -> {
                startSign()
            }

            is LoginEvent.SecondaryButtonPress -> {
                toggleMode()
            }
        }
    }

    private fun onEmailSet(email: String) {
        _emailLiveData.value = email
    }

    private fun onPasswordSet(password: String) {
        _passwordLiveData.value = password
    }

    private fun onConfirmPasswordSet(confirmPassword: String) {
        _confirmPasswordLiveData.value = confirmPassword
    }

    private fun startSign() {
        when (_loginMode.value) {
            LoginMode.SIGN_IN -> startSignIn()
            LoginMode.SIGN_UP -> startSignUp()
            else -> startSignIn()
        }
    }

    private fun toggleMode() {
        when (_loginMode.value) {
            LoginMode.SIGN_IN -> {
                _loginMode.value = LoginMode.SIGN_UP
                _screenState.value = Event(LoginScreenState.SignUp)
            }
            LoginMode.SIGN_UP -> {
                _loginMode.value = LoginMode.SIGN_IN
                _screenState.value = Event(LoginScreenState.SignIn)
            }
            else -> {
                _loginMode.value = LoginMode.SIGN_IN
                _screenState.value = Event(LoginScreenState.SignUp)
            }
        }
    }

    private fun startSignIn() {
        executeCoroutine {
            _screenState.value = Event(LoginScreenState.Loading)

            val result = repository.signIn(_emailLiveData.value!!, _passwordLiveData.value!!)

            when (result) {
                is ResultWrapper.Success<ApiUser> -> onSignInSuccess(result.value)
                is ResultWrapper.GenericError -> onError(result.error)
                is ResultWrapper.NetworkError -> onNetworkError();
            }
        }
    }

    private fun startSignUp() {
        executeCoroutine {
            _screenState.value = Event(LoginScreenState.Loading)

            val result = repository.singUp(_emailLiveData.value!!, _passwordLiveData.value!!)

            when (result) {
                is ResultWrapper.Success<ApiUser> -> onSignInSuccess(result.value)
                is ResultWrapper.GenericError -> onError(result.error)
                is ResultWrapper.NetworkError -> onNetworkError();
            }
        }
    }

    private fun onSignInSuccess(user: ApiUser) {
        _screenState.value = Event(LoginScreenState.Success)
    }


    private fun onError(error: ErrorResponse) {
        _screenState.value = when (error.code) {
            LoginRepository.ERROR_INVALID_EMAIL -> Event(LoginScreenState.EmailError(error.message))
            LoginRepository.ERROR_WRONG_PASSWORD -> Event(LoginScreenState.PasswordError(error.message))
            LoginRepository.ERROR_OPERATION_NOT_ALLOWED -> Event(LoginScreenState.EmailError(error.message))
            LoginRepository.ERROR_WEAK_PASSWORD -> Event(LoginScreenState.PasswordError(error.message))
            LoginRepository.ERROR_EMAIL_ALREADY_IN_USE -> Event(LoginScreenState.EmailError(error.message))
            LoginRepository.ERROR_INVALID_CREDENTIAL -> Event(LoginScreenState.EmailError(error.message))
            LoginRepository.ERROR_USER_NOT_FOUND -> Event(LoginScreenState.EmailError(error.message))
            LoginRepository.ERROR_USER_DISABLED -> Event(LoginScreenState.EmailError(error.message))
            LoginRepository.ERROR_TOO_MANY_REQUESTS -> Event(LoginScreenState.EmailError(error.message))
            else -> Event(LoginScreenState.Error(error.message))
        }
    }
}
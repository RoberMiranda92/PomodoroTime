package com.pomodorotime.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pomodorotime.core.BaseViewModel
import com.pomodorotime.core.Event
import com.pomodorotime.core.IdlingResourcesSync
import com.pomodorotime.core.SnackBarrError
import com.pomodorotime.domain.login.usecases.SigInUseCase
import com.pomodorotime.domain.login.usecases.SigUpUseCase
import com.pomodorotime.domain.models.ErrorEntity
import com.pomodorotime.domain.models.ResultWrapper
import com.pomodorotime.domain.models.User


class LoginViewModel constructor(
    private val signUpUseCase: SigUpUseCase,
    private val sigInUseCase: SigInUseCase,
    idlingResourceWrapper: IdlingResourcesSync? = null
) : BaseViewModel<LoginEvent, LoginScreenState>(idlingResourceWrapper) {

    private var loginMode: @LoginMode Int = LoginMode.SIGN_IN
    private var email: String = ""
    private var password: String = ""
    private var confirmPassword = ""

    private val _error: MutableLiveData<Event<SnackBarrError>> = MutableLiveData()
    val error: LiveData<Event<SnackBarrError>>
        get() = _error

    private val _emailError: MutableLiveData<Event<String>> = MutableLiveData()
    val emailError: LiveData<Event<String>>
        get() = _emailError

    private val _passwordError: MutableLiveData<Event<String>> = MutableLiveData()
    val passwordError: LiveData<Event<String>>
        get() = _passwordError

    private val _navigationToDashboard: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val navigationToDashboard: LiveData<Event<Boolean>>
        get() = _navigationToDashboard

    override fun initialState(): LoginScreenState = LoginScreenState.SignIn

    override fun postEvent(event: LoginEvent) {
        super.postEvent(event)
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
        this.email = email
    }

    private fun onPasswordSet(password: String) {
        this.password = password
    }

    private fun onConfirmPasswordSet(confirmPassword: String) {
        this.confirmPassword = confirmPassword
    }

    private fun startSign() {
        when (loginMode) {
            LoginMode.SIGN_IN -> startSignIn()
            LoginMode.SIGN_UP -> startSignUp()
        }
    }

    private fun toggleMode() {
        when (loginMode) {
            LoginMode.SIGN_IN -> {
                loginMode = LoginMode.SIGN_UP
                _screenState.value = Event(LoginScreenState.SignUp)
            }
            LoginMode.SIGN_UP -> {
                loginMode = LoginMode.SIGN_IN
                _screenState.value = Event(LoginScreenState.SignIn)
            }
        }
    }

    private fun startSignIn() {
        executeCoroutine {
            _screenState.value = Event(LoginScreenState.Loading)

            val result: ResultWrapper<User> =
                sigInUseCase.invoke(SigInUseCase.SignInParams(email, password))

            when (result) {
                is ResultWrapper.Success<User> -> onSignInSuccess(result.value)
                is ResultWrapper.Error -> onError(result.error)
//                is ResultWrapper.NetworkError -> onNetworkError()
            }
        }
    }

    private fun startSignUp() {
        executeCoroutine {
            _screenState.value = Event(LoginScreenState.Loading)

            val result: ResultWrapper<User> =
                signUpUseCase.invoke(SigUpUseCase.SignUpParams(email, password))

            when (result) {
                is ResultWrapper.Success<User> -> onSignInSuccess(result.value)
                is ResultWrapper.Error -> onError(result.error)
            }
        }
    }

    private fun onSignInSuccess(user: User) {
        _navigationToDashboard.value = Event(true)
    }

    private fun onError(error: ErrorEntity) {
        when (error) {
            is ErrorEntity.GenericError -> _error.value = Event(SnackBarrError(true, error.message))
            is ErrorEntity.UserEmailError -> _emailError.value= Event(error.message)
            is ErrorEntity.UserPasswordError -> _passwordError.value= Event(error.message)
            is ErrorEntity.NetworkError -> onNetworkError()
        }
        setMode()
    }

    override fun onNetworkError() {
        super.onNetworkError()
        setMode()

    }

    private fun setMode(){
        _screenState.value = if (LoginMode.SIGN_IN == loginMode) {
            Event(LoginScreenState.SignIn)
        } else {
            Event(LoginScreenState.SignUp)
        }
    }
}
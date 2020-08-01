package com.robertomiranda.pomodorotime.features.login

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.robertomiranda.data.ApiUser
import com.robertomiranda.data.ErrorResponse
import com.robertomiranda.data.ResultWrapper
import com.robertomiranda.data.login.repository.RemoteLoginRepository
import com.robertomiranda.pomodorotime.LoginScreenState
import com.robertomiranda.pomodorotime.commons.BaseViewModel
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
    private val _emailLiveData: MutableLiveData<String> = MutableLiveData("")
    private val _passwordLiveData: MutableLiveData<String> = MutableLiveData("")


    fun onEmailSet(email: String) {
        _emailLiveData.value = email
    }

    fun onPasswordSet(password: String) {
        _passwordLiveData.value = password
    }

    fun startSignUp() {

        _screenState.value =
            LoginScreenState.LOADING

        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.singUp(_emailLiveData.value!!, _passwordLiveData.value!!)

            when (result) {
                is ResultWrapper.Success<ApiUser> -> onSignInSuccess(result.value)
                is ResultWrapper.GenericError -> onError(result.error)
                is ResultWrapper.NetworkError -> onNetworkError();
            }
            print(result)
        }
    }

    private fun onSignInSuccess(user: ApiUser) {

    }

    private fun onNetworkError() {
        TODO("Not yet implemented")
    }

    private fun onError(result: ErrorResponse) {

    }


}
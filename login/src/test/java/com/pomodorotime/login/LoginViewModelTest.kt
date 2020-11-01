package com.pomodorotime.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.pomodorotime.core.Event
import com.pomodorotime.core.SnackBarrError
import com.pomodorotime.domain.login.usecases.SigInUseCase
import com.pomodorotime.domain.login.usecases.SigUpUseCase
import com.pomodorotime.domain.models.ErrorEntity
import com.pomodorotime.domain.models.ResultWrapper
import com.pomodorotime.domain.models.User
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesRule = CoroutinesRule()

    @MockK
    lateinit var signUpUseCase: SigUpUseCase

    @MockK
    lateinit var signInUseCase: SigInUseCase

    @RelaxedMockK
    lateinit var screenStateObserver: Observer<Event<LoginScreenState>>

    @RelaxedMockK
    lateinit var navigatorObserver: Observer<Event<Boolean>>

    @RelaxedMockK
    lateinit var emailErrorObserver: Observer<Event<String>>

    @RelaxedMockK
    lateinit var passwordErrorObserver: Observer<Event<String>>

    @RelaxedMockK
    lateinit var errorObserver: Observer<Event<SnackBarrError>>

    @RelaxedMockK
    lateinit var netWorkErrorObserver: Observer<Boolean>

    @RelaxedMockK
    lateinit var showTokenDialogObserver: Observer<Event<Boolean>>

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        viewModel = LoginViewModel(signUpUseCase, signInUseCase)

        viewModel.screenState.observeForever(screenStateObserver)
        viewModel.networkError.observeForever(netWorkErrorObserver)
        viewModel.saveTokenDialog.observeForever(showTokenDialogObserver)
        viewModel.navigationToDashboard.observeForever(navigatorObserver)
        viewModel.emailError.observeForever(emailErrorObserver)
        viewModel.passwordError.observeForever(passwordErrorObserver)
        viewModel.error.observeForever(errorObserver)
    }

    @After
    fun setDown() {
        viewModel.screenState.removeObserver(screenStateObserver)
        viewModel.networkError.removeObserver(netWorkErrorObserver)
        viewModel.navigationToDashboard.removeObserver(navigatorObserver)
        viewModel.emailError.removeObserver(emailErrorObserver)
        viewModel.passwordError.removeObserver(passwordErrorObserver)
        viewModel.error.removeObserver(errorObserver)
        viewModel.saveTokenDialog.removeObserver(showTokenDialogObserver)
    }

    private fun verifyInitialState() {
        verify { screenStateObserver.onChanged(Event(LoginScreenState.SignIn)) }
        verify { netWorkErrorObserver.onChanged(false) }
    }

    private fun coVerifyInitialState() {
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.SignIn)) }
        coVerify { netWorkErrorObserver.onChanged(false) }
    }

    private fun confirmVerifyMocks() {
        confirmVerified(screenStateObserver)
        confirmVerified(netWorkErrorObserver)
        confirmVerified(navigatorObserver)
        confirmVerified(emailErrorObserver)
        confirmVerified(passwordErrorObserver)
        confirmVerified(errorObserver)
        confirmVerified(signUpUseCase)
        confirmVerified(signInUseCase)
    }

    @Test
    fun onUserIsTypingUserEvent() {
        //Given
        val event = LoginEvent.LoginTyping("user", "password", "")

        //When
        viewModel.postEvent(event)
    }

    @Test
    fun onSecondaryButtonClickedAndModeIsSignIn() {
        //Given
        val event = LoginEvent.SecondaryButtonPress
        val loginScreenState = LoginScreenState.SignUp

        //When
        viewModel.postEvent(event)

        //Verify
        verifyInitialState()
        verify { screenStateObserver.onChanged(Event(loginScreenState)) }

        confirmVerifyMocks()
    }

    @Test
    fun onSecondaryButtonClickedAndModeIsSignUp() {
        //Given
        val event = LoginEvent.SecondaryButtonPress
        val loginScreenState = LoginScreenState.SignUp

        //When

        viewModel.postEvent(event)

        //Verify
        verifyInitialState()
        verify { screenStateObserver.onChanged(Event(loginScreenState)) }
        confirmVerified(screenStateObserver)
        confirmVerified(netWorkErrorObserver)
    }

    @Test
    fun onSecondaryButtonClickedTwice() {
        //Given
        val event = LoginEvent.SecondaryButtonPress

        //When

        viewModel.postEvent(event)
        viewModel.postEvent(event)
        viewModel.postEvent(event)

        //Verify
        verifyInitialState()
        verify { screenStateObserver.onChanged(Event(LoginScreenState.SignUp)) }
        verify { screenStateObserver.onChanged(Event(LoginScreenState.SignIn)) }
        confirmVerified(screenStateObserver)
        confirmVerified(netWorkErrorObserver)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun onMainButtonClickedAndModeIsSignIn() = runBlockingTest {
        //Given
        val user = User("user@email.com", "id", "token")
        val typeEvent = LoginEvent.LoginTyping("user@email.com", "password", "")
        val event = LoginEvent.MainButtonPress

        //When
        coEvery { signInUseCase.invoke(any()) } returns ResultWrapper.Success(user)

        viewModel.postEvent(typeEvent)
        viewModel.postEvent(event)
        viewModel.postEvent(LoginEvent.OnUserPositiveClickPress)

        //Verify
        coVerifyInitialState()
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.Loading)) }
        coVerify {
            signInUseCase.invoke(
                SigInUseCase.SignInParams(
                    typeEvent.user,
                    typeEvent.password
                )
            )
        }
        coVerify { showTokenDialogObserver.onChanged(Event(true)) }
        coVerify { navigatorObserver.onChanged(Event(true)) }
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.SignIn)) }

        confirmVerifyMocks()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun onMainButtonClickedAndModeIsSignInNegativeButton() = runBlockingTest {
        //Given
        val user = User("user@email.com", "id", "token")
        val typeEvent = LoginEvent.LoginTyping("user@email.com", "password", "")
        val event = LoginEvent.MainButtonPress

        //When
        coEvery { signInUseCase.invoke(any()) } returns ResultWrapper.Success(user)

        viewModel.postEvent(typeEvent)
        viewModel.postEvent(event)
        viewModel.postEvent(LoginEvent.OnNegativePositiveClickPress)

        //Verify
        coVerifyInitialState()
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.Loading)) }
        coVerify {
            signInUseCase.invoke(
                SigInUseCase.SignInParams(
                    typeEvent.user,
                    typeEvent.password
                )
            )
        }
        coVerify { showTokenDialogObserver.onChanged(Event(true)) }
        coVerify { navigatorObserver.onChanged(Event(true)) }
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.SignIn)) }

        confirmVerifyMocks()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun onMainButtonClickedAndModeIsSignInInvalidEmailError() = coroutinesRule.runBlockingTest {
        //Given
        val error: ErrorEntity.UserEmailError = ErrorEntity.UserEmailError("Invalid message")
        val typeEvent = LoginEvent.LoginTyping("user@email.com", "password", "")
        val event = LoginEvent.MainButtonPress

        //When
        coEvery { signInUseCase.invoke(any()) } returns ResultWrapper.Error(error)

        viewModel.postEvent(typeEvent)
        viewModel.postEvent(event)

        //Verify
        coVerifyInitialState()
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.Loading)) }
        coVerify {
            signInUseCase.invoke(
                SigInUseCase.SignInParams(
                    typeEvent.user,
                    typeEvent.password
                )
            )
        }
        coVerify { emailErrorObserver.onChanged(Event(error.message)) }
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.SignIn)) }
        confirmVerifyMocks()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun onMainButtonClickedAndModeIsSignInWrongPasswordError() = coroutinesRule.runBlockingTest {
        //Given
        val error: ErrorEntity.UserPasswordError = ErrorEntity.UserPasswordError("Invalid message")
        val typeEvent = LoginEvent.LoginTyping("user@email.com", "password", "")
        val event = LoginEvent.MainButtonPress

        //When
        coEvery { signInUseCase.invoke(any()) } returns ResultWrapper.Error(error)

        viewModel.postEvent(typeEvent)
        viewModel.postEvent(event)

        //Verify
        coVerifyInitialState()
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.Loading)) }
        coVerify {
            signInUseCase.invoke(
                SigInUseCase.SignInParams(
                    typeEvent.user,
                    typeEvent.password
                )
            )
        }
        coVerify { passwordErrorObserver.onChanged(Event(error.message)) }
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.SignIn)) }

        confirmVerifyMocks()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun onMainButtonClickedAndModeIsSignInGenericError() = coroutinesRule.runBlockingTest {
        //Given
        val error: ErrorEntity.GenericError = ErrorEntity.GenericError(-1, "Invalid message")
        val typeEvent = LoginEvent.LoginTyping("user@email.com", "password", "")
        val event = LoginEvent.MainButtonPress

        //When
        coEvery { signInUseCase.invoke(any()) } returns ResultWrapper.Error(error)

        viewModel.postEvent(typeEvent)
        viewModel.postEvent(event)

        //Verify
        coVerifyInitialState()
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.Loading)) }
        coVerify {
            signInUseCase.invoke(
                SigInUseCase.SignInParams(
                    typeEvent.user,
                    typeEvent.password
                )
            )
        }
        coVerify { errorObserver.onChanged(Event(SnackBarrError(true, error.message))) }
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.SignIn)) }

        confirmVerifyMocks()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun onMainButtonClickedAndModeIsSignInNetWorkError() = coroutinesRule.runBlockingTest {
        //Given
        val typeEvent = LoginEvent.LoginTyping("user@email.com", "password", "")
        val event = LoginEvent.MainButtonPress

        //When
        coEvery { signInUseCase.invoke(any()) } returns ResultWrapper.Error(ErrorEntity.NetworkError)

        viewModel.postEvent(typeEvent)
        viewModel.postEvent(event)

        //Verify
        coVerifyInitialState()
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.Loading)) }
        coVerify {
            signInUseCase.invoke(
                SigInUseCase.SignInParams(
                    typeEvent.user,
                    typeEvent.password
                )
            )
        }
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.SignIn)) }
        coVerify { netWorkErrorObserver.onChanged(true) }
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.SignIn)) }

        confirmVerifyMocks()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun onMainButtonClickedAndModeIsSignUp() = coroutinesRule.runBlockingTest {
        //Given
        val user = User("user@email.com", "id", "token")
        val typeEvent = LoginEvent.LoginTyping("user@email.com", "password", "password")
        val event = LoginEvent.MainButtonPress

        //When
        coEvery { signUpUseCase.invoke(any()) } returns ResultWrapper.Success(user)

        viewModel.postEvent(LoginEvent.SecondaryButtonPress)
        viewModel.postEvent(typeEvent)
        viewModel.postEvent(event)
        viewModel.postEvent(LoginEvent.OnUserPositiveClickPress)

        //Verify
        coVerifyInitialState()
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.SignUp)) }
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.Loading)) }
        coVerify {
            signUpUseCase.invoke(
                SigUpUseCase.SignUpParams(
                    typeEvent.user,
                    typeEvent.password
                )
            )
        }
        coVerify { showTokenDialogObserver.onChanged(Event(true)) }
        coVerify { navigatorObserver.onChanged(Event(true)) }

        confirmVerifyMocks()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun onMainButtonClickedAndModeIsSignUpNegativeDialog() = coroutinesRule.runBlockingTest {
        //Given
        val user = User("user@email.com", "id", "token")
        val typeEvent = LoginEvent.LoginTyping("user@email.com", "password", "password")
        val event = LoginEvent.MainButtonPress

        //When
        coEvery { signUpUseCase.invoke(any()) } returns ResultWrapper.Success(user)

        viewModel.postEvent(LoginEvent.SecondaryButtonPress)
        viewModel.postEvent(typeEvent)
        viewModel.postEvent(event)
        viewModel.postEvent(LoginEvent.OnNegativePositiveClickPress)

        //Verify
        coVerifyInitialState()
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.SignUp)) }
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.Loading)) }
        coVerify {
            signUpUseCase.invoke(
                SigUpUseCase.SignUpParams(
                    typeEvent.user,
                    typeEvent.password
                )
            )
        }
        coVerify { showTokenDialogObserver.onChanged(Event(true)) }
        coVerify { navigatorObserver.onChanged(Event(true)) }

        confirmVerifyMocks()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun onMainButtonClickedAndModeIsSignUpNetWorkError() = coroutinesRule.runBlockingTest {
        //Given
        val typeEvent = LoginEvent.LoginTyping("user@email.com", "password", "password")
        val event = LoginEvent.MainButtonPress

        //When
        coEvery { signUpUseCase.invoke(any()) } returns ResultWrapper.Error(ErrorEntity.NetworkError)

        viewModel.postEvent(typeEvent)
        viewModel.postEvent(LoginEvent.SecondaryButtonPress)
        viewModel.postEvent(event)

        //Verify
        coVerifyInitialState()
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.Loading)) }
        coVerify {
            signUpUseCase.invoke(
                SigUpUseCase.SignUpParams(
                    typeEvent.user,
                    typeEvent.password
                )
            )
        }
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.SignUp)) }
        coVerify { netWorkErrorObserver.onChanged(true) }
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.SignUp)) }

        confirmVerifyMocks()
    }

}
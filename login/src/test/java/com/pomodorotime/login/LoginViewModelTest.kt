package com.pomodorotime.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.pomodorotime.core.Event
import com.pomodorotime.data.ErrorResponse
import com.pomodorotime.data.ResultWrapper
import com.pomodorotime.data.login.api.models.ApiUser
import com.pomodorotime.data.login.repository.LoginRepository
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
    lateinit var loginRepository: LoginRepository

    @RelaxedMockK
    lateinit var screenStateObserver: Observer<Event<LoginScreenState>>

    @RelaxedMockK
    lateinit var netWorkErrorObserver: Observer<Boolean>

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        viewModel = LoginViewModel(loginRepository)
    }

    @After
    fun setDown() {
        viewModel.screenState.removeObserver(screenStateObserver)
        viewModel.networkError.removeObserver(netWorkErrorObserver)
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
        confirmVerified(loginRepository)
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
        viewModel.screenState.observeForever(screenStateObserver)
        viewModel.networkError.observeForever(netWorkErrorObserver)

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
        viewModel.screenState.observeForever(screenStateObserver)
        viewModel.networkError.observeForever(netWorkErrorObserver)

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
        viewModel.screenState.observeForever(screenStateObserver)
        viewModel.networkError.observeForever(netWorkErrorObserver)

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
        val user = ApiUser("user@email.com", "id", "token")
        val typeEvent = LoginEvent.LoginTyping("user@email.com", "password", "")
        val event = LoginEvent.MainButtonPress

        coEvery { loginRepository.signIn(any(), any()) } returns ResultWrapper.Success(user)

        //When
        viewModel.screenState.observeForever(screenStateObserver)
        viewModel.networkError.observeForever(netWorkErrorObserver)

        viewModel.postEvent(typeEvent)
        viewModel.postEvent(event)

        //Verify
        coVerifyInitialState()
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.Loading)) }
        coVerify { loginRepository.signIn(typeEvent.user, typeEvent.password) }
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.Success)) }

        confirmVerifyMocks()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun onMainButtonClickedAndModeIsSignInInvalidEmailError() = runBlockingTest {
        //Given
        val error =
            ErrorResponse(code = LoginRepository.ERROR_INVALID_EMAIL, message = "Invalid message")
        val typeEvent = LoginEvent.LoginTyping("user@email.com", "password", "")
        val event = LoginEvent.MainButtonPress

        coEvery { loginRepository.signIn(any(), any()) } returns ResultWrapper.GenericError(
            LoginRepository.ERROR_INVALID_EMAIL,
            error
        )

        //When
        viewModel.screenState.observeForever(screenStateObserver)
        viewModel.networkError.observeForever(netWorkErrorObserver)

        viewModel.postEvent(typeEvent)
        viewModel.postEvent(event)

        //Verify
        coVerifyInitialState()
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.Loading)) }
        coVerify { loginRepository.signIn(typeEvent.user, typeEvent.password) }
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.EmailError(error.message))) }

        confirmVerifyMocks()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun onMainButtonClickedAndModeIsSignInEmailInUseError() = runBlockingTest {
        //Given
        val error =
            ErrorResponse(
                code = LoginRepository.ERROR_EMAIL_ALREADY_IN_USE,
                message = "Invalid message"
            )
        val typeEvent = LoginEvent.LoginTyping("user@email.com", "password", "")
        val event = LoginEvent.MainButtonPress

        coEvery { loginRepository.signIn(any(), any()) } returns ResultWrapper.GenericError(
            error.code,
            error
        )

        //When
        viewModel.screenState.observeForever(screenStateObserver)
        viewModel.networkError.observeForever(netWorkErrorObserver)

        viewModel.postEvent(typeEvent)
        viewModel.postEvent(event)

        //Verify
        coVerifyInitialState()
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.Loading)) }
        coVerify { loginRepository.signIn(typeEvent.user, typeEvent.password) }
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.EmailError(error.message))) }

        confirmVerifyMocks()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun onMainButtonClickedAndModeIsSignInUserDisabledError() = runBlockingTest {
        //Given
        val error =
            ErrorResponse(code = LoginRepository.ERROR_USER_DISABLED, message = "Invalid message")
        val typeEvent = LoginEvent.LoginTyping("user@email.com", "password", "")
        val event = LoginEvent.MainButtonPress

        coEvery { loginRepository.signIn(any(), any()) } returns ResultWrapper.GenericError(
            error.code,
            error
        )

        //When
        viewModel.screenState.observeForever(screenStateObserver)
        viewModel.networkError.observeForever(netWorkErrorObserver)

        viewModel.postEvent(typeEvent)
        viewModel.postEvent(event)

        //Verify
        coVerifyInitialState()
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.Loading)) }
        coVerify { loginRepository.signIn(typeEvent.user, typeEvent.password) }
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.EmailError(error.message))) }

        confirmVerifyMocks()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun onMainButtonClickedAndModeIsSignInUserNotFoundError() = runBlockingTest {
        //Given
        val error =
            ErrorResponse(code = LoginRepository.ERROR_USER_NOT_FOUND, message = "Invalid message")
        val typeEvent = LoginEvent.LoginTyping("user@email.com", "password", "")
        val event = LoginEvent.MainButtonPress

        coEvery { loginRepository.signIn(any(), any()) } returns ResultWrapper.GenericError(
            error.code,
            error
        )

        //When
        viewModel.screenState.observeForever(screenStateObserver)
        viewModel.networkError.observeForever(netWorkErrorObserver)

        viewModel.postEvent(typeEvent)
        viewModel.postEvent(event)

        //Verify
        coVerifyInitialState()
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.Loading)) }
        coVerify { loginRepository.signIn(typeEvent.user, typeEvent.password) }
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.EmailError(error.message))) }

        confirmVerifyMocks()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun onMainButtonClickedAndModeIsSignInInvalidCredentialsError() = runBlockingTest {
        //Given
        val error =
            ErrorResponse(
                code = LoginRepository.ERROR_INVALID_CREDENTIAL,
                message = "Invalid message"
            )
        val typeEvent = LoginEvent.LoginTyping("user@email.com", "password", "")
        val event = LoginEvent.MainButtonPress

        coEvery { loginRepository.signIn(any(), any()) } returns ResultWrapper.GenericError(
            error.code,
            error
        )

        //When
        viewModel.screenState.observeForever(screenStateObserver)
        viewModel.networkError.observeForever(netWorkErrorObserver)
        viewModel.postEvent(typeEvent)
        viewModel.postEvent(event)

        //Verify
        coVerifyInitialState()
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.Loading)) }
        coVerify { loginRepository.signIn(typeEvent.user, typeEvent.password) }
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.EmailError(error.message))) }

        confirmVerifyMocks()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun onMainButtonClickedAndModeIsSignInOperationNotAllowedError() = runBlockingTest {
        //Given
        val error =
            ErrorResponse(
                code = LoginRepository.ERROR_OPERATION_NOT_ALLOWED,
                message = "Invalid message"
            )
        val typeEvent = LoginEvent.LoginTyping("user@email.com", "password", "")
        val event = LoginEvent.MainButtonPress

        coEvery { loginRepository.signIn(any(), any()) } returns ResultWrapper.GenericError(
            error.code,
            error
        )

        //When
        viewModel.screenState.observeForever(screenStateObserver)
        viewModel.networkError.observeForever(netWorkErrorObserver)

        viewModel.postEvent(typeEvent)
        viewModel.postEvent(event)

        //Verify
        coVerifyInitialState()
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.Loading)) }
        coVerify { loginRepository.signIn(typeEvent.user, typeEvent.password) }
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.EmailError(error.message))) }

        confirmVerifyMocks()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun onMainButtonClickedAndModeIsSignInTooManyRequestError() = runBlockingTest {
        //Given
        val error =
            ErrorResponse(
                code = LoginRepository.ERROR_TOO_MANY_REQUESTS,
                message = "Invalid message"
            )
        val typeEvent = LoginEvent.LoginTyping("user@email.com", "password", "")
        val event = LoginEvent.MainButtonPress

        coEvery { loginRepository.signIn(any(), any()) } returns ResultWrapper.GenericError(
            error.code,
            error
        )

        //When
        viewModel.screenState.observeForever(screenStateObserver)
        viewModel.networkError.observeForever(netWorkErrorObserver)

        viewModel.postEvent(typeEvent)
        viewModel.postEvent(event)

        //Verify
        coVerifyInitialState()
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.Loading)) }
        coVerify { loginRepository.signIn(typeEvent.user, typeEvent.password) }
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.EmailError(error.message))) }

        confirmVerifyMocks()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun onMainButtonClickedAndModeIsSignInWrongPasswordError() = runBlockingTest {
        //Given
        val error =
            ErrorResponse(code = LoginRepository.ERROR_WRONG_PASSWORD, message = "Invalid message")
        val typeEvent = LoginEvent.LoginTyping("user@email.com", "password", "")
        val event = LoginEvent.MainButtonPress

        coEvery { loginRepository.signIn(any(), any()) } returns ResultWrapper.GenericError(
            error.code,
            error
        )

        //When
        viewModel.screenState.observeForever(screenStateObserver)
        viewModel.networkError.observeForever(netWorkErrorObserver)

        viewModel.postEvent(typeEvent)
        viewModel.postEvent(event)

        //Verify
        coVerifyInitialState()
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.Loading)) }
        coVerify { loginRepository.signIn(typeEvent.user, typeEvent.password) }
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.PasswordError(error.message))) }

        confirmVerifyMocks()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun onMainButtonClickedAndModeIsSignInWeekPasswordError() = runBlockingTest {
        //Given
        val error =
            ErrorResponse(code = LoginRepository.ERROR_WEAK_PASSWORD, message = "Invalid message")
        val typeEvent = LoginEvent.LoginTyping("user@email.com", "password", "")
        val event = LoginEvent.MainButtonPress

        coEvery { loginRepository.signIn(any(), any()) } returns ResultWrapper.GenericError(
            error.code,
            error
        )

        //When
        viewModel.screenState.observeForever(screenStateObserver)
        viewModel.networkError.observeForever(netWorkErrorObserver)

        viewModel.postEvent(typeEvent)
        viewModel.postEvent(event)

        //Verify
        coVerifyInitialState()
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.Loading)) }
        coVerify { loginRepository.signIn(typeEvent.user, typeEvent.password) }
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.PasswordError(error.message))) }

        confirmVerifyMocks()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun onMainButtonClickedAndModeIsSignInGenericError() = runBlockingTest {
        //Given
        val error =
            ErrorResponse(
                code = -1,
                message = "Invalid message"
            )
        val typeEvent = LoginEvent.LoginTyping("user@email.com", "password", "")
        val event = LoginEvent.MainButtonPress

        coEvery { loginRepository.signIn(any(), any()) } returns ResultWrapper.GenericError(
            error.code,
            error
        )

        //When
        viewModel.screenState.observeForever(screenStateObserver)
        viewModel.networkError.observeForever(netWorkErrorObserver)

        viewModel.postEvent(typeEvent)
        viewModel.postEvent(event)

        //Verify
        coVerifyInitialState()
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.Loading)) }
        coVerify { loginRepository.signIn(typeEvent.user, typeEvent.password) }
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.Error(error.message))) }

        confirmVerifyMocks()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun onMainButtonClickedAndModeIsSignInNetWorkError() = runBlockingTest {
        //Given
        val typeEvent = LoginEvent.LoginTyping("user@email.com", "password", "")
        val event = LoginEvent.MainButtonPress

        coEvery { loginRepository.signIn(any(), any()) } returns ResultWrapper.NetworkError

        //When
        viewModel.screenState.observeForever(screenStateObserver)
        viewModel.networkError.observeForever(netWorkErrorObserver)

        viewModel.postEvent(typeEvent)
        viewModel.postEvent(event)

        //Verify
        coVerifyInitialState()
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.Loading)) }
        coVerify { loginRepository.signIn(typeEvent.user, typeEvent.password) }
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.SignIn)) }
        coVerify { netWorkErrorObserver.onChanged(true) }
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.SignIn)) }

        confirmVerifyMocks()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun onMainButtonClickedAndModeIsSignUp() = runBlockingTest {
        //Given
        val user = ApiUser("user@email.com", "id", "token")
        val typeEvent = LoginEvent.LoginTyping("user@email.com", "password", "password")
        val event = LoginEvent.MainButtonPress

        coEvery { loginRepository.signUp(any(), any()) } returns ResultWrapper.Success(user)

        //When
        viewModel.screenState.observeForever(screenStateObserver)
        viewModel.networkError.observeForever(netWorkErrorObserver)

        viewModel.postEvent(LoginEvent.SecondaryButtonPress)
        viewModel.postEvent(typeEvent)
        viewModel.postEvent(event)

        //Verify
        coVerifyInitialState()
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.SignUp)) }
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.Loading)) }
        coVerify { loginRepository.signUp(typeEvent.user, typeEvent.password) }
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.Success)) }

        confirmVerifyMocks()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun onMainButtonClickedAndModeIsSignUnNetWorkError() = runBlockingTest {
        //Given
        val typeEvent = LoginEvent.LoginTyping("user@email.com", "password", "password")
        val event = LoginEvent.MainButtonPress

        coEvery { loginRepository.signUp(any(), any()) } returns ResultWrapper.NetworkError

        //When
        viewModel.screenState.observeForever(screenStateObserver)
        viewModel.networkError.observeForever(netWorkErrorObserver)

        viewModel.postEvent(LoginEvent.SecondaryButtonPress)
        viewModel.postEvent(typeEvent)
        viewModel.postEvent(event)

        //Verify
        coVerifyInitialState()
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.SignUp)) }
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.Loading)) }
        coVerify { loginRepository.signUp(typeEvent.user, typeEvent.password) }
        coVerify { netWorkErrorObserver.onChanged(true) }
        coVerify { screenStateObserver.onChanged(Event(LoginScreenState.SignUp)) }

        confirmVerifyMocks()
    }

}
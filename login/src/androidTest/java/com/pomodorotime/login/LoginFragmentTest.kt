package com.pomodorotime.login

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.pomodorotime.core.IdlingResourceWrapper
import com.pomodorotime.core.IdlingResourcesSync
import com.pomodorotime.core.logger.PomodoroLogger
import com.pomodorotime.data.ErrorResponse
import com.pomodorotime.data.ResultWrapper
import com.pomodorotime.data.login.api.models.ApiUser
import com.pomodorotime.data.login.repository.LoginRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest

@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginFragmentTest : KoinTest {

    @MockK
    lateinit var loginRepository: LoginRepository

    @RelaxedMockK
    lateinit var navigator: LoginNavigator

    private val idlingResourceWrapper: IdlingResourcesSync = IdlingResourceWrapper

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        startKoin {
            modules(
                module { viewModel { LoginViewModel(get(), idlingResourceWrapper) } },
                module { single { navigator } },
                module { single { loginRepository } },
                module { single { PomodoroLogger() } }
            )
        }
        IdlingRegistry.getInstance().register(idlingResourceWrapper.getIdlingResource())
        launchFragmentInContainer<LoginFragment>(null, R.style.AppTheme)
    }

    @After
    fun setDown() {
        stopKoin()
        IdlingRegistry.getInstance().unregister(idlingResourceWrapper.getIdlingResource())
    }

    @Test
    fun loginFragmentInitOk() {
        //TexInputFields
        onView(withId(R.id.til_email)).check(matches(isDisplayed()))
        onView(withId(R.id.til_email)).check(matches(withTextInputLayoutHint(R.string.login_email_hint)))

        onView(withId(R.id.til_password)).check(matches(isDisplayed()))
        onView(withId(R.id.til_password)).check(matches(withTextInputLayoutHint(R.string.login_password_hint)))

        onView(withId(R.id.til_confirm_password)).check(matches(not(isDisplayed())))
        onView(withId(R.id.til_confirm_password)).check(matches(withTextInputLayoutHint(R.string.login_confirm_password_hint)))

        //Buttons
        onView(withId(R.id.login_loader)).check((matches(not(isDisplayed()))))
        onView(withId(R.id.btn_login)).check((matches(withText(R.string.login_sign_in))))
        onView(withId(R.id.btn_secondary)).check((matches(withText(R.string.login_create_an_account))))

        //Loader
        onView(withId(R.id.login_loader)).check(matches(not(isDisplayed())))
        onView(withId(R.id.login_container)).check(matches(isDisplayed()))
    }

    @Test
    fun loginFragmentToggleModeOK() {
        onView(withId(R.id.btn_secondary)).perform(click())

        //TexInputFields
        onView(withId(R.id.til_email)).check(matches(isDisplayed()))
        onView(withId(R.id.til_email)).check(matches(withTextInputLayoutHint(R.string.login_email_hint)))

        onView(withId(R.id.til_password)).check(matches(isDisplayed()))
        onView(withId(R.id.til_password)).check(matches(withTextInputLayoutHint(R.string.login_password_hint)))

        onView(withId(R.id.til_confirm_password)).check(matches(isDisplayed()))
        onView(withId(R.id.til_confirm_password)).check(matches(withTextInputLayoutHint(R.string.login_confirm_password_hint)))

        //Buttons
        onView(withId(R.id.login_loader)).check((matches(not(isDisplayed()))))
        onView(withId(R.id.btn_login)).check((matches(withText(R.string.login_sign_up))))
        onView(withId(R.id.btn_secondary)).check((matches(withText(R.string.login_sign_in_instead))))

        //Loader
        onView(withId(R.id.login_loader)).check(matches(not(isDisplayed())))
        onView(withId(R.id.login_container)).check(matches(isDisplayed()))
    }

    @Test
    fun loginFragmentSignInSuccess() {
        val user = "user@user.es"
        val password = "password"

        coEvery { loginRepository.signIn(any(), any()) } returns ResultWrapper.Success(
            ApiUser(user, "id", "token")
        )

        onView(withId(R.id.tx_email)).perform(replaceText(user))
        onView(withId(R.id.tx_password)).perform(replaceText(password))

        onView(withId(R.id.btn_login)).perform(click())

        onView(withId(R.id.login_loader)).check(matches(not(isDisplayed())))
        onView(withId(R.id.login_container)).check(matches(isDisplayed()))
        verify { navigator.navigateOnLoginSuccess() }
    }

    @Test
    fun loginFragmentSignUpSuccess() {
        val user = "user@user.es"
        val password = "password"

        coEvery { loginRepository.signUp(any(), any()) } returns ResultWrapper.Success(
            ApiUser(user, "id", "token")
        )

        onView(withId(R.id.tx_email)).perform(replaceText(user))
        onView(withId(R.id.tx_password)).perform(replaceText(password))
        onView(withId(R.id.btn_secondary)).perform(click())

        onView(withId(R.id.tx_confirm_password)).perform(replaceText(password))
        onView(withId(R.id.btn_login)).perform(click())

        onView(withId(R.id.login_loader)).check(matches(not(isDisplayed())))
        onView(withId(R.id.login_container)).check(matches(isDisplayed()))
        verify { navigator.navigateOnLoginSuccess() }
    }

    @Test
    fun loginFragmentEmailError() {
        val error =
            ErrorResponse(code = LoginRepository.ERROR_INVALID_EMAIL, message = "Invalid message")
        val user = "user@user.es"
        val password = "password"

        coEvery { loginRepository.signUp(any(), any()) } returns ResultWrapper.GenericError(
            error.code, error
        )

        onView(withId(R.id.tx_email)).perform(replaceText(user))
        onView(withId(R.id.tx_password)).perform(replaceText(password))
        onView(withId(R.id.btn_secondary)).perform(click())

        onView(withId(R.id.tx_confirm_password)).perform(replaceText(password))
        onView(withId(R.id.btn_login)).perform(click(), closeSoftKeyboard())

        onView(withId(R.id.login_loader)).check(matches(not(isDisplayed())))
        onView(withId(R.id.login_container)).check(matches(isDisplayed()))

        onView(withId(R.id.til_email)).check(matches(withTextInputError(error.message)))
    }

    @Test
    fun loginFragmentPasswordError() {
        val error =
            ErrorResponse(code = LoginRepository.ERROR_WRONG_PASSWORD, message = "Invalid message")
        val user = "user@user.es"
        val password = "password"

        coEvery { loginRepository.signUp(any(), any()) } returns ResultWrapper.GenericError(
            error.code, error
        )

        onView(withId(R.id.tx_email)).perform(replaceText(user))
        onView(withId(R.id.tx_password)).perform(replaceText(password))
        onView(withId(R.id.btn_secondary)).perform(click())

        onView(withId(R.id.tx_confirm_password)).perform(replaceText(password))
        onView(withId(R.id.btn_login)).perform(click(), closeSoftKeyboard())

        onView(withId(R.id.login_loader)).check(matches(not(isDisplayed())))
        onView(withId(R.id.login_container)).check(matches(isDisplayed()))

        onView(withId(R.id.til_password)).check(matches(withTextInputError(error.message)))
    }
}
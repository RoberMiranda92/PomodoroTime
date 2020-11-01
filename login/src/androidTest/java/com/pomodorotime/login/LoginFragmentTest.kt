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
import com.pomodorotime.domain.login.usecases.SigInUseCase
import com.pomodorotime.domain.login.usecases.SigUpUseCase
import com.pomodorotime.domain.models.ErrorEntity
import com.pomodorotime.domain.models.ResultWrapper
import com.pomodorotime.domain.models.User
import io.mockk.MockKAnnotations
import io.mockk.coEvery
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

    @RelaxedMockK
    lateinit var signInUseCase: SigInUseCase

    @RelaxedMockK
    lateinit var signUpUseCase: SigUpUseCase

    @RelaxedMockK
    lateinit var navigator: LoginNavigator

    private val idlingResourceWrapper: IdlingResourcesSync = IdlingResourceWrapper

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        startKoin {
            modules(
                module { viewModel { LoginViewModel(get(), get(), idlingResourceWrapper) } },
                module { single { navigator } },
                module {
                    single { signInUseCase }
                    single { signUpUseCase }
                },
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
    fun loginFragmentSignInSuccessWithSave() {
        val user = "user@user.es"
        val password = "password"

        coEvery { signInUseCase.invoke(any()) } returns
                ResultWrapper.Success(User(user, "id", "token"))

        onView(withId(R.id.tx_email)).perform(replaceText(user))
        onView(withId(R.id.tx_password)).perform(replaceText(password))

        onView(withId(R.id.btn_login)).perform(click())

        onView(withId(com.google.android.material.R.id.alertTitle))
            .check(matches(withText(R.string.login_dialog_title)))
        onView(withId(android.R.id.message))
            .check(matches(withText(R.string.login_dialog_message)))
        onView(withId(android.R.id.button1))
            .check(matches(withText(R.string.login_dialog_possitive)))
        onView(withId(android.R.id.button2))
            .check(matches(withText(R.string.login_dialog_negative)))

        onView(withId(android.R.id.button1))
            .perform(click())

        verify { navigator.navigateOnLoginSuccess() }
    }

    @Test
    fun loginFragmentSignUpSuccessWithSave() {
        val user = "user@user.es"
        val password = "password"

        coEvery { signUpUseCase.invoke(any()) } returns
                ResultWrapper.Success(User(user, "id", "token"))

        onView(withId(R.id.tx_email)).perform(replaceText(user))
        onView(withId(R.id.tx_password)).perform(replaceText(password))
        onView(withId(R.id.btn_secondary)).perform(click())

        onView(withId(R.id.tx_confirm_password)).perform(replaceText(password))
        onView(withId(R.id.btn_login)).perform(click())

        onView(withId(com.google.android.material.R.id.alertTitle))
            .check(matches(withText(R.string.login_dialog_title)))
        onView(withId(android.R.id.message))
            .check(matches(withText(R.string.login_dialog_message)))
        onView(withId(android.R.id.button1))
            .check(matches(withText(R.string.login_dialog_possitive)))
        onView(withId(android.R.id.button2))
            .check(matches(withText(R.string.login_dialog_negative)))

        onView(withId(android.R.id.button1))
            .perform(click())

        verify { navigator.navigateOnLoginSuccess() }
    }

    @Test
    fun loginFragmentSignInSuccessWithOutSave() {
        val user = "user@user.es"
        val password = "password"

        coEvery { signInUseCase.invoke(any()) } returns
                ResultWrapper.Success(User(user, "id", "token"))

        onView(withId(R.id.tx_email)).perform(replaceText(user))
        onView(withId(R.id.tx_password)).perform(replaceText(password))

        onView(withId(R.id.btn_login)).perform(click())

        onView(withId(com.google.android.material.R.id.alertTitle))
            .check(matches(withText(R.string.login_dialog_title)))
        onView(withId(android.R.id.message))
            .check(matches(withText(R.string.login_dialog_message)))
        onView(withId(android.R.id.button1))
            .check(matches(withText(R.string.login_dialog_possitive)))
        onView(withId(android.R.id.button2))
            .check(matches(withText(R.string.login_dialog_negative)))

        onView(withId(android.R.id.button2))
            .perform(click())

        verify { navigator.navigateOnLoginSuccess() }
    }

    @Test
    fun loginFragmentSignUpSuccessWithOutSave() {
        val user = "user@user.es"
        val password = "password"

        coEvery { signUpUseCase.invoke(any()) } returns
                ResultWrapper.Success(User(user, "id", "token"))

        onView(withId(R.id.tx_email)).perform(replaceText(user))
        onView(withId(R.id.tx_password)).perform(replaceText(password))
        onView(withId(R.id.btn_secondary)).perform(click())

        onView(withId(R.id.tx_confirm_password)).perform(replaceText(password))
        onView(withId(R.id.btn_login)).perform(click())

        onView(withId(com.google.android.material.R.id.alertTitle))
            .check(matches(withText(R.string.login_dialog_title)))
        onView(withId(android.R.id.message))
            .check(matches(withText(R.string.login_dialog_message)))
        onView(withId(android.R.id.button1))
            .check(matches(withText(R.string.login_dialog_possitive)))
        onView(withId(android.R.id.button2))
            .check(matches(withText(R.string.login_dialog_negative)))

        onView(withId(android.R.id.button2))
            .perform(click())

        verify { navigator.navigateOnLoginSuccess() }
    }

    @Test
    fun loginFragmentEmailError() {
        val error = ErrorEntity.UserEmailError("Invalid message")
        val user = "user@user.es"
        val password = "password"

        coEvery { signUpUseCase.invoke(any()) } returns ResultWrapper.Error(error)

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
        val error = ErrorEntity.UserPasswordError("Invalid message")
        val user = "user@user.es"
        val password = "password"

        coEvery { signUpUseCase.invoke(any()) } returns ResultWrapper.Error(error)

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
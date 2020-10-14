package com.pomodorotime.task.create

import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.pomodorotime.core.IdlingResourceWrapper
import com.pomodorotime.core.IdlingResourcesSync
import com.pomodorotime.core.logger.PomodoroLogger
import com.pomodorotime.domain.models.ResultWrapper
import com.pomodorotime.domain.task.usecases.CreateTaskUseCase
import com.pomodorotime.task.R
import com.pomodorotime.task.TaskNavigator
import com.pomodorotime.task.withMenu
import com.pomodorotime.task.withTextInputError
import com.pomodorotime.task.withTextInputLayoutHint
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
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
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class CreateTaskFragmentTest : KoinTest {

    @MockK
    lateinit var createTasUseCase: CreateTaskUseCase

    @RelaxedMockK
    lateinit var navigator: TaskNavigator

    private val idlingResourceWrapper: IdlingResourcesSync = IdlingResourceWrapper

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        startKoin {
            modules(
                module { viewModel { CreateTaskViewModel(get(), idlingResourceWrapper) } },
                module { single { navigator } },
                module { single { createTasUseCase } },
                module {
                    single { PomodoroLogger() }
                }
            )
        }
        IdlingRegistry.getInstance().register(idlingResourceWrapper.getIdlingResource())
        launchFragmentInContainer<CreateTaskFragment>(
            null,
            R.style.AppTheme
        )
    }

    @After
    fun setDown() {
        stopKoin()
        IdlingRegistry.getInstance().unregister(idlingResourceWrapper.getIdlingResource())
    }

    @Test
    fun createTaskOk() {
        coEvery { createTasUseCase.invoke(any()) } returns ResultWrapper.Success(1L)

        //Toolbar
        val toolbar = onView(withId(R.id.toolbar))
        toolbar.check(matches(isDisplayed()))
        toolbar.check(matches(withMenu(R.menu.create_task_menu)))

        //EditText
        val taskName = onView(withId(R.id.til_task_name))
        taskName.check(matches(isDisplayed()))
        taskName.check(matches(withTextInputLayoutHint(R.string.create_task_task_name_hint)))
        onView(withId(R.id.tx_task_name)).perform(replaceText("My First task"))

        //Pomodoros
        val pomodoros = onView(withId(R.id.tv_est_pomodoros))
        pomodoros.check(matches(isDisplayed()))
        pomodoros.check(matches(withText(R.string.create_task_estimated_pomodoros)))

        //Loader
        val loader = onView(withId(R.id.loader))
        loader.check(matches(not(isDisplayed())))

        //Counter
        val counter = onView(withId(R.id.pcv_counter))
        counter.check(matches(isDisplayed()))
        val plusButton = onView(withId(R.id.increment))
        plusButton.check(matches(isDisplayed()))

        val decrementButton = onView(withId(R.id.increment))
        decrementButton.check(matches(isDisplayed()))

        for (i in 1..3) {
            plusButton.perform(click())
        }

        onView(withId(R.id.action_save)).perform(click())

        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText(R.string.create_task_success)))

        coVerify { createTasUseCase.invoke(any()) }

        confirmVerified(createTasUseCase)
    }

    @Test
    fun createTaskInvalidName() {

        //EditText
        val taskName = onView(withId(R.id.til_task_name))
        taskName.check(matches(isDisplayed()))
        taskName.check(matches(withTextInputLayoutHint(R.string.create_task_task_name_hint)))

        onView(withId(R.id.action_save)).perform(click())

        taskName.check(matches(withTextInputError(R.string.create_task_invalid_name)))
    }

    @Test
    fun onBackPressed() {

        //Toolbar
        val toolbar = onView(withId(R.id.toolbar))
        toolbar.check(matches(isDisplayed()))

        onView(
            allOf(
                withParent(withId(R.id.toolbar)),
                isAssignableFrom(AppCompatImageButton::class.java)
            )
        ).perform(click())

        verify { navigator.onBack() }
        confirmVerified(navigator)
    }
}
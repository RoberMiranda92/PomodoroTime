package com.pomodorotime.task.tasklist

import android.text.format.DateFormat
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.pomodorotime.core.IdlingResourceWrapper
import com.pomodorotime.core.IdlingResourcesSync
import com.pomodorotime.data.ResultWrapper
import com.pomodorotime.data.task.TaskEntity
import com.pomodorotime.data.task.TaskRepository
import com.pomodorotime.task.R
import com.pomodorotime.task.RecyclerViewMatcher
import com.pomodorotime.task.TaskNavigator
import com.pomodorotime.task.withToolbarText
import io.mockk.MockKAnnotations
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
import java.util.*


@LargeTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class TaskListFragmentTest : KoinTest {

    @MockK
    lateinit var repository: TaskRepository

    @RelaxedMockK
    lateinit var navigator: TaskNavigator

    private val idlingResourceWrapper: IdlingResourcesSync = IdlingResourceWrapper

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        startKoin {
            modules(
                module { viewModel { TaskViewModel(get(), idlingResourceWrapper) } },
                module { single { navigator } },
                module { single { repository } }
            )
        }
        IdlingRegistry.getInstance().register(idlingResourceWrapper.getIdlingResource())
    }

    @After
    fun setDown() {
        stopKoin()
        IdlingRegistry.getInstance().unregister(idlingResourceWrapper.getIdlingResource())
    }

    @Test
    fun taskListInitEmptyOK() {
        every { repository.getAllTasks() } returns flowOf(ResultWrapper.Success(emptyList()))

        launchFragmentInContainer<TaskListFragment>(
            null,
            R.style.AppTheme
        )

        //Toolbar
        onView(withId(R.id.toolbar)).check(matches((isDisplayed())))
        onView(withId(R.id.toolbar)).check(
            matches(
                (withToolbarText(
                    R.string.task_list_title
                ))
            )
        )

        //List
        onView(withId(R.id.rv_tasks))
            .check(matches(not(isDisplayed())))

        //Button
        onView(withId(R.id.fb_add_task))
            .check(matches(not(isDisplayed())))

        //Status view
        onView(withId(R.id.status_view))
            .check(matches(isDisplayed()))
        onView(withId(R.id.title))
            .check(matches(withText(R.string.task_list_empty_state_title)))
        onView(withId(R.id.subtitle))
            .check(matches(withText(R.string.task_list_empty_state_subtitle)))
        onView(withId(R.id.button))
            .check(matches(withText(R.string.task_list_empty_state_button)))
        onView(withId(R.id.button))
            .check(matches(isDisplayed()))
    }

    @Test
    fun taskListInitEmptyClickOK() {
        every { repository.getAllTasks() } returns flowOf(ResultWrapper.Success(emptyList()))

        launchFragmentInContainer<TaskListFragment>(
            null,
            R.style.AppTheme
        )

        //Status view
        onView(withId(R.id.button))
            .perform(click())

        verify { navigator.navigateOnToCreateTask() }
        verify { repository.getAllTasks() }
        confirmVerified(repository)
        confirmVerified(navigator)
    }

    @Test
    fun taskListInitOK() {
        val list = listOf(TaskEntity1, TaskEntity2, TaskEntity3)
        every { repository.getAllTasks() } returns flowOf(ResultWrapper.Success(list))

        launchFragmentInContainer<TaskListFragment>(
            null,
            R.style.AppTheme
        )

        //Toolbar
        onView(withId(R.id.toolbar)).check(matches((isDisplayed())))
        onView(withId(R.id.toolbar)).check(
            matches(
                (withToolbarText(
                    R.string.task_list_title
                ))
            )
        )

        //List
        onView(withId(R.id.rv_tasks))
            .check(matches(isDisplayed()))

        //Button
        onView(withId(R.id.fb_add_task))
            .check(matches(isDisplayed()))

        //Status view
        onView(withId(R.id.status_view))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun taskElementsAreOK() {
        val list = listOf(TaskEntity1, TaskEntity2, TaskEntity3, TaskEntity4)
        every { repository.getAllTasks() } returns flowOf(ResultWrapper.Success(list))

        launchFragmentInContainer<TaskListFragment>(
            null,
            R.style.AppTheme
        )

        //List
        onView(withId(R.id.rv_tasks))
            .check(matches(isDisplayed()))

        for (i in list.indices) {

            /* check if the ViewHolder is being displayed */onView(
                RecyclerViewMatcher(R.id.rv_tasks)
                    .atPositionOnView(i, R.id.container)
            ).check(matches(isDisplayed()))

            onView(
                RecyclerViewMatcher(R.id.rv_tasks).atPositionOnView(i, R.id.tv_task_name)
            ).check(matches(withText(list[i].name)))

            onView(
                RecyclerViewMatcher(R.id.rv_tasks).atPositionOnView(i, R.id.tv_est_pommodoros)
            ).check(matches(withText(list[i].estimatedPomodoros.toString())))

            val date =
                DateFormat.getDateFormat(InstrumentationRegistry.getInstrumentation().context)
                    .format(list[i].creationDate)
            onView(
                RecyclerViewMatcher(R.id.rv_tasks).atPositionOnView(i, R.id.tv_task_date)

            ).check(matches(withText(date)))

            onView(
                RecyclerViewMatcher(R.id.rv_tasks).atPositionOnView(i, R.id.iv_task_check)
            ).check(matches(if (list[i].completed) isDisplayed() else not(isDisplayed())))
        }
    }

    @Test
    fun addTaskButtonClick() {
        val list = listOf(TaskEntity1, TaskEntity2, TaskEntity3)
        every { repository.getAllTasks() } returns flowOf(ResultWrapper.Success(list))

        launchFragmentInContainer<TaskListFragment>(
            null,
            R.style.AppTheme
        )

        //Toolbar
        onView(withId(R.id.toolbar)).check(matches((isDisplayed())))
        onView(withId(R.id.toolbar)).check(
            matches(
                (withToolbarText(
                    R.string.task_list_title
                ))
            )
        )

        //List
        onView(withId(R.id.rv_tasks))
            .check(matches(isDisplayed()))

        //Button
        onView(withId(R.id.fb_add_task))
            .check(matches(isDisplayed()))

        onView(withId(R.id.fb_add_task))
            .perform(click())

        verify { navigator.navigateOnToCreateTask() }
        verify { repository.getAllTasks() }
        confirmVerified(repository)
        confirmVerified(navigator)
    }

    companion object {
        private val TaskEntity1 = TaskEntity(
            id = 1,
            name = "Task1",
            creationDate = Date(),
            estimatedPomodoros = 1,
            completed = false
        )
        private val TaskEntity2 = TaskEntity(
            id = 2,
            name = "Task2",
            creationDate = Date(),
            estimatedPomodoros = 1,
            completed = false
        )
        private val TaskEntity3 = TaskEntity(
            id = 3,
            name = "Task3",
            creationDate = Date(),
            estimatedPomodoros = 1,
            completed = false
        )

        private val TaskEntity4 = TaskEntity(
            id = 3,
            name = "Task3",
            creationDate = Date(),
            estimatedPomodoros = 1,
            completed = true
        )

    }
}

    
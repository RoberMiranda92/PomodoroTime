package com.pomodorotime.task.create

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.pomodorotime.core.Event
import com.pomodorotime.core.getCurrentDate
import com.pomodorotime.data.ErrorResponse
import com.pomodorotime.data.ResultWrapper
import com.pomodorotime.data.task.TaskEntity
import com.pomodorotime.data.task.TaskRepository
import com.pomodorotime.task.CoroutinesRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

@ExperimentalCoroutinesApi
class CreateTaskViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesRule = CoroutinesRule()

    @MockK
    lateinit var repository: TaskRepository

    @RelaxedMockK
    lateinit var screenStateObserver: Observer<Event<CreateTaskScreenState>>

    private lateinit var viewModel: CreateTaskViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = CreateTaskViewModel(repository)
        viewModel.screenState.observeForever(screenStateObserver)
    }

    @After
    fun setDown() {
        viewModel.screenState.removeObserver(screenStateObserver)
    }

    private fun verifyAll() {
        confirmVerified(repository)
        confirmVerified(screenStateObserver)
    }

    @Test
    fun createTaskEventSaveTaskSuccessTest() = coroutinesRule.runBlockingTest {
        //Given
        viewModel.setTaskName(TaskEntity1.name)
        viewModel.setPomodoroCounter(TaskEntity1.estimatedPomodoros)

        //When
        coEvery { repository.insetTask(any()) } returns ResultWrapper.Success(Unit)
        viewModel.postEvent(CreateTaskEvent.SaveTask)

        //Verify
        coVerify { screenStateObserver.onChanged(Event(CreateTaskScreenState.Initial())) }
        coVerify { screenStateObserver.onChanged(Event(CreateTaskScreenState.Loading)) }
        coVerify { repository.insetTask(any()) }
        coVerify { screenStateObserver.onChanged(Event(CreateTaskScreenState.Success)) }
        verifyAll()
    }

    @Test
    fun createTaskEventSaveTaskErrorTest() = coroutinesRule.runBlockingTest {
        //Given
        val error = ErrorResponse(message = "my exception")
        viewModel.setTaskName(TaskEntity1.name)
        viewModel.setPomodoroCounter(TaskEntity1.estimatedPomodoros)

        //When
        coEvery { repository.insetTask(any()) } returns ResultWrapper.GenericError(null, error)
        viewModel.postEvent(CreateTaskEvent.SaveTask)

        //Verify
        coVerify { screenStateObserver.onChanged(Event(CreateTaskScreenState.Initial())) }
        coVerify { screenStateObserver.onChanged(Event(CreateTaskScreenState.Loading)) }
        coVerify { repository.insetTask(any()) }
        coVerify { screenStateObserver.onChanged(Event(CreateTaskScreenState.Error(error.message))) }
        coVerify {
            screenStateObserver.onChanged(
                Event(
                    CreateTaskScreenState.Initial(
                        TaskEntity1.name,
                        TaskEntity1.estimatedPomodoros
                    )
                )
            )
        }
        verifyAll()
    }

    @Test
    fun createTaskEventSaveTaskNetworkErrorTest() = coroutinesRule.runBlockingTest {
        //Given
        viewModel.setTaskName(TaskEntity1.name)
        viewModel.setPomodoroCounter(TaskEntity1.estimatedPomodoros)

        //When
        coEvery { repository.insetTask(any()) } returns ResultWrapper.NetworkError
        viewModel.postEvent(CreateTaskEvent.SaveTask)

        //Verify
        coVerify { screenStateObserver.onChanged(Event(CreateTaskScreenState.Initial())) }
        coVerify { screenStateObserver.onChanged(Event(CreateTaskScreenState.Loading)) }
        coVerify { repository.insetTask(any()) }
        coVerify {
            screenStateObserver.onChanged(
                Event(
                    CreateTaskScreenState.Initial(
                        TaskEntity1.name,
                        TaskEntity1.estimatedPomodoros
                    )
                )
            )
        }
        verifyAll()
    }

    @Test
    fun createTaskEventSaveTaskEmptyNameTest() = coroutinesRule.runBlockingTest {
        //Given
        viewModel.setTaskName("")

        //When
        coEvery { repository.insetTask(any()) } returns ResultWrapper.Success(Unit)
        viewModel.postEvent(CreateTaskEvent.SaveTask)

        //Verify
        coVerify { screenStateObserver.onChanged(Event(CreateTaskScreenState.Initial())) }
        coVerify { screenStateObserver.onChanged(Event(CreateTaskScreenState.InvalidName)) }
        verifyAll()
    }

    @Test
    fun createTaskEventEddiTaskEmptyNameTest() = coroutinesRule.runBlockingTest {

        //When
        coEvery { repository.insetTask(any()) } returns ResultWrapper.Success(Unit)
        viewModel.postEvent(
            CreateTaskEvent.EditingTask(
                TaskEntity1.name,
                TaskEntity1.estimatedPomodoros
            )
        )
        viewModel.postEvent(CreateTaskEvent.SaveTask)

        //Verify
        coVerify { screenStateObserver.onChanged(Event(CreateTaskScreenState.Initial())) }
        coVerify { screenStateObserver.onChanged(Event(CreateTaskScreenState.Loading)) }
        coVerify { repository.insetTask(any()) }
        coVerify { screenStateObserver.onChanged(Event(CreateTaskScreenState.Success)) }
        verifyAll()
    }

    companion object {
        private val TaskEntity1 = TaskEntity(
            name = "Task1",
            creationDate = Date(),
            donePomodoros = 0,
            estimatedPomodoros = 1,
            shortBreaks = 0,
            longBreaks = 0,
            completed = true
        )

    }
}
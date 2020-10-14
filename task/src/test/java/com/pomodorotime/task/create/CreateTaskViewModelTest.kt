package com.pomodorotime.task.create

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.pomodorotime.core.Event
import com.pomodorotime.core.SnackBarrError
import com.pomodorotime.domain.models.ErrorEntity
import com.pomodorotime.domain.models.ResultWrapper
import com.pomodorotime.domain.models.Task
import com.pomodorotime.domain.task.usecases.CreateTaskUseCase
import com.pomodorotime.task.CoroutinesRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import java.util.Date
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CreateTaskViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesRule = CoroutinesRule()

    @MockK
    lateinit var createTaskUseCase: CreateTaskUseCase

    @RelaxedMockK
    lateinit var screenStateObserver: Observer<Event<CreateTaskScreenState>>

    @RelaxedMockK
    lateinit var createTaskErrorObserver: Observer<Event<SnackBarrError>>

    private lateinit var viewModel: CreateTaskViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = CreateTaskViewModel(createTaskUseCase)
        viewModel.screenState.observeForever(screenStateObserver)
        viewModel.createTaskError.observeForever(createTaskErrorObserver)
    }

    @After
    fun setDown() {
        viewModel.screenState.removeObserver(screenStateObserver)
    }

    private fun verifyAll() {
        confirmVerified(createTaskUseCase)
        confirmVerified(screenStateObserver)
    }

    @Test
    fun createTaskEventSaveTaskSuccessTest() = coroutinesRule.runBlockingTest {
        //Given
        viewModel.setTaskName(Task.name)
        viewModel.setPomodoroCounter(Task.estimatedPomodoros)

        //When
        coEvery { createTaskUseCase.invoke(any()) } returns ResultWrapper.Success(1L)
        viewModel.postEvent(CreateTaskEvent.SaveTask)

        //Verify
        coVerify { screenStateObserver.onChanged(Event(CreateTaskScreenState.Initial())) }
        coVerify { screenStateObserver.onChanged(Event(CreateTaskScreenState.Loading)) }
        coVerify { createTaskUseCase.invoke(any()) }
        coVerify { screenStateObserver.onChanged(Event(CreateTaskScreenState.Success)) }
        verifyAll()
    }

    @Test
    fun createTaskEventSaveTaskErrorTest() = coroutinesRule.runBlockingTest {
        //Given
        val error = ErrorEntity.GenericError(-1, "my exception")

        viewModel.setTaskName(Task.name)
        viewModel.setPomodoroCounter(Task.estimatedPomodoros)

        //When
        coEvery { createTaskUseCase.invoke(any()) } returns ResultWrapper.Error(error)
        viewModel.postEvent(CreateTaskEvent.SaveTask)

        //Verify
        coVerify { screenStateObserver.onChanged(Event(CreateTaskScreenState.Initial())) }
        coVerify { screenStateObserver.onChanged(Event(CreateTaskScreenState.Loading)) }
        coVerify { createTaskUseCase.invoke(any()) }
        coVerify { createTaskErrorObserver.onChanged(Event(SnackBarrError(true,error.message))) }
        coVerify {
            screenStateObserver.onChanged(
                Event(
                    CreateTaskScreenState.Initial(
                        Task.name,
                        Task.estimatedPomodoros
                    )
                )
            )
        }

        verifyAll()
    }

    @Test
    fun createTaskEventSaveTaskNetworkErrorTest() = coroutinesRule.runBlockingTest {
        val error = ErrorEntity.NetworkError

        //Given
        viewModel.setTaskName(Task.name)
        viewModel.setPomodoroCounter(Task.estimatedPomodoros)

        //When
        coEvery { createTaskUseCase.invoke(any()) } returns ResultWrapper.Error(error)
        viewModel.postEvent(CreateTaskEvent.SaveTask)

        //Verify
        coVerify { screenStateObserver.onChanged(Event(CreateTaskScreenState.Initial())) }
        coVerify { screenStateObserver.onChanged(Event(CreateTaskScreenState.Loading)) }
        coVerify { createTaskUseCase.invoke(any()) }
        coVerify {
            screenStateObserver.onChanged(
                Event(
                    CreateTaskScreenState.Initial(
                        Task.name,
                        Task.estimatedPomodoros
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
        coEvery { createTaskUseCase.invoke(any()) } returns ResultWrapper.Success(1L)
        viewModel.postEvent(CreateTaskEvent.SaveTask)

        //Verify
        coVerify { screenStateObserver.onChanged(Event(CreateTaskScreenState.Initial())) }
        coVerify { screenStateObserver.onChanged(Event(CreateTaskScreenState.InvalidName)) }
        verifyAll()
    }

    @Test
    fun createTaskEventEddiTaskEmptyNameTest() = coroutinesRule.runBlockingTest {

        //When
        coEvery { createTaskUseCase.invoke(any()) } returns ResultWrapper.Success(1L)
        viewModel.postEvent(
            CreateTaskEvent.EditingTask(
                Task.name,
                Task.estimatedPomodoros
            )
        )
        viewModel.postEvent(CreateTaskEvent.SaveTask)

        //Verify
        coVerify { screenStateObserver.onChanged(Event(CreateTaskScreenState.Initial())) }
        coVerify { screenStateObserver.onChanged(Event(CreateTaskScreenState.Loading)) }
        coVerify { createTaskUseCase.invoke(any()) }
        coVerify { screenStateObserver.onChanged(Event(CreateTaskScreenState.Success)) }
        verifyAll()
    }

    companion object {
        private val Task = Task(
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
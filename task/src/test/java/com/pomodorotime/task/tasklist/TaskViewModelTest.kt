package com.pomodorotime.task.tasklist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.pomodorotime.core.Event
import com.pomodorotime.core.SnackBarrError
import com.pomodorotime.domain.models.ErrorEntity
import com.pomodorotime.domain.models.ResultWrapper
import com.pomodorotime.domain.models.Task
import com.pomodorotime.domain.task.usecases.DeleteTaskUseCase
import com.pomodorotime.domain.task.usecases.GetAllTaskUseCase
import com.pomodorotime.task.CoroutinesRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import java.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class TaskViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesRule = CoroutinesRule()

    @MockK
    lateinit var getAllTaskUseCase: GetAllTaskUseCase

    @MockK
    lateinit var deleteTaskUseCase: DeleteTaskUseCase

    @RelaxedMockK
    lateinit var screenStateObserver: Observer<Event<TaskListScreenState>>

    @RelaxedMockK
    lateinit var errorObserver: Observer<Event<SnackBarrError>>

    @RelaxedMockK
    lateinit var navigationToCreateTaskObserver: Observer<Event<Boolean>>

    private lateinit var viewModel: TaskViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = TaskViewModel(getAllTaskUseCase, deleteTaskUseCase)
        viewModel.screenState.observeForever(screenStateObserver)
        viewModel.navigationToCreateTask.observeForever(navigationToCreateTaskObserver)
        viewModel.taskListError.observeForever(errorObserver)
    }

    @After
    fun setDown() {
        viewModel.screenState.removeObserver(screenStateObserver)
        viewModel.navigationToCreateTask.removeObserver(navigationToCreateTaskObserver)
        viewModel.taskListError.removeObserver(errorObserver)
    }

    private fun verifyAll() {
        confirmVerified(getAllTaskUseCase)
        confirmVerified(screenStateObserver)
    }

    @Test
    fun loadEmptyTaskListTestSuccess() =
        coroutinesRule.runBlockingTest {
            //Given
            val list = ResultWrapper.Success(emptyList<Task>())

            //When
            coEvery { getAllTaskUseCase.invoke(any()) } returns flowOf(list)

            viewModel.postEvent(TaskListEvent.Load)

            //Verify
            coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.Initial)) }
            coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.Loading)) }
            coVerify { getAllTaskUseCase.invoke(any()) }
            coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.EmptyState)) }
            verifyAll()
        }

    @Test
    fun loadTaskListTestSuccess() = coroutinesRule.runBlockingTest {
        //Given
        val taskList = listOf(Task1, Task2, Task3)
        val list = ResultWrapper.Success(taskList)

        //When
        coEvery { getAllTaskUseCase.invoke(any()) } returns flowOf(list)

        viewModel.postEvent(TaskListEvent.Load)

        //Verify
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.Initial)) }
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.Loading)) }
        coVerify { getAllTaskUseCase.invoke(any()) }
        coVerify {
            screenStateObserver.onChanged(
                Event(
                    TaskListScreenState.DataLoaded(
                        fromModelToView(taskList)
                    )
                )
            )
        }
        verifyAll()
    }

    @Test
    fun loadErrorTaskListTestSuccess() = coroutinesRule.runBlockingTest {
        //Given
        val error = ErrorEntity.GenericError(message = "my exception")

        //When
        coEvery { getAllTaskUseCase.invoke(any()) } returns flowOf(ResultWrapper.Error(error))

        viewModel.postEvent(TaskListEvent.Load)

        //Verify
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.Initial)) }
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.Loading)) }
        coVerify { getAllTaskUseCase.invoke(any()) }
        coVerify { errorObserver.onChanged(Event(SnackBarrError(true, error.message))) }
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.EmptyState)) }
        verifyAll()
    }

    @Test
    fun onDeleteTaskElementsPressedSuccessTest() = coroutinesRule.runBlockingTest {
        //Given
        val taskList = fromModelToView(listOf(Task1, Task2, Task3))
        val toDeleteList = taskList.subList(0, 2)

        viewModel.setList(taskList)

        //When
        coEvery { deleteTaskUseCase.invoke(any()) } returns ResultWrapper.Success(Unit)

        viewModel.postEvent(TaskListEvent.DeleteTaskElementsPressed(toDeleteList))

        //Verify
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.Initial)) }
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.Loading)) }
        coVerify {
            deleteTaskUseCase.invoke(
                DeleteTaskUseCase.DeleteTaskUseCaseParams(
                    toDeleteList.map { it.id })
            )
        }
        coVerify {
            screenStateObserver.onChanged(
                Event(
                    TaskListScreenState.DataLoaded(
                        fromModelToView(listOf(Task3))
                    )
                )
            )
        }
        verifyAll()
    }

    @Test
    fun onDeleteTaskElementsPressedSuccessAndEmptyTest() = coroutinesRule.runBlockingTest {
        //Given
        val taskList = fromModelToView(listOf(Task1, Task2, Task3))
        viewModel.setList(taskList)

        //When
        coEvery { deleteTaskUseCase.invoke(any()) } returns ResultWrapper.Success(Unit)

        viewModel.postEvent(TaskListEvent.DeleteTaskElementsPressed(taskList))

        //Verify
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.Initial)) }
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.Loading)) }
        coVerify {
            deleteTaskUseCase.invoke(
                DeleteTaskUseCase.DeleteTaskUseCaseParams(taskList.map { it.id })
            )
        }
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.EmptyState)) }
        verifyAll()
    }

    @Test
    fun onDeleteTaskElementsPressedSuccessAndErrorTest() = coroutinesRule.runBlockingTest {
        //Given
        val error = ErrorEntity.GenericError(message = "my exception")
        val taskList = fromModelToView(listOf(Task1, Task2, Task3))
        viewModel.setList(taskList)

        //When
        coEvery { deleteTaskUseCase.invoke(any()) } returns ResultWrapper.Error(error)

        viewModel.postEvent(TaskListEvent.DeleteTaskElementsPressed(taskList))

        //Verify
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.Initial)) }
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.Loading)) }
        coVerify { deleteTaskUseCase.invoke(DeleteTaskUseCase.DeleteTaskUseCaseParams(taskList.map { it.id })) }
        coVerify { errorObserver.onChanged(Event(SnackBarrError(true, error.message))) }
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.DataLoaded(taskList))) }

        verifyAll()
    }

    @Test
    fun onEditTaskListEventTest() = coroutinesRule.runBlockingTest {
        //When
        viewModel.postEvent(TaskListEvent.EditTaskList)

        //Verify
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.Initial)) }
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.Editing)) }
        verifyAll()
    }

    @Test
    fun onAddTaskPressedTest() = coroutinesRule.runBlockingTest {
        //When
        viewModel.postEvent(TaskListEvent.AddTaskPressed)

        //Verify
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.Initial)) }
        coVerify { navigationToCreateTaskObserver.onChanged(Event(true)) }
        verifyAll()
    }

    @Test
    fun onEditTaskListFinishedTest() = coroutinesRule.runBlockingTest {
        val list = fromModelToView(listOf(Task1, Task2))

        //When
        viewModel.setList(list)
        viewModel.postEvent(TaskListEvent.EditTaskListFinished)

        //Verify
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.Initial)) }
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.DataLoaded(list))) }
        verifyAll()
    }

    companion object {
        private val Task1 = Task(
            id = 1,
            name = "Task1",
            creationDate = Date(),
            donePomodoros = 0,
            estimatedPomodoros = 1,
            shortBreaks = 0,
            longBreaks = 0,
            completed = true
        )
        private val Task2 = Task(
            id = 2,
            name = "Task2",
            creationDate = Date(),
            donePomodoros = 0,
            estimatedPomodoros = 1,
            shortBreaks = 0,
            longBreaks = 0,
            completed = true
        )
        private val Task3 = Task(
            id = 3,
            name = "Task3",
            creationDate = Date(),
            donePomodoros = 0,
            estimatedPomodoros = 1,
            shortBreaks = 0,
            longBreaks = 0,
            completed = true
        )

    }

}
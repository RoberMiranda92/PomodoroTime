package com.pomodorotime.task.tasklist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.pomodorotime.core.Event
import com.pomodorotime.data.ErrorResponse
import com.pomodorotime.data.ResultWrapper
import com.pomodorotime.data.task.dataBase.TaskEntity
import com.pomodorotime.data.task.TaskRepository
import com.pomodorotime.task.CoroutinesRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

@ExperimentalCoroutinesApi
class TaskViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesRule = CoroutinesRule()

    @MockK
    lateinit var repository: TaskRepository

    @RelaxedMockK
    lateinit var screenStateObserver: Observer<Event<TaskListScreenState>>

    private lateinit var viewModel: TaskViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = TaskViewModel(repository)
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
    fun loadEmptyTaskListTestSuccess() =
        coroutinesRule.runBlockingTest {
            //Given
            val list = ResultWrapper.Success(emptyList<TaskEntity>())

            //When
            coEvery { repository.getAllTasks() } returns flowOf(list)

            viewModel.postEvent(TaskListEvent.Load)

            //Verify
            coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.Initial)) }
            coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.Loading)) }
            coVerify { repository.getAllTasks() }
            coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.EmptyState)) }
            verifyAll()
        }

    @Test
    fun loadTaskListTestSuccess() = coroutinesRule.runBlockingTest {
        //Given
        val taskList = listOf(TaskEntity1, TaskEntity2, TaskEntity3)
        val list = ResultWrapper.Success(taskList)

        //When
        coEvery { repository.getAllTasks() } returns flowOf(list)

        viewModel.postEvent(TaskListEvent.Load)

        //Verify
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.Initial)) }
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.Loading)) }
        coVerify { repository.getAllTasks() }
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
        val error = ErrorResponse(message = "my exception")

        //When
        coEvery { repository.getAllTasks() } returns flowOf(ResultWrapper.GenericError(null, error))

        viewModel.postEvent(TaskListEvent.Load)

        //Verify
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.Initial)) }
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.Loading)) }
        coVerify { repository.getAllTasks() }
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.Error(error.message))) }
        verifyAll()
    }

    @Test
    fun onDeleteTaskElementsPressedSuccessTest() = coroutinesRule.runBlockingTest {
        //Given
        val taskList = fromModelToView(listOf(TaskEntity1, TaskEntity2, TaskEntity3))
        val toDeleteList = taskList.subList(0, 2)

        viewModel.setList(taskList)

        //When
        coEvery { repository.deleteTasks(any()) } returns ResultWrapper.Success(Unit)

        viewModel.postEvent(TaskListEvent.DeleteTaskElementsPressed(toDeleteList))

        //Verify
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.Initial)) }
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.Loading)) }
        coVerify { repository.deleteTasks(toDeleteList.map { it.id }) }
        coVerify {
            screenStateObserver.onChanged(
                Event(
                    TaskListScreenState.DataLoaded(
                        fromModelToView(listOf(TaskEntity3))
                    )
                )
            )
        }
        verifyAll()
    }

    @Test
    fun onDeleteTaskElementsPressedSuccessAndEmptyTest() = coroutinesRule.runBlockingTest {
        //Given
        val taskList = fromModelToView(listOf(TaskEntity1, TaskEntity2, TaskEntity3))
        viewModel.setList(taskList)

        //When
        coEvery { repository.deleteTasks(any()) } returns ResultWrapper.Success(Unit)

        viewModel.postEvent(TaskListEvent.DeleteTaskElementsPressed(taskList))

        //Verify
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.Initial)) }
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.Loading)) }
        coVerify { repository.deleteTasks(taskList.map { it.id }) }
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.EmptyState)) }
        verifyAll()
    }

    @Test
    fun onDeleteTaskElementsPressedSuccessAndErrorTest() = coroutinesRule.runBlockingTest {
        //Given
        val error = ErrorResponse(message = "my exception")
        val taskList = fromModelToView(listOf(TaskEntity1, TaskEntity2, TaskEntity3))
        viewModel.setList(taskList)

        //When
        coEvery { repository.deleteTasks(any()) } returns ResultWrapper.GenericError(null, error)

        viewModel.postEvent(TaskListEvent.DeleteTaskElementsPressed(taskList))

        //Verify
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.Initial)) }
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.Loading)) }
        coVerify { repository.deleteTasks(taskList.map { it.id }) }
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.Error(error.message))) }
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
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.NavigateToCreateTask)) }
        verifyAll()
    }

    @Test
    fun onEditTaskListFinishedTest() = coroutinesRule.runBlockingTest {
        val list = fromModelToView(listOf(TaskEntity1, TaskEntity2))

        //When
        viewModel.setList(list)
        viewModel.postEvent(TaskListEvent.EditTaskListFinished)

        //Verify
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.Initial)) }
        coVerify { screenStateObserver.onChanged(Event(TaskListScreenState.DataLoaded(list))) }
        verifyAll()
    }

    companion object {
        private val TaskEntity1 = TaskEntity(
            id = 1,
            name = "Task1",
            creationDate = Date(),
            donePomodoros = 0,
            estimatedPomodoros = 1,
            shortBreaks = 0,
            longBreaks = 0,
            completed = true
        )
        private val TaskEntity2 = TaskEntity(
            id = 2,
            name = "Task2",
            creationDate = Date(),
            donePomodoros = 0,
            estimatedPomodoros = 1,
            shortBreaks = 0,
            longBreaks = 0,
            completed = true
        )
        private val TaskEntity3 = TaskEntity(
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
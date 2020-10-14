package com.pomodorotime.domain.task.usecases

import com.pomodorotime.domain.CoroutinesRule
import com.pomodorotime.domain.IErrorHandler
import com.pomodorotime.domain.models.ErrorEntity
import com.pomodorotime.domain.models.ResultWrapper
import com.pomodorotime.domain.models.Task
import com.pomodorotime.domain.task.ITaskRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import java.util.*

class GetAllTaskUseCaseTest {

    @get:Rule
    val coroutinesRule = CoroutinesRule()

    @MockK
    lateinit var repository: ITaskRepository

    @MockK
    lateinit var errorHandler: IErrorHandler

    lateinit var useCase: GetAllTaskUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        useCase = GetAllTaskUseCase(
            repository,
            coroutinesRule.testCoroutineDispatcher,
            errorHandler
        )
    }

    private fun verifyAll() {
        confirmVerified(repository)
        confirmVerified(errorHandler)
    }

    @Test
    fun getAllTaskOkTest() = coroutinesRule.runBlockingTest {
        //Given
        val list = arrayListOf(Task1, Task2, Task3, Task4)

        //When
        coEvery { repository.getAllTasks() } returns flowOf(list)

        val resultWrapperFlow: Flow<ResultWrapper<List<Task>>> = useCase.invoke(Unit)

        //Verify
        val resultWrapper = resultWrapperFlow.toList().first()
        coVerify { repository.getAllTasks() }
        assert(resultWrapper is ResultWrapper.Success)
        assertEquals((resultWrapper as ResultWrapper.Success).value, list)
        verifyAll()
    }

    @Ignore
    fun getAllTaskErrorTest() = coroutinesRule.runBlockingTest {
        //Given
        val error = Exception("My Error")
        val list = arrayListOf(Task1, Task2, Task3, Task4)

        //When
        coEvery { repository.getAllTasks() } throws error
        coEvery { errorHandler.getError(error) } returns ErrorEntity.GenericError(
            -1,
            error.message ?: ""
        )
        val resultWrapperFlow: Flow<ResultWrapper<List<Task>>> = useCase.invoke(Unit)

        //Verify
        val resultWrapper = resultWrapperFlow.toList().first()

        coVerify { repository.getAllTasks() }
        coVerify { errorHandler.getError(error) }
        assert(resultWrapper is ResultWrapper.Error)
        assert((resultWrapper as ResultWrapper.Error).error is ErrorEntity.GenericError)

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

        private val Task4 = Task(
            id = 4,
            name = "Task4",
            creationDate = Date(),
            donePomodoros = 0,
            estimatedPomodoros = 1,
            shortBreaks = 0,
            longBreaks = 0,
            completed = true
        )
    }
}
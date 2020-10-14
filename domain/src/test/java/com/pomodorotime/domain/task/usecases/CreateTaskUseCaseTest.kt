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
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

class CreateTaskUseCaseTest {

    @get:Rule
    val coroutinesRule = CoroutinesRule()

    @MockK
    lateinit var repository: ITaskRepository

    @MockK
    lateinit var errorHandler: IErrorHandler

    lateinit var useCase: CreateTaskUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        useCase = CreateTaskUseCase(
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
    fun createTaskOkTest() = coroutinesRule.runBlockingTest {
        //Given
        val result = 1L
        val date = Date()
        val param = CreateTaskUseCase.CreateTaskParams("Name", date, 0, 1, false)

        val expected = Task(
            name = "Name",
            estimatedPomodoros = 1,
            donePomodoros = 0,
            shortBreaks = 4,
            longBreaks = 0,
            creationDate = date,
            completed = false
        )

        //When
        coEvery { repository.insetTask(any()) } returns result

        val resultWrapper = useCase.invoke(param)

        //Verify
        coVerify { repository.insetTask(expected) }
        assert(resultWrapper is ResultWrapper.Success)
        assertEquals((resultWrapper as ResultWrapper.Success).value, result)
        verifyAll()
    }

    @Test
    fun createBigTaskOkTest() = coroutinesRule.runBlockingTest {
        //Given
        val result = 1L
        val date = Date()
        val param = CreateTaskUseCase.CreateTaskParams("Name", date, 0, 10, false)

        val expected = Task(
            name = "Name",
            estimatedPomodoros = 10,
            donePomodoros = 0,
            shortBreaks = 40,
            longBreaks = 2,
            creationDate = date,
            completed = false
        )

        //When
        coEvery { repository.insetTask(any()) } returns result

        val resultWrapper = useCase.invoke(param)

        //Verify
        coVerify { repository.insetTask(expected) }
        assert(resultWrapper is ResultWrapper.Success)
        assertEquals((resultWrapper as ResultWrapper.Success).value, result)
        verifyAll()
    }

    @Test
    fun createTaskErrorTest() = coroutinesRule.runBlockingTest {
        //Given
        val error = Exception("My Error")
        val date = Date()
        val param = CreateTaskUseCase.CreateTaskParams("Name", date, 0, 1, false)

        val expected = Task(
            name = "Name",
            estimatedPomodoros = 1,
            donePomodoros = 0,
            shortBreaks = 4,
            longBreaks = 0,
            creationDate = date,
            completed = false
        )

        //When
        coEvery { repository.insetTask(any()) } throws error
        coEvery { errorHandler.getError(any()) } returns ErrorEntity.GenericError(
            -1,
            error.message!!
        )

        val resultWrapper = useCase.invoke(param)

        //Verify
        assert(resultWrapper is ResultWrapper.Error)
        assert((resultWrapper as ResultWrapper.Error).error is ErrorEntity.GenericError)
        coVerify { repository.insetTask(expected) }
        coVerify { errorHandler.getError(error) }
        verifyAll()
    }
}
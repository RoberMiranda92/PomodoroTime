package com.pomodorotime.domain.timer

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
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date

class GetTaskByIdUseCaseTest {

    @get:Rule
    val coroutinesRule = CoroutinesRule()

    @MockK
    lateinit var repository: ITaskRepository

    @MockK
    lateinit var errorHandler: IErrorHandler

    lateinit var useCase: GetTaskByIdUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        useCase = GetTaskByIdUseCase(
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
    fun getTaskByIdOkTest() = coroutinesRule.runBlockingTest {
        //Given
        val id = 1L
        val params = GetTaskByIdUseCase.GetTaskByIdParams(id)

        //When
        coEvery { repository.getTaskById(any()) } returns Task1

        val resultWrapper = useCase.invoke(params)

        //Verify
        assert(resultWrapper is ResultWrapper.Success)
        assertEquals((resultWrapper as ResultWrapper.Success).value, Task1)
        coVerify { repository.getTaskById(id) }
        verifyAll()
    }

    @Test
    fun getTaskByIDErrorTest() = coroutinesRule.runBlockingTest {
        val error = Exception("My Error")
        val id = 1L
        val params = GetTaskByIdUseCase.GetTaskByIdParams(id)

        //When
        coEvery { repository.getTaskById(any()) } throws error
        coEvery { errorHandler.getError(any()) } returns ErrorEntity.GenericError(
            -1,
            error.message!!
        )

        val resultWrapper = useCase.invoke(params)

        //Verify
        assert(resultWrapper is ResultWrapper.Error)
        assert((resultWrapper as ResultWrapper.Error).error is ErrorEntity.GenericError)
        coVerify { repository.getTaskById(id) }
        coVerify { errorHandler.getError(error) }
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
    }
}
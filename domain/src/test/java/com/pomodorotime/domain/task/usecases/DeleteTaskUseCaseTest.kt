package com.pomodorotime.domain.task.usecases

import com.pomodorotime.domain.CoroutinesRule
import com.pomodorotime.domain.IErrorHandler
import com.pomodorotime.domain.models.ErrorEntity
import com.pomodorotime.domain.models.ResultWrapper
import com.pomodorotime.domain.task.ITaskRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DeleteTaskUseCaseTest {

    @get:Rule
    val coroutinesRule = CoroutinesRule()

    @MockK
    lateinit var repository: ITaskRepository

    @MockK
    lateinit var errorHandler: IErrorHandler

    lateinit var useCase: DeleteTaskUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        useCase = DeleteTaskUseCase(
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
    fun deleteTaskOkTest() = coroutinesRule.runBlockingTest {
        //Given
        val list = arrayListOf<Long>(1, 2, 3, 4, 5)
        val params = DeleteTaskUseCase.DeleteTaskUseCaseParams(list)

        //When
        coEvery { repository.deleteTasks(any()) } returns Unit

        val resultWrapper = useCase.invoke(params)

        //Verify
        assert(resultWrapper is ResultWrapper.Success)
        coVerify { repository.deleteTasks(list) }
        verifyAll()
    }

    @Test
    fun deleteTaskError() = coroutinesRule.runBlockingTest {
        val error = Exception("My Error")
        val list = arrayListOf<Long>(1, 2, 3, 4, 5)
        val params = DeleteTaskUseCase.DeleteTaskUseCaseParams(list)

        //When
        coEvery { repository.deleteTasks(any()) } throws error
        coEvery { errorHandler.getError(any()) } returns ErrorEntity.GenericError(
            -1,
            error.message!!
        )

        val resultWrapper = useCase.invoke(params)

        //Verify
        assert(resultWrapper is ResultWrapper.Error)
        assert((resultWrapper as ResultWrapper.Error).error is ErrorEntity.GenericError)
        coVerify { repository.deleteTasks(list) }
        coVerify { errorHandler.getError(error) }
        verifyAll()
    }
}
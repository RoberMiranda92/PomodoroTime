package com.pomodorotime.domain.login.usecases

import com.pomodorotime.domain.CoroutinesRule
import com.pomodorotime.domain.IErrorHandler
import com.pomodorotime.domain.login.ILoginRepository
import com.pomodorotime.domain.models.ErrorEntity
import com.pomodorotime.domain.models.ResultWrapper
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SaveTokenUseCaseTest {

    @get:Rule
    val coroutinesRule = CoroutinesRule()

    @MockK
    lateinit var repository: ILoginRepository

    @MockK
    lateinit var errorHandler: IErrorHandler

    lateinit var useCase: SaveUserTokenUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        useCase = SaveUserTokenUseCase(
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
    fun saveUserTokenOkTest() = coroutinesRule.runBlockingTest {
        //Given
        val token = "token"
        val params = SaveUserTokenUseCase.SaveUserTokenParams(token)

        //When
        coEvery { repository.saveUserToken(any()) } returns Unit

        val resultWrapper = useCase.invoke(params)

        //Verify
        assert(resultWrapper is ResultWrapper.Success)
        coVerify { repository.saveUserToken(token) }
        verifyAll()
    }

    @Test
    fun saveUserTokenErrorTest() = coroutinesRule.runBlockingTest {
        val error = Exception("My Error")
        //Given
        val token = "token"
        val params = SaveUserTokenUseCase.SaveUserTokenParams(token)

        //When
        coEvery { repository.saveUserToken(any()) } throws error
        coEvery { errorHandler.getError(any()) } returns ErrorEntity.GenericError(
            -1,
            error.message!!
        )

        val resultWrapper = useCase.invoke(params)

        //Verify
        assert(resultWrapper is ResultWrapper.Error)
        assert((resultWrapper as ResultWrapper.Error).error is ErrorEntity.GenericError)
        coVerify { repository.saveUserToken(token) }
        coVerify { errorHandler.getError(error) }
        verifyAll()
    }
}
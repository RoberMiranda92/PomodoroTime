package com.pomodorotime.domain.login.usecases

import com.pomodorotime.domain.CoroutinesRule
import com.pomodorotime.domain.IErrorHandler
import com.pomodorotime.domain.login.ILoginRepository
import com.pomodorotime.domain.models.ErrorEntity
import com.pomodorotime.domain.models.ResultWrapper
import com.pomodorotime.domain.models.User
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SigInUseCaseTest {

    @get:Rule
    val coroutinesRule = CoroutinesRule()

    @MockK
    lateinit var repository: ILoginRepository

    @MockK
    lateinit var errorHandler: IErrorHandler

    lateinit var useCase: SigInUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        useCase = SigInUseCase(
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
    fun sigInOkTest() = coroutinesRule.runBlockingTest {
        //Given
        val email = "email@email.com"
        val password = "password"
        val params = SigInUseCase.SignInParams(email, password)
        val user = User(email, "id", "token")

        //When
        coEvery { repository.signIn(any(), any()) } returns user

        val resultWrapper = useCase.invoke(params)

        //Verify
        assert(resultWrapper is ResultWrapper.Success)
        assertEquals((resultWrapper as ResultWrapper.Success).value, user)
        coVerify { repository.signIn(email, password) }
        verifyAll()
    }

    @Test
    fun sigInErrorTest() = coroutinesRule.runBlockingTest {
        val error = Exception("My Error")
        //Given
        val email = "email@email.com"
        val password = "password"
        val params = SigInUseCase.SignInParams(email, password)

        //When
        coEvery { repository.signIn(any(), any()) } throws error
        coEvery { errorHandler.getError(any()) } returns ErrorEntity.GenericError(
            -1,
            error.message!!
        )

        val resultWrapper = useCase.invoke(params)

        //Verify
        assert(resultWrapper is ResultWrapper.Error)
        assert((resultWrapper as ResultWrapper.Error).error is ErrorEntity.GenericError)
        coVerify { repository.signIn(email, password) }
        coVerify { errorHandler.getError(error) }
        verifyAll()
    }
}
package com.pomodorotime.data.login.repository

import com.pomodorotime.data.CoroutinesRule
import com.pomodorotime.data.login.api.models.ApiUser
import com.pomodorotime.data.login.datasource.ILoginRemoteDataSource
import com.pomodorotime.data.user.IUserLocalDataSource
import com.pomodorotime.domain.login.ILoginRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginRepositoryTest {

    @get:Rule
    val coroutinesRule = CoroutinesRule()

    @MockK
    lateinit var loginRemoteDataSource: ILoginRemoteDataSource

    @MockK
    lateinit var userDataSource: IUserLocalDataSource

    lateinit var repository: ILoginRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        repository = LoginRepository(loginRemoteDataSource, userDataSource)
    }

    @Test
    fun `verify singUp and save user is ok`() = coroutinesRule.runBlockingTest {
        //Given
        val email = "email@email.com"
        val password = "password"
        val apiUser = ApiUser(email = email, id = "id", token = "token")
        val captureData = slot<ApiUser>()

        //When
        coEvery { loginRemoteDataSource.signUp(any(), any()) } returns apiUser
        coEvery { userDataSource.setUser(capture(captureData)) } returns Unit

        repository.signUp(email, password)

        //Verify
        assertEquals(apiUser, captureData.captured)
        coVerify { loginRemoteDataSource.signUp(email, password) }
        coVerify { userDataSource.setUser(captureData.captured) }
        confirmVerified(loginRemoteDataSource)
        confirmVerified(userDataSource)
    }

    @Test
    fun `verify singIn and save user is ok`() = coroutinesRule.runBlockingTest {
        //Given
        val email = "email@email.com"
        val password = "password"
        val apiUser = ApiUser(email = email, id = "id", token = "token")
        val captureData = slot<ApiUser>()

        //When
        coEvery { loginRemoteDataSource.signIn(any(), any()) } returns apiUser
        coEvery { userDataSource.setUser(capture(captureData)) } returns Unit

        repository.signIn(email, password)

        //Verify
        assertEquals(apiUser, captureData.captured)
        coVerify { loginRemoteDataSource.signIn(email, password) }
        coVerify { userDataSource.setUser(captureData.captured) }
        confirmVerified(loginRemoteDataSource)
        confirmVerified(userDataSource)
    }
}
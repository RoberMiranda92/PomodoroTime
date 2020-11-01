package com.pomodorotime.data.login.repository

import com.pomodorotime.data.CoroutinesRule
import com.pomodorotime.data.login.api.models.ApiUser
import com.pomodorotime.data.login.datasource.ILoginRemoteDataSource
import com.pomodorotime.data.user.IUserLocalDataSource
import com.pomodorotime.domain.login.ILoginRepository
import io.mockk.*
import io.mockk.impl.annotations.MockK
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
    fun `verify singUp is ok`() = coroutinesRule.runBlockingTest {
        //Given
        val email = "email@email.com"
        val password = "password"
        val apiUser = ApiUser(email = email, id = "id", token = "token")

        //When
        coEvery { loginRemoteDataSource.signUp(any(), any()) } returns apiUser

        repository.signUp(email, password)

        //Verify
        coVerify { loginRemoteDataSource.signUp(email, password) }
        confirmVerified(loginRemoteDataSource)
        confirmVerified(userDataSource)
    }

    @Test
    fun `verify singIn is ok`() = coroutinesRule.runBlockingTest {
        //Given
        val email = "email@email.com"
        val password = "password"
        val apiUser = ApiUser(email = email, id = "id", token = "token")

        //When
        coEvery { loginRemoteDataSource.signIn(any(), any()) } returns apiUser

        repository.signIn(email, password)

        //Verify
        coVerify { loginRemoteDataSource.signIn(email, password) }
        confirmVerified(loginRemoteDataSource)
        confirmVerified(userDataSource)
    }

    @Test
    fun `save user token is ok`() = coroutinesRule.runBlockingTest {
        //Given
        val token = "token"

        //When
        coEvery { userDataSource.saveToken(any()) } returns Unit

        repository.saveUserToken(token)

        //Verify
        coVerify { userDataSource.saveToken(token) }
        confirmVerified(loginRemoteDataSource)
        confirmVerified(userDataSource)
    }
}
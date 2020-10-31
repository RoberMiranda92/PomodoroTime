package com.pomodorotime.data.login.datasource

import com.pomodorotime.data.CoroutinesRule
import com.pomodorotime.data.login.api.ILoginApi
import com.pomodorotime.data.login.api.models.ApiUser
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginRemoteDataSourceImplTest {

    @get:Rule
    val coroutinesRule = CoroutinesRule()

    @MockK
    lateinit var api: ILoginApi

    lateinit var dataSource: ILoginRemoteDataSource

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        dataSource = LoginRemoteDataSourceImpl(api)
    }

    @Test
    fun `signIn OK`() = coroutinesRule.runBlockingTest {
        //Given
        val user = "email@email.com"
        val password = "123456"
        val id = "id"
        val token = "token"

        coEvery { api.signIn(any(), any()) } returns ApiUser(user, id, token)

        //When
        val result = dataSource.signIn(user, password)

        //Verify

        Assert.assertEquals(result.email, user)
        Assert.assertEquals(result.id, id)
        Assert.assertEquals(result.token, token)
        coVerify { api.signIn(user, password) }
        confirmVerified(api)
    }

    @Test
    fun `signUp OK`() = coroutinesRule.runBlockingTest {
        //Given
        val user = "email@email.com"
        val password = "123456"
        val id = "id"
        val token = "token"

        coEvery { api.signUp(any(), any()) } returns ApiUser(user, id, token)

        //When
        val result = dataSource.signUp(user, password)

        //Verify

        Assert.assertEquals(result.email, user)
        Assert.assertEquals(result.id, id)
        Assert.assertEquals(result.token, token)
        coVerify { api.signUp(user, password) }
        confirmVerified(api)
    }
}
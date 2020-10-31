package com.pomodorotime.data.user

import com.pomodorotime.data.login.api.models.ApiUser
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UserLocalDataSourceImpTest {

    lateinit var localDataSource: IUserLocalDataSource

    @Before
    fun setUp() {
        localDataSource = UserLocalDataSourceImp
    }

    @After
    fun tearDown() {
        localDataSource.clear()
    }

    @Test
    fun `on null ApiUser`() {
        assertEquals("", localDataSource.getUserId())
        assertEquals("", localDataSource.getEmail())
    }

    @Test
    fun `on non null ApiUser`() {
        localDataSource.setUser(ApiUser)

        assertEquals(ApiUser.id, localDataSource.getUserId())
        assertEquals(ApiUser.email, localDataSource.getEmail())
    }

    companion object {
        private val ApiUser = ApiUser(
            "email@example.com",
            "id", "token"
        )
    }
}
package com.pomodorotime.data.user

import com.pomodorotime.data.login.api.models.ApiUser
import com.pomodorotime.data.preferences.ISharedPreferences
import io.mockk.MockKAnnotations
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock

class UserLocalDataSourceImpTest {

    @Mock
    lateinit var sharedPreferences: ISharedPreferences

    lateinit var localDataSource: IUserLocalDataSource

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        localDataSource = UserLocalDataSourceImp(sharedPreferences)
    }

    @After
    fun tearDown() {
        localDataSource.clear()
    }

    @Test
    fun `on null ApiUser`() {
        assertEquals("", localDataSource.getToken())
        assertEquals("", localDataSource.getEmail())
    }

    @Test
    fun `on non null ApiUser`() {
        localDataSource.setUser(ApiUser)

        assertEquals(ApiUser.id, localDataSource.getToken())
        assertEquals(ApiUser.email, localDataSource.getEmail())
    }

    companion object {
        private val ApiUser = ApiUser(
            "email@example.com",
            "id", "token"
        )
    }
}
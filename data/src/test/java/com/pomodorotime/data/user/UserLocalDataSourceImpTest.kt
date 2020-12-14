package com.pomodorotime.data.user

import com.pomodorotime.data.CoroutinesRule
import com.pomodorotime.data.login.api.models.ApiUser
import com.pomodorotime.data.preferences.ISharedPreferences
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock

class UserLocalDataSourceImpTest {

    @get:Rule
    val coroutinesRule = CoroutinesRule()

    @RelaxedMockK
    lateinit var sharedPreferences: ISharedPreferences

    lateinit var localDataSource: IUserLocalDataSource

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        localDataSource = UserLocalDataSourceImp(sharedPreferences)
    }

    @Test
    fun setTokenTest() = coroutinesRule.runBlockingTest {
        //Given
        val token = "token"

        //When
        coEvery { sharedPreferences.putString(any(), any()) } returns Unit

        localDataSource.saveToken(token)

        //Verify
        coVerify { sharedPreferences.putString(UserLocalDataSourceImp.USER_TOKEN, token) }
    }

    @Test
    fun getTokenTest() = coroutinesRule.runBlockingTest {
        //Given
        val token = "token"

        //When
        coEvery { sharedPreferences.getString(any()) } returns token

        val result = localDataSource.getToken()

        //Verify
        Assert.assertEquals(result, token)
        coVerify { sharedPreferences.getString(UserLocalDataSourceImp.USER_TOKEN) }
    }

    @Test
    fun removeTokenTest() = coroutinesRule.runBlockingTest {
        //Given
        coEvery { sharedPreferences.removeKey(any()) } returns Unit

        //When
        localDataSource.clearToken()

        //Verify
        coVerify { sharedPreferences.removeKey(UserLocalDataSourceImp.USER_TOKEN) }
    }

    companion object {
        private val ApiUser = ApiUser(
            "email@example.com",
            "id", "token"
        )
    }
}
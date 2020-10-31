package com.pomodorotime.data.login

import com.pomodorotime.data.login.api.models.ApiUser
import com.pomodorotime.domain.models.User
import org.junit.Assert.assertEquals
import org.junit.Test

class MapperTest {

    @Test
    fun `from User to ApiUser`() {
        val domainUser = User
        val apiUser = domainUser.toApiModel()

        apiUser.run {
            assertEquals(id, domainUser.id)
            assertEquals(token, domainUser.token)
            assertEquals(email, domainUser.email)
        }
    }

    @Test
    fun `from ApiUser to User`() {
        val apiUser = ApiUser
        val domainUser = apiUser.toDomainModel()

        domainUser.run {
            assertEquals(id, apiUser.id)
            assertEquals(token, apiUser.token)
            assertEquals(email, apiUser.email)
        }
    }

    companion object {
        private val User = User(
            "email@example.com",
            "id", "token"
        )

        private val ApiUser = ApiUser(
            "email@example.com",
            "id", "token"
        )
    }
}
package com.pomodorotime.data

import com.google.firebase.auth.FirebaseAuthException
import com.pomodorotime.domain.IErrorHandler
import com.pomodorotime.domain.models.ErrorEntity
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException

class ErrorHandlerImplTest {

    @MockK
    lateinit var mockHttpException: HttpException

    lateinit var errorHandler: ErrorHandlerImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        errorHandler = ErrorHandlerImpl()
    }

    @Test
    fun `manage IOException ok`() {
        //Given
        val exception = IOException()

        //when
        val result = errorHandler.getError(exception)

        //Verify
        Assert.assertThat(result, `is`(ErrorEntity.NetworkError))
    }

    @Test
    fun `manage HttpException ok`() {
        val code = 500
        val message = "Message"

        //Given
        val exception = mockHttpException

        every { mockHttpException.code() } returns code
        every { mockHttpException.message } returns message

        //when
        val result = errorHandler.getError(exception)

        //Verify
        Assert.assertThat(result, `is`(ErrorEntity.GenericError(code, message)))
    }

    @Test
    fun `manage Unexpected Exception ok `() {
        //Given
        val message = "Message"
        val exception = Exception("Message")

        //when
        val result = errorHandler.getError(exception)

        //Verify
        Assert.assertThat(result, `is`(ErrorEntity.GenericError(null, message = message)))
    }

    @Test
    fun `manage FirebaseAuthException ERROR_OPERATION_NOT_ALLOWED ok`() {
        //Given
        val message = "Message"
        val exception = FirebaseAuthException("ERROR_OPERATION_NOT_ALLOWED", message)

        //when
        val result = errorHandler.getError(exception)

        //Verify
        Assert.assertThat(result, `is`(ErrorEntity.GenericError(message = message)))
    }

    @Test
    fun `manage FirebaseAuthException ERROR_WEAK_PASSWORD ok`() {
        //Given
        val message = "Message"
        val exception = FirebaseAuthException("ERROR_WEAK_PASSWORD", message)

        //when
        val result = errorHandler.getError(exception)

        //Verify
        Assert.assertThat(result, `is`(ErrorEntity.UserPasswordError(message = message)))
    }

    @Test
    fun `manage FirebaseAuthException ERROR_INVALID_EMAIL ok`() {
        //Given
        val message = "Message"
        val exception = FirebaseAuthException("ERROR_INVALID_EMAIL", message)

        //when
        val result = errorHandler.getError(exception)

        //Verify
        Assert.assertThat(result, `is`(ErrorEntity.UserEmailError(message = message)))
    }

    @Test
    fun `manage FirebaseAuthException ERROR_WRONG_PASSWORD ok`() {
        //Given
        val message = "Message"
        val exception = FirebaseAuthException("ERROR_WRONG_PASSWORD", message)

        //when
        val result = errorHandler.getError(exception)

        //Verify
        Assert.assertThat(result, `is`(ErrorEntity.UserPasswordError(message = message)))
    }

    @Test
    fun `manage FirebaseAuthException ERROR_EMAIL_ALREADY_IN_USE ok`() {
        //Given
        val message = "Message"
        val exception = FirebaseAuthException("ERROR_EMAIL_ALREADY_IN_USE", message)

        //when
        val result = errorHandler.getError(exception)

        //Verify
        Assert.assertThat(result, `is`(ErrorEntity.UserEmailError(message = message)))
    }

    @Test
    fun `manage FirebaseAuthException ERROR_INVALID_CREDENTIAL ok`() {
        //Given
        val message = "Message"
        val exception = FirebaseAuthException("ERROR_INVALID_CREDENTIAL", message)

        //when
        val result = errorHandler.getError(exception)

        //Verify
        Assert.assertThat(result, `is`(ErrorEntity.UserEmailError(message = message)))
    }

    @Test
    fun `manage FirebaseAuthException ERROR_USER_NOT_FOUND ok`() {
        //Given
        val message = "Message"
        val exception = FirebaseAuthException("ERROR_USER_NOT_FOUND", message)

        //when
        val result = errorHandler.getError(exception)

        //Verify
        Assert.assertThat(result, `is`(ErrorEntity.UserEmailError(message = message)))
    }

    @Test
    fun `manage FirebaseAuthException ERROR_USER_DISABLED ok`() {
        //Given
        val message = "Message"
        val exception = FirebaseAuthException("ERROR_USER_DISABLED", message)

        //when
        val result = errorHandler.getError(exception)

        //Verify
        Assert.assertThat(result, `is`(ErrorEntity.UserEmailError(message = message)))
    }

    @Test
    fun `manage FirebaseAuthException ERROR_TOO_MANY_REQUESTS ok`() {
        //Given
        val message = "Message"
        val exception = FirebaseAuthException("ERROR_TOO_MANY_REQUESTS", message)

        //when
        val result = errorHandler.getError(exception)

        //Verify
        Assert.assertThat(result, `is`(ErrorEntity.GenericError(message = message)))
    }

    @Test
    fun `manage FirebaseAuthException INVALID `() {
        //Given
        val message = "Message"
        val exception = FirebaseAuthException("INVALID", message)

        //when
        val result = errorHandler.getError(exception)

        //Verify
        Assert.assertThat(result, `is`(ErrorEntity.GenericError(message = message)))
    }
}
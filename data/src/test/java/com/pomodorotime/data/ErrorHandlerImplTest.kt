package com.pomodorotime.data

import com.google.firebase.auth.FirebaseAuthException
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

    @MockK
    lateinit var mockFirebaseAuthException: FirebaseAuthException

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
    fun `manage HttpException null message`() {
        val code = 500

        //Given
        val exception = mockHttpException

        every { mockHttpException.code() } returns code
        every { mockHttpException.message } returns null

        //when
        val result = errorHandler.getError(exception)

        //Verify
        Assert.assertThat(result, `is`(ErrorEntity.GenericError(code, "")))
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
        val code = "ERROR_OPERATION_NOT_ALLOWED"
        val exception = mockFirebaseAuthException

        //when
        every { mockFirebaseAuthException.errorCode } returns code
        every { mockFirebaseAuthException.message } returns message
        val result = errorHandler.getError(exception)

        //Verify
        Assert.assertThat(result, `is`(ErrorEntity.GenericError(message = message)))
    }

    @Test
    fun `manage FirebaseAuthException ERROR_OPERATION_NOT_ALLOWED null message`() {
        val message = null
        val code = "ERROR_OPERATION_NOT_ALLOWED"
        val exception = mockFirebaseAuthException

        //when
        every { mockFirebaseAuthException.errorCode } returns code
        every { mockFirebaseAuthException.message } returns message
        val result = errorHandler.getError(exception)

        //Verify
        Assert.assertThat(result, `is`(ErrorEntity.GenericError(message = "")))
    }

    @Test
    fun `manage FirebaseAuthException ERROR_WEAK_PASSWORD ok`() {
        //Given
        val message = "Message"
        val code = "ERROR_WEAK_PASSWORD"
        val exception = mockFirebaseAuthException

        //when
        every { mockFirebaseAuthException.errorCode } returns code
        every { mockFirebaseAuthException.message } returns message
        val result = errorHandler.getError(exception)

        //Verify
        Assert.assertThat(result, `is`(ErrorEntity.UserPasswordError(message = message)))
    }

    @Test
    fun `manage FirebaseAuthException ERROR_WEAK_PASSWORD null message`() {
        //Given
        val message = null
        val code = "ERROR_WEAK_PASSWORD"
        val exception = mockFirebaseAuthException

        //when
        every { mockFirebaseAuthException.errorCode } returns code
        every { mockFirebaseAuthException.message } returns message
        val result = errorHandler.getError(exception)

        //Verify
        Assert.assertThat(result, `is`(ErrorEntity.UserPasswordError(message = "")))
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
        val code = "ERROR_WRONG_PASSWORD"
        val exception = mockFirebaseAuthException

        //when
        every { mockFirebaseAuthException.errorCode } returns code
        every { mockFirebaseAuthException.message } returns message
        val result = errorHandler.getError(exception)

        //Verify
        Assert.assertThat(result, `is`(ErrorEntity.UserPasswordError(message = message)))
    }

    @Test
    fun `manage FirebaseAuthException ERROR_WRONG_PASSWORD null message`() {
        //Given
        val message = null
        val code = "ERROR_WRONG_PASSWORD"
        val exception = mockFirebaseAuthException

        //when
        every { mockFirebaseAuthException.errorCode } returns code
        every { mockFirebaseAuthException.message } returns message
        val result = errorHandler.getError(exception)

        //Verify
        Assert.assertThat(result, `is`(ErrorEntity.UserPasswordError(message = "")))
    }

    @Test
    fun `manage FirebaseAuthException ERROR_EMAIL_ALREADY_IN_USE ok`() {
        //Given
        val message = "Message"
        val code = "ERROR_EMAIL_ALREADY_IN_USE"
        val exception = mockFirebaseAuthException

        //when
        every { mockFirebaseAuthException.errorCode } returns code
        every { mockFirebaseAuthException.message } returns message
        val result = errorHandler.getError(exception)

        //Verify
        Assert.assertThat(result, `is`(ErrorEntity.UserEmailError(message = message)))
    }

    @Test
    fun `manage FirebaseAuthException ERROR_EMAIL_ALREADY_IN_USE null message`() {
        //Given
        val message = null
        val code = "ERROR_EMAIL_ALREADY_IN_USE"
        val exception = mockFirebaseAuthException

        //when
        every { mockFirebaseAuthException.errorCode } returns code
        every { mockFirebaseAuthException.message } returns message
        val result = errorHandler.getError(exception)

        //Verify
        Assert.assertThat(result, `is`(ErrorEntity.UserEmailError(message = "")))
    }

    @Test
    fun `manage FirebaseAuthException ERROR_INVALID_CREDENTIAL ok`() {
        //Given
        val message = "Message"
        val code = "ERROR_INVALID_CREDENTIAL"
        val exception = mockFirebaseAuthException

        //when
        every { mockFirebaseAuthException.errorCode } returns code
        every { mockFirebaseAuthException.message } returns message
        val result = errorHandler.getError(exception)

        //Verify
        Assert.assertThat(result, `is`(ErrorEntity.UserEmailError(message = message)))
    }

    @Test
    fun `manage FirebaseAuthException ERROR_INVALID_CREDENTIAL null message`() {
        //Given
        val message = null
        val code = "ERROR_INVALID_CREDENTIAL"
        val exception = mockFirebaseAuthException

        //when
        every { mockFirebaseAuthException.errorCode } returns code
        every { mockFirebaseAuthException.message } returns message
        val result = errorHandler.getError(exception)

        //Verify
        Assert.assertThat(result, `is`(ErrorEntity.UserEmailError(message = "")))
    }

    @Test
    fun `manage FirebaseAuthException ERROR_USER_NOT_FOUND ok`() {
        //Given
        val message = "Message"
        val code = "ERROR_USER_NOT_FOUND"
        val exception = mockFirebaseAuthException

        //when
        every { mockFirebaseAuthException.errorCode } returns code
        every { mockFirebaseAuthException.message } returns message
        val result = errorHandler.getError(exception)

        //Verify
        Assert.assertThat(result, `is`(ErrorEntity.UserEmailError(message = message)))
    }

    @Test
    fun `manage FirebaseAuthException ERROR_USER_NOT_FOUND null message`() {
        //Given
        val message = null
        val code = "ERROR_USER_NOT_FOUND"
        val exception = mockFirebaseAuthException

        //when
        every { mockFirebaseAuthException.errorCode } returns code
        every { mockFirebaseAuthException.message } returns message
        val result = errorHandler.getError(exception)

        //Verify
        Assert.assertThat(result, `is`(ErrorEntity.UserEmailError(message = "")))
    }

    @Test
    fun `manage FirebaseAuthException ERROR_USER_DISABLED ok`() {
        //Given
        val message = "Message"
        val code = "ERROR_USER_DISABLED"
        val exception = mockFirebaseAuthException

        //when
        every { mockFirebaseAuthException.errorCode } returns code
        every { mockFirebaseAuthException.message } returns message
        val result = errorHandler.getError(exception)

        //Verify
        Assert.assertThat(result, `is`(ErrorEntity.UserEmailError(message = message)))
    }

    @Test
    fun `manage FirebaseAuthException ERROR_USER_DISABLED null message`() {
        //Given
        val message = null
        val code = "ERROR_USER_DISABLED"
        val exception = mockFirebaseAuthException

        //when
        every { mockFirebaseAuthException.errorCode } returns code
        every { mockFirebaseAuthException.message } returns message
        val result = errorHandler.getError(exception)

        //Verify
        Assert.assertThat(result, `is`(ErrorEntity.UserEmailError(message = "")))
    }

    @Test
    fun `manage FirebaseAuthException ERROR_TOO_MANY_REQUESTS ok`() {
        //Given
        val message = "Message"
        val code = "ERROR_TOO_MANY_REQUESTS"
        val exception = mockFirebaseAuthException

        //when
        every { mockFirebaseAuthException.errorCode } returns code
        every { mockFirebaseAuthException.message } returns message
        val result = errorHandler.getError(exception)

        //Verify
        Assert.assertThat(result, `is`(ErrorEntity.GenericError(message = message)))
    }

    @Test
    fun `manage FirebaseAuthException ERROR_TOO_MANY_REQUESTS null message`() {
        //Given
        val message = null
        val code = "ERROR_TOO_MANY_REQUESTS"
        val exception = mockFirebaseAuthException

        //when
        every { mockFirebaseAuthException.errorCode } returns code
        every { mockFirebaseAuthException.message } returns message
        val result = errorHandler.getError(exception)

        //Verify
        Assert.assertThat(result, `is`(ErrorEntity.GenericError(message = "")))
    }

    @Test
    fun `manage FirebaseAuthException INVALID OK`() {
        //Given
        val message = "Message"
        val code = "INVALID"
        val exception = mockFirebaseAuthException

        //when
        every { mockFirebaseAuthException.errorCode } returns code
        every { mockFirebaseAuthException.message } returns message
        val result = errorHandler.getError(exception)

        //Verify
        Assert.assertThat(result, `is`(ErrorEntity.GenericError(message = message)))
    }

    @Test
    fun `manage FirebaseAuthException INVALID null message`() {
        //Given
        val message = null
        val code = "INVALID"
        val exception = mockFirebaseAuthException

        //when
        every { mockFirebaseAuthException.errorCode } returns code
        every { mockFirebaseAuthException.message } returns message
        val result = errorHandler.getError(exception)

        //Verify
        Assert.assertThat(result, `is`(ErrorEntity.GenericError(message = "")))
    }
}
package com.pomodorotime.data.login.api

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import com.pomodorotime.data.CoroutinesRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FirebaseLoginApiTest {

    @get:Rule
    val coroutinesRule = CoroutinesRule()

    @MockK
    lateinit var firebaseAuth: FirebaseAuth

    @MockK
    lateinit var authResult: AuthResult

    @MockK
    lateinit var taskAuthResult: Task<AuthResult>

    @MockK
    lateinit var tokenResult: GetTokenResult

    @MockK
    lateinit var taskTokenResult: Task<GetTokenResult>

    lateinit var api: FirebaseLoginApi

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        api = FirebaseLoginApi(firebaseAuth)
    }

    @Test
    fun `on sign in with email and password is success`() = coroutinesRule.runBlockingTest {
        //Given
        val email = "email@email.com"
        val password = "1234567"
        val uid = "uid"
        val token = "my_token"

        //When
        coEvery {
            firebaseAuth.signInWithEmailAndPassword(any(), any())
        } returns taskAuthResult

        coEvery {
            firebaseAuth.getAccessToken(any())
        } returns taskTokenResult

        coEvery { taskAuthResult.isComplete } returns true
        coEvery { taskAuthResult.exception } returns null
        coEvery { taskAuthResult.isCanceled } returns false
        coEvery { taskAuthResult.result } returns authResult

        coEvery { taskTokenResult.isComplete } returns true
        coEvery { taskTokenResult.exception } returns null
        coEvery { taskTokenResult.isCanceled } returns false
        coEvery { taskTokenResult.result } returns tokenResult

        coEvery { authResult.user?.email } returns email
        coEvery { authResult.user?.uid } returns uid
        coEvery { tokenResult.token } returns token

        val result = api.signIn(email, password)

        //Verify
        assert(result.email == email)
        assert(result.token == token)
        assert(result.id == uid)
        coVerify { firebaseAuth.signInWithEmailAndPassword(email, password) }
        coVerify { firebaseAuth.getAccessToken(true) }

        confirmVerified(firebaseAuth)
    }

    @Test
    fun `on sign in with email and password is success but data is null`() = coroutinesRule.runBlockingTest {
        //Given
        val email = "email@email.com"
        val password = "1234567"
        val empty = ""

        //When
        coEvery {
            firebaseAuth.signInWithEmailAndPassword(any(), any())
        } returns taskAuthResult

        coEvery {
            firebaseAuth.getAccessToken(any())
        } returns taskTokenResult

        coEvery { taskAuthResult.isComplete } returns true
        coEvery { taskAuthResult.exception } returns null
        coEvery { taskAuthResult.isCanceled } returns false
        coEvery { taskAuthResult.result } returns authResult

        coEvery { taskTokenResult.isComplete } returns true
        coEvery { taskTokenResult.exception } returns null
        coEvery { taskTokenResult.isCanceled } returns false
        coEvery { taskTokenResult.result } returns tokenResult

        coEvery { authResult.user?.email } returns null
        coEvery { authResult.user?.uid } returns null
        coEvery { tokenResult.token } returns null

        val result = api.signIn(email, password)

        //Verify
        assert(result.email == empty)
        assert(result.token == empty)
        assert(result.id == empty)
        coVerify { firebaseAuth.signInWithEmailAndPassword(email, password) }
        coVerify { firebaseAuth.getAccessToken(true) }

        confirmVerified(firebaseAuth)
    }

    @Test
    fun `on sign up with email and password is success`() = coroutinesRule.runBlockingTest {
        //Given
        val email = "email@email.com"
        val password = "1234567"
        val uid = "uid"
        val token = "my_token"

        //When
        coEvery {
            firebaseAuth.createUserWithEmailAndPassword(any(), any())
        } returns taskAuthResult

        coEvery {
            firebaseAuth.getAccessToken(any())
        } returns taskTokenResult

        coEvery { taskAuthResult.isComplete } returns true
        coEvery { taskAuthResult.exception } returns null
        coEvery { taskAuthResult.isCanceled } returns false
        coEvery { taskAuthResult.result } returns authResult

        coEvery { taskTokenResult.isComplete } returns true
        coEvery { taskTokenResult.exception } returns null
        coEvery { taskTokenResult.isCanceled } returns false
        coEvery { taskTokenResult.result } returns tokenResult

        coEvery { authResult.user?.email } returns email
        coEvery { authResult.user?.uid } returns uid
        coEvery { tokenResult.token } returns token

        val result = api.signUp(email, password)

        //Verify
        assert(result.email == email)
        assert(result.token == token)
        assert(result.id == uid)
        coVerify { firebaseAuth.createUserWithEmailAndPassword(email, password) }
        coVerify { firebaseAuth.getAccessToken(true) }

        confirmVerified(firebaseAuth)
    }

    @Test
    fun `on sign up with email and password is success but data is null`() = coroutinesRule.runBlockingTest {
        //Given
        val email = "email@email.com"
        val password = "1234567"
        val empty = ""

        //When
        coEvery {
            firebaseAuth.createUserWithEmailAndPassword(any(), any())
        } returns taskAuthResult

        coEvery {
            firebaseAuth.getAccessToken(any())
        } returns taskTokenResult

        coEvery { taskAuthResult.isComplete } returns true
        coEvery { taskAuthResult.exception } returns null
        coEvery { taskAuthResult.isCanceled } returns false
        coEvery { taskAuthResult.result } returns authResult

        coEvery { taskTokenResult.isComplete } returns true
        coEvery { taskTokenResult.exception } returns null
        coEvery { taskTokenResult.isCanceled } returns false
        coEvery { taskTokenResult.result } returns tokenResult

        coEvery { authResult.user?.email } returns null
        coEvery { authResult.user?.uid } returns null
        coEvery { tokenResult.token } returns null

        val result = api.signUp(email, password)

        //Verify
        assert(result.email == empty)
        assert(result.token == empty)
        assert(result.id == empty)
        coVerify { firebaseAuth.createUserWithEmailAndPassword(email, password) }
        coVerify { firebaseAuth.getAccessToken(true) }

        confirmVerified(firebaseAuth)
    }
}
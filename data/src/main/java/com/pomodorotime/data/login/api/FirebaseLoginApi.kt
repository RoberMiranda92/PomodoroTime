package com.pomodorotime.data.login.api

import com.google.firebase.auth.FirebaseAuth
import com.pomodorotime.data.login.api.models.ApiUser
import kotlinx.coroutines.tasks.await

class FirebaseLoginApi(private val firebaseAuth: FirebaseAuth) : ILoginApi {

    override suspend fun signIn(email: String, password: String): ApiUser {

        val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        val tokenResult = firebaseAuth.getAccessToken(true).await()

        return ApiUser(
            authResult.user?.email ?: "",
            authResult.user?.uid ?: "",
            tokenResult.token ?: ""
        )
    }

    override suspend fun signUp(email: String, password: String): ApiUser {
        val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val tokenResult = firebaseAuth.getAccessToken(true).await()

        return ApiUser(
            authResult.user?.email ?: "",
            authResult.user?.uid ?: "",
            tokenResult.token ?: ""
        )
    }
}
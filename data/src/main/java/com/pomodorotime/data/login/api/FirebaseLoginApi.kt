package com.pomodorotime.data.login.api

import com.google.firebase.auth.FirebaseAuth
import com.pomodorotime.data.ApiUser
import kotlinx.coroutines.tasks.await

class FirebaseLoginApi : ILoginApi {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override suspend fun signIn(email: String, password: String): ApiUser {

        val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        val tokenResult = firebaseAuth.getAccessToken(true).await()

        return ApiUser(authResult.user?.email, tokenResult.token)
    }

    override suspend fun signUp(email: String, password: String): ApiUser {
        val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val tokenResult = firebaseAuth.getAccessToken(true).await()

        return ApiUser(authResult.user?.email, tokenResult.token)
    }
}
package com.jeuxdevelopers.superchat.repositories

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class AuthRepository() {
    private val mAuth = Firebase.auth

    class AuthenticationException(message: String, cause: Throwable?) : Throwable(message, cause)

    suspend fun signUpWithEmailAndPassword(email: String, password: String): AuthResult? {
        return try {
            mAuth.createUserWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun loginWithEmailAndPassword(email: String, password: String): AuthResult? {
        return try {
            mAuth.signInWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            null
        }
    }

    // google signIn
    suspend fun loginWithGoogle(account: GoogleSignInAccount): AuthResult? {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        return try {
            mAuth.signInWithCredential(credential).await()
        } catch (e: Exception) {
            null
        }
    }

}
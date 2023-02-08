package com.example.core_firebase_auth

import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow


interface FirebaseAuthApi {
    val user: Flow<FirebaseUser?>

    fun registerResultActivity(fragment: Fragment)
    suspend fun singInWithGoogle()
    suspend fun singOutWithGoogle()
}
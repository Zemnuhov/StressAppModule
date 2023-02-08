package com.example.core_firebase_auth_impl.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides

@Module
class FirebaseAuthModule {

    @Provides
    @FirebaseAuthScope
    fun provideFirebaseAuth(): FirebaseAuth{
        return Firebase.auth
    }
}
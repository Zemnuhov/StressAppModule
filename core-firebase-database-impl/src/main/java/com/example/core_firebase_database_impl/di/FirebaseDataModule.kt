package com.example.core_firebase_database_impl.di

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides

@Module
class FirebaseDataModule {

    @Provides
    @FirebaseDataScope
    fun provideFirebaseDatabase(): FirebaseDatabase{
        return Firebase.database
    }
}
package com.neurotech.modulestressapp.di

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.core_database_control_api.DatabaseControllerApi
import com.example.core_database_control_impl.DatabaseController
import com.example.core_firebase_auth.FirebaseAuthApi
import com.example.core_firebase_auth_impl.AppFirebaseAuth
import com.example.core_signal_control_api.SignalControlApi
import com.example.core_signal_control_impl.SignalControlImpl
import com.example.feature_notification_api.NotificationApi
import com.example.feature_notification_impl.NotificationImpl
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides

@Module
class FeatureModule {

    @Provides
    @FeatureScope
    fun provideNotificationApi(): NotificationApi {
        return NotificationImpl()
    }

    @Provides
    @FeatureScope
    fun provideContext(activity: AppCompatActivity): Context {
        return activity.applicationContext
    }

    @Provides
    @FeatureScope
    fun provideSignalController(): SignalControlApi {
        return SignalControlImpl()
    }

    @Provides
    @FeatureScope
    fun provideDatabaseController(): DatabaseControllerApi {
        return DatabaseController()
    }

    @Provides
    @FeatureScope
    fun provideAppFirebaseAuth(): FirebaseAuthApi {
        return AppFirebaseAuth()
    }

}
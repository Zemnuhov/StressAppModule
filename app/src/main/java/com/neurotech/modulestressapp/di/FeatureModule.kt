package com.neurotech.modulestressapp.di

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.work.WorkManager
import com.example.core_database_control_api.DatabaseControllerApi
import com.example.core_database_control_impl.DatabaseController
import com.example.core_firebase_auth.FirebaseAuthApi
import com.example.core_firebase_auth_impl.AppFirebaseAuth
import com.example.core_firebase_database_api.FirebaseDataApi
import com.example.core_firebase_database_impl.FirebaseData
import com.example.core_notification_controller_api.NotificationControllerApi
import com.example.core_notification_controller_impl.NotificationController
import com.example.core_signal_control_api.SignalControlApi
import com.example.core_signal_control_impl.SignalControlImpl
import com.example.feature_notification_api.NotificationApi
import com.example.feature_notification_impl.NotificationImpl
import com.neurotech.core_bluetooth_comunication_api.BluetoothSynchronizerApi
import com.neurotech.core_bluetooth_comunication_impl.implementation.BluetoothSynchronizer
import com.neurotech.core_database_api.PhaseApi
import com.neurotech.core_database_api.TonicApi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module

class FeatureModule {



    @Provides
    fun provideContext(activity: AppCompatActivity): Context {
        return activity.applicationContext
    }



    @Provides
    @Singleton
    fun provideAppFirebaseAuth(): FirebaseAuthApi {
        return AppFirebaseAuth()
    }



}
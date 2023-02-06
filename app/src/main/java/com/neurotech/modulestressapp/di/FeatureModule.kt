package com.neurotech.modulestressapp.di

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.core_database_control_api.DatabaseControllerApi
import com.example.core_database_control_impl.DatabaseController
import com.example.core_signal_control_api.SignalControlApi
import com.example.core_signal_control_impl.SignalControlImpl
import com.example.feature_notification_api.NotificationApi
import com.example.feature_notification_impl.NotificationImpl
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

}
package com.neurotech.modulestressapp.di.app

import android.app.Application
import android.content.Context
import com.example.navigation.AppNavigation
import com.example.navigation_api.NavigationApi
import com.neurotech.modulestressapp.MainActivity
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule{

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideNavigation(): NavigationApi {
        return AppNavigation()
    }

    @Provides
    @Singleton
    fun provideMainClass(): Class<Any> {
        return MainActivity::class.java as Class<Any>
    }
}
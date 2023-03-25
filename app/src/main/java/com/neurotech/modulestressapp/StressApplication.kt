package com.neurotech.modulestressapp

import android.app.Application
import com.neurotech.modulestressapp.di.AppComponent

class StressApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        if(!AppComponent.componentIsInit()){
            AppComponent.init(this)
            AppComponent.provideDependencies()
        }

    }
}

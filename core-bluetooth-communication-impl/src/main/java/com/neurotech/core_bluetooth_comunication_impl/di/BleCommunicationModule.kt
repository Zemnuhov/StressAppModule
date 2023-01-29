package com.neurotech.core_bluetooth_comunication_impl.di

import android.content.Context
import com.neurotech.core_bluetooth_comunication_impl.AppBluetoothManager
import dagger.Module
import dagger.Provides

@Module
class BleCommunicationModule {

    @Provides
    @BleCommunicationScope
    fun provideBleManager(context: Context): AppBluetoothManager{
        return AppBluetoothManager(context)
    }
}
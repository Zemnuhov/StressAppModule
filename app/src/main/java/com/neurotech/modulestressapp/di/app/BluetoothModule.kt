package com.neurotech.modulestressapp.di.app

import com.neurotech.core_bluetooth_comunication_api.BluetoothConnectionApi
import com.neurotech.core_bluetooth_comunication_api.BluetoothDataApi
import com.neurotech.core_bluetooth_comunication_api.BluetoothWriterApi
import com.neurotech.core_bluetooth_comunication_impl.implementation.BluetoothConnection
import com.neurotech.core_bluetooth_comunication_impl.implementation.BluetoothData
import com.neurotech.core_bluetooth_comunication_impl.implementation.BluetoothWriter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class BluetoothModule {

    @Provides
    @Singleton
    fun provideBluetoothConnection(): BluetoothConnectionApi{
        return BluetoothConnection()
    }

    @Provides
    @Singleton
    fun provideBluetoothData(): BluetoothDataApi {
        return BluetoothData()
    }

    @Provides
    @Singleton
    fun provideBluetoothWriter(): BluetoothWriterApi {
        return BluetoothWriter()
    }
}
package com.neurotech.modulestressapp.di

import com.neurotech.core_ble_device_scan.impl.BluetoothScan
import com.neurotech.core_ble_device_scan_api.BluetoothScanAPI
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FeatureScanModule {

    @Provides
    @Singleton
    fun provideBluetoothScan(): BluetoothScanAPI {
        return BluetoothScan()
    }

}
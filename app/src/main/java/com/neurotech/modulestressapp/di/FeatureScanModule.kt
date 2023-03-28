package com.neurotech.modulestressapp.di

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.navigation.AppNavigation
import com.example.navigation_api.NavigationApi
import com.neurotech.core_ble_device_scan.impl.BluetoothScan
import com.neurotech.core_ble_device_scan_api.BluetoothScanAPI
import com.neurotech.modulestressapp.R
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
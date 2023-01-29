package com.neurotech.modulestressapp.di

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.navigation.AppNavigation
import com.example.navigation_api.NavigationApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.neurotech.core_ble_device_scan.impl.BluetoothScan
import com.neurotech.core_ble_device_scan_api.BluetoothScanAPI
import com.neurotech.modulestressapp.R
import dagger.Module
import dagger.Provides

@Module
class FeatureScanModule {

    @Provides
    @FeatureScope
    fun provideBluetoothScan(): BluetoothScanAPI{
        return BluetoothScan()
    }

    @Provides
    @FeatureScope
    fun provideNavigation(): NavigationApi{
        return AppNavigation()
    }

    @Provides
    @FeatureScope
    fun provideNavHostFragment(activity: AppCompatActivity): NavHostFragment{
        return activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }

}
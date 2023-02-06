package com.neurotech.feature_scan_impl.di

import com.example.navigation_api.NavigationApi
import com.neurotech.core_ble_device_scan_api.BluetoothScanAPI
import com.neurotech.core_bluetooth_comunication_api.BluetoothConnectionApi
import com.neurotech.core_database_api.SettingApi
import com.neurotech.feature_scan_impl.ScanFragment
import dagger.Component
import dagger.Component.Builder
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.internal.artificialFrame
import javax.inject.Scope
import kotlin.properties.Delegates.notNull

@Component(dependencies = [ScanDependencies::class])
@ScanScope
interface ScanComponent {
    fun inject(scanFragment: ScanFragment)

    @Builder
    @ScanScope
    interface ScanBuilder{
        fun provideDependencies(dependencies: ScanDependencies): ScanBuilder
        fun build(): ScanComponent
    }

    companion object{
        private var component: ScanComponent? = null

        internal fun get(): ScanComponent{
            if(component == null){
                component = DaggerScanComponent
                    .builder()
                    .provideDependencies(ScanDependenciesProvider.dependencies)
                    .build()
            }
            return component!!
        }

        internal fun clear(){
            component = null
        }
    }
}

interface ScanDependencies{
    val bluetoothScan: BluetoothScanAPI
    val bluetoothConnection: BluetoothConnectionApi
    val navigationApi: NavigationApi
    val settingApi: SettingApi
}

interface ScanDependenciesProvider {
    val dependencies: ScanDependencies
    companion object : ScanDependenciesProvider by ScanDependenciesStore
}

object ScanDependenciesStore : ScanDependenciesProvider {
    override var dependencies: ScanDependencies by notNull()
}

@Scope
annotation class ScanScope

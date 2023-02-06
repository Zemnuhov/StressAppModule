package com.neurotech.feature_scan_impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.neurotech.core_ble_device_scan_api.BluetoothScanAPI
import com.neurotech.core_bluetooth_comunication_api.BluetoothConnectionApi
import com.neurotech.core_database_api.SettingApi
import com.neurotech.core_database_api.model.Device
import com.neurotech.utils.StressLogger.log
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Provider

class ScanViewModel(
    private val bluetoothScan: BluetoothScanAPI,
    private val bluetoothConnection: BluetoothConnectionApi,
    private val settingApi: SettingApi
): ViewModel() {

    val devices = liveData{
        bluetoothScan.getDevicesFlow().collect{
            emit(it)
        }
    }

    val scanState = liveData{
        bluetoothScan.getScanState().collect{
            emit(it)
        }
    }

    val connectionState = liveData{
        bluetoothConnection.getConnectionStateFlow().collect{
            emit(it)
        }
    }

    val deviceInMemory = runBlocking { settingApi.getDevice() }

    fun startScan(){
        viewModelScope.launch {
            this@ScanViewModel.log("Start scan")
            bluetoothScan.startScan()
        }
    }

    fun connectToDevice(mac: String){
        viewModelScope.launch {
            bluetoothConnection.connectionToPeripheral(mac)
        }
    }

    fun rememberDevice(device: Device){
        viewModelScope.launch{
            settingApi.rememberDevice(device)
        }
    }

    @Suppress("UNCHECKED_CAST")
    internal class Factory @Inject constructor (
        private val scanCore:Provider<BluetoothScanAPI>,
        private val connectionApi: Provider<BluetoothConnectionApi>,
        private val settingApi: Provider<SettingApi>
    ): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == ScanViewModel::class.java)
            return ScanViewModel(scanCore.get(), connectionApi.get(), settingApi.get()) as T
        }
    }
}



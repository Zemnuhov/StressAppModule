package com.example.feature_main_screen_impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.neurotech.core_bluetooth_comunication_api.BluetoothConnectionApi
import com.neurotech.core_bluetooth_comunication_api.BluetoothWriterApi
import com.neurotech.core_bluetooth_comunication_api.ConnectionState
import com.neurotech.core_database_api.SettingApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class MainScreenViewModel(
    private val bluetoothConnection: BluetoothConnectionApi,
    private val bluetoothWriter: BluetoothWriterApi,
    private val setting: SettingApi
) : ViewModel() {

    val bluetoothState = liveData {
        bluetoothConnection.getConnectionStateFlow().collect{
            emit(it)
        }
    }

    init {
        viewModelScope.launch{
            if(bluetoothConnection.getConnectionStateFlow().first() == ConnectionState.DISCONNECTED){
                val device = setting.getDevice()
                if(device != null){
                    bluetoothConnection.connectionToPeripheral(device.mac)
                }
            }
        }
        viewModelScope.launch {
            bluetoothWriter.writeNotifyFlag(true)
        }
    }

    fun disconnectDevice(){
        viewModelScope.launch {
            bluetoothConnection.disconnectDevice()
        }
    }


    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val bluetoothConnection: Provider<BluetoothConnectionApi>,
        private val bluetoothWriter: Provider<BluetoothWriterApi>,
        private val setting: Provider<SettingApi>
    ): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == MainScreenViewModel::class.java)
            return MainScreenViewModel(
                bluetoothConnection.get(),
                bluetoothWriter.get(),
                setting.get()
            ) as T
        }
    }
}
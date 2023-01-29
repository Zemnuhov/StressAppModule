package com.neurotech.core_bluetooth_comunication_api

import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.flow.Flow

interface BluetoothConnectionApi {
    suspend fun connectionToPeripheral(deviceMac: String)
    suspend fun getConnectionStateFlow(): Flow<ConnectionState>
    suspend fun getConnectionState():ConnectionState
    suspend fun disconnectDevice()
}
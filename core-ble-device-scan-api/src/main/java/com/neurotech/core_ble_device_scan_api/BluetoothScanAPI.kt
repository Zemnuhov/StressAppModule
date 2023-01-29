package com.neurotech.core_ble_device_scan_api

import kotlinx.coroutines.flow.Flow

interface BluetoothScanAPI {
    suspend fun getDevicesFlow(): Flow<Devices>
    suspend fun getScanState(): Flow<Boolean>
    suspend fun startScan()
    suspend fun stopScan()
}

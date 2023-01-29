package com.neurotech.core_ble_device_scan.impl

import android.annotation.SuppressLint

import com.neurotech.core_ble_device_scan_api.BluetoothScanAPI
import com.neurotech.core_ble_device_scan_api.Device
import com.neurotech.core_ble_device_scan_api.Devices
import com.neurotech.utils.StressLogger.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import no.nordicsemi.android.support.v18.scanner.*

class BluetoothScan: BluetoothScanAPI {
    private var scanner = BluetoothLeScannerCompat.getScanner()
    private var scanCallback: BleScanCallback =  BleScanCallback()
    private val filters: MutableList<ScanFilter> = ArrayList()
    private val settings: ScanSettings get() =  ScanSettings.Builder()
        .setLegacy(false)
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .setUseHardwareBatchingIfSupported(true)
        .build()

    private val deviceList = mutableListOf<Device>()
    private val devicesFlow = MutableStateFlow(Devices(listOf()))
    private val scanState = MutableStateFlow(false)

    private val scope = CoroutineScope(Dispatchers.IO)


    override suspend fun getDevicesFlow(): Flow<Devices> {
        return devicesFlow
    }

    override suspend fun getScanState(): Flow<Boolean> = scanState

    override suspend fun stopScan() {
        scanner.stopScan(scanCallback)
        scanState.value = false
    }

    override suspend fun startScan(){
        if(!scanState.value){
            stopScan()
            devicesFlow.emit(Devices(emptyList()))
            scanner = BluetoothLeScannerCompat.getScanner()
            scanCallback =  BleScanCallback()
            scanner.startScan(filters, settings, scanCallback)
            scanState.value = true
            scope.launch {
                delay(10000)
                stopScan()
            }
        }
    }

    inner class BleScanCallback : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            if(result.device.name != null){
                with(result.device){
                    val device = Device(name, address)
                    if(device.mac !in deviceList.map { it.mac }){
                        scope.launch {
                            deviceList.add(device)
                            delay(1000)
                            devicesFlow.value = Devices(deviceList)
                            log(deviceList.toString())
                        }

                    }
                }
            }
        }
    }
}
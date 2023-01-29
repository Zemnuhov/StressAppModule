package com.neurotech.core_ble_device_scan_api

data class Devices(
    val list: List<Device>
)

data class Device(
    val name: String,
    val mac: String
)

package com.neurotech.core_bluetooth_comunication_api

import java.util.Date

interface BluetoothWriterApi {
    suspend fun writeNotifyFlag(isNotify: Boolean)
    suspend fun writeDateTime(dateTime: Date)
}
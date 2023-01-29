package com.neurotech.core_bluetooth_comunication_impl.implementation

import com.neurotech.core_bluetooth_comunication_api.BluetoothConnectionApi
import com.neurotech.core_bluetooth_comunication_api.BluetoothWriterApi
import com.neurotech.core_bluetooth_comunication_impl.AppBluetoothManager
import com.neurotech.core_bluetooth_comunication_impl.di.BleCommunicationComponent
import java.util.*
import javax.inject.Inject

class BluetoothWriter: BluetoothWriterApi {

    @Inject
    lateinit var bleManager: AppBluetoothManager

    init {
        BleCommunicationComponent.get().inject(this)
    }

    override suspend fun writeNotifyFlag(isNotify: Boolean) {
        bleManager.writeNotifyFlag(isNotify)
    }

    override suspend fun writeDateTime(dateTime: Date) {
        //TODO("Not yet implemented")
    }
}
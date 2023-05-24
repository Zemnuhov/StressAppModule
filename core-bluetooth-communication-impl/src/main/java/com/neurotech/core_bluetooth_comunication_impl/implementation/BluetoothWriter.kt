package com.neurotech.core_bluetooth_comunication_impl.implementation

import com.cesarferreira.tempo.Tempo
import com.cesarferreira.tempo.toString
import com.neurotech.core_bluetooth_comunication_api.BluetoothConnectionApi
import com.neurotech.core_bluetooth_comunication_api.BluetoothWriterApi
import com.neurotech.core_bluetooth_comunication_impl.AppBluetoothManager
import com.neurotech.core_bluetooth_comunication_impl.di.BleCommunicationComponent
import com.neurotech.utils.StressLogger.log
import com.neurotech.utils.TimeFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.nordicsemi.android.ble.ktx.stateAsFlow
import java.util.*
import javax.inject.Inject

class BluetoothWriter: BluetoothWriterApi {

    @Inject
    lateinit var bleManager: AppBluetoothManager

    private val timeFormat = "HH:mm:ss"
    private val dateFormat = "yyyy-MM-dd"

    init {
        BleCommunicationComponent.get().inject(this)
        CoroutineScope(Dispatchers.IO).launch {
            launch {
                bleManager.stateAsFlow().collect{
                    if(it.isReady){
                        writeDateTime(Tempo.now)
                    }
                }
            }
            launch {
                while (true){
                    delay(60000)
                    if(bleManager.isReady){
                        writeDateTime(Tempo.now)
                    }
                }
            }

        }
    }

    override suspend fun writeNotifyFlag(isNotify: Boolean) {
        bleManager.writeNotifyFlag(isNotify)
    }

    override suspend fun writeDateTime(dateTime: Date) {
        log("$dateTime writing")
        val timeString = dateTime.toString(timeFormat)
        val dateString = dateTime.toString(dateFormat)
        val timeByteArray = timeString.toByteArray()
        val dateByteArray = dateString.toByteArray()
        if (timeByteArray.isNotEmpty() && dateByteArray.isNotEmpty()) {
            bleManager.writeDateTime(timeByteArray, dateByteArray)
        }

    }
}
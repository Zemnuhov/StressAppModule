package com.neurotech.core_bluetooth_comunication_impl.implementation

import com.cesarferreira.tempo.Tempo
import com.neurotech.core_bluetooth_comunication_api.BluetoothSynchronizerApi
import com.neurotech.core_bluetooth_comunication_impl.AppBluetoothManager
import com.neurotech.core_bluetooth_comunication_impl.di.BleCommunicationComponent
import com.neurotech.core_database_api.PhaseApi
import com.neurotech.core_database_api.TonicApi
import com.neurotech.core_database_api.model.Phase
import com.neurotech.core_database_api.model.Tonic
import com.neurotech.utils.StressLogger.log
import kotlinx.coroutines.*
import javax.inject.Inject

class BluetoothSynchronizer(
    private val phaseDatabase: PhaseApi,
    private val tonicDatabase: TonicApi
) : BluetoothSynchronizerApi {

    @Inject
    lateinit var manager: AppBluetoothManager


    init {
        BleCommunicationComponent.get().inject(this)
    }


    private suspend fun getPeakFromDeviceInMemory() = coroutineScope {
        val peaksFromDevice = manager.getPeaks()
        if (peaksFromDevice != null) {
            try {
                phaseDatabase.writePhase(
                    Phase(
                        peaksFromDevice.timeBegin,
                        peaksFromDevice.timeEnd,
                        peaksFromDevice.max
                    )
                )
            } catch (e: Exception) {
                log(e.message.toString())
            }
        }
        log(peaksFromDevice.toString())
        delay(500)
        manager.writeMemoryFlag()
    }

    private suspend fun getPeakFromDevice() = coroutineScope {
        val peaksFromDevice = manager.getPeaks()
        if (peaksFromDevice != null) {
            try {
                phaseDatabase.writePhase(
                    Phase(
                        peaksFromDevice.timeBegin,
                        peaksFromDevice.timeEnd,
                        peaksFromDevice.max
                    )
                )
            } catch (e: Exception) {
                log(e.message.toString())
            }
        }
        log(peaksFromDevice.toString())
    }

    override suspend fun synchronize() {
        withContext(Dispatchers.IO){
            launch {
                manager.memoryStateFlow.collect {
                    if (it == 1) {
                        getPeakFromDeviceInMemory()
                    }
                    if (it == 2) {
                        getPeakFromDevice()
                    }
                }
            }
            launch {
                manager.memoryTonicFlow.collect {
                    tonicDatabase.writeTonic(
                        Tonic(
                            Tempo.now,
                            it
                        )
                    )
                    log("Write tonic value in background")
                }
            }
        }
    }

}
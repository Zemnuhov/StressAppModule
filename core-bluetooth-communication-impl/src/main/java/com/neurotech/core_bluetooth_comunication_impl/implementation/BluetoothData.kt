package com.neurotech.core_bluetooth_comunication_impl.implementation

import com.neurotech.core_bluetooth_comunication_api.BluetoothDataApi
import com.neurotech.core_bluetooth_comunication_api.model.Phase
import com.neurotech.core_bluetooth_comunication_api.model.Tonic
import com.neurotech.core_bluetooth_comunication_impl.AppBluetoothManager
import com.neurotech.core_bluetooth_comunication_impl.di.BleCommunicationComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

class BluetoothData: BluetoothDataApi {
    @Inject
    lateinit var bleManager: AppBluetoothManager

    init {
        BleCommunicationComponent.get().inject(this)
    }

    override suspend fun getTonicValueFlow(): Flow<Tonic> =
        bleManager.tonicValueFlow.map { Tonic(it, Date()) }


    override suspend fun getPhaseValueFlow(): Flow<Phase> =
        bleManager.phaseValueFlow.map { Phase(it,Date()) }
}
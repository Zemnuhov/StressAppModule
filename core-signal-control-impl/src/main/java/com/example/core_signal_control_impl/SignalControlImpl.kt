package com.example.core_signal_control_impl

import com.example.core_signal_control_api.SignalControlApi
import com.neurotech.core_bluetooth_comunication_api.BluetoothDataApi
import com.neurotech.core_database_api.PhaseApi
import com.neurotech.core_database_api.TonicApi
import com.neurotech.core_database_api.model.Phase
import com.neurotech.core_database_api.model.Tonic
import java.util.*
import javax.inject.Inject
import kotlin.math.max

class SignalControlImpl: SignalControlApi {

    @Inject
    lateinit var bluetoothData: BluetoothDataApi

    @Inject
    lateinit var phaseDataBase: PhaseApi

    @Inject
    lateinit var tonicDataBase: TonicApi

    private val threshold = 3

    init {
        SignalControlComponent.get().inject(this)
    }

    override suspend fun listenPhaseSignal() {
        var beyondThreshold = false
        var beginTime: Date? = null
        var endTime: Date? = null
        var maxValue: Double = threshold.toDouble()

        bluetoothData.getPhaseValueFlow().collect{
            if(it.value > threshold && !beyondThreshold){
                beginTime = it.time
                beyondThreshold = true
            }
            if(beyondThreshold){
                maxValue = max(maxValue, it.value)
            }
            if(it.value < threshold && beyondThreshold){
                endTime = it.time
                if (beginTime != null){
                    phaseDataBase.writePhase(Phase(beginTime!!, endTime!!, maxValue))
                }
                beyondThreshold = false
                beginTime = null
                endTime = null
            }
        }
    }

    override suspend fun listenTonicSignal() {
        var lastWriting = 0L
        bluetoothData.getTonicValueFlow().collect{
            if(System.currentTimeMillis() - lastWriting > 30000){
                if(it.value>0){
                    lastWriting = System.currentTimeMillis()
                    tonicDataBase.writeTonic(Tonic(it.time, it.value))
                }
            }
        }
    }
}
package com.example.feature_screen_relax_impl

import androidx.lifecycle.*
import com.cesarferreira.tempo.Tempo
import com.example.core_firebase_database_api.FirebaseDataApi
import com.neurotech.core_bluetooth_comunication_api.BluetoothDataApi
import com.neurotech.core_database_api.PhaseApi
import com.neurotech.core_database_api.RelaxRecordApi
import com.neurotech.core_database_api.model.RelaxRecord
import com.neurotech.core_database_api.model.Tonic
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class RelaxViewModel(
    private val firebaseDatabaseApi: FirebaseDataApi,
    private val bluetoothDataApi: BluetoothDataApi,
    private val phaseApi: PhaseApi,
    private val relaxRecordApi: RelaxRecordApi
): ViewModel() {

    private val timeBegin = Tempo.now
    private val _beginTonic = MutableLiveData<Int>()
    val beginTonic: LiveData<Int> get() = _beginTonic
    private var timerCount = 0
    val sessionState = MutableLiveData(SessionState.NOT_RECORDING)
    var lastLampWriting = 0L

    val tonic = liveData {
        bluetoothDataApi.getTonicValueFlow().collect{
            emit(it)
            if(System.currentTimeMillis() - lastLampWriting > 500){
                lastLampWriting = System.currentTimeMillis()
                firebaseDatabaseApi.writeTonicValue(Tonic(it.time,it.value))
            }
        }
    }

    val tonicDifference = liveData {
        var isFirst = true
        bluetoothDataApi.getTonicValueFlow().collect{
            if (isFirst){
                _beginTonic.value = it.value
                isFirst = false
            }
            if(beginTonic.value!! - it.value > 0){
                emit(beginTonic.value!! - it.value)
            }else{
                emit(0)
            }

        }
    }

    val phase = liveData {
        phaseApi.getPhaseFromNow(timeBegin).collect{
            emit(it)
        }
    }

    val timer = liveData {
        while (true){
            withContext(Dispatchers.IO){
                timerCount += 1
                val minute: Int = timerCount / 60
                val seconds = timerCount % 60
                emit(
                    "${
                        if (minute < 10) {
                            "0$minute"
                        } else {
                            minute.toString()
                        }
                    }:${
                        if (seconds < 10) {
                            "0$seconds"
                        } else {
                            seconds.toString()
                        }
                    }"
                )
                delay(1000)
            }
        }
    }

    fun processSession(){
        viewModelScope.launch {
            if(sessionState.value == SessionState.NOT_RECORDING){
                sessionState.postValue(SessionState.RECORDING)
                firebaseDatabaseApi.writeLedMode("Relax")
                timerCount = 0
                _beginTonic.postValue(bluetoothDataApi.getTonicValueFlow().first().value)
            }else{
                sessionState.postValue(SessionState.NOT_RECORDING)
                firebaseDatabaseApi.writeLedMode("GRADIENT")
                val minute: Int = timerCount / 60
                if(minute > 0){
                    val relaxRecord = RelaxRecord(
                        Tempo.now,
                        minute,
                        phase.value!!,
                        tonicDifference.value!!
                    )
                    relaxRecordApi.writeRelaxRecord(relaxRecord)
                }
            }


        }
    }




    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val firebaseDatabaseApi: Provider<FirebaseDataApi>,
        private val bluetoothDataApi: Provider<BluetoothDataApi>,
        private val phaseApi: Provider<PhaseApi>,
        private val relaxRecordApi: Provider<RelaxRecordApi>
    ): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == RelaxViewModel::class.java)
            return RelaxViewModel(firebaseDatabaseApi.get(),bluetoothDataApi.get(), phaseApi.get(), relaxRecordApi.get()) as T
        }
    }
}
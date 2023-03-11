package com.example.feature_screen_relax_impl

import android.util.Log
import androidx.lifecycle.*
import com.cesarferreira.tempo.Tempo
import com.neurotech.core_bluetooth_comunication_api.BluetoothDataApi
import com.neurotech.core_database_api.PhaseApi
import com.neurotech.core_database_api.RelaxRecord
import com.neurotech.core_database_api.model.Tonic
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class RelaxViewModel(
    private val bluetoothDataApi: BluetoothDataApi,
    private val phaseApi: PhaseApi
): ViewModel() {

    private val timeBegin = Tempo.now
    private val _beginTonic = MutableLiveData<Int>()
    val beginTonic: LiveData<Int> get() = _beginTonic
    private var timerCount = 0
    val sessionState = MutableLiveData(SessionState.NOT_RECORDING)

    val tonic = liveData {
        bluetoothDataApi.getTonicValueFlow().collect{
            emit(it)
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
                timerCount = 0
                _beginTonic.postValue(bluetoothDataApi.getTonicValueFlow().first().value)
            }else{
                sessionState.postValue(SessionState.NOT_RECORDING)
                val minute: Int = timerCount / 60
                if(minute > 0){
                    val relaxRecord = RelaxRecord(
                        Tempo.now,
                        minute,
                        phase.value!!,
                        tonicDifference.value!!
                    )
                }
            }


        }
    }








    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val bluetoothDataApi: Provider<BluetoothDataApi>,
        private val phaseApi: Provider<PhaseApi>
    ): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == RelaxViewModel::class.java)
            return RelaxViewModel(bluetoothDataApi.get(), phaseApi.get()) as T
        }
    }
}
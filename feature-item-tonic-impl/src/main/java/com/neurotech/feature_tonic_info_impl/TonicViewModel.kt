package com.neurotech.feature_tonic_info_impl

import androidx.lifecycle.*
import com.neurotech.core_bluetooth_comunication_api.BluetoothDataApi
import com.neurotech.core_database_api.TonicApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class TonicViewModel(
    private val bluetoothData: BluetoothDataApi,
    private val tonicApi: TonicApi
): ViewModel() {

    val tonicValue = liveData {
        bluetoothData.getTonicValueFlow().collect{
            emit(it.value)
        }
    }

    private val _avgTonic = MutableLiveData<Int>()
    val avgTonic: LiveData<Int> get() = _avgTonic

    private val jobList = mutableListOf<Job>()

    fun setInterval(interval: Interval){
        viewModelScope.launch {
            stopJob()
            val flow = when(interval){
                Interval.TEN_MINUTE -> tonicApi.getTenMinuteAverage()
                Interval.HOUR -> tonicApi.getHourAverage()
                Interval.DAY ->  tonicApi.getDayAverage()
            }
            jobList.add(
                viewModelScope.launch {
                    flow.collect{
                        _avgTonic.postValue(it)
                    }
                }
            )
        }
    }

    private fun stopJob(){
        jobList.forEach { it.cancel() }
        jobList.clear()
    }



    @Suppress("UNCHECKED_CAST")
    internal class Factory @Inject constructor (
        private val bluetoothDataApi: Provider<BluetoothDataApi>,
        private val tonicApi: Provider<TonicApi>
    ): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == TonicViewModel::class.java)
            return TonicViewModel(bluetoothDataApi.get(), tonicApi.get()) as T
        }
    }
}


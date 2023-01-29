package com.example.feature_item_graph_impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.neurotech.core_bluetooth_comunication_api.BluetoothDataApi
import dagger.Provides
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import javax.inject.Provider

class GraphViewModel(
    bluetoothDataApi: BluetoothDataApi
): ViewModel() {

    val phaseValue = liveData {
        bluetoothDataApi.getPhaseValueFlow().collect{
            emit(it)
        }
    }

    val tonicValue = liveData {
        bluetoothDataApi.getTonicValueFlow().collect{
            emit(it)
        }
    }



    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val bluetoothDataApi: Provider<BluetoothDataApi>
    ) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == GraphViewModel::class.java)
            return GraphViewModel(bluetoothDataApi.get()) as T
        }
    }
}
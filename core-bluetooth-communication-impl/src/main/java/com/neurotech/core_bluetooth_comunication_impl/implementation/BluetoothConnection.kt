package com.neurotech.core_bluetooth_comunication_impl.implementation

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import androidx.core.content.ContextCompat
import com.neurotech.core_bluetooth_comunication_api.BluetoothConnectionApi
import com.neurotech.core_bluetooth_comunication_api.ConnectionState
import com.neurotech.core_bluetooth_comunication_impl.AppBluetoothManager
import com.neurotech.core_bluetooth_comunication_impl.di.BleCommunicationComponent
import com.neurotech.utils.StressLogger.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import no.nordicsemi.android.ble.ktx.stateAsFlow
import no.nordicsemi.android.ble.ktx.suspend
import javax.inject.Inject

class BluetoothConnection: BluetoothConnectionApi {
    @Inject
    lateinit var bleManager: AppBluetoothManager
    @Inject
    lateinit var context:Context

    private var mac: String? = null

    init {
        BleCommunicationComponent.get().inject(this)
        CoroutineScope(Dispatchers.IO).launch {
            while (true){
                delay(60000)
                if(!bleManager.isConnected && mac != null){
                    connectionToPeripheral(mac!!)
                }
            }
        }
    }

    override suspend fun connectionToPeripheral(deviceMac: String) {
        mac = deviceMac
        val bluetoothManager: BluetoothManager? =
            ContextCompat.getSystemService(context, BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter
        if(bluetoothAdapter != null){
            bleManager.connectToDevice(bluetoothAdapter.getRemoteDevice(deviceMac))
        }
    }

    override suspend fun getConnectionStateFlow(): Flow<ConnectionState> {
        return flow {
            bleManager.stateAsFlow().collect{
                if (!it.isConnected) {
                    emit(ConnectionState.DISCONNECTED)
                    this@BluetoothConnection.log(ConnectionState.DISCONNECTED.name)
                } else {
                    emit(ConnectionState.CONNECTING)
                    this@BluetoothConnection.log(ConnectionState.CONNECTING.name)
                    if (it.isReady) {
                        emit(ConnectionState.CONNECTED)
                        this@BluetoothConnection.log(ConnectionState.CONNECTED.name)
                    }
                }
            }
        }
    }

    override suspend fun getConnectionState(): ConnectionState {
        return when (bleManager.connectionState) {
            BluetoothProfile.STATE_CONNECTING -> ConnectionState.CONNECTING
            BluetoothProfile.STATE_CONNECTED -> ConnectionState.CONNECTED
            BluetoothProfile.STATE_DISCONNECTING -> ConnectionState.DISCONNECTED
            else -> ConnectionState.DISCONNECTED
        }
    }

    override suspend fun disconnectDevice() {
        try {
            bleManager.disconnect().suspend()
        } catch (e: Exception) {
            log(e.message.toString())
        }
        bleManager.isAutoConnect = false
    }
}
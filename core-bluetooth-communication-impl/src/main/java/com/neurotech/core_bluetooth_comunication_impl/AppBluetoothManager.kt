package com.neurotech.core_bluetooth_comunication_impl

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import com.cesarferreira.tempo.toDate
import com.neurotech.core_bluetooth_comunication_impl.ListUUID.dataServiceUUID
import com.neurotech.core_bluetooth_comunication_impl.ListUUID.dateUUID
import com.neurotech.core_bluetooth_comunication_impl.ListUUID.memoryCharacteristicUUID
import com.neurotech.core_bluetooth_comunication_impl.ListUUID.memoryDateEndCharacteristicUUID
import com.neurotech.core_bluetooth_comunication_impl.ListUUID.memoryMaxPeakValueCharacteristicUUID
import com.neurotech.core_bluetooth_comunication_impl.ListUUID.memoryServiceUUID
import com.neurotech.core_bluetooth_comunication_impl.ListUUID.memoryTimeBeginCharacteristicUUID
import com.neurotech.core_bluetooth_comunication_impl.ListUUID.memoryTimeEndCharacteristicUUID
import com.neurotech.core_bluetooth_comunication_impl.ListUUID.memoryTonicCharacteristicUUID
import com.neurotech.core_bluetooth_comunication_impl.ListUUID.notifyStateCharacteristicUUID
import com.neurotech.core_bluetooth_comunication_impl.ListUUID.phaseFlowUUID
import com.neurotech.core_bluetooth_comunication_impl.ListUUID.settingServiceUUID
import com.neurotech.core_bluetooth_comunication_impl.ListUUID.timeUUID
import com.neurotech.core_bluetooth_comunication_impl.ListUUID.tonicFlowUUID
import com.neurotech.core_bluetooth_comunication_impl.model.PhaseEntityFromDevice
import com.neurotech.data.modules.bluetooth.data.filters.ExpRunningAverage
import com.neurotech.utils.StressLogger.log
import com.neurotech.utils.TimeFormat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.ktx.asFlow
import no.nordicsemi.android.ble.ktx.state
import no.nordicsemi.android.ble.ktx.stateAsFlow
import no.nordicsemi.android.ble.ktx.suspend
import java.nio.ByteBuffer

@OptIn(ExperimentalCoroutinesApi::class)
class AppBluetoothManager(
    context: Context
) : BleManager(context) {

    private var notifyStateCharacteristic: BluetoothGattCharacteristic? = null

    private var phaseFlowCharacteristic: BluetoothGattCharacteristic? = null
    private var tonicFlowCharacteristic: BluetoothGattCharacteristic? = null
    private var timeCharacteristic: BluetoothGattCharacteristic? = null
    private var dateCharacteristic: BluetoothGattCharacteristic? = null
    private var characteristicServiceCharacteristic: BluetoothGattCharacteristic? = null

    private var memoryCharacteristic: BluetoothGattCharacteristic? = null
    private var memoryTimeBeginCharacteristic: BluetoothGattCharacteristic? = null
    private var memoryTimeEndCharacteristic: BluetoothGattCharacteristic? = null
    private var memoryDateEndCharacteristic: BluetoothGattCharacteristic? = null
    private var memoryMaxPeakValueCharacteristic: BluetoothGattCharacteristic? = null
    private var memoryTonicCharacteristic: BluetoothGattCharacteristic? = null

    @OptIn(DelicateCoroutinesApi::class)
    val scope = CoroutineScope(newSingleThreadContext("BleFlow"))

    private val _phaseValueFlow = MutableSharedFlow<Double>()
    private val _tonicValueFlow = MutableSharedFlow<Int>()
    private val _memoryStateFlow = MutableSharedFlow<Int>()
    private val _memoryTonicFlow = MutableSharedFlow<Int>()


    val phaseValueFlow: Flow<Double> get() = _phaseValueFlow
    val tonicValueFlow: Flow<Int> get() = _tonicValueFlow
    val memoryStateFlow: Flow<Int> get() = _memoryStateFlow
    val memoryTonicFlow: Flow<Int> get() = _memoryTonicFlow

    var isAutoConnect = true

    override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
        val settingService = try {
            gatt.getService(settingServiceUUID)
        } catch (e: Exception) {
            null
        }
        val dataService = try {
            gatt.getService(dataServiceUUID)
        } catch (e: Exception) {
            null
        }
        val memoryService = try {
            gatt.getService(memoryServiceUUID)
        } catch (e: Exception) {
            null
        }
        var settingCharacteristicResult = false
        var dataCharacteristicResult = false
        var memoryCharacteristicResult = false
        if (settingService != null && dataService != null && memoryService != null) {
            try {
                settingCharacteristicResult = settingCharacteristicInit(settingService)
                dataCharacteristicResult = dataCharacteristicInit(dataService)
                memoryCharacteristicResult = memoryCharacteristicInit(memoryService)
            } catch (e: Exception) {
                log(e.message.toString())
            }
        }
        return settingCharacteristicResult && dataCharacteristicResult && memoryCharacteristicResult
    }


    override fun log(priority: Int, message: String) {
        log("$message. Priority $priority")
        super.log(priority, message)
    }

    private fun settingCharacteristicInit(settingService: BluetoothGattService): Boolean {
        notifyStateCharacteristic = try {
            settingService.getCharacteristic(notifyStateCharacteristicUUID)
        } catch (e: Exception) {
            null
        }
        return notifyStateCharacteristic != null
    }

    private fun memoryCharacteristicInit(memoryService: BluetoothGattService): Boolean {
        memoryCharacteristic = try {
            memoryService.getCharacteristic(memoryCharacteristicUUID)
        } catch (e: Exception) {
            null
        }
        memoryTimeBeginCharacteristic = try {
            memoryService.getCharacteristic(
                memoryTimeBeginCharacteristicUUID
            )
        } catch (e: Exception) {
            null
        }
        memoryTimeEndCharacteristic = try {
            memoryService.getCharacteristic(
                memoryTimeEndCharacteristicUUID
            )
        } catch (e: Exception) {
            null
        }
        memoryDateEndCharacteristic = try {
            memoryService.getCharacteristic(
                memoryDateEndCharacteristicUUID
            )
        } catch (e: Exception) {
            null
        }
        memoryMaxPeakValueCharacteristic = try {
            memoryService.getCharacteristic(
                memoryMaxPeakValueCharacteristicUUID
            )
        } catch (e: Exception) {
            null
        }
        memoryTonicCharacteristic = try {
            memoryService.getCharacteristic(
                memoryTonicCharacteristicUUID
            )
        } catch (e: Exception) {
            null
        }
        return memoryCharacteristic != null &&
                memoryTimeBeginCharacteristic != null &&
                memoryTimeEndCharacteristic != null &&
                memoryDateEndCharacteristic != null &&
                memoryMaxPeakValueCharacteristic != null &&
                memoryTonicCharacteristic != null
    }

    private fun dataCharacteristicInit(dataService: BluetoothGattService): Boolean {
        phaseFlowCharacteristic = try {
            dataService.getCharacteristic(phaseFlowUUID)
        } catch (e: Exception) {
            null
        }
        tonicFlowCharacteristic = try {
            dataService.getCharacteristic(tonicFlowUUID)
        } catch (e: Exception) {
            null
        }
        timeCharacteristic = try {
            dataService.getCharacteristic(timeUUID)
        } catch (e: Exception) {
            null
        }
        dateCharacteristic = try {
            dataService.getCharacteristic(dateUUID)
        } catch (e: Exception) {
            null
        }

        log("$phaseFlowCharacteristic -- $tonicFlowCharacteristic -- $timeCharacteristic -- $dateCharacteristic")
        return phaseFlowCharacteristic != null &&
                tonicFlowCharacteristic != null &&
                timeCharacteristic != null &&
                dateCharacteristic != null
    }

    override fun initialize() {
        try {
            requestMtu(512).enqueue()
        } catch (e: Exception) {
            log(e.message.toString())
        }
    }

    override fun onServicesInvalidated() {
        notifyStateCharacteristic = null
        phaseFlowCharacteristic = null
        tonicFlowCharacteristic = null
        timeCharacteristic = null
        dateCharacteristic = null
        characteristicServiceCharacteristic = null
        memoryCharacteristic = null
        memoryTimeBeginCharacteristic = null
        memoryTimeEndCharacteristic = null
        memoryDateEndCharacteristic = null
        memoryMaxPeakValueCharacteristic = null
        memoryTonicCharacteristic = null
    }


    init {
        scope.launch(Dispatchers.IO) {
            delay(500)
            stateAsFlow().collect {
                if (it.isReady) {
                    try {
                        observeNotification()
                    } catch (e: Exception) {
                        log(e.message.toString())
                    }
                }
            }
        }

    }

    suspend fun connectToDevice(device: BluetoothDevice) {
        isAutoConnect = true
        try {
            if (!state.isConnected) {
                connect(device)
                    .retry(4, 300)
                    .useAutoConnect(true)
                    .timeout(15_000)
                    .suspend()
            }
        } catch (e: Exception) {
            log(e.message.toString())
            connectToDevice(device)
        }
    }


    private suspend fun observeNotification() {
        val filter = ExpRunningAverage(0.1)
        try {
            enableNotifications(phaseFlowCharacteristic).suspend()
            enableNotifications(tonicFlowCharacteristic).suspend()
            enableNotifications(memoryCharacteristic).suspend()
            enableNotifications(memoryTonicCharacteristic).suspend()
        } catch (e: Exception) {
            log(e.message.toString())
            observeNotification()
        }
        writeMemoryFlag()
        scope.launch {
            try {
                setNotificationCallback(phaseFlowCharacteristic).asFlow()
                    .collect {
                        val bytes = it.value
                        if (bytes != null) {
                            var value = (ByteBuffer.wrap(bytes).int).toDouble()
                            value = filter.filter(value)
                            _phaseValueFlow.emit(value)
                        }
                    }
            } catch (e: Exception) {
                log(e.message.toString())
            }
        }
        scope.launch {
            try {
                setNotificationCallback(tonicFlowCharacteristic).asFlow()
                    .collect {
                        val bytes = it.value
                        if (bytes != null) {
                            val value = ByteBuffer.wrap(bytes).int
                            _tonicValueFlow.emit(value)
                        }
                    }
            } catch (e: Exception) {
                log(e.message.toString())
            }
        }

        scope.launch {
            try {
                setNotificationCallback(memoryCharacteristic).asFlow()
                    .collect {
                        val bytes = it.value
                        bytes?.let { b ->
                            _memoryStateFlow.emit(ByteBuffer.wrap(b).int)
                        }
                    }
            } catch (e: Exception) {
                log(e.message.toString())
            }
        }

        scope.launch {
            try {
                setNotificationCallback(memoryTonicCharacteristic).asFlow()
                    .collect {
                        val bytes = it.value
                        bytes?.let { b ->
                            _memoryTonicFlow.emit(ByteBuffer.wrap(b).int)
                        }
                    }
            } catch (e: Exception) {
                log(e.message.toString())
            }
        }
    }

    suspend fun getPeaks(): PhaseEntityFromDevice? {
        val timeBeginRequest = try {
            readCharacteristic(memoryTimeBeginCharacteristic).suspend()
        } catch (e: Exception) {
            null
        }
        val timeEndRequest = try {
            readCharacteristic(memoryTimeEndCharacteristic).suspend()
        } catch (e: Exception) {
            null
        }
        val dateRequest = try {
            readCharacteristic(memoryDateEndCharacteristic).suspend()
        } catch (e: Exception) {
            null
        }
        val maxRequest = try {
            readCharacteristic(memoryMaxPeakValueCharacteristic).suspend()
        } catch (e: Exception) {
            null
        }

        val timeBegin = timeBeginRequest?.value?.let { String(it) }
        val timeEnd = timeEndRequest?.value?.let { String(it) }
        val max = maxRequest?.value?.let { ByteBuffer.wrap(it).int }
        val date = dateRequest?.value?.let { String(it) }
        return try {
            val dateTimeBegin = "$date $timeBegin".toDate(TimeFormat.dateTimeIsoPattern)
            val dateTimeEnd = "$date $timeEnd".toDate(TimeFormat.dateTimeIsoPattern)
            log("Write Phase in background")
            PhaseEntityFromDevice(dateTimeBegin, dateTimeEnd, max!!.toDouble())
        } catch (e: Exception) {
            null
        }
    }

    suspend fun writeMemoryFlag() {
        try {
            val byteValue = ByteBuffer.allocate(4).putInt(1).array()
            byteValue.reverse()
            writeCharacteristic(
                memoryCharacteristic,
                byteValue,
                BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
            ).suspend()
        } catch (e: Exception) {
            log(e.message.toString())
        }
    }

    suspend fun writeNotifyFlag(isNotify: Boolean) {
        try {
            val byteValue = if (isNotify) {
                ByteBuffer.allocate(4).putInt(1).array()
            } else {
                ByteBuffer.allocate(4).putInt(0).array()
            }
            byteValue.reverse()
            writeCharacteristic(
                notifyStateCharacteristic,
                byteValue,
                BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
            ).suspend()
        } catch (e: Exception) {
            log(e.message.toString())
        }
    }

    suspend fun writeDateTime(time: ByteArray, date: ByteArray) {
        try {
            writeCharacteristic(
                timeCharacteristic,
                time,
                BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
            ).suspend()
            writeCharacteristic(
                dateCharacteristic,
                date,
                BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
            ).suspend()
        } catch (e: Exception) {
            log(e.message.toString())
        }
    }
}
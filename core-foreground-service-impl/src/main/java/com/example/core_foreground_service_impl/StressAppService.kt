package com.example.core_foreground_service_impl

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.core_database_control_api.DatabaseControllerApi
import com.example.core_firebase_controller_impl.FirebaseController
import com.example.core_foreground_service_impl.di.ServiceComponent
import com.example.core_notification_controller_api.NotificationControllerApi
import com.example.core_screen_controller.ScreenControllerApi
import com.example.core_screen_controller.ScreenState
import com.example.core_signal_control_api.SignalControlApi
import com.example.feature_notification_api.NotificationApi
import com.neurotech.core_bluetooth_comunication_api.BluetoothConnectionApi
import com.neurotech.core_bluetooth_comunication_api.BluetoothSynchronizerApi
import com.neurotech.core_bluetooth_comunication_api.BluetoothWriterApi
import com.neurotech.core_bluetooth_comunication_api.ConnectionState
import com.neurotech.core_database_api.SettingApi
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class StressAppService : Service() {

    companion object {
        private const val FOREGROUND_SERVICE_ID = 1
    }

    @Inject lateinit var notification: NotificationApi
    @Inject lateinit var notificationController: NotificationControllerApi
    @Inject lateinit var signalController: SignalControlApi
    @Inject lateinit var databaseControllerApi: DatabaseControllerApi
    @Inject lateinit var workManager: WorkManager
    @Inject lateinit var bluetoothSynchronizer: BluetoothSynchronizerApi
    @Inject lateinit var bluetoothConnectionApi: BluetoothConnectionApi
    @Inject lateinit var settingApi: SettingApi
    @Inject lateinit var screenControllerApi: ScreenControllerApi
    @Inject lateinit var bluetoothWriterApi: BluetoothWriterApi

    private val binder = LocalBinder()


    override fun onCreate() {
        super.onCreate()
        ServiceComponent.get().inject(this)

        startForeground(FOREGROUND_SERVICE_ID, notification.getForegroundNotification())
        CoroutineScope(Dispatchers.IO).launch {
            launch { bluetoothSynchronizer.synchronize() }
            launch { signalController.listenPhaseSignal() }
            launch { signalController.listenTonicSignal() }
            launch { databaseControllerApi.controlResultTenMinute() }
            launch { databaseControllerApi.controlResultHour() }
            launch { databaseControllerApi.controlResultDay() }
            launch { databaseControllerApi.controlUserData() }
            launch { notificationController.control() }
            launch {
                val dbControlRequest =
                    PeriodicWorkRequestBuilder<FirebaseController>(1, TimeUnit.DAYS)
                        .addTag("db_control")
                        .setConstraints(
                            Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                .build()
                        )
                        .build()
                workManager.cancelAllWorkByTag("db_control")
                workManager.enqueue(dbControlRequest)
            }
            launch {
                while (true){
                    val device = settingApi.getDevice()
                    if(device != null && bluetoothConnectionApi.getConnectionState() == ConnectionState.DISCONNECTED){
                        bluetoothConnectionApi.connectionToPeripheral(device.mac)
                    }
                    delay(30000)
                }
            }
            launch {
                bluetoothConnectionApi.getConnectionStateFlow().collect{
                    if(it == ConnectionState.CONNECTED){
                        val notifyFlag =
                            when(screenControllerApi.getMainScreenStateFlow().first()){
                                ScreenState.START, ScreenState.RESUME -> true
                                else -> false
                            }
                        bluetoothWriterApi.writeNotifyFlag(notifyFlag)
                    }
                }
            }
            launch {
                screenControllerApi.getMainScreenStateFlow().collect{
                    val notifyFlag =
                        when(it){
                            ScreenState.START, ScreenState.RESUME -> true
                            else -> false
                        }
                    bluetoothWriterApi.writeNotifyFlag(notifyFlag)
                }
            }
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(FOREGROUND_SERVICE_ID, notification.getForegroundNotification())
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder {
        return binder
    }


    inner class LocalBinder : Binder() {
        fun getService(): StressAppService = this@StressAppService
    }
}
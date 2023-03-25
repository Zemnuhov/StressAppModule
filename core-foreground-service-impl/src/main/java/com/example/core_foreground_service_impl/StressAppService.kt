package com.example.core_foreground_service_impl

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.core_database_control_api.DatabaseControllerApi
import com.example.core_firebase_controller_impl.FirebaseController
import com.example.core_foreground_service_impl.di.ServiceComponent
import com.example.core_notification_controller_api.NotificationControllerApi
import com.example.core_signal_control_api.SignalControlApi
import com.example.feature_notification_api.NotificationApi
import com.neurotech.core_bluetooth_comunication_api.BluetoothSynchronizerApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class StressAppService: Service() {

    companion object{
        private const val FOREGROUND_SERVICE_ID = 1
    }

    @Inject
    lateinit var notification: NotificationApi

    @Inject
    lateinit var notificationController: NotificationControllerApi

    @Inject
    lateinit var signalController: SignalControlApi

    @Inject
    lateinit var databaseControllerApi: DatabaseControllerApi

    @Inject
    lateinit var workManager: WorkManager

    @Inject
    lateinit var bluetoothSynchronizer: BluetoothSynchronizerApi

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
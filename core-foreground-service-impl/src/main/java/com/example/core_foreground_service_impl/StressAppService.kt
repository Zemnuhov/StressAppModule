package com.example.core_foreground_service_impl

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.core_database_control_api.DatabaseControllerApi
import com.example.core_foreground_service_impl.di.ServiceComponent
import com.example.core_signal_control_api.SignalControlApi
import com.example.feature_notification_api.NotificationApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class StressAppService: Service() {

    companion object{
        private const val FOREGROUND_SERVICE_ID = 1
    }

    @Inject
    lateinit var notification: NotificationApi

    @Inject
    lateinit var signalController: SignalControlApi

    @Inject
    lateinit var databaseControllerApi: DatabaseControllerApi

    private val binder = LocalBinder()


    override fun onCreate() {
        super.onCreate()
        ServiceComponent.get().inject(this)
        startForeground(FOREGROUND_SERVICE_ID, notification.getForegroundNotification())
        CoroutineScope(Dispatchers.IO).launch {
            launch { signalController.listenPhaseSignal() }
            launch { signalController.listenTonicSignal() }
            launch { databaseControllerApi.controlResultTenMinute() }
            launch { databaseControllerApi.controlResultHour() }
            launch { databaseControllerApi.controlResultDay() }
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder {
        return binder
    }


    inner class LocalBinder : Binder() {
        fun getService(): StressAppService = this@StressAppService
    }
}
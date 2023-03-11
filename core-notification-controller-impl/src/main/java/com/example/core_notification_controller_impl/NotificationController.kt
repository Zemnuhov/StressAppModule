package com.example.core_notification_controller_impl

import android.util.Log
import com.example.core_notification_controller_api.NotificationControllerApi
import com.example.core_notification_controller_impl.di.NotificationControllerComponent
import com.example.feature_notification_api.NotificationApi
import com.neurotech.core_bluetooth_comunication_api.BluetoothConnectionApi
import com.neurotech.core_bluetooth_comunication_api.ConnectionState
import com.neurotech.core_database_api.ResultApi
import com.neurotech.core_database_api.UserApi
import com.neurotech.core_database_api.model.ResultTenMinute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NotificationController: NotificationControllerApi {

    @Inject
    lateinit var resultApi: ResultApi

    @Inject
    lateinit var bluetoothCommunication: BluetoothConnectionApi

    @Inject
    lateinit var notificationApi: NotificationApi

    @Inject
    lateinit var userApi: UserApi

    init {
        NotificationControllerComponent.get().inject(this)
    }

    override suspend fun control(){
        withContext(Dispatchers.IO){
            launch {
                var previousResult: ResultTenMinute? = null
                resultApi.getResultTenMinute().collect{
                    Log.e("NotificationController", it.toString())
                    if(it != null && previousResult?.time != it.time){
                        previousResult = it
                        if (it.peakCount > userApi.getUser().phaseNormal){
                            notificationApi.showStressExcessNotification(it)
                        }
                    }
                }
            }
            launch {
                bluetoothCommunication.getConnectionStateFlow().collect{
                    when(it){
                        ConnectionState.DISCONNECTED -> notificationApi.showDisconnectNotification()
                        ConnectionState.CONNECTED -> notificationApi.deleteDisconnectNotification()
                        else -> {}
                    }
                }
            }
        }
    }


}
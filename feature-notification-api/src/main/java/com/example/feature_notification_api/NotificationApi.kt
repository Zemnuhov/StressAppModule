package com.example.feature_notification_api

import android.app.Notification
import com.neurotech.core_database_api.model.ResultTenMinute

interface NotificationApi {
    suspend fun showDisconnectNotification()
    suspend fun deleteDisconnectNotification()
    suspend fun showStressExcessNotification(tenMinuteResult: ResultTenMinute)
    fun getForegroundNotification(): Notification
}
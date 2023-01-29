package com.example.feature_notification_api

import android.app.Notification

interface NotificationApi {
    suspend fun showDisconnectNotification()
    suspend fun showStressExcessNotification()
    suspend fun getForegroundNotification(): Notification
}
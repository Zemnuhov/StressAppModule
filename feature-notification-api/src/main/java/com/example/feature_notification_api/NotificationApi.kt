package com.example.feature_notification_api

import android.app.Notification

interface NotificationApi {
    suspend fun showDisconnectNotification()
    suspend fun showStressExcessNotification()
    fun getForegroundNotification(): Notification
}
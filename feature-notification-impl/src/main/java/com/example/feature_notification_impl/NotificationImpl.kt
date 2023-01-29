package com.example.feature_notification_impl

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.feature_notification_api.NotificationApi
import javax.inject.Inject


class NotificationImpl: NotificationApi {

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var activity: AppCompatActivity

    companion object {
        const val FOREGROUND_CHANNEL_ID = "foreground_channel"
        const val WARNING_CHANNEL_ID = "warning_channel"
    }

    private val titleNotification = "StressApp"
    private val foregroundContent = "Будьте спокойны..."
    private val warningContent = "Был обнаружен повышенный стресс"
    private val disconnectContent = "Произошёл разрыв с устройством"



    @SuppressLint("MissingPermission")
    override suspend fun showDisconnectNotification() {
        val pendingIntent: PendingIntent =
            Intent(context, activity.javaClass).let { notificationIntent ->
                PendingIntent.getActivity(
                    context.applicationContext,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            }

        val builder = NotificationCompat.Builder(context, WARNING_CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_stress)
            .setContentTitle(titleNotification)
            .setContentText(disconnectContent)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(WARNING_CHANNEL_ID, WARNING_CHANNEL_ID, WARNING_CHANNEL_ID)
        }

        with(NotificationManagerCompat.from(context)) {
            cancel(4)
            notify(4, builder.build())
        }

    }

    override suspend fun showStressExcessNotification() {
        TODO("Not yet implemented")
    }

    override suspend fun getForegroundNotification(): Notification {
        val pendingIntent: PendingIntent =
            Intent(context, activity.javaClass).let { notificationIntent ->
                PendingIntent.getActivity(
                    context.applicationContext,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            }

        val notification: Notification =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Notification.Builder(context.applicationContext, FOREGROUND_CHANNEL_ID)
                    .setContentTitle(titleNotification)
                    .setContentText(foregroundContent)
                    .setSmallIcon(R.drawable.icon_stress)
                    .setContentIntent(pendingIntent)
                    .setTicker(titleNotification)
                    .build()
            } else {
                NotificationCompat.Builder(context)
                    .setContentTitle(titleNotification)
                    .setContentText(foregroundContent)
                    .setSmallIcon(R.drawable.icon_stress)
                    .setContentIntent(pendingIntent)
                    .setTicker(titleNotification)
                    .build()
            }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(
                FOREGROUND_CHANNEL_ID,
                "ForegroundChannel",
                "ForegroundService"
            )
        }

        return notification
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(
        channelId: String,
        name: String,
        descriptionText: String
    ) {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
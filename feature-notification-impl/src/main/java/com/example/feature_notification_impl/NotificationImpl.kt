package com.example.feature_notification_impl

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.cesarferreira.tempo.toString
import com.example.feature_notification_api.NotificationApi
import com.example.feature_notification_impl.di.NotificationComponent
import com.neurotech.core_database_api.ResultApi
import com.neurotech.core_database_api.SettingApi
import com.neurotech.core_database_api.model.Cause
import com.neurotech.core_database_api.model.ResultTenMinute
import com.neurotech.utils.TimeFormat
import javax.inject.Inject


class NotificationImpl: NotificationApi {

    @Inject
    lateinit var activity: Class<Any>

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var settingApi: SettingApi

    @Inject
    lateinit var resultApi: ResultApi



    companion object {
        const val FOREGROUND_CHANNEL_ID = "foreground_channel"
        const val WARNING_CHANNEL_ID = "warning_channel"
    }

    private val titleNotification = "StressApp"
    private val foregroundContent = "Будьте спокойны..."
    private val warningContent = "Был обнаружен повышенный стресс"
    private val disconnectContent = "Произошёл разрыв с устройством"

    init {
        NotificationComponent.get().inject(this)
    }



    @SuppressLint("MissingPermission")
    override suspend fun showDisconnectNotification() {
        val pendingIntent: PendingIntent =
            Intent(context, activity).let { notificationIntent ->
                PendingIntent.getActivity(
                    context,
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

    override suspend fun deleteDisconnectNotification() {
        with(NotificationManagerCompat.from(context)) {
            cancel(4)
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun showStressExcessNotification(tenMinuteResult: ResultTenMinute) {
        val pendingIntent: PendingIntent =
            Intent(context, activity).let { notificationIntent ->
                PendingIntent.getActivity(
                    context,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
        val builder = NotificationCompat.Builder(context, WARNING_CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_stress)
            .setContentTitle(titleNotification)
            .setContentText(warningContent)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        addMarkupAction(builder, tenMinuteResult)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(WARNING_CHANNEL_ID, WARNING_CHANNEL_ID, WARNING_CHANNEL_ID)
        }

        with(NotificationManagerCompat.from(context)) {
            cancel(3)
            notify(3, builder.build())
        }
    }

    private suspend fun addMarkupAction(builder: NotificationCompat.Builder, tenMinuteResult: ResultTenMinute){
        val dayPlan = settingApi.getDayPlanByTime(tenMinuteResult.time.toString(TimeFormat.timePattern))
        if(dayPlan != null){
            if(dayPlan.firstSource != null && dayPlan.autoMarkup){
                resultApi.setCauseByTime(Cause(dayPlan.firstSource!!), tenMinuteResult.time)
            }
            if(dayPlan.firstSource != null && !dayPlan.autoMarkup){
                val firstCausePendingIntent: PendingIntent =
                    Intent(context, NotificationReceiver::class.java).let {
                        it.action = NotificationReceiver.FIRST_SOURCE_ACTION
                        it.putExtra(NotificationReceiver.SOURCE_EXTRA, dayPlan.firstSource)
                        it.putExtra(
                            NotificationReceiver.TIME_EXTRA,
                            tenMinuteResult.time.toString(TimeFormat.dateTimeIsoPattern)
                        )
                        PendingIntent.getBroadcast(
                            context,
                            0,
                            it,
                            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                        )
                    }
                builder.addAction(R.drawable.icon_stress, dayPlan.firstSource, firstCausePendingIntent)
            }
            if(dayPlan.secondSource != null && !dayPlan.autoMarkup){
                val secondSourcePendingIntent: PendingIntent =
                    Intent(context, NotificationReceiver::class.java).let {
                        it.action = NotificationReceiver.SECOND_SOURCE_ACTION
                        it.putExtra(NotificationReceiver.SOURCE_EXTRA, dayPlan.secondSource)
                        it.putExtra(
                            NotificationReceiver.TIME_EXTRA,
                            tenMinuteResult.time.toString(TimeFormat.dateTimeIsoPattern)
                        )
                        PendingIntent.getBroadcast(
                            context,
                            0,
                            it,
                            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                        )
                    }
                builder.addAction(R.drawable.icon_stress, dayPlan.secondSource, secondSourcePendingIntent)
            }
        }
    }

    override fun getForegroundNotification(): Notification {
        val pendingIntent: PendingIntent =
            Intent(context, activity).let { notificationIntent ->
                PendingIntent.getActivity(
                    context,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            }

        val notification: Notification =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Notification.Builder(context, FOREGROUND_CHANNEL_ID)
                    .setContentTitle(titleNotification)
                    .setContentText(foregroundContent)
                    .setSmallIcon(R.drawable.icon_stress)
                    .setContentIntent(pendingIntent)
                    .setTicker(titleNotification)
                    .setOngoing(true)
                    .build()
            } else {
                NotificationCompat.Builder(context)
                    .setContentTitle(titleNotification)
                    .setContentText(foregroundContent)
                    .setSmallIcon(R.drawable.icon_stress)
                    .setContentIntent(pendingIntent)
                    .setTicker(titleNotification)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
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
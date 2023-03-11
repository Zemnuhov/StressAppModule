package com.example.feature_notification_impl

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.cesarferreira.tempo.toDate
import com.example.feature_notification_impl.di.NotificationComponent
import com.neurotech.core_database_api.ResultApi
import com.neurotech.core_database_api.model.Cause
import com.neurotech.utils.StressLogger.log
import com.neurotech.utils.TimeFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class NotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var resultApi: ResultApi

    companion object {
        const val SOURCE_EXTRA = "com.neurotech.stressapp.notification.SOURCE_EXTRA"
        const val TIME_EXTRA = "com.neurotech.stressapp.notification.TIME_EXTRA"
        const val FIRST_SOURCE_ACTION = "com.neurotech.stressapp.notification.FIRST_SOURCE_ACTION"
        const val SECOND_SOURCE_ACTION = "com.neurotech.stressapp.notification.SECOND_SOURCE_ACTION"
    }


    override fun onReceive(context: Context, intent: Intent) {
        NotificationComponent.get().inject(this)
        if (intent.action in listOf(FIRST_SOURCE_ACTION, SECOND_SOURCE_ACTION)) {
            CoroutineScope(Dispatchers.IO).launch {
                val causeName = intent.getStringExtra(SOURCE_EXTRA)
                val time = intent.getStringExtra(TIME_EXTRA)
                log("From notification markup. Cause: $causeName Time: $time")
                if (causeName != null && time != null) {
                    resultApi.setCauseByTime(Cause(causeName), time.toDate(TimeFormat.dateTimeIsoPattern))
                }
            }
        }
        NotificationManagerCompat.from(context).apply {
            cancel(3)
        }
    }
}
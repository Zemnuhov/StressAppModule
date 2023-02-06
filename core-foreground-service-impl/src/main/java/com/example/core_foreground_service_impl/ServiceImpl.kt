package com.example.core_foreground_service_impl

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.example.core_foreground_service_api.ServiceApi
import com.example.core_foreground_service_impl.di.ServiceComponent
import javax.inject.Inject

class ServiceImpl: ServiceApi {

    @Inject
    lateinit var context: Context

    private var stressAppService: StressAppService? = null
    private var boundState = false

    init {
        ServiceComponent.get().inject(this)
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as StressAppService.LocalBinder
            stressAppService = binder.getService()
            boundState = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            boundState = false
        }

    }

    override fun bindService() {
        Intent(context, StressAppService::class.java).also { intent ->
            context.bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun unbindService() {
        context.unbindService(serviceConnection)
        boundState = false
    }
}
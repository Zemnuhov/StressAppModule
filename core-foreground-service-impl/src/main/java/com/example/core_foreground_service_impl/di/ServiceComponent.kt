package com.example.core_foreground_service_impl.di

import android.content.Context
import androidx.work.WorkManager
import com.example.core_database_control_api.DatabaseControllerApi
import com.example.core_foreground_service_impl.ServiceImpl
import com.example.core_foreground_service_impl.StressAppService
import com.example.core_notification_controller_api.NotificationControllerApi
import com.example.core_signal_control_api.SignalControlApi
import com.example.feature_notification_api.NotificationApi
import com.neurotech.core_bluetooth_comunication_api.BluetoothSynchronizerApi
import dagger.Component
import dagger.Component.Builder
import javax.inject.Scope
import javax.inject.Singleton
import kotlin.properties.Delegates.notNull

@Component(dependencies = [ServiceDependencies::class])
@Singleton
internal interface ServiceComponent {

    fun inject(stressAppService: StressAppService)
    fun inject(stressAppService: ServiceImpl)

    @Builder
    interface ServiceComponentBuilder{
        fun provideDependencies(dependencies: ServiceDependencies): ServiceComponentBuilder
        fun build(): ServiceComponent
    }

    companion object{
        private var component: ServiceComponent? = null

        fun get(): ServiceComponent{
            if(component == null){
                component = DaggerServiceComponent
                    .builder()
                    .provideDependencies(ServiceDependenciesProvider.dependencies)
                    .build()
            }
            return component!!
        }
    }

}

interface ServiceDependencies{
    val context: Context
    val notificationApi: NotificationApi
    val signalController: SignalControlApi
    val databaseController: DatabaseControllerApi
    val workManager: WorkManager
    val bluetoothSynchronizer: BluetoothSynchronizerApi
    val notificationController: NotificationControllerApi
}

interface ServiceDependenciesProvider{
    val dependencies: ServiceDependencies
    companion object: ServiceDependenciesProvider by ServiceDependenciesStore
}

object ServiceDependenciesStore: ServiceDependenciesProvider{
    override var dependencies: ServiceDependencies by notNull()
}

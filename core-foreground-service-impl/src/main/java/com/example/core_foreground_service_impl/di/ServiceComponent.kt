package com.example.core_foreground_service_impl.di

import android.content.Context
import androidx.work.WorkManager
import com.example.core_database_control_api.DatabaseControllerApi
import com.example.core_foreground_service_impl.ServiceImpl
import com.example.core_foreground_service_impl.StressAppService
import com.example.core_signal_control_api.SignalControlApi
import com.example.feature_notification_api.NotificationApi
import dagger.Component
import dagger.Component.Builder
import javax.inject.Scope
import kotlin.properties.Delegates.notNull

@Component(dependencies = [ServiceDependencies::class])
@ServiceScope
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

        fun clear(){
            component = null
        }
    }

}

interface ServiceDependencies{
    val context: Context
    val notificationApi: NotificationApi
    val signalController: SignalControlApi
    val databaseController: DatabaseControllerApi
    val workManager: WorkManager
}

interface ServiceDependenciesProvider{
    val dependencies: ServiceDependencies
    companion object: ServiceDependenciesProvider by ServiceDependenciesStore
}

object ServiceDependenciesStore: ServiceDependenciesProvider{
    override var dependencies: ServiceDependencies by notNull()
}

@Scope
annotation class ServiceScope
package com.example.core_notification_controller_impl.di

import com.example.core_notification_controller_impl.NotificationController
import com.example.feature_notification_api.NotificationApi
import com.neurotech.core_bluetooth_comunication_api.BluetoothConnectionApi
import com.neurotech.core_database_api.ResultApi
import com.neurotech.core_database_api.UserApi
import dagger.Component
import dagger.Component.Builder
import javax.inject.Scope
import kotlin.properties.Delegates.notNull

@Component(dependencies = [NotificationControllerDependencies::class])
@NotificationControllerScope
internal interface NotificationControllerComponent {
    fun inject(notificationController: NotificationController)

    @Builder
    interface ComponentBuilder{
        fun provideDependencies(dependencies: NotificationControllerDependencies): ComponentBuilder
        fun build(): NotificationControllerComponent
    }

    companion object{
        private var component: NotificationControllerComponent? = null

        fun get(): NotificationControllerComponent{
            if(component == null){
                component = DaggerNotificationControllerComponent
                    .builder()
                    .provideDependencies(NotificationControllerDependenciesProvider.dependencies)
                    .build()
            }
            return component!!
        }
    }
}

interface NotificationControllerDependencies{
    val resultApi: ResultApi
    val bluetoothCommunication: BluetoothConnectionApi
    val notificationApi: NotificationApi
    val userApi: UserApi
}

interface NotificationControllerDependenciesProvider{
    val dependencies: NotificationControllerDependencies
    companion object: NotificationControllerDependenciesProvider by NotificationControllerDependenciesStore
}

object NotificationControllerDependenciesStore: NotificationControllerDependenciesProvider{
    override var dependencies: NotificationControllerDependencies by notNull()
}

@Scope
annotation class NotificationControllerScope




package com.example.feature_notification_impl.di

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.feature_notification_impl.NotificationImpl
import dagger.Component
import dagger.Component.Builder
import javax.inject.Scope
import kotlin.properties.Delegates.notNull

@Component(dependencies = [NotificationDependencies::class])
@NotificationScope
interface NotificationComponent {
    fun inject(notificationImpl: NotificationImpl)
    @Builder
    interface NotificationComponentBuilder{
        fun provideDependencies(dependencies: NotificationDependencies): NotificationComponentBuilder
        fun build(): NotificationComponent
    }

    companion object{
        private var component: NotificationComponent? = null

        fun get(): NotificationComponent{
            if(component == null){
                component = DaggerNotificationComponent
                    .builder()
                    .provideDependencies(NotificationDependenciesProvider.dependencies)
                    .build()
            }
            return component!!
        }

        fun clear(){
            component = null
        }
    }
}

interface NotificationDependencies{
    val context: Context
    val activity: AppCompatActivity
}

interface NotificationDependenciesProvider{
    val dependencies :NotificationDependencies
    companion object: NotificationDependenciesProvider by NotificationDependenciesStore
}

object NotificationDependenciesStore: NotificationDependenciesProvider{
    override var dependencies: NotificationDependencies by notNull()
}

@Scope
annotation class NotificationScope
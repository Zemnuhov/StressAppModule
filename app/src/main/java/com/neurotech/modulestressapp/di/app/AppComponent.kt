package com.neurotech.modulestressapp.di.app

import android.app.Application
import android.util.Log
import com.example.core_database_control_impl.di.DatabaseControlDependencies
import com.example.core_database_control_impl.di.DatabaseControlDependenciesStore
import com.example.core_firebase_controller_impl.di.FirebaseControllerDependencies
import com.example.core_firebase_controller_impl.di.FirebaseControllerDependenciesStore
import com.example.core_foreground_service_impl.di.ServiceDependencies
import com.example.core_foreground_service_impl.di.ServiceDependenciesStore
import com.example.core_notification_controller_impl.di.NotificationControllerDependencies
import com.example.core_notification_controller_impl.di.NotificationControllerDependenciesStore
import com.example.core_signal_control_impl.SignalControlDependencies
import com.example.core_signal_control_impl.SignalControlDependenciesStore
import com.example.feature_notification_impl.di.NotificationDependencies
import com.example.feature_notification_impl.di.NotificationDependenciesStore
import com.neurotech.core_bluetooth_comunication_impl.di.BleCommunicationDependencies
import com.neurotech.core_bluetooth_comunication_impl.di.BleCommunicationDependenciesStore
import com.neurotech.core_database_impl.di.DatabaseComponentDependenciesStore
import com.neurotech.core_database_impl.di.DatabaseDependencies
import com.neurotech.modulestressapp.MainActivity
import dagger.BindsInstance
import dagger.Component
import dagger.Component.Builder
import javax.inject.Singleton

@Component(modules = [AppModule::class, BluetoothModule::class, DatabaseModule::class,CoreModule::class])
@Singleton
interface AppComponent:
    BleCommunicationDependencies,
    NotificationDependencies,
    ServiceDependencies,
    SignalControlDependencies,
    DatabaseControlDependencies,
    DatabaseDependencies,
    FirebaseControllerDependencies,
    NotificationControllerDependencies
{

    fun inject(mainActivity: MainActivity)

    @Builder
    interface AppBuilder{
        @BindsInstance
        fun application(application: Application): AppBuilder
        fun build(): AppComponent
    }

    companion object{
        private var component: AppComponent? = null

        fun componentIsInit() = component != null

        fun init(application: Application){
            component = DaggerAppComponent.builder().application(application).build()
        }

        fun get(): AppComponent = checkNotNull(component) {
            Log.e(
                "AppComponent",
                "Component is null"
            )
        }

        fun provideDependencies(){
            BleCommunicationDependenciesStore.dependencies = get()
            NotificationDependenciesStore.dependencies = component!!
            ServiceDependenciesStore.dependencies = component!!
            SignalControlDependenciesStore.dependencies = component!!
            DatabaseControlDependenciesStore.dependencies = component!!
            DatabaseComponentDependenciesStore.dependencies = component!!
            FirebaseControllerDependenciesStore.dependencies = component!!
            NotificationControllerDependenciesStore.dependencies = component!!
        }
    }


}



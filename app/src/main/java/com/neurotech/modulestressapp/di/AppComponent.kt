package com.neurotech.modulestressapp.di

import android.app.Application
import android.content.Context
import android.util.Log
import com.neurotech.core_bluetooth_comunication_impl.di.BleCommunicationDependencies
import com.neurotech.core_bluetooth_comunication_impl.di.BleCommunicationDependenciesStore
import com.neurotech.core_database_impl.di.DatabaseComponentDependenciesStore
import com.neurotech.core_database_impl.di.DatabaseDependencies
import com.neurotech.modulestressapp.MainActivity
import dagger.BindsInstance
import dagger.Component
import dagger.Component.Builder
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Component(modules = [AppModule::class, CoreModule::class])
@Singleton
interface AppComponent:
    BleCommunicationDependencies,
    DatabaseDependencies{

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
            DatabaseComponentDependenciesStore.dependencies = get()
        }
    }
}

@Module
class AppModule{

    @Provides
    @Singleton
    fun provideContext(application: Application): Context{
        return application.applicationContext
    }
}

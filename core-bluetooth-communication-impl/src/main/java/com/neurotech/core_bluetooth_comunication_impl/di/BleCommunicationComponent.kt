package com.neurotech.core_bluetooth_comunication_impl.di

import android.content.Context
import com.neurotech.core_bluetooth_comunication_impl.implementation.BluetoothConnection
import com.neurotech.core_bluetooth_comunication_impl.implementation.BluetoothData
import com.neurotech.core_bluetooth_comunication_impl.implementation.BluetoothWriter

import dagger.Component
import dagger.Component.Builder
import javax.inject.Scope
import kotlin.properties.Delegates.notNull

@Component(dependencies = [BleCommunicationDependencies::class], modules = [BleCommunicationModule::class])
@BleCommunicationScope
internal interface BleCommunicationComponent {
    fun inject(bluetoothConnection: BluetoothConnection)
    fun inject(bluetoothConnection: BluetoothData)
    fun inject(bluetoothWriter: BluetoothWriter)

    @Builder
    @BleCommunicationScope
    interface BleCommunicationBuilder{
        fun provideDependencies(dependencies: BleCommunicationDependencies): BleCommunicationBuilder
        fun build(): BleCommunicationComponent
    }
    companion object{
        private var component: BleCommunicationComponent? = null

        private fun init(){
            component = DaggerBleCommunicationComponent.builder()
                .provideDependencies(BleCommunicationDependenciesProvider.dependencies)
                .build()
        }

        fun get(): BleCommunicationComponent {
            if(component == null) init()
            return component!!
        }

        fun clear(){
            component = null
        }
    }
}

interface  BleCommunicationDependencies{
    val context: Context
}

internal interface BleCommunicationDependenciesProvider {
    val dependencies: BleCommunicationDependencies
    companion object : BleCommunicationDependenciesProvider by BleCommunicationDependenciesStore
}

object BleCommunicationDependenciesStore : BleCommunicationDependenciesProvider {
    override var dependencies: BleCommunicationDependencies by notNull()
}

@Scope
annotation class BleCommunicationScope
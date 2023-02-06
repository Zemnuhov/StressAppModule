package com.example.core_signal_control_impl

import com.neurotech.core_bluetooth_comunication_api.BluetoothDataApi
import com.neurotech.core_database_api.PhaseApi
import com.neurotech.core_database_api.TonicApi
import dagger.Component
import dagger.Component.Builder
import javax.inject.Scope
import kotlin.properties.Delegates.notNull

@Component(dependencies = [SignalControlDependencies::class])
@SignalControlScope
internal interface SignalControlComponent {
    fun inject(signalControlImpl: SignalControlImpl)

    @Builder
    interface SignalControlComponentBuilder{
        fun provideDependencies(dependencies: SignalControlDependencies): SignalControlComponentBuilder
        fun build(): SignalControlComponent
    }

    companion object{
        private var component: SignalControlComponent? = null

        fun get(): SignalControlComponent{
            if(component == null){
                component = DaggerSignalControlComponent
                    .builder()
                    .provideDependencies(SignalControlDependenciesProvider.dependencies)
                    .build()
            }
            return component!!
        }

        fun clear(){
            component = null
        }
    }

}

interface SignalControlDependencies{
    val phaseApi: PhaseApi
    val tonicApi: TonicApi
    val bluetoothData: BluetoothDataApi
}

internal interface SignalControlDependenciesProvider{
    val dependencies: SignalControlDependencies
    companion object: SignalControlDependenciesProvider by SignalControlDependenciesStore
}

object SignalControlDependenciesStore: SignalControlDependenciesProvider{
    override var dependencies: SignalControlDependencies by notNull()
}

@Scope
annotation class SignalControlScope
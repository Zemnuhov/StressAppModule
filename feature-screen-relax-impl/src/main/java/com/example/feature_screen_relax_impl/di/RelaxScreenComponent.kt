package com.example.feature_screen_relax_impl.di

import com.example.core_firebase_database_api.FirebaseDataApi
import com.example.feature_item_graph_api.ItemGraphApi
import com.example.feature_screen_relax_impl.RelaxFragment
import com.neurotech.core_bluetooth_comunication_api.BluetoothDataApi
import com.neurotech.core_database_api.PhaseApi
import com.neurotech.core_database_api.RelaxRecordApi
import dagger.Component
import dagger.Component.Builder
import javax.inject.Scope
import kotlin.properties.Delegates.notNull

@Component(dependencies = [RelaxDependencies::class])
@RelaxScreenScope
interface RelaxScreenComponent {
    fun inject(relaxFragment: RelaxFragment)

    @Builder
    interface RelaxScreenComponentBuilder{
        fun provideDependencies(dependencies: RelaxDependencies): RelaxScreenComponentBuilder
        fun build(): RelaxScreenComponent
    }

    companion object{
        private var component: RelaxScreenComponent? = null

        fun get(): RelaxScreenComponent{
            if(component == null){
                component = DaggerRelaxScreenComponent
                    .builder()
                    .provideDependencies(RelaxDependenciesProvider.dependencies)
                    .build()
            }
            return component!!
        }
    }
}

interface RelaxDependencies{
    val bluetoothDataApi: BluetoothDataApi
    val phaseApi: PhaseApi
    val itemGraphApi: ItemGraphApi
    val relaxRecordApi: RelaxRecordApi
    val firebaseDatabaseApi: FirebaseDataApi
}

interface RelaxDependenciesProvider{
    val dependencies: RelaxDependencies
    companion object: RelaxDependenciesProvider by RelaxDependenciesStore
}

object RelaxDependenciesStore: RelaxDependenciesProvider{
    override var dependencies: RelaxDependencies by notNull()
}

@Scope
annotation class RelaxScreenScope
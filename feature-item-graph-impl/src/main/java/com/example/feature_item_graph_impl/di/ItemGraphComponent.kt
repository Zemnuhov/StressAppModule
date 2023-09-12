package com.example.feature_item_graph_impl

import com.neurotech.core_bluetooth_comunication_api.BluetoothDataApi
import dagger.Component
import dagger.Component.Builder
import javax.inject.Scope
import kotlin.properties.Delegates.notNull

@Component(dependencies = [ItemGraphDependencies::class])
@ItemGraphScope
internal interface ItemGraphComponent {
    fun inject(phaseGraphFragment: PhaseGraphFragment)
    fun inject(itemGraph: ItemGraph)

    @Builder
    interface ItemGraphBuilder{
        fun provideDependencies(dependencies: ItemGraphDependencies): ItemGraphBuilder
        fun build(): ItemGraphComponent
    }

    companion object{
        private var component: ItemGraphComponent? = null

        fun get(): ItemGraphComponent{
            if(component == null){
                component = DaggerItemGraphComponent
                    .builder()
                    .provideDependencies(ItemGraphDependenciesProvider.dependencies)
                    .build()
            }
            return component!!
        }

        fun clear(){
            component = null
        }

    }
}

interface ItemGraphDependencies{
    val bluetoothDataApi: BluetoothDataApi
}

interface ItemGraphDependenciesProvider{
    val dependencies: ItemGraphDependencies
    companion object: ItemGraphDependenciesProvider by ItemGraphDependenciesStore
}

object ItemGraphDependenciesStore: ItemGraphDependenciesProvider{
    override var dependencies: ItemGraphDependencies by notNull()
}

@Scope
annotation class ItemGraphScope


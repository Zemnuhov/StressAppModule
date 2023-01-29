package com.neurotech.feature_tonic_info_impl.di

import com.neurotech.core_bluetooth_comunication_api.BluetoothDataApi
import com.neurotech.core_database_api.TonicApi
import com.neurotech.feature_tonic_info_impl.TonicFragment
import dagger.Component
import dagger.Component.Builder
import javax.inject.Scope
import kotlin.properties.Delegates.notNull

@Component(dependencies = [ItemTonicDependencies::class])
@TonicInfoScope
internal interface TonicInfoComponent {

    fun inject(tonicFragment: TonicFragment)

    @Builder
    interface TonicInfoBuilder{
        fun provideDependencies(dependencies: ItemTonicDependencies): TonicInfoBuilder
        fun build(): TonicInfoComponent
    }

    companion object{
        private var component: TonicInfoComponent? = null

        fun get(): TonicInfoComponent{
            if(component == null){
                component = DaggerTonicInfoComponent
                    .builder()
                    .provideDependencies(TonicInfoDependenciesProvider.dependencies)
                    .build()
            }
            return component!!
        }

        fun clear(){
            component = null
        }
    }
}

interface ItemTonicDependencies{
    val bluetoothDataApi: BluetoothDataApi
    val tonicApi: TonicApi
}

interface TonicInfoDependenciesProvider{
    val dependencies: ItemTonicDependencies
    companion object: TonicInfoDependenciesProvider by TonicInfoDependenciesStore
}

object TonicInfoDependenciesStore: TonicInfoDependenciesProvider{
    override var dependencies: ItemTonicDependencies by notNull()
}

@Scope
annotation class TonicInfoScope


package com.example.feature_main_screen_impl.di

import com.example.feature_item_graph_api.ItemGraphApi
import com.example.feature_item_markup_api.ItemMarkupApi
import com.example.feature_main_screen_impl.MainFragment
import com.example.feature_phase_info_api.ItemPhaseApi
import com.example.navigation_api.NavigationApi
import com.neurotech.core_bluetooth_comunication_api.BluetoothConnectionApi
import com.neurotech.core_bluetooth_comunication_api.BluetoothWriterApi
import com.neurotech.core_database_api.SettingApi
import com.neurotech.feature_tonic_info_api.ItemTonicApi
import dagger.Component
import dagger.Component.Builder
import javax.inject.Scope
import kotlin.properties.Delegates.notNull

@Component(dependencies = [MainScreenDependencies::class])
@MainScreenScope
internal interface MainScreenComponent {
    fun inject(mainFragment: MainFragment)
    @Builder
    interface MainScreenBuilder{
        fun provideDependencies(dependencies: MainScreenDependencies): MainScreenBuilder
        fun build(): MainScreenComponent
    }

    companion object{
        private var component: MainScreenComponent? = null
        fun get(): MainScreenComponent{
            if(component == null){
                component = DaggerMainScreenComponent
                    .builder()
                    .provideDependencies(MainScreenDependenciesProvider.dependencies)
                    .build()
            }
            return component!!
        }

        fun clear(){
            component = null
        }
    }
}

interface MainScreenDependencies{
    val bluetoothConnection: BluetoothConnectionApi
    val bluetoothWriter: BluetoothWriterApi
    val setting: SettingApi
    val tonicInfo: ItemTonicApi
    val phaseInfo: ItemPhaseApi
    val itemGraph: ItemGraphApi
    val itemMarkup: ItemMarkupApi
    val navigationApi: NavigationApi
}

interface MainScreenDependenciesProvider{
    val dependencies: MainScreenDependencies
    companion object: MainScreenDependenciesProvider by MainScreenDependenciesStore
}

object MainScreenDependenciesStore: MainScreenDependenciesProvider{
    override var dependencies: MainScreenDependencies by notNull()
}

@Scope
annotation class MainScreenScope
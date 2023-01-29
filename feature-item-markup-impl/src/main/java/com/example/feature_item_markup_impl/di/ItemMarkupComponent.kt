package com.example.feature_item_markup_impl.di

import com.example.feature_item_markup_impl.ItemMarkupFragment
import com.example.navigation_api.NavigationApi
import com.neurotech.core_database_api.ResultApi
import com.neurotech.core_database_api.SettingApi
import dagger.Component
import dagger.Component.Builder
import kotlin.properties.Delegates.notNull

@Component(dependencies = [ItemMarkupDependencies::class])
interface ItemMarkupComponent {
    fun inject(itemMarkupFragment: ItemMarkupFragment)

    @Builder
    interface ItemMarkupBuilder{
        fun provideDependencies(dependencies: ItemMarkupDependencies): ItemMarkupBuilder
        fun build(): ItemMarkupComponent
    }

    companion object{
        private var component: ItemMarkupComponent? = null

        fun get(): ItemMarkupComponent{
            if(component == null){
                component = DaggerItemMarkupComponent
                    .builder()
                    .provideDependencies(ItemMarkupDependenciesProvider.dependencies)
                    .build()
            }
            return component!!
        }

        fun clear(){
            component = null
        }
    }
}

interface ItemMarkupDependencies{
    val resultTenMinuteApi: ResultApi
    val settingApi: SettingApi
    val navigationApi: NavigationApi
}

interface ItemMarkupDependenciesProvider{
    val dependencies: ItemMarkupDependencies
    companion object: ItemMarkupDependenciesProvider by ItemMarkupDependenciesStore
}

object ItemMarkupDependenciesStore: ItemMarkupDependenciesProvider{
    override var dependencies: ItemMarkupDependencies by notNull()
}
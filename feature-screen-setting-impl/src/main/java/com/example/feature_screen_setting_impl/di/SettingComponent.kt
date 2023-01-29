package com.example.feature_screen_setting_impl.di

import com.example.feature_screen_setting_impl.SettingFragment
import com.example.navigation_api.NavigationApi
import com.neurotech.core_database_api.SettingApi
import dagger.Component
import dagger.Component.Builder
import javax.inject.Scope
import kotlin.properties.Delegates.notNull

@Component(dependencies = [SettingDependencies::class])
@SettingScreenScope
interface SettingComponent {
    fun inject(settingFragment: SettingFragment)

    @Builder
    interface SettingsComponentBuilder {
        fun provideDependencies(dependencies: SettingDependencies): SettingsComponentBuilder
        fun build(): SettingComponent
    }

    companion object{
        private var component: SettingComponent? = null
        fun get(): SettingComponent{
            if(component == null){
                component = DaggerSettingComponent
                    .builder()
                    .provideDependencies(SettingDependenciesProvider.dependencies)
                    .build()
            }
            return component!!
        }

        fun clear(){
            component = null
        }
    }
}

interface SettingDependencies {
    val settingApi: SettingApi
    val navigationApi: NavigationApi
}

interface SettingDependenciesProvider {
    val dependencies: SettingDependencies

    companion object : SettingDependenciesProvider by SettingDependenciesStore
}

object SettingDependenciesStore : SettingDependenciesProvider {
    override var dependencies: SettingDependencies by notNull()
}

@Scope
annotation class SettingScreenScope
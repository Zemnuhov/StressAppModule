package com.example.feature_screen_analitic_impl.di

import com.example.feature_screen_analitic_impl.AnalyticFragment
import com.neurotech.core_database_api.RelaxRecordApi
import com.neurotech.core_database_api.ResultApi
import com.neurotech.core_database_api.SettingApi
import com.neurotech.core_database_api.UserApi
import dagger.Component
import dagger.Component.Builder
import javax.inject.Scope
import kotlin.properties.Delegates.notNull

@Component(dependencies = [AnalyticDependencies::class])
@AnalyticScope
internal interface AnalyticComponent {
    fun inject(analyticFragment: AnalyticFragment)

    @Builder
    interface AnalyticComponentBuilder{
        fun provideDependencies(dependencies: AnalyticDependencies): AnalyticComponentBuilder
        fun build(): AnalyticComponent
    }

    companion object{
        private var component: AnalyticComponent? = null

        fun get():AnalyticComponent{
            if(component == null){
                component = DaggerAnalyticComponent
                    .builder()
                    .provideDependencies(AnalyticDependenciesProvider.dependencies)
                    .build()
            }
            return component!!
        }

        fun clear(){
            component = null
        }
    }
}

interface AnalyticDependencies{
    val resultApi: ResultApi
    val userApi :UserApi
    val settingApi :SettingApi
    val relaxRecordApi: RelaxRecordApi
}

interface AnalyticDependenciesProvider{
    val dependencies: AnalyticDependencies
    companion object: AnalyticDependenciesProvider by AnalyticDependenciesStore
}

object AnalyticDependenciesStore: AnalyticDependenciesProvider{
    override var dependencies: AnalyticDependencies by notNull()
}

@Scope
annotation class AnalyticScope
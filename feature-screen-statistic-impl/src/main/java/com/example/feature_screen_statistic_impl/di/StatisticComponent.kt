package com.example.feature_screen_statistic_impl.di

import com.example.feature_item_markup_api.ItemMarkupApi
import com.example.feature_screen_statistic_impl.StatisticFragment
import com.neurotech.core_database_api.RelaxRecordApi
import com.neurotech.core_database_api.ResultApi
import com.neurotech.core_database_api.UserApi
import dagger.Component
import dagger.Component.Builder
import javax.inject.Scope
import kotlin.properties.Delegates.notNull

@Component(dependencies = [StatisticDependencies::class])
@StatisticScreenScope
interface StatisticComponent {
    fun inject(statisticFragment: StatisticFragment)

    @Builder
    interface StatisticComponentBuilder{
        fun provideDependencies(statisticDependencies: StatisticDependencies): StatisticComponentBuilder
        fun build(): StatisticComponent
    }

    companion object{
        private var component: StatisticComponent? = null

        fun get(): StatisticComponent {
            if(component == null){
                component = DaggerStatisticComponent
                    .builder()
                    .provideDependencies(StatisticDependenciesProvider.dependencies)
                    .build()
            }
            return component!!
        }

        fun clear(){
            component = null
        }
    }

}

interface StatisticDependencies{
    val resultApi: ResultApi
    val userApi: UserApi
    val itemMarkupApi: ItemMarkupApi
    val relaxRecordApi: RelaxRecordApi
}

interface StatisticDependenciesProvider{
    val dependencies: StatisticDependencies
    companion object: StatisticDependenciesProvider by StatisticDependenciesStore
}

object StatisticDependenciesStore: StatisticDependenciesProvider{
    override var dependencies: StatisticDependencies by notNull()
}

@Scope
annotation class StatisticScreenScope
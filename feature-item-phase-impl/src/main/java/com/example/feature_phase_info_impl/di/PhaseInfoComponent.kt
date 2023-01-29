package com.example.feature_phase_info_impl.di

import com.example.feature_phase_info_impl.PhaseInfoFragment
import com.neurotech.core_database_api.PhaseApi
import com.neurotech.core_database_api.ResultApi
import com.neurotech.core_database_api.UserApi
import dagger.Component
import dagger.Component.Builder
import javax.inject.Scope
import kotlin.properties.Delegates.notNull

@Component(dependencies = [ItemPhaseDependencies::class])
@PhaseInfoScope
internal interface PhaseInfoComponent {
    fun inject(phaseInfoFragment: PhaseInfoFragment)

    @Builder
    interface PhaseInfoBuilder{
        fun provideDependencies(dependencies: ItemPhaseDependencies): PhaseInfoBuilder
        fun build(): PhaseInfoComponent
    }

    companion object{
        var component: PhaseInfoComponent? = null
        fun get(): PhaseInfoComponent{
            if(component == null){
                component = DaggerPhaseInfoComponent
                    .builder()
                    .provideDependencies(PhaseInfoDependenciesProvider.dependencies)
                    .build()
            }
            return component!!
        }

        fun clear(){
            component = null
        }
    }
}

interface ItemPhaseDependencies{
     val phaseApi: PhaseApi
     val resultTenMinuteApi: ResultApi
     val userApi: UserApi
}

interface PhaseInfoDependenciesProvider{
    val dependencies: ItemPhaseDependencies
    companion object: PhaseInfoDependenciesProvider by PhaseInfoDependenciesStore
}

object PhaseInfoDependenciesStore: PhaseInfoDependenciesProvider{
    override var dependencies: ItemPhaseDependencies by notNull()
}

@Scope
annotation class PhaseInfoScope

package com.example.feature_screen_markup_impl.di

import com.example.feature_screen_markup_impl.MarkupFragment
import com.neurotech.core_database_api.ResultApi
import com.neurotech.core_database_api.SettingApi
import com.neurotech.core_database_api.UserApi
import dagger.Component
import dagger.Component.Builder
import javax.inject.Scope
import kotlin.properties.Delegates.notNull

@Component(dependencies = [MarkupComponentDependencies::class])
@MarkupScope
interface MarkupComponent {
    fun inject(markupFragment: MarkupFragment)

    @Builder
    interface MarkupComponentBuilder{
        fun provideDependencies(dependencies: MarkupComponentDependencies): MarkupComponentBuilder
        fun build(): MarkupComponent
    }

    companion object{
        private var component: MarkupComponent? = null

        fun get(): MarkupComponent{
            if (component == null){
                component = DaggerMarkupComponent
                    .builder()
                    .provideDependencies(MarkupComponentDependenciesProvider.dependencies)
                    .build()
            }
            return component!!
        }

        fun clear(){
            component = null
        }
    }
}


interface MarkupComponentDependencies{
    val result: ResultApi
    val user: UserApi
    val setting: SettingApi
}

interface MarkupComponentDependenciesProvider{
    val dependencies: MarkupComponentDependencies
    companion object: MarkupComponentDependenciesProvider by MarkupComponentDependenciesStore
}

object MarkupComponentDependenciesStore: MarkupComponentDependenciesProvider{
    override var dependencies: MarkupComponentDependencies by notNull()
}

@Scope
annotation class MarkupScope
package com.example.feature_screen_editing_day_plan_impl.di

import com.example.feature_screen_editing_day_plan_impl.EditingDayPlanFragment
import com.neurotech.core_database_api.SettingApi
import dagger.Component
import dagger.Component.Builder
import javax.inject.Scope
import kotlin.properties.Delegates.notNull


@Component(dependencies = [EditingDayPlanDependencies::class])
@EditingDayPlanScope
interface EditingDayPlanComponent {
    fun inject(editingDayPlanFragment: EditingDayPlanFragment)

    @Builder
    interface ComponentBuilder{
        fun provideDependencies(dependencies: EditingDayPlanDependencies): ComponentBuilder
        fun build(): EditingDayPlanComponent
    }

    companion object{
        private var component: EditingDayPlanComponent? = null

        fun get(): EditingDayPlanComponent{
            if(component == null){
                component = DaggerEditingDayPlanComponent
                    .builder()
                    .provideDependencies(EditingDayPlanDependenciesProvider.dependencies)
                    .build()
            }
            return component!!
        }
    }
}

interface EditingDayPlanDependencies{
    val settingApi: SettingApi
}

interface EditingDayPlanDependenciesProvider{
    val dependencies: EditingDayPlanDependencies
    companion object: EditingDayPlanDependenciesProvider by EditingDayPlanDependenciesStore
}

object EditingDayPlanDependenciesStore: EditingDayPlanDependenciesProvider{
    override var dependencies: EditingDayPlanDependencies by notNull()
}


@Scope
annotation class EditingDayPlanScope
package com.example.core_database_control_impl.di

import android.content.Context
import com.example.core_database_control_impl.DatabaseController
import com.example.core_firebase_database_api.FirebaseDataApi
import com.neurotech.core_database_api.PhaseApi
import com.neurotech.core_database_api.ResultApi
import com.neurotech.core_database_api.TonicApi
import com.neurotech.core_database_api.UserApi
import dagger.Component
import dagger.Component.Builder
import javax.inject.Scope
import kotlin.properties.Delegates.notNull

@Component(dependencies = [DatabaseControlDependencies::class])
@DatabaseControlScope
internal interface DatabaseControlComponent {

    fun inject(databaseController: DatabaseController)

    @Builder
    interface DatabaseControlComponentBuilder{
        fun provideDependencies(dependencies: DatabaseControlDependencies): DatabaseControlComponentBuilder
        fun build(): DatabaseControlComponent
    }

    companion object{
        private var component: DatabaseControlComponent? = null

        fun get(): DatabaseControlComponent{
            if(component == null){
                component = DaggerDatabaseControlComponent
                    .builder()
                    .provideDependencies(DatabaseControlDependenciesProvider.dependencies)
                    .build()
            }
            return component!!
        }
    }
}

interface DatabaseControlDependencies{
    val resultApi: ResultApi
    val phaseApi: PhaseApi
    val tonicApi: TonicApi
    val userApi: UserApi
    val firebaseDataApi: FirebaseDataApi
    val context: Context
}

internal interface DatabaseControlDependenciesProvider{
    val dependencies: DatabaseControlDependencies
    companion object: DatabaseControlDependenciesProvider by DatabaseControlDependenciesStore
}

object DatabaseControlDependenciesStore: DatabaseControlDependenciesProvider{
    override var dependencies: DatabaseControlDependencies by notNull()
}

@Scope
annotation class DatabaseControlScope
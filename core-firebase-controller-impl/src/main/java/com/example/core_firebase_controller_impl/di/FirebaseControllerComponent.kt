package com.example.core_firebase_controller_impl.di

import com.example.core_firebase_controller_impl.FirebaseController
import com.example.core_firebase_database_api.FirebaseDataApi
import com.neurotech.core_database_api.ResultApi
import dagger.Component
import dagger.Component.Builder
import javax.inject.Inject
import javax.inject.Scope
import kotlin.properties.Delegates.notNull

@Component(dependencies = [FirebaseControllerDependencies::class])
@FirebaseControllerScope
internal interface FirebaseControllerComponent {
    fun inject(firebaseController: FirebaseController)

    @Builder
    interface FirebaseControllerComponentBuilder{
        fun provideDependencies(dependencies: FirebaseControllerDependencies): FirebaseControllerComponentBuilder
        fun build(): FirebaseControllerComponent
    }

    companion object{
        private var component: FirebaseControllerComponent? = null

        fun get(): FirebaseControllerComponent{
            if(component == null){
                component = DaggerFirebaseControllerComponent
                    .builder()
                    .provideDependencies(FirebaseControllerDependenciesProvider.dependencies)
                    .build()
            }
            return component!!
        }
    }
}

interface FirebaseControllerDependencies{
    val resultApi: ResultApi
    val firebaseDataApi: FirebaseDataApi
}

interface FirebaseControllerDependenciesProvider{
    val dependencies: FirebaseControllerDependencies
    companion object: FirebaseControllerDependenciesProvider by FirebaseControllerDependenciesStore
}

object FirebaseControllerDependenciesStore: FirebaseControllerDependenciesProvider{
    override var dependencies: FirebaseControllerDependencies by notNull()
}

@Scope
annotation class FirebaseControllerScope


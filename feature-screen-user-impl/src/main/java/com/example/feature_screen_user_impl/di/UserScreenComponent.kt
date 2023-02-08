package com.example.feature_screen_user_impl.di

import com.example.core_firebase_auth.FirebaseAuthApi
import com.example.feature_screen_user_impl.UserFragment
import com.neurotech.core_database_api.ResultApi
import com.neurotech.core_database_api.UserApi
import dagger.Component
import dagger.Component.Builder
import javax.inject.Scope
import kotlin.properties.Delegates.notNull


@Component(dependencies = [UserScreenDependencies::class])
@UserScreenScope
interface UserScreenComponent {
    fun inject(userFragment: UserFragment)
    @Builder
    interface UserScreenComponentBuilder{
        fun provideDependencies(dependencies: UserScreenDependencies): UserScreenComponentBuilder
        fun build(): UserScreenComponent
    }

    companion object{
        private var component: UserScreenComponent? = null

        fun get(): UserScreenComponent{
            if(component == null){
                component = DaggerUserScreenComponent
                    .builder()
                    .provideDependencies(UserScreenDependenciesProvider.dependencies)
                    .build()
            }
            return component!!
        }

        fun clear(){
            component = null
        }
    }
}

interface UserScreenDependencies{
    val userApi: UserApi
    val resultApi: ResultApi
    val firebaseAuthApi: FirebaseAuthApi
}

interface UserScreenDependenciesProvider{
    val dependencies: UserScreenDependencies
    companion object: UserScreenDependenciesProvider by UserScreenDependenciesStore
}

object UserScreenDependenciesStore: UserScreenDependenciesProvider{
    override var dependencies: UserScreenDependencies by notNull()
}

@Scope
annotation class UserScreenScope
package com.example.core_firebase_auth_impl.di

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.example.core_firebase_auth_impl.AppFirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.neurotech.core_database_api.UserApi
import dagger.Component
import dagger.Component.Builder
import javax.inject.Scope
import kotlin.properties.Delegates.notNull

@Component(dependencies = [FirebaseAuthDependencies::class], modules = [FirebaseAuthModule::class])
@FirebaseAuthScope
internal interface FirebaseAuthComponent {
    fun inject(appFirebaseAuth: AppFirebaseAuth)

    @Builder
    interface FirebaseAuthComponentBuilder{
        fun provideDependencies(dependencies: FirebaseAuthDependencies): FirebaseAuthComponentBuilder
        fun build(): FirebaseAuthComponent
    }

    companion object{
        private var component: FirebaseAuthComponent? = null

        fun get(): FirebaseAuthComponent{
            if(component == null){
                component = DaggerFirebaseAuthComponent
                    .builder()
                    .provideDependencies(FirebaseAuthDependenciesProvider.dependencies)
                    .build()
            }
            return component!!
        }
    }
}

interface FirebaseAuthDependencies{
    val activity: AppCompatActivity
    val userApi: UserApi
}

interface FirebaseAuthDependenciesProvider{
    val dependencies: FirebaseAuthDependencies
    companion object: FirebaseAuthDependenciesProvider by FirebaseAuthDependenciesStore
}

object FirebaseAuthDependenciesStore: FirebaseAuthDependenciesProvider{
    override var dependencies: FirebaseAuthDependencies by notNull()
}

@Scope
annotation class FirebaseAuthScope
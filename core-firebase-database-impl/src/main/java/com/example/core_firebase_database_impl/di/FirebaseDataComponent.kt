package com.example.core_firebase_database_impl.di

import com.example.core_firebase_auth.FirebaseAuthApi
import com.example.core_firebase_database_impl.FirebaseData
import dagger.Component
import dagger.Component.Builder
import javax.inject.Scope
import kotlin.properties.Delegates.notNull

@Component(modules = [FirebaseDataModule::class])
@FirebaseDataScope
internal interface FirebaseDataComponent {
    fun inject(firebaseData: FirebaseData)

    @Builder
    interface FirebaseDataBuilder{
        fun build(): FirebaseDataComponent
    }

    companion object{
        private var component: FirebaseDataComponent? = null

        fun get(): FirebaseDataComponent{
            if(component == null){
                component = DaggerFirebaseDataComponent
                    .builder()
                    .build()
            }
            return component!!
        }
    }
}


@Scope
annotation class FirebaseDataScope
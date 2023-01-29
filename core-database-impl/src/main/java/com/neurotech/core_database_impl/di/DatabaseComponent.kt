package com.neurotech.core_database_impl.di

import android.content.Context
import com.neurotech.core_database_impl.implementation.*
import dagger.Component
import dagger.Component.Builder
import javax.inject.Scope
import kotlin.properties.Delegates.notNull

@DatabaseScope
@Component(modules = [
    DatabaseModule::class,
    MainDaoModule::class,
    SettingDaoModule::class,
    UserDaoModule::class
                     ],
    dependencies = [DatabaseDependencies::class])
internal interface DatabaseComponent {
    fun inject(tonicDB: TonicDB)
    fun inject(tonicDB: SettingDB)
    fun inject(resultDB: ResultDB)
    fun inject(phaseDB: PhaseDB)
    fun inject(userDB: UserDB)

    @DatabaseScope
    @Builder
    interface ComponentBuilder{
        fun provideDependencies(dependencies: DatabaseDependencies): ComponentBuilder
        fun build(): DatabaseComponent
    }

    companion object{
        private var component: DatabaseComponent? = null

        fun get(): DatabaseComponent{
            if(component == null){
                component = DaggerDatabaseComponent
                    .builder()
                    .provideDependencies(DatabaseComponentDependenciesProvider.dependencies)
                    .build()
            }
            return component!!
        }

        fun clear(){
            component = null
        }
    }
}

interface DatabaseDependencies{
    val context: Context
}

internal interface DatabaseComponentDependenciesProvider{
    var dependencies: DatabaseDependencies
    companion object: DatabaseComponentDependenciesProvider by DatabaseComponentDependenciesStore

}

@Scope
annotation class DatabaseScope

object DatabaseComponentDependenciesStore: DatabaseComponentDependenciesProvider{
    override var dependencies: DatabaseDependencies by notNull()
}

package com.example.navigation.di

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.navigation.AppNavigation
import com.example.navigation_api.ViewID
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.Component
import dagger.Component.Builder
import javax.inject.Scope
import kotlin.properties.Delegates.notNull

@Component(dependencies = [NavigationDependencies::class])
@NavigationScope
interface NavigationComponent {
    fun inject(appNavigation: AppNavigation)

    @Builder
    interface NavigationBuilder {
        fun provideDependencies(dependencies: NavigationDependencies): NavigationBuilder
        fun build(): NavigationComponent
    }

    companion object {
        private var component: NavigationComponent? = null

        fun get(): NavigationComponent {

            component = DaggerNavigationComponent
                .builder()
                .provideDependencies(NavigationDependenciesProvider.dependencies)
                .build()

            return component!!
        }

    }

}

interface NavigationDependencies {
//    val navigationHostFragment: NavHostFragment
//    val activity: AppCompatActivity
//    val viewID: ViewID
}

interface NavigationDependenciesProvider {
    val dependencies: NavigationDependencies

    companion object : NavigationDependenciesProvider by NavigationDependenciesStore
}

object NavigationDependenciesStore : NavigationDependenciesProvider {
    override var dependencies: NavigationDependencies by notNull()
}

@Scope
annotation class NavigationScope
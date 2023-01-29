package com.example.navigation

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.navigation.di.NavigationComponent
import com.example.navigation_api.NavigationApi
import com.example.navigation_api.ViewID
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class AppNavigation: NavigationApi {
    @Inject
    lateinit var navigationHostFragment: NavHostFragment

    @Inject
    lateinit var activity: AppCompatActivity

    @Inject
    lateinit var viewID: ViewID

    private val bottomNavigationView get() =
        activity.findViewById<BottomNavigationView>(viewID.bottomNavigationView)

    private val navigationController: NavController by lazy {
        navigationHostFragment.navController
    }


    init {
        NavigationComponent.get().inject(this)
        CoroutineScope(Dispatchers.IO).launch {
            delay(100)
            launch(Dispatchers.Main) {
                val appBarConfiguration = AppBarConfiguration(navigationController.graph)
                activity.setupActionBarWithNavController(navigationController,appBarConfiguration)
            }
        }

    }


    override fun navigateScanToMain() {
        navigationController.setGraph(R.navigation.main_navigation)
        val appBarConfiguration = AppBarConfiguration(navigationController.graph)
        activity.setupActionBarWithNavController(navigationController,appBarConfiguration)
        bottomNavigationView.isVisible = true
        bottomNavigationView.setupWithNavController(navigationController)
    }

    override fun navigateMainToScan() {
        navigationController.setGraph(R.navigation.navigation)
        val appBarConfiguration = AppBarConfiguration(navigationController.graph)
        activity.setupActionBarWithNavController(navigationController,appBarConfiguration)
        bottomNavigationView.isVisible = false
    }

    override fun navigateMainToStatistic() {
        navigationController.navigate(R.id.action_mainFragment_to_statisticScreenFragment )
    }
}
package com.example.navigation

import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
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
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Provider

class AppNavigation : NavigationApi {
    @Inject
    lateinit var navigationHostFragment: Provider<NavHostFragment>

    @Inject
    lateinit var activity: Provider<AppCompatActivity>

    @Inject
    lateinit var viewID: ViewID

    private val bottomNavigationView
        get() =
            activity.get().findViewById<BottomNavigationView>(viewID.bottomNavigationView)

    private val navigationController: NavController get() = navigationHostFragment.get().navController

    private fun waitNotNull(obj: Any?){
        while (true){
            if(obj != null){
                break
            }
        }
    }

    init {
        NavigationComponent.get().inject(this)
        CoroutineScope(Dispatchers.IO).launch {
            waitNotNull(activity.get().actionBar)
            launch(Dispatchers.Main) {
                val appBarConfiguration = AppBarConfiguration(navigationController.graph)
                activity.get()
                    .setupActionBarWithNavController(navigationController, appBarConfiguration)
            }
        }
    }


    override fun navigateScanToMain() {
        navigationController.setGraph(R.navigation.main_navigation)
        val appBarConfiguration = AppBarConfiguration(navigationController.graph)
        activity.get().setupActionBarWithNavController(navigationController, appBarConfiguration)
        bottomNavigationView.isVisible = true
        bottomNavigationView.setupWithNavController(navigationController)
    }

    override fun navigateMainToScan() {
        navigationController.setGraph(R.navigation.navigation)
        val appBarConfiguration = AppBarConfiguration(navigationController.graph)
        activity.get().setupActionBarWithNavController(navigationController, appBarConfiguration)
        bottomNavigationView.isVisible = false
    }

    override fun navigateMainToStatistic() {
        navigationController.navigate(R.id.action_mainFragment_to_statisticScreenFragment)
    }

    override fun navigateMainToRelax() {
        navigationController.navigate(R.id.action_mainFragment_to_relaxFragment)
    }

    override fun navigateSettingToEditingDayPlan(dayPlanId: Int) {
        navigationController.navigate(
            R.id.action_settingFragment_to_editingDayPlanFragment,
            bundleOf("ID" to dayPlanId)
        )
    }
}
package com.example.navigation

import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.example.navigation_api.NavigationApi
import com.example.navigation_api.NavigationState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class AppNavigation : NavigationApi {

    private var navigationController: NavController? = null
    private val navigationState = MutableStateFlow(NavigationState.SCAN_STATE)

    override fun navigateScanToMain() {
        navigationController?.setGraph(R.navigation.main_navigation)

    }

    override fun navigateMainToScan() {
        navigationController?.setGraph(R.navigation.navigation)
    }

    override fun navigateMainToStatistic() {
        navigationController?.navigate(R.id.action_mainFragment_to_statisticScreenFragment)
    }

    override fun navigateMainToRelax() {
        navigationController?.navigate(R.id.action_mainFragment_to_relaxFragment)
    }

    override fun navigateSettingToEditingDayPlan(dayPlanId: Int) {
        navigationController?.navigate(
            R.id.action_settingFragment_to_editingDayPlanFragment,
            bundleOf("ID" to dayPlanId)
        )
    }

    override fun bind(navController: NavController) {
        navigationController = navController
    }

    override fun unbind() {
        navigationController = null
    }

    override fun getNavigationState(): Flow<NavigationState> {
        return navigationState
    }
}
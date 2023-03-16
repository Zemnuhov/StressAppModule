package com.example.navigation_api

import androidx.navigation.NavController
import kotlinx.coroutines.flow.Flow

interface NavigationApi {
    fun navigateScanToMain()
    fun navigateMainToScan()
    fun navigateMainToStatistic()
    fun navigateMainToRelax()
    fun navigateSettingToEditingDayPlan(dayPlanId: Int)
    fun bind(navController: NavController)
    fun unbind()
    fun getNavigationState(): Flow<NavigationState>
}
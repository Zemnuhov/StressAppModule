package com.example.navigation_api

import androidx.navigation.NavController

interface NavigationApi {
    fun navigateScanToMain()
    fun navigateMainToScan()
    fun navigateMainToStatistic()
    fun navigateMainToRelax()
    fun navigateSettingToEditingDayPlan(dayPlanId: Int)
}
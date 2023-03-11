package com.example.navigation_api

interface NavigationApi {
    fun navigateScanToMain()
    fun navigateMainToScan()
    fun navigateMainToStatistic()
    fun navigateMainToRelax()
    fun navigateSettingToEditingDayPlan(dayPlanId: Int)
}
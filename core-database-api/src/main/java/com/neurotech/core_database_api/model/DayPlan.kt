package com.neurotech.core_database_api.model

data class DayPlans(
    val values: List<DayPlan>
)


data class DayPlan(
    val planId: Int,
    val plan: String,
    var timeBegin: String?,
    var timeEnd: String?,
    var firstSource: String?,
    var secondSource: String?,
    var autoMarkup: Boolean
)
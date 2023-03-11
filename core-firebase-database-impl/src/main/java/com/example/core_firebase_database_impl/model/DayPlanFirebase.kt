package com.example.core_firebase_database_impl.model

import com.neurotech.core_database_api.model.DayPlan

data class DayPlanFirebase(
    val planId: Int? = null,
    val plan: String? = null,
    var timeBegin: String? = null,
    var timeEnd: String? = null,
    var firstSource: String? = null,
    var secondSource: String? = null,
    var autoMarkup: Boolean? = null
){
    fun mapToDayPlan(): DayPlan{
        return DayPlan(
            planId!!,
            plan!!,
            timeBegin,
            timeEnd,
            firstSource,
            secondSource,
            autoMarkup!!
        )
    }
}
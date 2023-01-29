package com.example.feature_screen_statistic_impl

import java.util.*

data class ResultStatistic(
    val time: Date,
    val peakCount: Int,
    val tonicAvg: Int,
    val conditionAssessment: Int,
    val stressCause: String? = null,
    val keep: String? = null
)

package com.example.feature_screen_markup_impl

import java.util.Date

data class ResultsMarkup(
    val value: List<ResultMarkup>
)

data class ResultMarkup(
    val time: Date,
    val peakCount: Int,
    val tonicAvg: Int,
    val conditionAssessment: Int,
    var stressCause: String? = null,
    val keep: String? = null
)
package com.example.feature_screen_markup_impl.adapter

import java.util.*

data class ResultAdapterModel(
    val time: Date,
    val peakCount: Int,
    val tonicAvg: Int,
    val conditionAssessment: Int,
    var stressCause: String? = null,
    val keep: String? = null,
    var isChecked: Boolean,
    val color: Int
)

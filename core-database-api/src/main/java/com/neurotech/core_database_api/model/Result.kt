package com.neurotech.core_database_api.model

import java.time.LocalDateTime
import java.util.*

data class ResultsTenMinute(
    val list: List<ResultTenMinute>
)

data class ResultTenMinute(
    val time: Date,
    val peakCount: Int,
    val tonicAvg: Int,
    val conditionAssessment: Int,
    val stressCause: String? = null,
    val keep: String? = null
)

data class ResultsHour(
    val list: List<ResultHour>
)

data class ResultHour(
    val date: Date,
    val peaks: Int,
    val peaksAvg: Int,
    val tonic: Int,
    val stressCause: String?
)

data class ResultsDay(
    val list: List<ResultDay>
)

data class ResultDay(
    val date: Date,
    val peaks: Int,
    val peaksAvg: Int,
    val tonic: Int,
    val stressCause: String?
)
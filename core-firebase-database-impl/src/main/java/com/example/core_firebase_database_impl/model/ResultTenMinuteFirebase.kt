package com.example.core_firebase_database_impl.model

import com.cesarferreira.tempo.toDate
import com.neurotech.core_database_api.model.ResultTenMinute
import com.neurotech.utils.TimeFormat

data class ResultTenMinuteFirebase(
    val time: String? = null,
    val peakCount: Int? = null,
    val tonicAvg: Int? = null,
    val conditionAssessment: Int? = null,
    val stressCause: String? = null,
    val keep: String? = null
) {
    fun toResultEntity(): ResultTenMinute {
        return ResultTenMinute(
            time!!.toDate(TimeFormat.firebaseDateTimePattern),
            peakCount!!,
            tonicAvg!!,
            conditionAssessment!!,
            stressCause,
            keep
        )
    }
}

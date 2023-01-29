package com.neurotech.core_database_impl.main_database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cesarferreira.tempo.toDate
import com.neurotech.core_database_api.model.ResultTenMinute
import com.neurotech.utils.TimeFormat

@Entity
data class ResultTenMinuteEntity(
    @PrimaryKey
    @ColumnInfo(name = "time")
    val time: String,
    @ColumnInfo(name = "phaseCount")
    val peakCount: Int,
    @ColumnInfo(name = "tonicAvg")
    val tonicAvg: Int,
    @ColumnInfo(name = "conditionAssessment")
    val conditionAssessment: Int,
    @ColumnInfo(name = "stressCause")
    val stressCause: String? = null,
    @ColumnInfo(name = "keep")
    val keep: String? = null
){
    fun mapToResultTenMinute(): ResultTenMinute{
        return ResultTenMinute(
            time.toDate(TimeFormat.dateTimeIsoPattern),
            peakCount,
            tonicAvg,
            conditionAssessment,
            stressCause,
            keep
        )
    }
}

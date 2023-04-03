package com.neurotech.core_database_impl.main_database.model

import androidx.room.ColumnInfo
import com.neurotech.core_database_impl.main_database.entity.ResultTenMinuteEntity

data class ResultDataDB(
    @ColumnInfo(name = "time")
    val date: String,
    @ColumnInfo(name = "phaseCount")
    val phase: Int,
    @ColumnInfo(name = "tonicAvg")
    val tonic: Int
){
    fun mapToResultTenMinute(): ResultTenMinuteEntity{
        return ResultTenMinuteEntity(
            date,
            phase,
            tonic,
            1,
            null,
            null
        )
    }
}

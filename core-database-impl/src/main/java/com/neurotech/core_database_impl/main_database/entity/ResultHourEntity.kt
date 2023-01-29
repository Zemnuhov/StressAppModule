package com.neurotech.core_database_impl.main_database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cesarferreira.tempo.toDate
import com.neurotech.core_database_api.model.ResultHour
import com.neurotech.core_database_api.model.ResultsHour
import com.neurotech.utils.TimeFormat

@Entity
data class ResultHourEntity(
    @PrimaryKey
    @ColumnInfo(name = "date")
    val date: String,
    @ColumnInfo(name = "peaks")
    val peaks: Int,
    @ColumnInfo(name = "peaksAvg")
    val peaksAvg: Int,
    @ColumnInfo(name = "tonic")
    val tonic: Int,
    @ColumnInfo(name = "stressCause")
    val stressCause: String?
){
    fun mapToResultHour(): ResultHour {
        return ResultHour(
            date.toDate(TimeFormat.dateTimeIsoPattern),
            peaks,
            peaksAvg,
            tonic,
            stressCause
        )
    }
}
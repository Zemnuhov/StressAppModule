package com.neurotech.core_database_impl.main_database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cesarferreira.tempo.toDate
import com.neurotech.core_database_api.model.RelaxRecord
import com.neurotech.core_database_api.model.RelaxRecords
import com.neurotech.utils.TimeFormat

@Entity
data class RelaxRecordEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "date")
    val date: String,
    @ColumnInfo(name = "relaxationDuration")
    val relaxationDuration: Int,
    @ColumnInfo(name = "phaseCount")
    val phaseCount: Int,
    @ColumnInfo(name = "tonicAdjusted")
    val tonicAdjusted: Int
){
    fun mapToRelaxRecord(): RelaxRecord{
        return RelaxRecord(
            date.toDate(TimeFormat.dateIsoPattern),
            relaxationDuration,
            phaseCount,
            tonicAdjusted
        )
    }
}

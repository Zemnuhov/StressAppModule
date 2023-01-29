package com.neurotech.core_database_impl.setting_database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.neurotech.core_database_api.model.DayPlan

@Entity
data class DayPlanEntity(
    @PrimaryKey(autoGenerate = true)
    val planId: Int,
    @ColumnInfo(name = "plan")
    val plan: String,
    @ColumnInfo(name = "timeBegin")
    var timeBegin: String?,
    @ColumnInfo(name = "timeEnd")
    var timeEnd: String?,
    @ColumnInfo(name = "firstCause")
    var firstSource: String?,
    @ColumnInfo(name = "secondCause")
    var secondSource: String?,
    @ColumnInfo(name = "autoMarkup", defaultValue = false.toString())
    var autoMarkup: Boolean
){
    fun mapToDayPlan(): DayPlan = DayPlan(
        planId,
        plan,
        timeBegin,
        timeEnd,
        firstSource,
        secondSource,
        autoMarkup
    )
}
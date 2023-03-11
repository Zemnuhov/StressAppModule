package com.neurotech.core_database_impl.main_database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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
)

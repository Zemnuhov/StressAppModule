package com.neurotech.core_database_impl.main_database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PhaseEntity(
    @PrimaryKey
    @ColumnInfo(name = "timeBegin")
    val timeBegin: String,
    @ColumnInfo(name = "timeEnd")
    val timeEnd: String,
    @ColumnInfo(name = "max")
    val max: Double
)

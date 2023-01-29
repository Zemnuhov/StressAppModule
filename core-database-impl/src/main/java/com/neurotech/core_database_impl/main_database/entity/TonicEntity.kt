package com.neurotech.core_database_impl.main_database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TonicEntity(
    @PrimaryKey
    @ColumnInfo(name = "time")
    val time: String,
    @ColumnInfo(name = "value")
    val value: Int
)

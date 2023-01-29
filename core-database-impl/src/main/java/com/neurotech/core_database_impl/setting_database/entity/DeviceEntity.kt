package com.neurotech.core_database_impl.setting_database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DeviceEntity(
    @PrimaryKey
    @ColumnInfo(name = "mac")
    val mac: String,
    @ColumnInfo(name = "name")
    val name: String
)
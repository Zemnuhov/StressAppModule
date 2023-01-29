package com.neurotech.core_database_impl.setting_database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.neurotech.core_database_api.model.Cause

@Entity
data class CauseEntity(
    @PrimaryKey
    @ColumnInfo(name = "cause")
    val cause: String
){
    fun mapToCause(): Cause{
        return Cause(cause)
    }
}
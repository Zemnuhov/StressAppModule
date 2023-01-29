package com.neurotech.core_database_impl.main_database.entity

import androidx.room.ColumnInfo
import com.neurotech.core_database_api.model.Cause

data class CountForCauseDB(
    @ColumnInfo(name = "stressCause")
    val cause: String,
    @ColumnInfo(name = "count")
    val count: Int
)
package com.neurotech.core_database_impl.main_database.model

import androidx.room.ColumnInfo
import com.neurotech.core_database_api.model.UserParameters

data class UserParameterDB( @ColumnInfo(name = "maxTonic")
                            val tonic: Int,
                            @ColumnInfo(name = "maxPeakInDay")
                            val dayPhase: Int,
                            @ColumnInfo(name = "maxHourInDay")
                            val hourPhase: Int,
                            @ColumnInfo(name = "maxPeaksInTenMinute")
                            val tenMinutePhase: Int
){
    fun mapToDomain(): UserParameters{
        return UserParameters(
            tonic,
            tenMinutePhase,
            hourPhase,
            dayPhase
        )
    }
}


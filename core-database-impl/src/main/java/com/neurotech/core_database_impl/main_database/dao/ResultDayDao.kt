package com.neurotech.core_database_impl.main_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.neurotech.core_database_impl.main_database.entity.ResultDayEntity
import com.neurotech.core_database_impl.main_database.model.UserParameterDB
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultDayDao {
    @Query("SELECT date from ResultDayEntity")
    fun getDates(): List<String>

    @Query("SELECT * from ResultDayEntity")
    fun getDayResultsFlow(): Flow<List<ResultDayEntity>>

    @Query("SELECT * from ResultDayEntity WHERE date>= :beginInterval and date<= :endInterval")
    fun getResultDayByInterval(beginInterval: String, endInterval: String): Flow<List<ResultDayEntity>>

    @Query("SELECT * FROM ResultDayEntity ORDER BY date DESC LIMIT 5")
    fun getLastFiveValidDay(): Flow<List<ResultDayEntity>>

    @Query("SELECT MAX(tonic) as maxTonic, " +
            "MAX(peaks) as maxPeakInDay,  " +
            "0 as maxHourInDay, " +
            "MAX(peaksAvg) as maxPeaksInTenMinute " +
            "FROM ResultDayEntity  " +
            "WHERE peaks > peaksAvg * 144 - 300 AND peaks < peaksAvg * 144 + 300")
    fun getMaxParameters(): UserParameterDB

    @Insert
    fun insertDayResult(vararg dayResult: ResultDayEntity)

    @Update
    fun updateDayResult(vararg dayResult: ResultDayEntity)

}
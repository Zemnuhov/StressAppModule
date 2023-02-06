package com.neurotech.core_database_impl.main_database.dao

import androidx.room.*
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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDayResult(dayResult: ResultDayEntity): Long

    @Update
    fun updateDayResult(dayResult: ResultDayEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDayResult(dayResult: List<ResultDayEntity>): List<Long>

    @Update
    fun updateDayResult(dayResult: List<ResultDayEntity>)

    @Transaction
    fun insertOrUpdate(objList: List<ResultDayEntity>) {
        val insertResult = insertDayResult(objList)
        val updateList = mutableListOf<ResultDayEntity>()

        for (i in insertResult.indices) {
            if (insertResult[i] == -1L) updateList.add(objList[i])
        }

        if (updateList.isNotEmpty()) updateDayResult(updateList)
    }
}
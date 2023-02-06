package com.neurotech.core_database_impl.main_database.dao

import androidx.room.*
import com.neurotech.core_database_impl.main_database.entity.ResultDayEntity
import com.neurotech.core_database_impl.main_database.entity.ResultHourEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultHourDao {
    @Query("SELECT date from ResultHourEntity")
    fun getDates(): List<String>

    @Query("SELECT * from ResultHourEntity")
    fun getHourResultsFlow(): Flow<List<ResultHourEntity>>

    @Query("SELECT * from ResultHourEntity WHERE date>= :beginInterval and date<= :endInterval")
    fun getResultHourByInterval(beginInterval: String, endInterval: String): Flow<List<ResultHourEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertResultHour(dayResult: ResultHourEntity): Long

    @Update
    fun updateResultHour(dayResult: ResultHourEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertResultHour(dayResult: List<ResultHourEntity>): List<Long>

    @Update
    fun updateResultHour(dayResult: List<ResultHourEntity>)

    @Transaction
    fun insertOrUpdate(objList: List<ResultHourEntity>) {
        val insertResult = insertResultHour(objList)
        val updateList = mutableListOf<ResultHourEntity>()

        for (i in insertResult.indices) {
            if (insertResult[i] == -1L) updateList.add(objList[i])
        }

        if (!updateList.isEmpty()) updateResultHour(updateList)
    }
}
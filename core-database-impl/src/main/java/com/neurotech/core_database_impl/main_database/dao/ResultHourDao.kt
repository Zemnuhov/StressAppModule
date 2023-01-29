package com.neurotech.core_database_impl.main_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
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

    @Insert
    fun insertHourResult(vararg hourResult: ResultHourEntity)

    @Update
    fun updateHourResult(vararg hourResult: ResultHourEntity)
}
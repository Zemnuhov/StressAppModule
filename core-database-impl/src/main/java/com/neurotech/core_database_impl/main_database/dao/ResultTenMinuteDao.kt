package com.neurotech.core_database_impl.main_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.neurotech.core_database_impl.main_database.entity.CountForCauseDB
import com.neurotech.core_database_impl.main_database.entity.ResultTenMinuteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultTenMinuteDao {

    @Query("SELECT * FROM ResultTenMinuteEntity GROUP BY time")
    fun getResult(): Flow<List<ResultTenMinuteEntity>>

    @Query("SELECT * FROM ResultTenMinuteEntity WHERE time >= datetime('now','-1 hour','localtime')")
    fun getResultsInOneHour(): Flow<List<ResultTenMinuteEntity>>

    @Query("SELECT stressCause, COUNT(*) as count FROM ResultTenMinuteEntity WHERE stressCause in (:causes) GROUP BY stressCause")
    fun getCountForEachCause(causes: List<String>): Flow<List<CountForCauseDB>>

    @Query("SELECT * FROM ResultTenMinuteEntity WHERE datetime(time, 'localtime') BETWEEN datetime(:beginInterval, 'localtime') and datetime(:endInterval, 'localtime')")
    fun getResultsInInterval(beginInterval: String, endInterval:String): Flow<List<ResultTenMinuteEntity>>

    @Query("UPDATE ResultTenMinuteEntity SET keep = :keep WHERE time = :time")
    fun setKeepByTime(keep: String?, time: String)

    @Query("select stressCause, COUNT(*) as count from ResultTenMinuteEntity where datetime(time,'localtime') between datetime(:beginInterval, 'localtime') and datetime(:endInterval, 'localtime') group by stressCause ")
    fun getCountStressCauseInInterval(beginInterval: String, endInterval:String): Flow<List<CountForCauseDB>>

    @Insert
    fun insertResult(vararg resultEntity: ResultTenMinuteEntity)
}
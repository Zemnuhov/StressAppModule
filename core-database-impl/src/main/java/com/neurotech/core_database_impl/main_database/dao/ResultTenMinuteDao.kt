package com.neurotech.core_database_impl.main_database.dao

import androidx.room.*
import com.neurotech.core_database_api.model.ResultHour
import com.neurotech.core_database_api.model.ResultsTenMinute
import com.neurotech.core_database_impl.main_database.entity.CountForCauseDB
import com.neurotech.core_database_impl.main_database.entity.ResultDayEntity
import com.neurotech.core_database_impl.main_database.entity.ResultHourEntity
import com.neurotech.core_database_impl.main_database.entity.ResultTenMinuteEntity
import com.neurotech.core_database_impl.main_database.model.UserParameterDB
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultTenMinuteDao {

    @Query("SELECT * FROM ResultTenMinuteEntity GROUP BY time")
    fun getResult(): Flow<ResultTenMinuteEntity?>

    @Query("SELECT * FROM ResultTenMinuteEntity GROUP BY time")
    fun getResults(): Flow<List<ResultTenMinuteEntity>>

    @Query("SELECT * FROM ResultTenMinuteEntity WHERE time >= datetime('now','-1 hour','localtime')")
    fun getResultsInOneHour(): Flow<List<ResultTenMinuteEntity>>

    @Query("SELECT stressCause, COUNT(*) as count FROM ResultTenMinuteEntity WHERE stressCause in (:causes) GROUP BY stressCause")
    fun getCountForEachCause(causes: List<String>): Flow<List<CountForCauseDB>>

    @Query("SELECT * FROM ResultTenMinuteEntity WHERE datetime(time, 'localtime') BETWEEN datetime(:beginInterval, 'localtime') and datetime(:endInterval, 'localtime')")
    fun getResultsInInterval(beginInterval: String, endInterval:String): Flow<List<ResultTenMinuteEntity>>

    @Query("UPDATE ResultTenMinuteEntity SET keep = :keep WHERE time = :time")
    fun setKeepByTime(keep: String?, time: String)

    @Query("select stressCause, COUNT(*) as count from ResultTenMinuteEntity where datetime(time,'localtime') between datetime(:beginInterval, 'localtime') and datetime(:endInterval, 'localtime') and stressCause != null  group by stressCause ")
    fun getCountStressCauseInInterval(beginInterval: String, endInterval:String): Flow<List<CountForCauseDB>>

    @Query("SELECT strftime('%Y-%m-%d %H:00:00.000', time) AS date, SUM(phaseCount) AS peaks, AVG(phaseCount) AS peaksAvg, AVG(tonicAvg) AS tonic,s AS stressCause " +
            "FROM ResultTenMinuteEntity " +
            "JOIN (SELECT  day,s,max(count) " +
            "FROM ResultTenMinuteEntity JOIN(SELECT strftime('%Y-%m-%d %H', time) AS day, stressCause AS s, count(stressCause) AS count FROM ResultTenMinuteEntity GROUP BY day, stressCause) GROUP BY day) ON day = strftime('%Y-%m-%d %H', time) " +
            "WHERE date >= :beginInterval AND date <= :endInterval GROUP BY date")
    fun getResultsHour(beginInterval: String, endInterval:String): List<ResultHourEntity>

    @Query("SELECT date(time,'localtime') AS date, SUM(phaseCount) AS peaks, AVG(phaseCount) AS peaksAvg, AVG(tonicAvg) AS tonic,s AS stressCause " +
            "FROM ResultTenMinuteEntity " +
            "JOIN (SELECT  day,s,max(count) FROM ResultTenMinuteEntity JOIN(SELECT date(time) AS day, stressCause AS s, count(stressCause) AS count FROM ResultTenMinuteEntity GROUP BY day, stressCause) GROUP BY day) ON day = date(time,'localtime') " +
            "WHERE date >= date(:beginInterval, 'localtime') AND date <= date(:endInterval, 'localtime') " +
            "GROUP BY date")
    fun getResultForTheDay(beginInterval: String, endInterval:String): List<ResultDayEntity>

    @Query("SELECT * FROM ResultTenMinuteEntity WHERE phaseCount > :threshold and stressCause is NULL")
    fun getResultsTenMinuteAboveThreshold(threshold: Int): Flow<List<ResultTenMinuteEntity>>

    @Query("SELECT AVG(tonicAvg) AS maxTonic, AVG(phaseCount) AS maxPeaksInTenMinute, " +
            "(SELECT AVG(peaks) FROM ResultHourEntity) AS maxHourInDay, " +
            "(SELECT AVG(peaks) FROM ResultDayEntity) AS maxPeakInDay FROM ResultTenMinuteEntity")
    fun getUserParameter(): Flow<UserParameterDB>

    @Query("SELECT" +
            "   AVG(tonicAvg) AS maxTonic," +
            "   AVG(phaseCount) AS maxPeaksInTenMinute," +
            "   (SELECT AVG(peaks) FROM ResultHourEntity WHERE date >= strftime('%Y-%m-%d %H', :beginInterval)  AND date <= strftime('%Y-%m-%d %H', :endInterval) ) AS maxHourInDay," +
            "   (SELECT AVG(peaks) FROM ResultDayEntity WHERE date >= strftime('%Y-%m-%d', :beginInterval)  AND date <= strftime('%Y-%m-%d', :endInterval)) AS maxPeakInDay " +
            "FROM ResultTenMinuteEntity " +
            "WHERE time >= :beginInterval  AND time <= :endInterval")
    fun getUserParameterInInterval(beginInterval: String, endInterval:String): Flow<UserParameterDB>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertResult(vararg resultEntity: ResultTenMinuteEntity)

    @Update
    fun updateResult(vararg resultEntity: ResultTenMinuteEntity)
}
package com.neurotech.core_database_impl.main_database.dao

import androidx.room.*
import com.neurotech.core_database_impl.main_database.entity.CountForCauseDB
import com.neurotech.core_database_impl.main_database.entity.ResultDayEntity
import com.neurotech.core_database_impl.main_database.entity.ResultHourEntity
import com.neurotech.core_database_impl.main_database.entity.ResultTenMinuteEntity
import com.neurotech.core_database_impl.main_database.model.ResultDataDB
import com.neurotech.core_database_impl.main_database.model.UserParameterDB
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultTenMinuteDao {

    @Query("SELECT * FROM ResultTenMinuteEntity ORDER BY time DESC")
    fun getResult(): Flow<ResultTenMinuteEntity?>

    @Query("SELECT * FROM ResultTenMinuteEntity WHERE time = :time")
    fun getResultByDateTime(time: String): ResultTenMinuteEntity?

    @Query("SELECT * FROM ResultTenMinuteEntity ORDER BY time DESC LIMIT 1000")
    fun getResults(): Flow<List<ResultTenMinuteEntity>>

    @Query("SELECT * FROM ResultTenMinuteEntity WHERE time >= datetime('now','-1 hour','localtime')")
    fun getResultsInOneHour(): Flow<List<ResultTenMinuteEntity>>

    @Query("SELECT stressCause, COUNT(*) as count FROM ResultTenMinuteEntity WHERE stressCause in (:causes) GROUP BY stressCause")
    fun getCountForEachCause(causes: List<String>): Flow<List<CountForCauseDB>>

    @Query("SELECT * FROM ResultTenMinuteEntity WHERE time BETWEEN :beginInterval and :endInterval ORDER BY time")
    fun getResultsInInterval(beginInterval: String, endInterval:String): Flow<List<ResultTenMinuteEntity>>

    @Query("UPDATE ResultTenMinuteEntity SET stressCause = :cause WHERE time = :time")
    fun setCauseByTime(cause: String, time: String)

    @Query("UPDATE ResultTenMinuteEntity SET stressCause = NULL WHERE time = :time")
    fun deleteMarkupByTime(time: String)

    @Query("UPDATE ResultTenMinuteEntity SET keep = :keep WHERE time = :time")
    fun setKeepByTime(keep: String?, time: String)

    @Query("SELECT stressCause, COUNT(*) AS count FROM ResultTenMinuteEntity WHERE time BETWEEN :beginInterval AND :endInterval AND stressCause NOT null AND stressCause not in ('Артефакты', 'Сон')  GROUP BY stressCause ")
    fun getCountStressCauseInInterval(beginInterval: String, endInterval:String): Flow<List<CountForCauseDB>>

    @Query("SELECT strftime('%Y-%m-%d %H:00:00.000', time) AS date, SUM(phaseCount) AS peaks, AVG(phaseCount) AS peaksAvg, AVG(tonicAvg) AS tonic, s AS stressCause " +
            "FROM ResultTenMinuteEntity " +
            "JOIN (SELECT day, s, max(count) " +
            "FROM ( SELECT substr(time, 1, 13) AS day, stressCause AS s, count(stressCause) AS count FROM ResultTenMinuteEntity GROUP BY day, stressCause) " +
            "GROUP BY day" +
            ") ON day = substr(time, 1, 13) " +
            "WHERE time >= :beginInterval AND time <= :endInterval " +
            "GROUP BY date")
    fun getResultsHour(beginInterval: String, endInterval:String): List<ResultHourEntity>

    @Query("SELECT date(time,'localtime') AS date,SUM(phaseCount) AS peaks, AVG(phaseCount) AS peaksAvg, AVG(tonicAvg) AS tonic, s AS stressCause " +
            "FROM ResultTenMinuteEntity JOIN ( " +
            "SELECT day, s, max(count) " +
            "FROM ( SELECT substr(time, 1, 10) AS day, stressCause AS s, count(stressCause) AS count " +
            "FROM ResultTenMinuteEntity " +
            "WHERE (stressCause NOT IN ('Сон', 'Артефакты') OR stressCause IS NULL) AND date(time, 'localtime') >= date(:beginInterval, 'localtime') AND date(time, 'localtime') <= date(:endInterval, 'localtime')\n" +
            "GROUP BY day, stressCause) " +
            "GROUP BY day) ON day = substr(time, 1, 10) " +
            "GROUP BY date")
    fun getResultForTheDay(beginInterval: String, endInterval:String): List<ResultDayEntity>

    @Query("SELECT * FROM ResultTenMinuteEntity WHERE phaseCount > :threshold and stressCause is NULL ORDER BY time")
    fun getResultsTenMinuteAboveThreshold(threshold: Int): Flow<List<ResultTenMinuteEntity>>

    @Query("SELECT " +
            "  AVG(rte.tonicAvg) AS maxTonic, " +
            "  AVG(rte.phaseCount) AS maxPeaksInTenMinute," +
            "  AVG(rhe.peaks) AS maxHourInDay," +
            "  AVG(rde.peaks) AS maxPeakInDay " +
            "FROM ResultTenMinuteEntity rte " +
            "LEFT JOIN (SELECT AVG(peaks) AS peaks FROM ResultHourEntity) rhe ON 1=1 " +
            "LEFT JOIN (SELECT AVG(peaks) AS peaks FROM ResultDayEntity) rde ON 1=1")
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
    fun insertResultOnIgnore(vararg resultEntity: ResultTenMinuteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertResultOnReplace(vararg resultEntity: ResultTenMinuteEntity)

    @Update
    fun updateResult(vararg resultEntity: ResultTenMinuteEntity)

    @Query("UPDATE ResultTenMinuteEntity SET phaseCount = :phaseCount, tonicAvg = :tonicAvg WHERE time = :time")
    fun updateResult(time: String, phaseCount: Int, tonicAvg: Int)

    @Query("WITH RECURSIVE dates(date) AS ( " +
            "  VALUES (strftime('%Y-%m-%d %H:00:00.000', datetime(:nowDateTime, '-1 day'))) " +
            "  UNION ALL " +
            "  SELECT datetime(date, '+10 minute') " +
            "  FROM dates " +
            "  WHERE date < :nowDateTime " +
            ") " +
            "SELECT strftime('%Y-%m-%d %H:%M:00.000', date) as time, phase as phaseCount, AVG(value) as tonicAvg FROM TonicEntity\n" +
            "JOIN( " +
            "SELECT date, COUNT(*) as phase FROM PhaseEntity " +
            "JOIN(SELECT date FROM dates) " +
            "WHERE timeBegin >= datetime(date, '-10 minute') and timeBegin <= date GROUP BY date) " +
            "WHERE time >= datetime(date, '-10 minute') and time <= date GROUP BY date")
    fun getDataByInterval(nowDateTime: String):List<ResultDataDB>
}

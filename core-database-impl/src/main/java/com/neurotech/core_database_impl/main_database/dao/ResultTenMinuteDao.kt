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

    @Query("SELECT * FROM ResultTenMinuteEntity WHERE time BETWEEN :beginInterval and :endInterval")
    fun getResultsInInterval(beginInterval: String, endInterval:String): Flow<List<ResultTenMinuteEntity>>

    @Query("UPDATE ResultTenMinuteEntity SET stressCause = :cause WHERE time = :time")
    fun setCauseByTime(cause: String, time: String)

    @Query("UPDATE ResultTenMinuteEntity SET stressCause = NULL WHERE time = :time")
    fun deleteMarkupByTime(time: String)

    @Query("UPDATE ResultTenMinuteEntity SET keep = :keep WHERE time = :time")
    fun setKeepByTime(keep: String?, time: String)

    @Query("SELECT stressCause, COUNT(*) AS count FROM ResultTenMinuteEntity WHERE time BETWEEN :beginInterval AND :endInterval AND stressCause NOT null AND stressCause not in ('Артефакты', 'Сон')  GROUP BY stressCause ")
    fun getCountStressCauseInInterval(beginInterval: String, endInterval:String): Flow<List<CountForCauseDB>>

    @Query("SELECT strftime('%Y-%m-%d %H:00:00.000', time) AS date, SUM(phaseCount) AS peaks, AVG(phaseCount) AS peaksAvg, AVG(tonicAvg) AS tonic,s AS stressCause " +
            "FROM ResultTenMinuteEntity " +
            "JOIN (SELECT  day,s,max(count) " +
            "FROM ResultTenMinuteEntity JOIN(SELECT strftime('%Y-%m-%d %H', time) AS day, stressCause AS s, count(stressCause) AS count FROM ResultTenMinuteEntity GROUP BY day, stressCause) GROUP BY day) ON day = strftime('%Y-%m-%d %H', time) " +
            "WHERE date >= :beginInterval AND date <= :endInterval GROUP BY date")
    fun getResultsHour(beginInterval: String, endInterval:String): List<ResultHourEntity>

    @Query("SELECT date(time,'localtime') AS date, SUM(phaseCount) AS peaks, AVG(phaseCount) AS peaksAvg, AVG(tonicAvg) AS tonic,s AS stressCause " +
            "FROM ResultTenMinuteEntity " +
            "JOIN (SELECT  day,s,max(count) FROM ResultTenMinuteEntity JOIN(SELECT date(time) AS day, stressCause AS s, count(stressCause) AS count FROM ResultTenMinuteEntity WHERE stressCause not in ('Сон', 'Артефакты') or stressCause is null GROUP BY day, stressCause) GROUP BY day) ON day = date(time,'localtime') " +
            "WHERE date >= date(:beginInterval, 'localtime') AND date <= date(:endInterval, 'localtime') " +
            "GROUP BY date")
    fun getResultForTheDay(beginInterval: String, endInterval:String): List<ResultDayEntity>

    @Query("SELECT * FROM ResultTenMinuteEntity WHERE phaseCount > :threshold and stressCause is NULL")
    fun getResultsTenMinuteAboveThreshold(threshold: Int): Flow<List<ResultTenMinuteEntity>>

    @Query("SELECT \n" +
            "  AVG(rte.tonicAvg) AS maxTonic, \n" +
            "  AVG(rte.phaseCount) AS maxPeaksInTenMinute,\n" +
            "  AVG(rhe.peaks) AS maxHourInDay,\n" +
            "  AVG(rde.peaks) AS maxPeakInDay\n" +
            "FROM ResultTenMinuteEntity rte\n" +
            "LEFT JOIN (SELECT AVG(peaks) AS peaks FROM ResultHourEntity) rhe ON 1=1\n" +
            "LEFT JOIN (SELECT AVG(peaks) AS peaks FROM ResultDayEntity) rde ON 1=1")
    fun getUserParameter(): Flow<UserParameterDB>

    @Query("SELECT\n" +
            "    AVG(rte.tonicAvg) AS maxTonic,\n" +
            "    AVG(rte.phaseCount) AS maxPeaksInTenMinute,\n" +
            "    AVG(rhe.peaks) AS maxHourInDay,\n" +
            "    AVG(rde.peaks) AS maxPeakInDay\n" +
            "FROM ResultTenMinuteEntity rte\n" +
            "LEFT JOIN ResultHourEntity rhe ON strftime('%Y-%m-%d %H', rhe.date) = strftime('%Y-%m-%d %H', rte.time)\n" +
            "LEFT JOIN ResultDayEntity rde ON strftime('%Y-%m-%d', rde.date) = strftime('%Y-%m-%d', rte.time)\n" +
            "WHERE rte.time BETWEEN :beginInterval AND :endInterval")
    fun getUserParameterInInterval(beginInterval: String, endInterval:String): Flow<UserParameterDB>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertResultOnIgnore(vararg resultEntity: ResultTenMinuteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertResultOnReplace(vararg resultEntity: ResultTenMinuteEntity)

    @Update
    fun updateResult(vararg resultEntity: ResultTenMinuteEntity)

    @Query("UPDATE ResultTenMinuteEntity SET phaseCount = :phaseCount, tonicAvg = :tonicAvg WHERE time = :time")
    fun updateResult(time: String, phaseCount: Int, tonicAvg: Int)

    @Query("WITH RECURSIVE dates(date) AS (\n" +
            "  VALUES (strftime('%Y-%m-%d %H:00:00.000', datetime(:nowDateTime, \"-1 day\")))\n" +
            "  UNION ALL\n" +
            "  SELECT datetime(date, '+10 minute')\n" +
            "  FROM dates\n" +
            "  WHERE date < :nowDateTime\n" +
            ")\n" +
            "SELECT strftime('%Y-%m-%d %H:%M:00.000', date) as time, phase as phaseCount, AVG(value) as tonicAvg FROM TonicEntity\n" +
            "JOIN(\n" +
            "SELECT date, COUNT(*) as phase FROM PhaseEntity\n" +
            "JOIN(SELECT date FROM dates) \n" +
            "WHERE timeBegin >= datetime(date, '-10 minute') and timeBegin <= date GROUP BY date)\n" +
            "WHERE time >= datetime(date, '-10 minute') and time <= date GROUP BY date\n")
    fun getDataByInterval(nowDateTime: String):List<ResultDataDB>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTenMinuteResult(dayResult: List<ResultTenMinuteEntity>): List<Long>

    @Transaction
    fun insertOrUpdate(objList: List<ResultTenMinuteEntity>) {
        val insertResult = insertTenMinuteResult(objList)
        for (i in insertResult.indices) {
            if (insertResult[i] == -1L) updateResult(objList[i].time,objList[i].peakCount,objList[i].tonicAvg)
        }
    }
}

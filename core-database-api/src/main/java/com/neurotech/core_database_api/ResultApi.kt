package com.neurotech.core_database_api

import com.neurotech.core_database_api.model.*
import kotlinx.coroutines.flow.Flow
import java.util.*

interface ResultApi {
    suspend fun writeResultTenMinute(resultsTenMinute: ResultTenMinute)
    suspend fun writeResultHour(resultsHour: ResultsHour)
    suspend fun writeResultDay(resultsDay: ResultsDay)
    suspend fun setKeepByTime(keep: String?, time: Date)
    suspend fun updateResultTenMinute(resultsTenMinute: ResultsTenMinute)

    suspend fun getResultHourFromResultTenMinute(beginInterval: Date, endInterval: Date): ResultsHour
    suspend fun getResultDayFromResultTenMinute(beginInterval: Date, endInterval: Date): ResultsDay

    suspend fun getResultTenMinute(): Flow<ResultTenMinute?>
    suspend fun getResultHour(): Flow<ResultHour>

    suspend fun getCountForEachCause(causes: Causes): Flow<CountForEachCause>
    suspend fun getMaxUserParameter(): UserParameters

    suspend fun getResultTenMinuteInLastHour(): Flow<ResultsTenMinute>
    suspend fun getResultsInInterval(beginInterval: Date, endInterval: Date): Flow<ResultsTenMinute>
    suspend fun getLastFiveValidDay(): Flow<ResultsDay>
    suspend fun getCountCauseInInterval(causes: Causes,beginInterval: Date, endInterval: Date): Flow<CountForEachCause>
    suspend fun getResultHourByInterval(beginInterval: Date, endInterval: Date): Flow<ResultsHour>
    suspend fun getResultsTenMinuteAboveThreshold(threshold: Int): Flow<ResultsTenMinute>

    suspend fun getMonthlyResults(month: Date, filToFull: Boolean): Flow<ResultsDay>
}
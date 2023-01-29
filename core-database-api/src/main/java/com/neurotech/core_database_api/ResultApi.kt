package com.neurotech.core_database_api

import com.neurotech.core_database_api.model.*
import kotlinx.coroutines.flow.Flow
import java.util.*

interface ResultApi {
    suspend fun getResultTenMinuteInLastHour(): Flow<ResultsTenMinute>
    suspend fun getCountForEachCause(causes: Causes): Flow<CountForEachCause>
    suspend fun getResultsInInterval(beginInterval: Date, endInterval: Date): Flow<ResultsTenMinute>
    suspend fun setKeepByTime(keep: String?, time: Date)
    suspend fun getLastFiveValidDay(): Flow<ResultsDay>
    suspend fun getMaxUserParameter(): UserParameters
    suspend fun getCountCauseInInterval(causes: Causes,beginInterval: Date, endInterval: Date): Flow<CountForEachCause>

    suspend fun getResultHourByInterval(beginInterval: Date, endInterval: Date): Flow<ResultsHour>

    suspend fun getMonthlyResults(month: Date, filToFull: Boolean): Flow<ResultsDay>
}
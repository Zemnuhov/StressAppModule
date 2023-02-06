package com.neurotech.core_database_api

import com.neurotech.core_database_api.model.Tonic
import kotlinx.coroutines.flow.Flow
import java.util.*

interface TonicApi {
    suspend fun getTenMinuteAverage(): Flow<Int>
    suspend fun getHourAverage(): Flow<Int>
    suspend fun getDayAverage(): Flow<Int>
    suspend fun getTenMinuteAverageInInterval(beginDateTime: Date, endDateTime: Date): Int
    suspend fun writeTonic(tonic: Tonic)
}
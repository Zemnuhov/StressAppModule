package com.neurotech.core_database_api

import kotlinx.coroutines.flow.Flow

interface TonicApi {
    fun getTenMinuteAverage(): Flow<Int>
    fun getHourAverage(): Flow<Int>
    fun getDayAverage(): Flow<Int>
}
package com.neurotech.core_database_api

import com.neurotech.core_database_api.model.Phase
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.util.*

interface PhaseApi {
    suspend fun getPhaseCountInTenMinute(): Flow<Int>
    suspend fun getPhaseCountInHour(): Flow<Int>
    suspend fun getPhaseCountInDay(): Flow<Int>
    suspend fun getPhaseCountInInterval(beginDateTime: Date, endDateTime: Date): Int
    suspend fun getPhaseFromNow(dateTime: Date): Flow<Int>
    suspend fun writePhase(model: Phase)
}
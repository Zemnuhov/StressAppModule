package com.neurotech.core_database_api

import com.neurotech.core_database_api.model.*
import kotlinx.coroutines.flow.Flow


interface SettingApi {
    suspend fun getDevice(): Device?
    suspend fun rememberDevice(device: Device)
    suspend fun removedDevice()

    suspend fun getCauses(): Flow<Causes>
    suspend fun addCause(cause: Cause)
    suspend fun deleteCause(cause: Cause)

    suspend fun getDayPlans(): Flow<DayPlans>
    suspend fun addDayPlan(dayPlan: DayPlan)
    suspend fun deleteDayPlan(dayPlan: DayPlan)
}
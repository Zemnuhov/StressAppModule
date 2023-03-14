package com.neurotech.core_database_api

import com.neurotech.core_database_api.model.RelaxRecord
import com.neurotech.core_database_api.model.RelaxRecords
import kotlinx.coroutines.flow.Flow
import java.util.*

interface RelaxRecordApi {
    suspend fun writeRelaxRecord(record: RelaxRecord)
    suspend fun getRelaxRecordByDates(dates: List<Date>): Flow<RelaxRecords>
}
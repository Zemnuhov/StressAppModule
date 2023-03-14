package com.neurotech.core_database_impl.implementation

import com.cesarferreira.tempo.toString
import com.neurotech.core_database_api.RelaxRecordApi
import com.neurotech.core_database_api.model.RelaxRecord
import com.neurotech.core_database_api.model.RelaxRecords
import com.neurotech.core_database_impl.di.DatabaseComponent
import com.neurotech.core_database_impl.main_database.dao.RelaxRecordDao
import com.neurotech.core_database_impl.main_database.entity.RelaxRecordEntity
import com.neurotech.utils.TimeFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class RelaxDB : RelaxRecordApi {
    @Inject
    lateinit var relaxRecordDao: RelaxRecordDao

    init {
        DatabaseComponent.get().inject(this)
    }

    override suspend fun writeRelaxRecord(record: RelaxRecord) {
        withContext(Dispatchers.IO) {
            relaxRecordDao.writeRelaxRecord(
                RelaxRecordEntity(
                    0,
                    record.date.toString(TimeFormat.dateIsoPattern),
                    record.relaxationDuration,
                    record.phaseCount,
                    record.tonicAdjusted
                )
            )
        }
    }

    override suspend fun getRelaxRecordByDates(dates: List<Date>): Flow<RelaxRecords> =
        withContext(Dispatchers.IO) {
            return@withContext relaxRecordDao.getRelaxRecordByDates(dates.map {
                it.toString(
                    TimeFormat.dateIsoPattern
                )
            }).map { RelaxRecords(it.map { it.mapToRelaxRecord() }) }
        }
}
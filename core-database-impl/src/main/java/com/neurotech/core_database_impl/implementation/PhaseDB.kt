package com.neurotech.core_database_impl.implementation

import com.cesarferreira.tempo.toString
import com.neurotech.core_database_api.PhaseApi
import com.neurotech.core_database_api.model.Phase
import com.neurotech.core_database_impl.di.DatabaseComponent
import com.neurotech.core_database_impl.main_database.dao.PhaseDao
import com.neurotech.core_database_impl.main_database.entity.PhaseEntity
import com.neurotech.utils.TimeFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class PhaseDB: PhaseApi {

    @Inject
    lateinit var phaseDao: PhaseDao


    init {
        DatabaseComponent.get().inject(this)
    }

    override suspend fun getPhaseCountInTenMinute(): Flow<Int> = phaseDao.getTenMinuteCount()

    override suspend fun getPhaseCountInHour(): Flow<Int> = phaseDao.getOneHourCount()

    override suspend fun getPhaseCountInDay(): Flow<Int> = phaseDao.getOneDayCount()

    override suspend fun getPhaseCountInInterval(beginDateTime: Date, endDateTime: Date): Int = withContext(Dispatchers.IO) {
        return@withContext phaseDao.getPhaseInInterval(
            beginDateTime.toString(TimeFormat.dateTimeIsoPattern),
            endDateTime.toString(TimeFormat.dateTimeIsoPattern)
        )
    }

    override suspend fun writePhase(model: Phase) {
        phaseDao.insertPhase(
            PhaseEntity(
                model.timeBegin.toString(TimeFormat.dateTimeIsoPattern),
                model.timeEnd.toString(TimeFormat.dateTimeIsoPattern),
                model.max,
            )
        )
    }
}
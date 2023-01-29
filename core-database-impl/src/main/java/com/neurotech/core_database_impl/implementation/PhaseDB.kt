package com.neurotech.core_database_impl.implementation

import com.cesarferreira.tempo.Tempo
import com.cesarferreira.tempo.toString
import com.neurotech.core_database_api.PhaseApi
import com.neurotech.core_database_api.model.Phase
import com.neurotech.core_database_impl.di.DatabaseComponent
import com.neurotech.core_database_impl.main_database.dao.PhaseDao
import com.neurotech.core_database_impl.main_database.entity.PhaseEntity
import com.neurotech.utils.TimeFormat
import com.neurotech.utils.toString
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
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

    override suspend fun getPhaseCountInInterval(dateTime: Date): Flow<Int> =
        phaseDao.getPhaseInInterval(dateTime.toString(TimeFormat.dateTimeIsoPattern))

    override suspend fun writePhase(model: Phase) {
        phaseDao.insertPhase(
            PhaseEntity(
                model.timeBegin,
                model.timeEnd,
                model.max,
            )
        )
    }
}
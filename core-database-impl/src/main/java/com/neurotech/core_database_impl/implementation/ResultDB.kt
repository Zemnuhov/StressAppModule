package com.neurotech.core_database_impl.implementation

import com.cesarferreira.tempo.*
import com.neurotech.core_database_api.ResultApi
import com.neurotech.core_database_api.SettingApi
import com.neurotech.core_database_api.model.*
import com.neurotech.core_database_impl.di.DatabaseComponent
import com.neurotech.core_database_impl.main_database.dao.ResultDayDao
import com.neurotech.core_database_impl.main_database.dao.ResultHourDao
import com.neurotech.core_database_impl.main_database.dao.ResultTenMinuteDao
import com.neurotech.core_database_impl.main_database.entity.CountForCauseDB
import com.neurotech.core_database_impl.main_database.entity.ResultDayEntity
import com.neurotech.utils.TimeFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class ResultDB : ResultApi {

    @Inject
    lateinit var resultTenMinuteDao: ResultTenMinuteDao

    @Inject
    lateinit var resultHourDao: ResultHourDao

    @Inject
    lateinit var resultDayDao: ResultDayDao


    init {
        DatabaseComponent.get().inject(this)
    }

    override suspend fun getResultTenMinuteInLastHour(): Flow<ResultsTenMinute> {
        return resultTenMinuteDao.getResultsInOneHour().map {
            ResultsTenMinute(it.map { entity ->
                entity.mapToResultTenMinute()
            })
        }
    }

    override suspend fun getCountForEachCause(causes: Causes): Flow<CountForEachCause> {
        return flow {
            resultTenMinuteDao.getCountForEachCause(causes.values.map { it.name }).collect {
                emit(CountForEachCause(it.toMutableList().apply {
                    causes.values.forEach { cause ->
                        if (cause.name !in this.map { countForCauseDB -> countForCauseDB.cause }) {
                            add(CountForCauseDB(cause.name, 0))
                        }
                    }
                }.map { countForCauseDB ->
                    CountForCause(Cause(countForCauseDB.cause), countForCauseDB.count)
                }))
            }
        }
    }

    override suspend fun getResultsInInterval(
        beginInterval: Date,
        endInterval: Date
    ): Flow<ResultsTenMinute> {
        return resultTenMinuteDao
            .getResultsInInterval(
                beginInterval.toString(TimeFormat.dateTimeIsoPattern),
                endInterval.toString(TimeFormat.dateTimeIsoPattern)
            ).map {
                ResultsTenMinute(it.map { it.mapToResultTenMinute() })
            }
    }

    override suspend fun setKeepByTime(keep: String?, time: Date) {
        TODO("Not yet implemented")
    }

    override suspend fun getLastFiveValidDay(): Flow<ResultsDay> {
        return resultDayDao.getLastFiveValidDay().map {
            ResultsDay(
                it.map {
                    it.mapToResultDay()
                }
            )
        }
    }

    override suspend fun getMaxUserParameter(): UserParameters = withContext(Dispatchers.IO){
        return@withContext resultDayDao.getMaxParameters().mapToDomain()
    }

    override suspend fun getCountCauseInInterval(
        causes: Causes,
        beginInterval: Date,
        endInterval: Date
    ): Flow<CountForEachCause> {
        return flow {
            resultTenMinuteDao
                .getCountStressCauseInInterval(
                    beginInterval.toString(TimeFormat.dateTimeIsoPattern),
                    endInterval.toString(TimeFormat.dateTimeIsoPattern)
                ).map {
                    CountForEachCause(it.map { CountForCause(Cause(it.cause), it.count) })
                }.collect {
                    val resultList = it.list.toMutableList()
                    causes.values.forEach { cause ->
                        if (cause !in it.list.map { it.cause }) {
                            resultList.add(CountForCause(cause, 0))
                        }
                    }
                    emit(CountForEachCause(resultList))
                }
        }
    }

    override suspend fun getResultHourByInterval(
        beginInterval: Date,
        endInterval: Date
    ): Flow<ResultsHour> {
        return resultHourDao.getResultHourByInterval(
            beginInterval.toString(TimeFormat.dateTimeIsoPattern),
            endInterval.toString(TimeFormat.dateTimeIsoPattern)
        ).map {
            ResultsHour(it.map { it.mapToResultHour() })
        }
    }

    override suspend fun getMonthlyResults(month: Date, filToFull: Boolean): Flow<ResultsDay> {
        return flow {
            resultDayDao.getResultDayByInterval(
                month.beginningOfMonth.toString(TimeFormat.dateTimeIsoPattern),
                month.endOfMonth.toString(TimeFormat.dateTimeIsoPattern)
            ).map {
                it.map {
                    ResultDay(
                        it.date.toDate(TimeFormat.dateTimeIsoPattern),
                        it.peaks,
                        it.peaksAvg,
                        it.tonic,
                        it.stressCause
                    )
                }
            }.collect {
                val listResultDay = it.toMutableList()
                if (filToFull) {
                    val thisYear = month.toString("yyyy").toInt()
                    val thisMonth = month.toString("MM").toInt()
                    val dayInMonth = month.endOfMonth.toString("dd").toInt()
                    val existingDates = it.map { it.date }
                    for (day in 1..dayInMonth) {
                        val date =
                            Tempo.with(year = thisYear, month = thisMonth, day = day).beginningOfDay
                        if (date !in existingDates) {
                            listResultDay.add(
                                ResultDay(
                                    date, 0, 0, 0, ""
                                )
                            )
                        }
                    }
                    emit(
                        ResultsDay(
                            listResultDay
                        )
                    )
                }
            }
        }
    }
}
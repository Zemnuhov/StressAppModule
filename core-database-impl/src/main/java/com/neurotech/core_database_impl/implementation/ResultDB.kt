package com.neurotech.core_database_impl.implementation

import com.cesarferreira.tempo.*
import com.example.core_firebase_database_api.FirebaseDataApi
import com.neurotech.core_database_api.ResultApi
import com.neurotech.core_database_api.model.*
import com.neurotech.core_database_impl.di.DatabaseComponent
import com.neurotech.core_database_impl.main_database.dao.ResultDayDao
import com.neurotech.core_database_impl.main_database.dao.ResultHourDao
import com.neurotech.core_database_impl.main_database.dao.ResultTenMinuteDao
import com.neurotech.core_database_impl.main_database.entity.CountForCauseDB
import com.neurotech.core_database_impl.main_database.entity.ResultDayEntity
import com.neurotech.core_database_impl.main_database.entity.ResultHourEntity
import com.neurotech.core_database_impl.main_database.entity.ResultTenMinuteEntity
import com.neurotech.utils.TimeFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.thread

class ResultDB : ResultApi {

    @Inject
    lateinit var resultTenMinuteDao: ResultTenMinuteDao

    @Inject
    lateinit var resultHourDao: ResultHourDao

    @Inject
    lateinit var resultDayDao: ResultDayDao

    @Inject
    lateinit var firebaseData: FirebaseDataApi


    init {
        DatabaseComponent.get().inject(this)
    }

    override suspend fun writeResultTenMinute(resultTenMinute: ResultTenMinute) {
        withContext(Dispatchers.IO){
            launch {
                resultTenMinuteDao.insertResult(
                    ResultTenMinuteEntity(
                        resultTenMinute.time.toString(TimeFormat.dateTimeIsoPattern),
                        resultTenMinute.peakCount,
                        resultTenMinute.tonicAvg,
                        resultTenMinute.conditionAssessment,
                        resultTenMinute.stressCause,
                        resultTenMinute.keep
                    )
                )
            }
            launch {
                firebaseData.writeTenMinuteResult(resultTenMinute)
            }
        }
    }

    override suspend fun writeResultsTenMinute(resultsTenMinute: ResultsTenMinute) {
        withContext(Dispatchers.IO){
            resultTenMinuteDao.insertResult(
                *resultsTenMinute.list.map {
                    ResultTenMinuteEntity(
                        it.time.toString(TimeFormat.dateTimeIsoPattern),
                        it.peakCount,
                        it.tonicAvg,
                        it.conditionAssessment,
                        it.stressCause,
                        it.keep
                    )
                }.toTypedArray()
            )
        }
    }

    override suspend fun writeResultHour(resultsHour: ResultsHour) {
        withContext(Dispatchers.IO){
            resultHourDao.insertOrUpdate(resultsHour.list.map {
                ResultHourEntity(
                    it.date.toString(TimeFormat.dateTimeIsoPattern),
                    it.peaks,
                    it.peaksAvg,
                    it.tonic,
                    it.stressCause
                )
            }
            )
        }
    }

    override suspend fun writeResultDay(resultsDay: ResultsDay) {
        resultDayDao.insertOrUpdate(resultsDay.list.map {
            ResultDayEntity(
                it.date.toString(TimeFormat.dateTimeIsoPattern),
                it.peaks,
                it.peaksAvg,
                it.tonic,
                it.stressCause
            )
        })
    }

    override suspend fun getResultTenMinute(): Flow<ResultTenMinute?> {
        return resultTenMinuteDao.getResult().map { it?.mapToResultTenMinute() }
    }

    override suspend fun getResultsTenMinute(): Flow<ResultsTenMinute> {
        return resultTenMinuteDao.getResults().map { ResultsTenMinute(it.map { it.mapToResultTenMinute() }) }
    }

    override suspend fun getResultHour(): Flow<ResultHour> {
        TODO("Not yet implemented")
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
            resultTenMinuteDao.getCountForEachCause(causes.values.map { it.name }.filter {
                it !in arrayOf("Сон", "Артефакты")
            }).collect {
                emit(CountForEachCause(it.toMutableList().apply {
                    causes.values.forEach { cause ->
                        if (cause.name !in this.map { countForCauseDB -> countForCauseDB.cause } &&
                            cause.name !in arrayOf("Сон", "Артефакты")) {
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
        withContext(Dispatchers.IO){
            resultTenMinuteDao.setKeepByTime(keep, time.toString(TimeFormat.dateTimeIsoPattern))
            firebaseData
                .writeTenMinuteResult(
                    resultTenMinuteDao
                        .getResultByDateTime(
                            time.toString(TimeFormat.dateTimeIsoPattern)
                        )!!.mapToResultTenMinute()
                )
        }
    }

    override suspend fun updateResultTenMinute(resultsTenMinute: ResultsTenMinute) {
        withContext(Dispatchers.IO) {
            launch {
                resultTenMinuteDao.updateResult(
                    *resultsTenMinute.list.map {
                        ResultTenMinuteEntity(
                            it.time.toString(TimeFormat.dateTimeIsoPattern),
                            it.peakCount,
                            it.tonicAvg,
                            it.conditionAssessment,
                            it.stressCause,
                            it.keep
                        )
                    }.toTypedArray()
                )
            }
            thread(start = true) {
                launch {
                    firebaseData.writeTenMinuteResults(resultsTenMinute)
                }
            }
        }

    }

    override suspend fun setCauseByTime(cause: Cause, time: Date) {
        withContext(Dispatchers.IO){
            launch {
                resultTenMinuteDao.setCauseByTime(cause.name, time.toString(TimeFormat.dateTimeIsoPattern))
                firebaseData
                    .writeTenMinuteResult(
                        resultTenMinuteDao
                            .getResultByDateTime(
                                time.toString(TimeFormat.dateTimeIsoPattern)
                            )!!.mapToResultTenMinute()
                    )
            }
        }
    }

    override suspend fun deleteMarkupByTime(time: Date) {
        withContext(Dispatchers.IO){
            resultTenMinuteDao.deleteMarkupByTime(time.toString(TimeFormat.dateTimeIsoPattern))
        }
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

    override suspend fun getMaxUserParameter(): UserParameters = withContext(Dispatchers.IO) {
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
                    val mutableCauses = causes.values.toMutableList()
                    mutableCauses.removeAll(listOf(Cause("Артефакты"),Cause("Сон")))
                    mutableCauses.forEach { cause ->
                        if (cause !in it.list.map { it.cause }) {
                            resultList.add(CountForCause(cause, 0))
                        }
                    }
                    emit(CountForEachCause(resultList.sortedBy { it.cause.name }))
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

    override suspend fun getResultsTenMinuteAboveThreshold(threshold: Int): Flow<ResultsTenMinute> {
        return resultTenMinuteDao.getResultsTenMinuteAboveThreshold(threshold).map {
            ResultsTenMinute(
                it.map { it.mapToResultTenMinute() }
            )
        }
    }

    override suspend fun getResultHourFromResultTenMinute(
        beginInterval: Date,
        endInterval: Date
    ): ResultsHour {
        return ResultsHour(
            resultTenMinuteDao.getResultsHour(
                beginInterval.toString(TimeFormat.dateTimeIsoPattern),
                endInterval.toString(TimeFormat.dateTimeIsoPattern)
            ).map { it.mapToResultHour() }
        )


    }

    override suspend fun getResultDayFromResultTenMinute(
        beginInterval: Date,
        endInterval: Date
    ): ResultsDay {
        return ResultsDay(resultTenMinuteDao.getResultForTheDay(
            beginInterval.toString(TimeFormat.dateTimeIsoPattern),
            endInterval.toString(TimeFormat.dateTimeIsoPattern),
        ).map { it.mapToResultDay() })
    }

    override suspend fun getMonthlyResults(month: Date, filToFull: Boolean): Flow<ResultsDay> {
        return flow {
            resultDayDao.getResultDayByInterval(
                month.beginningOfMonth.toString(TimeFormat.dateTimeIsoPattern),
                month.endOfMonth.toString(TimeFormat.dateTimeIsoPattern)
            ).map { resultDayList ->
                resultDayList.map {
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
                    val existingDates = it.map { result -> result.date }
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
package com.neurotech.core_database_impl.implementation

import com.cesarferreira.tempo.toString
import com.neurotech.core_database_api.TonicApi
import com.neurotech.core_database_api.model.Tonic
import com.neurotech.core_database_impl.di.DatabaseComponent
import com.neurotech.core_database_impl.main_database.dao.TonicDao
import com.neurotech.core_database_impl.main_database.entity.TonicEntity
import com.neurotech.utils.TimeFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class TonicDB: TonicApi {

    @Inject
    lateinit var tonicDao: TonicDao

    init {
        DatabaseComponent.get().inject(this)
    }

    override suspend fun getTenMinuteAverage(): Flow<Int> = tonicDao.getTenMinuteAvg()

    override suspend fun getHourAverage(): Flow<Int> = tonicDao.getOneHourAvg()

    override suspend fun getDayAverage(): Flow<Int> = tonicDao.getOneDayAvg()
    override suspend fun getTenMinuteAverageInInterval(
        beginDateTime: Date,
        endDateTime: Date
    ): Int = withContext(Dispatchers.IO){
        return@withContext tonicDao.getTonicAverageInInterval(
            beginDateTime.toString(TimeFormat.dateTimeIsoPattern),
            endDateTime.toString(TimeFormat.dateTimeIsoPattern)
        )
    }

    override suspend fun writeTonic(tonic: Tonic) = withContext(Dispatchers.IO){
        if(tonic.value != 0){
            tonicDao.insertTonicValue(
                TonicEntity(
                    tonic.time.toString(TimeFormat.dateTimeIsoPattern),
                    tonic.value
                )
            )
        }
    }
}
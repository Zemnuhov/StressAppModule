package com.neurotech.core_database_impl.implementation

import com.neurotech.core_database_api.TonicApi
import com.neurotech.core_database_impl.di.DatabaseComponent
import com.neurotech.core_database_impl.main_database.dao.TonicDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import javax.inject.Inject

class TonicDB: TonicApi {

    @Inject
    lateinit var tonicDao: TonicDao

    init {
        DatabaseComponent.get().inject(this)
    }

    override fun getTenMinuteAverage(): Flow<Int> = tonicDao.getTenMinuteAvg()

    override fun getHourAverage(): Flow<Int> = tonicDao.getOneHourAvg()

    override fun getDayAverage(): Flow<Int> = tonicDao.getOneDayAvg()
}
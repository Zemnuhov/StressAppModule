package com.neurotech.core_database_impl.implementation

import com.neurotech.core_database_api.SettingApi
import com.neurotech.core_database_api.model.*
import com.neurotech.core_database_impl.di.DatabaseComponent
import com.neurotech.core_database_impl.setting_database.dao.CauseDao
import com.neurotech.core_database_impl.setting_database.dao.DayPlanDao
import com.neurotech.core_database_impl.setting_database.dao.DeviceDao
import com.neurotech.core_database_impl.setting_database.entity.CauseEntity
import com.neurotech.core_database_impl.setting_database.entity.DayPlanEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SettingDB: SettingApi {
    @Inject
    lateinit var deviceDao: DeviceDao

    @Inject
    lateinit var causeDao: CauseDao

    @Inject
    lateinit var dayPlanDao: DayPlanDao

    init {
        DatabaseComponent.get().inject(this)
        CoroutineScope(Dispatchers.IO).launch {
            if("Артефакты" !in causeDao.getCause().first().map { it.cause }){
                causeDao.insertCause(CauseEntity("Артефакты"))
            }
            if("Сон" !in causeDao.getCause().first().map { it.cause }){
                causeDao.insertCause(CauseEntity("Сон"))
            }
        }

    }


    override suspend fun getDevice(): Device? {
        //TODO
//        val device = deviceDao.getDevice()
//        if(device != null){
//            return Device(name = device.name, mac = device.mac)
//        }
        return null

    }

    override suspend fun getCauses(): Flow<Causes> {
        return causeDao
                .getCause()
                .map { entity ->
                    Causes(entity
                        .map { it.mapToCause() })
                }
    }

    override suspend fun addCause(cause: Cause) = withContext(Dispatchers.IO){
        causeDao.insertCause(CauseEntity(cause.name))
    }

    override suspend fun deleteCause(cause: Cause) = withContext(Dispatchers.IO){
        causeDao.deleteCause(CauseEntity(cause.name))
    }

    override suspend fun getDayPlans(): Flow<DayPlans> = dayPlanDao
        .getDayPlans()
        .map {
            DayPlans(
                it.map { entity ->
                    entity.mapToDayPlan()
                }
            )
        }


    override suspend fun addDayPlan(dayPlan: DayPlan) = withContext(Dispatchers.IO){
        dayPlanDao.insertDayPlan(
            DayPlanEntity(
                0,
                dayPlan.plan,
                dayPlan.timeBegin,
                dayPlan.timeEnd,
                dayPlan.firstSource,
                dayPlan.secondSource,
                dayPlan.autoMarkup
            )
        )
    }

    override suspend fun deleteDayPlan(dayPlan: DayPlan) = withContext(Dispatchers.IO){
        dayPlanDao.deleteDayPlan(
            DayPlanEntity(
                dayPlan.planId,
                dayPlan.plan,
                dayPlan.timeBegin,
                dayPlan.timeEnd,
                dayPlan.firstSource,
                dayPlan.secondSource,
                dayPlan.autoMarkup
            )
        )
    }
}
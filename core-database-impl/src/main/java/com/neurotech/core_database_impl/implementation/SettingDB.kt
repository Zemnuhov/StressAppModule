package com.neurotech.core_database_impl.implementation

import com.example.core_firebase_database_api.FirebaseDataApi
import com.neurotech.core_database_api.SettingApi
import com.neurotech.core_database_api.model.*
import com.neurotech.core_database_impl.di.DatabaseComponent
import com.neurotech.core_database_impl.setting_database.dao.CauseDao
import com.neurotech.core_database_impl.setting_database.dao.DayPlanDao
import com.neurotech.core_database_impl.setting_database.dao.DeviceDao
import com.neurotech.core_database_impl.setting_database.entity.CauseEntity
import com.neurotech.core_database_impl.setting_database.entity.DayPlanEntity
import com.neurotech.core_database_impl.setting_database.entity.DeviceEntity
import com.neurotech.utils.StressLogger.log
import com.neurotech.utils.WorkResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SettingDB : SettingApi {
    @Inject
    lateinit var deviceDao: DeviceDao

    @Inject
    lateinit var causeDao: CauseDao

    @Inject
    lateinit var dayPlanDao: DayPlanDao

    @Inject
    lateinit var firebaseDataApi: FirebaseDataApi

    init {
        DatabaseComponent.get().inject(this)
        CoroutineScope(Dispatchers.IO).launch {
            if ("Артефакты" !in causeDao.getCause().first().map { it.cause }) {
                causeDao.insertCause(CauseEntity("Артефакты"))
            }
            if ("Сон" !in causeDao.getCause().first().map { it.cause }) {
                causeDao.insertCause(CauseEntity("Сон"))
            }
        }

    }


    override suspend fun getDevice(): Device? = withContext(Dispatchers.IO) {
        val device = deviceDao.getDevice()
        if (device != null) {
            return@withContext Device(name = device.name, mac = device.mac)
        }
        return@withContext null

    }

    override suspend fun rememberDevice(device: Device) {
        withContext(Dispatchers.IO) {
            deviceDao.insertDevice(DeviceEntity(device.mac, device.name))
        }
    }

    override suspend fun removedDevice() {
        withContext(Dispatchers.IO) {
            deviceDao.removedDevice()
        }
    }

    override suspend fun getCauses(): Flow<Causes> {
        return causeDao
            .getCause()
            .map { entity ->
                Causes(entity
                    .map { it.mapToCause() })
            }
    }

    override suspend fun addCause(cause: Cause) = withContext(Dispatchers.IO) {
        causeDao.insertCause(CauseEntity(cause.name))
        firebaseDataApi.writeCause(cause)
    }

    override suspend fun deleteCause(cause: Cause) = withContext(Dispatchers.IO) {
        causeDao.deleteCause(CauseEntity(cause.name))
        firebaseDataApi.removeCause(cause)
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

    override suspend fun getDayPlanById(id: Int): DayPlan {
        return withContext(Dispatchers.IO) {
            dayPlanDao.getDayPlanById(id).mapToDayPlan()
        }
    }

    override suspend fun getDayPlanByTime(time: String): DayPlan? {
        return dayPlanDao.getDayPlanByTime(time)?.mapToDayPlan()
    }


    override suspend fun addDayPlan(dayPlan: DayPlan, autoGenerateId: Boolean) = withContext(Dispatchers.IO) {
        if(autoGenerateId){
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
        }else{
            dayPlanDao.insertDayPlan(
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
        firebaseDataApi.writeDayPlan(dayPlanDao.getDayPlan().mapToDayPlan())

    }

    private fun isValidTime(timeBegin: String, timeEnd: String): Boolean {
        val begin = timeBegin.split(":").joinToString("").toInt()
        val end = timeEnd.split(":").joinToString("").toInt()
        if (begin < end) {
            return true
        }
        return false
    }

    override suspend fun updateDayPlan(dayPlan: DayPlan): WorkResult = withContext(Dispatchers.IO) {
        if (isValidTime(dayPlan.timeBegin!!, dayPlan.timeEnd!!)) {
            dayPlanDao.updateDayPlan(
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
            firebaseDataApi.writeDayPlan(dayPlanDao.getDayPlan().mapToDayPlan())
            return@withContext WorkResult(isError = false)
        }
        return@withContext WorkResult(
            isError = true,
            message = "Событие не может начаться позже чем закончиться!"
        )
    }


    override suspend fun deleteDayPlan(dayPlan: DayPlan) = withContext(Dispatchers.IO) {
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
        firebaseDataApi.removeDayPlan(dayPlan)
    }
}
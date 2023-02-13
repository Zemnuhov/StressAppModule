package com.neurotech.core_database_impl.implementation

import com.cesarferreira.tempo.toString
import com.neurotech.core_database_api.UserApi
import com.neurotech.core_database_api.model.User
import com.neurotech.core_database_api.model.UserParameters
import com.neurotech.core_database_impl.di.DatabaseComponent
import com.neurotech.core_database_impl.main_database.dao.ResultTenMinuteDao
import com.neurotech.core_database_impl.user_database.dao.UserDao
import com.neurotech.core_database_impl.user_database.entity.UserEntity
import com.neurotech.utils.StressLogger.log
import com.neurotech.utils.TimeFormat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

class UserDB: UserApi {

    @Inject
    lateinit var userDao: UserDao

    @Inject
    lateinit var resultTenMinuteDao: ResultTenMinuteDao

    init {
        DatabaseComponent.get().inject(this)
    }

    override suspend fun getUser(): User  {
        val a = CoroutineScope(Dispatchers.IO).async {
            userDao.getUser()?.mapToUser()?:
            User("null",
                "",
                null,
                null,
                tonicAvg = 2000,
                phaseNormal = 15,
                phaseInHourNormal = 90,
                phaseInDayNormal = 2100)
        }
        return a.await()
    }


    override suspend fun registerUser(user: User) {
        userDao.registerUser(
            UserEntity(
                user.id,
                user.name,
                user.dateOfBirth?.toString(TimeFormat.dateIsoPattern),
                user.gender,
                user.tonicAvg,
                user.phaseNormal,
                user.phaseInHourNormal,
                user.phaseInDayNormal
            )
        )
    }

    override suspend fun setUserParameters(userParameters: UserParameters){
        userDao.setUserParameters(userParameters)
    }

    override suspend fun getUserParameters(): Flow<UserParameters> {
        return resultTenMinuteDao.getUserParameter().map {
            it.mapToDomain()
        }
    }

    override suspend fun getUserParametersByInterval(begin: Date, end: Date): Flow<UserParameters> {
        return resultTenMinuteDao.getUserParameterInInterval(
            begin.toString(TimeFormat.dateIsoPattern),
            end.toString(TimeFormat.dateIsoPattern)
        ).map { it.mapToDomain() }
    }

    override suspend fun setDateOfBirth(date: Date) {
        userDao.setBirthDate(date.toString(TimeFormat.dateIsoPattern))
    }

    override suspend fun setGender(gender: Boolean) {
        userDao.setGender(
            if (gender){
                "male"
            }else{
                "female"
            }
        )
    }
}
package com.example.core_database_control_impl

import android.content.Context
import com.cesarferreira.tempo.*
import com.example.core_database_control_api.DatabaseControllerApi
import com.example.core_database_control_impl.di.DatabaseControlComponent
import com.example.core_firebase_database_api.FirebaseDataApi
import com.neurotech.core_database_api.PhaseApi
import com.neurotech.core_database_api.ResultApi
import com.neurotech.core_database_api.TonicApi
import com.neurotech.core_database_api.UserApi
import com.neurotech.core_database_api.model.ResultTenMinute
import com.neurotech.core_database_api.model.UserParameters
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.io.File
import javax.inject.Inject

class DatabaseController : DatabaseControllerApi {

    @Inject
    lateinit var resultApi: ResultApi

    @Inject
    lateinit var phaseApi: PhaseApi

    @Inject
    lateinit var tonicApi: TonicApi

    @Inject
    lateinit var userApi: UserApi

    @Inject
    lateinit var firebaseData: FirebaseDataApi

    @Inject
    lateinit var context: Context


    private val file:File

    init {
        DatabaseControlComponent.get().inject(this)
        file = File(context.getExternalFilesDir("files"), "file.txt")
    }

    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun controlResultTenMinute() {
        CoroutineScope(newSingleThreadContext("TimeThread")).launch {
            while (true) {
                val time = Tempo.now.beginningOfMinute
                if (time.toString("mm").toInt() % 10 == 0) {
                    val tonic = tonicApi.getTenMinuteAverageInInterval(time - 10.minute, time)
                    if(tonic != 0){
                        resultApi.writeResultTenMinute(
                            ResultTenMinute(
                                time,
                                phaseApi.getPhaseCountInInterval(time - 10.minute, time),
                                tonic,
                                1,
                                null,
                                null
                            )
                        )
                        file.appendText(Tempo.now.toString() + " Writen\n")
                    }
                }
                delay(10000)
            }
        }
    }

    override suspend fun controlResultHour() {
        withContext(Dispatchers.IO) {
            resultApi.getResultTenMinute().collect {
                resultApi.writeResultHour(
                    resultApi.getResultHourFromResultTenMinute(
                        (Tempo.now - 1.year),
                        Tempo.now
                    )
                )
            }
        }
    }

    override suspend fun controlResultDay() {
        withContext(Dispatchers.IO){
            resultApi.getResultTenMinute().collect{
                resultApi.writeResultDay(
                    resultApi.getResultDayFromResultTenMinute(
                        (Tempo.now - 1.year),
                        Tempo.now
                    )
                )
            }
        }
    }

    override suspend fun controlUserData(){
        withContext(Dispatchers.IO){
            resultApi.getResultTenMinute().collect{
                val params = userApi.getUserParameters().first()
                userApi.setUserParameters(
                    UserParameters(
                        (params.tonic * 0.8).toInt(),
                        (params.tenMinutePhase * 0.8).toInt(),
                        (params.hourPhase * 0.8).toInt(),
                        (params.dayPhase * 0.8).toInt(),
                    )
                )
                firebaseData.setUser(userApi.getUser())
            }
        }
    }
}
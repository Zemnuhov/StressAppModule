package com.example.core_database_control_impl

import com.cesarferreira.tempo.*
import com.example.core_database_control_api.DatabaseControllerApi
import com.example.core_database_control_impl.di.DatabaseControlComponent
import com.neurotech.core_database_api.PhaseApi
import com.neurotech.core_database_api.ResultApi
import com.neurotech.core_database_api.TonicApi
import com.neurotech.core_database_api.model.ResultTenMinute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DatabaseController : DatabaseControllerApi {

    @Inject
    lateinit var resultApi: ResultApi

    @Inject
    lateinit var phaseApi: PhaseApi

    @Inject
    lateinit var tonicApi: TonicApi

    init {
        DatabaseControlComponent.get().inject(this)
    }


    override suspend fun controlResultTenMinute() {
        withContext(Dispatchers.IO) {
            while (true) {
                val time = Tempo.now.beginningOfMinute
                if (time.toString("mm").toInt() % 10 == 0) {
                    resultApi.writeResultTenMinute(
                        ResultTenMinute(
                            time,
                            phaseApi.getPhaseCountInInterval(time - 10.minute, time),
                            tonicApi.getTenMinuteAverageInInterval(time - 10.minute, time),
                            1,
                            null,
                            null
                        )
                    )
                }
                delay(60000)
            }
        }
    }

    override suspend fun controlResultHour() {
        withContext(Dispatchers.IO) {
            resultApi.getResultTenMinute().collect {
                resultApi.writeResultHour(
                    resultApi.getResultHourFromResultTenMinute(
                        (Tempo.now - 1.day),
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
}
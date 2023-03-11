package com.example.core_firebase_controller_impl

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.core_firebase_controller_impl.di.FirebaseControllerComponent
import com.example.core_firebase_database_api.FirebaseDataApi
import com.neurotech.core_database_api.ResultApi
import com.neurotech.core_database_api.model.ResultsTenMinute
import com.neurotech.utils.StressLogger.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class FirebaseController (context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {
    @Inject
    lateinit var resultApi: ResultApi

    @Inject
    lateinit var firebaseDataApi: FirebaseDataApi

    init {
        FirebaseControllerComponent.get().inject(this)
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO){
        try{
            val localDatabase = resultApi.getResultsTenMinute().first()
            val firebaseDatabase = firebaseDataApi.readTenMinuteResults()
            val checkLocalDatabase =
                localDatabase.list.map { ResultTimeAndCause(it.time, it.stressCause) }
            val checkFirebaseDatabase =
                firebaseDatabase.list.map { ResultTimeAndCause(it.time, it.stressCause) }

            val mutableCheckLocalDatabase = checkLocalDatabase.toMutableList()
            mutableCheckLocalDatabase.removeAll(checkFirebaseDatabase)
            val mutableCheckFirebaseDatabase = checkFirebaseDatabase.toMutableList()
            mutableCheckFirebaseDatabase.removeAll(checkLocalDatabase)

            mutableCheckLocalDatabase.forEach { result ->
                if (result.time in mutableCheckFirebaseDatabase.map { it.time }) {
                    val localResult = localDatabase.list.filter { it.time == result.time }[0]
                    val firebaseResult = firebaseDatabase.list.filter { it.time == result.time }[0]
                    if (localResult.stressCause != null) {
                        firebaseDataApi.writeTenMinuteResult(localResult)
                    } else {
                        resultApi.updateResultTenMinute(ResultsTenMinute(listOf(firebaseResult)))
                    }
                } else {
                    val localResult = localDatabase.list.filter { it.time == result.time }[0]
                    firebaseDataApi.writeTenMinuteResult(localResult)
                }
            }
        }catch (e: java.lang.Exception){
            return@withContext Result.failure()
        }

        return@withContext Result.success()
    }

    internal data class ResultTimeAndCause(
        val time: Date,
        val cause: String?
    )
}
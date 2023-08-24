package com.example.core_firebase_database_impl

import com.cesarferreira.tempo.toString
import com.example.core_firebase_database_api.FirebaseDataApi
import com.example.core_firebase_database_impl.di.FirebaseDataComponent
import com.example.core_firebase_database_impl.model.CauseFirebase
import com.example.core_firebase_database_impl.model.DayPlanFirebase
import com.example.core_firebase_database_impl.model.ResultTenMinuteFirebase
import com.example.core_firebase_database_impl.model.UserFirebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.neurotech.core_database_api.model.*
import com.neurotech.utils.StressLogger.log
import com.neurotech.utils.TimeFormat
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseData : FirebaseDataApi {

    @Inject
    lateinit var firebaseDatabase: FirebaseDatabase


    private val databaseReference: DatabaseReference by lazy { firebaseDatabase.reference }
    private val firebaseUser get() = Firebase.auth.currentUser

    @OptIn(DelicateCoroutinesApi::class)
    private val scope = CoroutineScope(newFixedThreadPoolContext(64, "FirebaseThreadPool"))

    init {
        FirebaseDataComponent.get().inject(this)
    }

    override suspend fun writeLedMode(mode: String) {
        withContext(Dispatchers.IO) {
            launch {
                firebaseUser?.let {
                    databaseReference.child("lamp").child(it.uid).child("mode").setValue(mode)
                }
            }
        }
    }
    override suspend fun writeTonicValue(tonic: Tonic) {
        withContext(Dispatchers.IO) {
            launch {
                firebaseUser?.let {
                    databaseReference.child("lamp").child(it.uid).child("tonicValue").setValue(tonic.value)
                }
            }
        }
    }

    override suspend fun getUserFromFirebase(): User? = withContext(Dispatchers.IO) {
        return@withContext databaseReference
            .child("users")
            .child(firebaseUser?.uid ?: "")
            .get()
            .await().getValue<UserFirebase>()?.toUserEntity()
    }

    override suspend fun setUser(user: User) {
        withContext(Dispatchers.IO) {
            launch {
                firebaseUser?.let {
                    if (it.uid == user.id) {
                        databaseReference.child("users").child(user.id).setValue(
                            UserFirebase(
                                user.id,
                                user.name,
                                user.dateOfBirth?.toString(TimeFormat.dateIsoPattern),
                                user.gender,
                                user.tonicAvg,
                                user.phaseInDayNormal,
                                user.phaseInHourNormal,
                                user.phaseNormal
                            )
                        )
                    }
                }
            }
        }
    }


    override suspend fun writeTenMinuteResult(result: ResultTenMinute) {
        firebaseUser?.let { user ->
            withContext(Dispatchers.IO) {
                databaseReference
                    .child("tenMinutesData")
                    .child(user.uid)
                    .child(result.time.toString(TimeFormat.firebaseDateTimePattern))
                    .setValue(
                        mapToResultTenMinuteFirebase(result)
                    ).addOnSuccessListener {
                        log("Write results in Firebase: ${result.time}")
                    }
            }
        }
    }

    override suspend fun writeTenMinuteResults(results: ResultsTenMinute) {
        firebaseUser?.let { user ->
            withContext(Dispatchers.IO) {
                results.list.forEachIndexed { index, result ->
                    val key = result.time.toString(TimeFormat.firebaseDateTimePattern)
                    databaseReference
                        .child("tenMinutesData")
                        .child(user.uid)
                        .child(key)
                        .setValue(mapToResultTenMinuteFirebase(result))
                        .addOnSuccessListener {
                            log("Write result $index in Firebase: $key")
                        }
                }
            }
        }
    }


    override suspend fun getCauses(): Causes = withContext(Dispatchers.IO) {
        var causeList = emptyList<Cause>()
        if (firebaseUser != null) {
            val causeTask = databaseReference
                .child("causes")
                .child(firebaseUser!!.uid)
                .get().addOnFailureListener {
                    log("Read Causes from Firebase failure: ${it.message}")
                }.await()
            causeList = causeTask.children.mapNotNull {
                it.getValue<CauseFirebase>()?.mapToCause()
            }
        }
        return@withContext Causes(causeList)
    }


    override suspend fun writeCause(cause: Cause) {
        withContext(Dispatchers.IO) {
            databaseReference.child("causes").child(firebaseUser!!.uid).child(cause.name)
                .setValue(cause)
        }
    }

    override suspend fun writeCauses(causes: Causes) {
        withContext(Dispatchers.IO) {
            causes.values.forEach {
                databaseReference.child("causes").child(firebaseUser!!.uid).child(it.name)
                    .setValue(it)
            }
        }
    }

    override suspend fun removeCause(cause: Cause) {
        databaseReference.child("causes").child(firebaseUser!!.uid).child(cause.name).removeValue()
    }


    override suspend fun readTenMinuteResults(): ResultsTenMinute = withContext(Dispatchers.IO) {
        var resultList = emptyList<ResultTenMinute>()
        if(firebaseUser != null){
            val resultTenMinuteTask = databaseReference
                .child("tenMinutesData")
                .child(firebaseUser!!.uid)
                .get().addOnFailureListener {
                    log("Read from Firebase failure: ${it.message}")
                }.await()

            resultList = resultTenMinuteTask.children.mapNotNull {
                it.getValue<ResultTenMinuteFirebase>()?.toResultEntity()
            }
        }
        return@withContext ResultsTenMinute(resultList)
    }

    override suspend fun readTenMinuteResultsByLimit(limit: Int): ResultsTenMinute = withContext(Dispatchers.IO) {
        var resultList = emptyList<ResultTenMinute>()
        if(firebaseUser != null){
            val resultTenMinuteTask = databaseReference
                .child("tenMinutesData")
                .child(firebaseUser!!.uid)
                .limitToLast(limit)
                .get().addOnFailureListener {
                    log("Read from Firebase failure: ${it.message}")
                }.await()

            resultList = resultTenMinuteTask.children.mapNotNull {
                it.getValue<ResultTenMinuteFirebase>()?.toResultEntity()
            }
        }
        return@withContext ResultsTenMinute(resultList)
    }

    private fun mapToResultTenMinuteFirebase(result: ResultTenMinute): ResultTenMinuteFirebase {
        return ResultTenMinuteFirebase(
            result.time.toString(TimeFormat.dateTimeIsoPattern),
            result.peakCount,
            result.tonicAvg,
            result.conditionAssessment,
            result.stressCause,
            result.keep
        )
    }

    override suspend fun writeDayPlan(dayPlan: DayPlan) {
        withContext(Dispatchers.IO) {
            databaseReference.child("dayPlan").child(firebaseUser!!.uid)
                .child(dayPlan.planId.toString())
                .setValue(dayPlan)
        }
    }

    override suspend fun writeDayPlans(dayPlans: DayPlans) {
        withContext(Dispatchers.IO) {
            dayPlans.values.forEach {
                writeDayPlan(it)
            }
        }
    }

    override suspend fun removeDayPlan(dayPlan: DayPlan) {
        databaseReference.child("dayPlan").child(firebaseUser!!.uid)
            .child(dayPlan.planId.toString()).removeValue()
    }

    override suspend fun getDayPlans(): DayPlans = withContext(Dispatchers.IO) {
        var dayPlanList = emptyList<DayPlan>()
        if (firebaseUser != null) {
            val dayPlanTask = databaseReference
                .child("dayPlan")
                .child(firebaseUser!!.uid)
                .get().addOnFailureListener {
                    log("Read DayPlans from Firebase failure: ${it.message}")
                }.await()
            dayPlanList = dayPlanTask.children.mapNotNull {
                it.getValue<DayPlanFirebase>()?.mapToDayPlan()
            }
        }
        return@withContext DayPlans(dayPlanList)
    }

}
package com.example.core_firebase_database_impl

import android.util.Log
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

    @OptIn(ExperimentalCoroutinesApi::class)
    private val scope = CoroutineScope(Dispatchers.IO.limitedParallelism(128))

    init {
        FirebaseDataComponent.get().inject(this)
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
                val firebaseUser = firebaseUser
                if (firebaseUser != null && firebaseUser.uid == user.id) {
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


    override suspend fun writeTenMinuteResult(result: ResultTenMinute) {
        withContext(Dispatchers.IO) {
            if (firebaseUser != null) {
                val key = result.time.toString(TimeFormat.firebaseDateTimePattern)
                databaseReference
                    .child("tenMinutesData")
                    .child(firebaseUser!!.uid)
                    .child(key)
                    .setValue(
                        mapToResultTenMinuteFirebase(result)
                    ).addOnSuccessListener {
                        log("Write results in Firebase: $key")
                    }
            }
        }
    }

    override suspend fun writeTenMinuteResults(results: ResultsTenMinute) {
        scope.launch {
            if (firebaseUser != null) {
                results.list.forEach {
                    val key = it.time.toString(TimeFormat.firebaseDateTimePattern)
                    databaseReference
                        .child("tenMinutesData")
                        .child(firebaseUser!!.uid)
                        .child(key)
                        .setValue(
                            mapToResultTenMinuteFirebase(it)
                        ).addOnSuccessListener {
                            log("Write results in Firebase: $key")
                        }.await()
                }

            }

        }
    }

    override suspend fun getCauses(): Causes = withContext(Dispatchers.IO) {
        var isRead = false
        var causeList = emptyList<Cause>()
        if (firebaseUser != null) {
            databaseReference
                .child("causes")
                .child(firebaseUser!!.uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        causeList = snapshot.children.mapNotNull {
                            it.getValue<CauseFirebase>()?.mapToCause()
                        }
                        isRead = true
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("Firebase read error: ", error.message)
                        Log.e("Firebase read error: ", error.details)
                        Log.e("Firebase read error: ", error.code.toString())
                    }

                }
                )
        } else isRead = true
        return@withContext scope.async {
            while (!isRead) {
                delay(1)
            }
            return@async Causes(causeList)
        }.await()
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


    //TODO(Попробовать с JOB)
    override suspend fun readTenMinuteResults(): ResultsTenMinute = withContext(Dispatchers.IO) {
        val resultList = mutableListOf<ResultTenMinute>()
        var isRead = false
        if (firebaseUser != null) {
            databaseReference
                .child("tenMinutesData")
                .child(firebaseUser!!.uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {
                            val receivedData = it.getValue<ResultTenMinuteFirebase>()
                            if (receivedData != null) {
                                resultList.add(receivedData.toResultEntity())
                            }
                        }
                        isRead = true

                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("Firebase read error: ", error.message)
                        Log.e("Firebase read error: ", error.details)
                        Log.e("Firebase read error: ", error.code.toString())
                    }

                }
                )
        } else isRead = true
        return@withContext scope.async {
            while (!isRead) {
                delay(1)
            }
            return@async ResultsTenMinute(resultList)
        }.await()
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
        var isRead = false
        var dayPlanList = emptyList<DayPlan>()
        if (firebaseUser != null) {
            databaseReference
                .child("dayPlan")
                .child(firebaseUser!!.uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        dayPlanList = snapshot.children.mapNotNull {
                            it.getValue<DayPlanFirebase>()?.mapToDayPlan()
                        }
                        isRead = true
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("Firebase read error: ", error.message)
                        Log.e("Firebase read error: ", error.details)
                        Log.e("Firebase read error: ", error.code.toString())
                    }

                }
                )
        } else isRead = true
        return@withContext scope.async {
            while (!isRead) {
                delay(1)
            }
            return@async DayPlans(dayPlanList)
        }.await()
    }

}
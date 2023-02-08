package com.example.core_firebase_database_impl

import android.util.Log
import com.cesarferreira.tempo.toString
import com.example.core_firebase_auth.FirebaseAuthApi
import com.example.core_firebase_database_impl.model.ResultTenMinuteFirebase
import com.example.core_firebase_database_impl.model.UserFirebase
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.neurotech.core_database_api.model.ResultTenMinute
import com.neurotech.core_database_api.model.ResultsTenMinute
import com.neurotech.core_database_api.model.User
import com.neurotech.utils.TimeFormat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseData {

    @Inject
    lateinit var firebaseDatabase: FirebaseDatabase

    @Inject
    lateinit var firebaseAuthApi: FirebaseAuthApi

    private val databaseReference: DatabaseReference by lazy { firebaseDatabase.reference }
    private val firebaseUser by lazy { runBlocking { firebaseAuthApi.user.first() }  }



    private val scope = CoroutineScope(Dispatchers.IO)
    init {

    }

    suspend fun getUserFromFirebase(): User? = withContext(Dispatchers.IO) {
        return@withContext databaseReference
            .child("users")
            .child(firebaseUser?.uid ?: "")
            .get()
            .await().getValue<UserFirebase>()?.toUserEntity()
    }

    suspend fun setUser(user: User) = withContext(Dispatchers.IO) {
        databaseReference.child("users").child(user.id).setValue(user)
    }

    suspend fun writeTenMinuteResult(result: ResultTenMinute) = withContext(Dispatchers.IO) {
        if (firebaseUser != null) {
            val key = result.time.toString(TimeFormat.firebaseDateTimePattern)
            databaseReference.child("tenMinutesData").child(firebaseUser!!.uid).child(key).setValue(result)
        }
    }

    suspend fun writeTenMinuteResults(results: ResultsTenMinute) = withContext(Dispatchers.IO) {
        scope.launch {
            if (firebaseUser != null) {
                results.list.forEach {
                    val key = it.time.toString(TimeFormat.firebaseDateTimePattern)
                    databaseReference.child("tenMinutesData")
                        .child(firebaseUser!!.uid)
                        .child(key)
                        .setValue(it)
                }
            }
        }
    }

    //TODO(Попробовать с JOB)
    suspend fun readTenMinuteResults(): ResultsTenMinute = withContext(Dispatchers.IO) {
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
            }
            return@async ResultsTenMinute(resultList)
        }.await()
    }

}
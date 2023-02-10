package com.example.core_firebase_database_api

import com.neurotech.core_database_api.model.*


interface FirebaseDataApi {
    suspend fun getUserFromFirebase(): User?
    suspend fun setUser(user: User)
    suspend fun writeTenMinuteResult(result: ResultTenMinute)
    suspend fun writeTenMinuteResults(results: ResultsTenMinute)
    suspend fun getCauses(): Causes
    suspend fun writeCause(cause: Cause)
    suspend fun writeCauses(causes: Causes)
    suspend fun removeCause(cause: Cause)

    suspend fun readTenMinuteResults(): ResultsTenMinute
}
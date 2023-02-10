package com.example.core_firebase_database_api

import com.neurotech.core_database_api.model.ResultTenMinute
import com.neurotech.core_database_api.model.ResultsTenMinute
import com.neurotech.core_database_api.model.User


interface FirebaseDataApi {
    suspend fun getUserFromFirebase(): User?
    suspend fun setUser(user: User)
    suspend fun writeTenMinuteResult(result: ResultTenMinute)
    suspend fun writeTenMinuteResults(results: ResultsTenMinute)
    suspend fun readTenMinuteResults(): ResultsTenMinute
}
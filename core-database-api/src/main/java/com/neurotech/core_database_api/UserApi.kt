package com.neurotech.core_database_api

import com.neurotech.core_database_api.model.User
import com.neurotech.core_database_api.model.UserParameters
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.util.*

interface UserApi {
    suspend fun getUser(): User
    suspend fun registerUser(user: User)
    suspend fun getUserParameters(): Flow<UserParameters>
    suspend fun setDateOfBirth(date: Date)
    suspend fun setGender(gender: Boolean)
}
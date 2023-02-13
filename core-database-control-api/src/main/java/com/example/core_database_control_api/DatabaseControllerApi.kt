package com.example.core_database_control_api

interface DatabaseControllerApi {
    suspend fun controlResultTenMinute()
    suspend fun controlResultHour()
    suspend fun controlResultDay()
    suspend fun controlUserData()
}
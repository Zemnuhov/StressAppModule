package com.example.core_screen_controller

import kotlinx.coroutines.flow.Flow

interface ScreenControllerApi {
    suspend fun getMainScreenStateFlow(): Flow<ScreenState>
    fun setState(state: ScreenState)
}
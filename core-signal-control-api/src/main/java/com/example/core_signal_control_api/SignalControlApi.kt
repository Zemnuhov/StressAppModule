package com.example.core_signal_control_api

interface SignalControlApi {
    suspend fun listenPhaseSignal()
    suspend fun listenTonicSignal()
}
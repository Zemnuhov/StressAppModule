package com.neurotech.core_bluetooth_comunication_impl.model

import java.util.Date

data class PhaseEntityFromDevice(
    val timeBegin: Date,
    val timeEnd: Date,
    val max: Double
)
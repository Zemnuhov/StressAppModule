package com.neurotech.core_database_api.model

import java.time.LocalDateTime
import java.util.*

data class User(
    val id:String,
    val name: String,
    val dateOfBirth: Date?,
    val gender: String?,
    val tonicAvg: Int,
    val phaseNormal:Int,
    val phaseInHourNormal:Int,
    val phaseInDayNormal:Int
)
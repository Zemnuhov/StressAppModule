package com.neurotech.core_database_api

import java.util.Date

data class RelaxRecord(
    val date: Date,
    val relaxationDuration: Int,
    val phaseCount: Int,
    val tonicAdjusted: Int
)

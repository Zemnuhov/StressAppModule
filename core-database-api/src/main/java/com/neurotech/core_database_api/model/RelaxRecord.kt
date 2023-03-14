package com.neurotech.core_database_api.model

import java.util.Date

data class RelaxRecords(
    val value:List<RelaxRecord>
)

data class RelaxRecord(
    val date: Date,
    val relaxationDuration: Int,
    val phaseCount: Int,
    val tonicAdjusted: Int
)

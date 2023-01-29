package com.neurotech.core_database_api.model

data class CountForEachCause(
    val list: List<CountForCause>
)

data class CountForCause(
    val cause: Cause,
    val count: Int
)

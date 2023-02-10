package com.example.core_firebase_database_impl.model

import com.neurotech.core_database_api.model.Cause

data class CauseFirebase(
    val name: String? = null
){
    fun mapToCause(): Cause = Cause(name!!)
}

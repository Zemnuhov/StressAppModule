package com.example.core_firebase_auth_impl.model

import com.cesarferreira.tempo.toDate
import com.google.firebase.database.IgnoreExtraProperties
import com.neurotech.core_database_api.model.User
import com.neurotech.utils.TimeFormat

@IgnoreExtraProperties
data class UserFirebase(
    val id: String? = null,
    val name: String? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val tonicAvg: Int? = null,
    val peakInDayNormal: Int? = null,
    val peakInHourNormal: Int? = null,
    val peakNormal: Int? = null
) {
    fun toUserEntity(): User {
        return User(
            id!!,
            name!!,
            dateOfBirth?.toDate(TimeFormat.dateIsoPattern),
            gender,
            tonicAvg!!,
            peakInDayNormal!!,
            peakInHourNormal!!,
            peakNormal!!
        )
    }
}

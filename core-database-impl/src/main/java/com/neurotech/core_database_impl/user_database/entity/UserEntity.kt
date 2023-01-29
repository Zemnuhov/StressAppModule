package com.neurotech.core_database_impl.user_database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cesarferreira.tempo.toDate
import com.neurotech.core_database_api.model.User
import com.neurotech.utils.TimeFormat
import com.neurotech.utils.toLocalDateTime

@Entity
data class UserEntity(
    @PrimaryKey
    val id:String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "dateOfBirth")
    val dateOfBirth: String?,
    @ColumnInfo(name = "gender")
    val gender: String?,
    @ColumnInfo(name = "tonicAvg")
    val tonicAvg: Int,
    @ColumnInfo(name = "phaseNormal")
    val phaseNormal:Int,
    @ColumnInfo(name = "phaseInHourNormal")
    val phaseInHourNormal:Int,
    @ColumnInfo(name = "phaseInDayNormal")
    val phaseInDayNormal:Int
){
    fun mapToUser(): User {
        return User(
            id,name,dateOfBirth?.toDate(TimeFormat.dateTimeIsoPattern),gender,tonicAvg,phaseNormal,phaseInHourNormal,phaseInDayNormal
        )
    }
}
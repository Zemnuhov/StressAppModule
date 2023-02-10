package com.neurotech.core_database_impl.user_database.dao

import androidx.room.*
import com.neurotech.core_database_api.model.UserParameters
import com.neurotech.core_database_impl.main_database.model.UserParameterDB
import com.neurotech.core_database_impl.user_database.entity.UserEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface UserDao {
    @Insert
    fun addUser(userEntity: UserEntity)

    @Transaction
    fun registerUser(userEntity: UserEntity): Boolean {
        unregisterUser()
        addUser(userEntity)
        return true
    }

    @Transaction
    fun setUserParameters(userParameters: UserParameters): Boolean {

        return true
    }


    @Query("SELECT * FROM UserEntity")
    fun getUser(): UserEntity?

    @Query("SELECT * FROM UserEntity")
    fun getUserFlow(): Flow<UserEntity?>

    @Query("SELECT id FROM UserEntity")
    fun getUserId(): Int

    @Query("SELECT COUNT(*) FROM UserEntity")
    fun countUser(): Int

    @Query("DELETE FROM UserEntity")
    fun unregisterUser()

    @Query("UPDATE UserEntity SET dateOfBirth = :birthDate")
    fun setBirthDate(birthDate: String)

    @Query("UPDATE UserEntity SET gender = :gender")
    fun setGender(gender: String)

    @Update
    fun updateUser(userEntity: UserEntity)

    @Transaction
    fun insertUser(userEntity: UserEntity): Boolean {
        if (countUser() == 0) {
            addUser(userEntity)
            return true
        }
        return false
    }

}
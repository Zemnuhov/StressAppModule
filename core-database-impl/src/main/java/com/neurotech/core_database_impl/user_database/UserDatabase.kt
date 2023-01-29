package com.neurotech.core_database_impl.user_database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.neurotech.core_database_impl.user_database.dao.UserDao
import com.neurotech.core_database_impl.user_database.entity.UserEntity

@Database(
    entities = [
        UserEntity::class
               ],
    version = 1
)
abstract class UserDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
}
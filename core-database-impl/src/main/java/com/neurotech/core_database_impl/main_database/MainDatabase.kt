package com.neurotech.core_database_impl.main_database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.neurotech.core_database_impl.main_database.dao.*
import com.neurotech.core_database_impl.main_database.entity.*

@Database(
    entities = [
        PhaseEntity::class,
        TonicEntity::class,
        ResultTenMinuteEntity::class,
        ResultHourEntity::class,
        ResultDayEntity::class],
    version = 1
)
abstract class MainDatabase: RoomDatabase() {
    abstract fun phaseDao(): PhaseDao
    abstract fun tonicDao(): TonicDao
    abstract fun resultTenMinuteDao(): ResultTenMinuteDao
    abstract fun resultHourDao(): ResultHourDao
    abstract fun resultDayDao(): ResultDayDao
}
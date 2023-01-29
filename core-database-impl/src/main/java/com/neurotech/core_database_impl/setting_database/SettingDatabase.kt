package com.neurotech.core_database_impl.setting_database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.neurotech.core_database_impl.setting_database.dao.CauseDao
import com.neurotech.core_database_impl.setting_database.dao.DayPlanDao
import com.neurotech.core_database_impl.setting_database.dao.DeviceDao
import com.neurotech.core_database_impl.setting_database.entity.CauseEntity
import com.neurotech.core_database_impl.setting_database.entity.DayPlanEntity
import com.neurotech.core_database_impl.setting_database.entity.DeviceEntity

@Database(
    entities = [
        CauseEntity::class,
        DayPlanEntity::class,
        DeviceEntity::class
               ],
    version = 1
)
abstract class SettingDatabase: RoomDatabase() {
    abstract fun dayPlanDao(): DayPlanDao
    abstract fun causeDao(): CauseDao
    abstract fun deviceDao(): DeviceDao
}
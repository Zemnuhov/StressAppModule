package com.neurotech.core_database_impl.di

import com.neurotech.core_database_impl.setting_database.SettingDatabase
import com.neurotech.core_database_impl.setting_database.dao.CauseDao
import com.neurotech.core_database_impl.setting_database.dao.DayPlanDao
import com.neurotech.core_database_impl.setting_database.dao.DeviceDao
import dagger.Module
import dagger.Provides

@Module
class SettingDaoModule {

    @Provides
    @DatabaseScope
    fun provideCauseDao(database: SettingDatabase): CauseDao{
        return database.causeDao()
    }

    @Provides
    @DatabaseScope
    fun provideDayPlanDao(database: SettingDatabase): DayPlanDao{
        return database.dayPlanDao()
    }

    @Provides
    @DatabaseScope
    fun provideDeviceDao(database: SettingDatabase): DeviceDao{
        return database.deviceDao()
    }

}
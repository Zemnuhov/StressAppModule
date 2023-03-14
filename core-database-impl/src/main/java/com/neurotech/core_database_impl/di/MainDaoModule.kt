package com.neurotech.core_database_impl.di

import com.neurotech.core_database_impl.main_database.MainDatabase
import com.neurotech.core_database_impl.main_database.dao.*
import dagger.Module
import dagger.Provides

@Module
class MainDaoModule {

    @Provides
    @DatabaseScope
    fun providePhaseDao(database: MainDatabase): PhaseDao{
        return database.phaseDao()
    }

    @Provides
    @DatabaseScope
    fun provideTonicDao(database: MainDatabase): TonicDao{
        return database.tonicDao()
    }

    @Provides
    @DatabaseScope
    fun provideResultTenMinuteDao(database: MainDatabase): ResultTenMinuteDao{
        return database.resultTenMinuteDao()
    }

    @Provides
    @DatabaseScope
    fun provideResultHourDao(database: MainDatabase): ResultHourDao{
        return database.resultHourDao()
    }

    @Provides
    @DatabaseScope
    fun provideResultDayDao(database: MainDatabase): ResultDayDao{
        return database.resultDayDao()
    }

    @Provides
    @DatabaseScope
    fun provideRelaxRecordDao(database: MainDatabase): RelaxRecordDao{
        return database.relaxRecordDao()
    }

}
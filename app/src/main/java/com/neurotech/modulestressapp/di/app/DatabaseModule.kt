package com.neurotech.modulestressapp.di.app

import com.neurotech.core_database_api.*
import com.neurotech.core_database_impl.implementation.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideTonicApi(): TonicApi {
        return TonicDB()
    }

    @Provides
    @Singleton
    fun providePhaseApi(): PhaseApi {
        return PhaseDB()
    }

    @Provides
    @Singleton
    fun provideRelaxRecordApi(): RelaxRecordApi {
        return RelaxDB()
    }

    @Provides
    @Singleton
    fun provideResultTenMinuteApi(): ResultApi {
        return ResultDB()
    }

    @Provides
    @Singleton
    fun provideSettingApi(): SettingApi {
        return SettingDB()
    }

    @Provides
    @Singleton
    fun provideUserApi(): UserApi {
        return UserDB()
    }
}
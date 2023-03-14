package com.neurotech.modulestressapp.di

import com.neurotech.core_database_api.*
import com.neurotech.core_database_impl.implementation.*
import dagger.Module
import dagger.Provides

@Module
class DatabaseModule {

    @Provides
    @FeatureScope
    fun provideTonicApi(): TonicApi {
        return TonicDB()
    }

    @Provides
    @FeatureScope
    fun providePhaseApi(): PhaseApi {
        return PhaseDB()
    }

    @Provides
    @FeatureScope
    fun provideRelaxRecordApi(): RelaxRecordApi {
        return RelaxDB()
    }

    @Provides
    @FeatureScope
    fun provideResultTenMinuteApi(): ResultApi {
        return ResultDB()
    }

    @Provides
    @FeatureScope
    fun provideSettingApi(): SettingApi {
        return SettingDB()
    }

    @Provides
    @FeatureScope
    fun provideUserApi(): UserApi {
        return UserDB()
    }
}
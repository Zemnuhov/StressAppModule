package com.neurotech.core_database_impl.di

import android.content.Context
import androidx.room.Room
import com.neurotech.core_database_impl.main_database.MainDatabase
import com.neurotech.core_database_impl.setting_database.SettingDatabase
import com.neurotech.core_database_impl.user_database.UserDatabase
import dagger.Module
import dagger.Provides

@Module
class DatabaseModule {

    @Provides
    @DatabaseScope
    fun provideMainDatabase(context: Context): MainDatabase{
        return Room.databaseBuilder(
            context,
            MainDatabase::class.java,
            name = "stress_app_main_database"
        ).build()
    }

    @Provides
    @DatabaseScope
    fun provideSettingDatabase(context: Context): SettingDatabase{
        return Room.databaseBuilder(
            context,
            SettingDatabase::class.java,
            name = "stress_app_setting_database"
        ).build()
    }

    @Provides
    @DatabaseScope
    fun provideUserDatabase(context: Context): UserDatabase{
        return Room.databaseBuilder(
            context,
            UserDatabase::class.java,
            name = "stress_app_user_database"
        ).build()
    }
}
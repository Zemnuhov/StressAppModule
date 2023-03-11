package com.neurotech.core_database_impl.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import com.neurotech.core_database_impl.main_database.MainDatabase
import com.neurotech.core_database_impl.setting_database.SettingDatabase
import com.neurotech.core_database_impl.user_database.UserDatabase
import dagger.Module
import dagger.Provides

@Module
class DatabaseModule {

    val MAIN_MIGRATION_1_2 = Migration(1,2){
        it.execSQL("CREATE TABLE 'RelaxRecordEntity' (\n" +
                "    'id' INTEGER NOT NULL, " +
                "    'date' TEXT NOT NULL," +
                "    'relaxationDuration' INTEGER NOT NULL," +
                "    'phaseCount' INTEGER NOT NULL," +
                "    'tonicAdjusted' INTEGER NOT NULL, " +
                "PRIMARY KEY ('id')"+
                ")")
    }

    @Provides
    @DatabaseScope
    fun provideMainDatabase(context: Context): MainDatabase{
        return Room.databaseBuilder(
            context,
            MainDatabase::class.java,
            name = "stress_app_main_database"
        ).addMigrations(MAIN_MIGRATION_1_2).build()
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
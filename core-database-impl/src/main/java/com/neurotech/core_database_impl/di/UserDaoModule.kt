package com.neurotech.core_database_impl.di

import com.neurotech.core_database_impl.user_database.UserDatabase
import com.neurotech.core_database_impl.user_database.dao.UserDao
import dagger.Module
import dagger.Provides

@Module
class UserDaoModule {

    @Provides
    @DatabaseScope
    fun provideUserDao(database: UserDatabase): UserDao{
        return database.userDao()
    }
}
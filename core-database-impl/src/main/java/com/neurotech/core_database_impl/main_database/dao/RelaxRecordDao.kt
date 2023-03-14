package com.neurotech.core_database_impl.main_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.neurotech.core_database_impl.main_database.entity.RelaxRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RelaxRecordDao {

    @Insert
    fun writeRelaxRecord(vararg record: RelaxRecordEntity)


    @Query("SELECT * FROM RelaxRecordEntity WHERE date in (:dates)")
    fun getRelaxRecordByDates(dates: List<String>): Flow<List<RelaxRecordEntity>>




}
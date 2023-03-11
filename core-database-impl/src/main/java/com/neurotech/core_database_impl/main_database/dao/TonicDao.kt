package com.neurotech.core_database_impl.main_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.neurotech.core_database_impl.main_database.entity.TonicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TonicDao {

    @Query("SELECT IFNULL(AVG(value),0) FROM TonicEntity WHERE time >= datetime('now','-10 minute','localtime')")
    fun getTenMinuteAvg(): Flow<Int>

    @Query("SELECT IFNULL(AVG(value),0) FROM TonicEntity WHERE time >= datetime('now','-1 hour','localtime')")
    fun getOneHourAvg(): Flow<Int>

    @Query("SELECT IFNULL(AVG(value),0) FROM TonicEntity WHERE time >= datetime('now','-1 day','localtime')")
    fun getOneDayAvg(): Flow<Int>

    @Query("SELECT AVG(value) FROM TonicEntity WHERE time >= :beginDateTime AND time <= :endDateTime")
    fun getTonicAverageInInterval(beginDateTime: String, endDateTime: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTonicValue(vararg peak: TonicEntity)

    @Query("DELETE FROM TonicEntity WHERE time >= datetime('now','-2 day','localtime')")
    fun deleteTonicValue()
}
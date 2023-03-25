package com.neurotech.core_database_impl.main_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.neurotech.core_database_impl.main_database.entity.PhaseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhaseDao {
    @Query("SELECT COUNT(*) FROM PhaseEntity WHERE timeBegin >= datetime('now','-10 minute','localtime')")
    fun getTenMinuteCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM PhaseEntity WHERE timeBegin >= datetime('now','-1 hour','localtime')")
    fun getOneHourCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM PhaseEntity WHERE timeBegin >= datetime('now','-1 day','localtime')")
    fun getOneDayCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM PhaseEntity WHERE timeBegin >= :beginDateTime AND timeBegin <= :endDateTime")
    fun getPhaseInInterval(beginDateTime: String, endDateTime: String): Int

    @Query("SELECT COUNT(*) FROM PhaseEntity WHERE timeBegin >= :dateTime")
    fun getPhaseFromNow(dateTime: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPhase(vararg peak: PhaseEntity)

    @Query("DELETE FROM PhaseEntity WHERE timeBegin >= datetime('now','-2 day')")
    fun deletePhase()
}
package com.neurotech.core_database_impl.setting_database.dao

import androidx.room.*
import com.neurotech.core_database_impl.setting_database.entity.DayPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DayPlanDao {
    @Update
    fun updateDayPlan(vararg markup: DayPlanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDayPlan(vararg markup: DayPlanEntity)

    @Delete
    fun deleteDayPlan(vararg markup: DayPlanEntity)

    @Query("SELECT * from DayPlanEntity WHERE :time > timeBegin and :time < timeEnd")
    fun getDayPlanByTime(time: String): DayPlanEntity?

    @Query("SELECT * from DayPlanEntity ORDER BY timeBegin")
    fun getDayPlans(): Flow<List<DayPlanEntity>>

    @Query("SELECT * from DayPlanEntity ORDER BY planId DESC LIMIT 1")
    fun getDayPlan(): DayPlanEntity

    @Query("SELECT * from DayPlanEntity WHERE planId = :id")
    fun getDayPlanById(id: Int): DayPlanEntity

}
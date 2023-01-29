package com.neurotech.core_database_impl.setting_database.dao

import androidx.room.*
import com.neurotech.core_database_impl.setting_database.entity.DayPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DayPlanDao {
    @Update
    fun updateDayPlan(vararg markup: DayPlanEntity)

    @Insert
    fun insertDayPlan(vararg markup: DayPlanEntity)

    @Delete
    fun deleteDayPlan(vararg markup: DayPlanEntity)

    @Query("SELECT * from DayPlanEntity ORDER BY timeBegin")
    fun getDayPlans(): Flow<List<DayPlanEntity>>

}
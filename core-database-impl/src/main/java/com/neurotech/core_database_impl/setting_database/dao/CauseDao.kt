package com.neurotech.core_database_impl.setting_database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.neurotech.core_database_impl.setting_database.entity.CauseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CauseDao {
    @Query("SELECT * FROM CauseEntity")
    fun getCause(): Flow<List<CauseEntity>>

    @Insert
    fun insertCause(vararg cause: CauseEntity)

    @Delete
    fun deleteCause(vararg causeEntity: CauseEntity)
}
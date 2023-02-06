package com.neurotech.core_database_impl.setting_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.neurotech.core_database_impl.setting_database.entity.DeviceEntity

@Dao
interface DeviceDao {

    @Query("SELECT * FROM DeviceEntity")
    fun getDevice(): DeviceEntity?

    @Query("SELECT COUNT() FROM DeviceEntity")
    fun getDeviceCount(): Int

    /*
    * Функция не для прямого успользования. Использовать insertDevice
    */
    @Insert
    fun addDevice(device: DeviceEntity)

    @Query("DELETE FROM DeviceEntity")
    fun removedDevice()

    @Transaction
    fun insertDevice(device: DeviceEntity): Boolean{
        if(getDeviceCount() == 0){
            addDevice(device)
            return true
        }
        return false
    }
}
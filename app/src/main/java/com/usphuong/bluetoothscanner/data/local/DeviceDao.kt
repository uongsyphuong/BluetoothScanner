package com.usphuong.bluetoothscanner.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.usphuong.bluetoothscanner.data.model.Device

@Dao
interface DeviceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(device: Device): Long

    @Query("SELECT * FROM Device ORDER BY timeStamp DESC")
    fun getListDevice(): List<Device>?

}

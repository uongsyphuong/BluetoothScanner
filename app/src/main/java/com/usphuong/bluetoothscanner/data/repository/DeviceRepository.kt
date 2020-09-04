package com.usphuong.bluetoothscanner.data.repository

import com.usphuong.bluetoothscanner.data.local.DeviceDao
import com.usphuong.bluetoothscanner.data.model.Device
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeviceRepository @Inject constructor(private val deviceDao: DeviceDao) {

    suspend fun insertDevice(device: Device): Boolean {
        return withContext(Dispatchers.IO) {
            val isInsert = deviceDao.insert(device)
            isInsert > 0
        }
    }

    suspend fun getListDevice(): List<Device>? {
        return withContext(Dispatchers.IO) {
            deviceDao.getListDevice()
        }
    }

}

package com.usphuong.bluetoothscanner.di

import com.usphuong.bluetoothscanner.BluetoothScannerApplication
import com.usphuong.bluetoothscanner.data.local.DeviceDao
import com.usphuong.bluetoothscanner.data.local.MyAppDb
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DbModule {
    @Provides
    @Singleton
    fun provideDatabase(application: BluetoothScannerApplication): MyAppDb {
        return MyAppDb.getInstance(application)
    }

    @Provides
    @Singleton
    fun provideDeviceDao(myAppDb: MyAppDb): DeviceDao {
        return myAppDb.getDeviceDao()
    }
}


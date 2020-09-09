package com.usphuong.bluetoothscanner.di

import android.content.Context
import com.usphuong.bluetoothscanner.data.local.DeviceDao
import com.usphuong.bluetoothscanner.data.local.MyAppDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
class DbModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MyAppDb {
        return MyAppDb.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideDeviceDao(myAppDb: MyAppDb): DeviceDao {
        return myAppDb.getDeviceDao()
    }
}


package com.usphuong.bluetoothscanner.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.usphuong.bluetoothscanner.data.local.MyAppDb.Companion.DB_VERSION
import com.usphuong.bluetoothscanner.data.model.Device

@Database(
    entities = [(Device::class)],
    version = DB_VERSION, exportSchema = false
)
abstract class MyAppDb : RoomDatabase() {

    companion object {
        const val DB_VERSION = 1
        private const val DB_NAME = "myApp.db"

        @Volatile
        private var INSTANCE: MyAppDb? = null

        fun getInstance(context: Context): MyAppDb =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: build(
                        context
                    )
                        .also { INSTANCE = it }
            }

        private fun build(context: Context) =
            Room.databaseBuilder(
                context, MyAppDb::class.java,
                DB_NAME
            )
                .build()

    }

    abstract fun getDeviceDao(): DeviceDao

}

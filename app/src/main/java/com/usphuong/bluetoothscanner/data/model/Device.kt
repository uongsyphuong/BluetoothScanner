package com.usphuong.bluetoothscanner.data.model

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat

@Parcelize
@Entity(tableName = "Device")
data class Device(
    var name: String? = null,
    @PrimaryKey
    var macAddress: String = "",
    var rssi: Int? = null,
    var timeStamp: Long? = null
) : Parcelable {
    @SuppressLint("SimpleDateFormat")
    fun getLastSeen(): String {
        val sdf = SimpleDateFormat("dd/M/yyyy HH:mm:ss")
        try {
            return sdf.format(timeStamp)
        } catch (e: Exception) {
        }
        return ""
    }
}

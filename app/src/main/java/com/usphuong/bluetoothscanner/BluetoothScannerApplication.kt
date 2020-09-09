package com.usphuong.bluetoothscanner

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BluetoothScannerApplication : Application() {

    companion object {
        lateinit var instance: BluetoothScannerApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
    }


    @Suppress("DEPRECATION")
    fun isNetworkConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val connection = cm.getNetworkCapabilities(cm.activeNetwork)
            return connection != null && (
                connection.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    connection.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
        } else {
            val activeNetwork = cm.activeNetworkInfo
            if (activeNetwork != null) {
                return (activeNetwork.type == ConnectivityManager.TYPE_WIFI ||
                    activeNetwork.type == ConnectivityManager.TYPE_MOBILE)
            }
            return false
        }
    }
}

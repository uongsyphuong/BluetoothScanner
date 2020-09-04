package com.usphuong.bluetoothscanner.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.usphuong.bluetoothscanner.Constants
import com.usphuong.bluetoothscanner.R

object AppUtils {

    fun enableBluetooth(): Boolean {
        var res = false
        try {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled) {
                bluetoothAdapter.enable()
            }
            res = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return res
    }

    private var notificationManager: NotificationManager? = null
    private var notificationBuider: NotificationCompat.Builder? = null
    private var notification: Notification? = null

    fun startNotification(
        service: Service,
        context: Context
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotification(context)
            if (notification != null) {
                service.startForeground(
                    Constants.NOTIFICATION_SERVICE_ID,
                    notification
                )
            }
        }
    }

    private fun createNotification(context: Context) {
        val notificationIntent = Intent(
            context,
            getMainActivityClass(
                context
            )
        )
        val pendingIntent = PendingIntent.getActivity(
            context,
            Constants.NOTIFICATION_CHANNEL_ID_CODE, notificationIntent, 0
        )
        val content = "Scanning bluetooth"
        val title = "Bluetooth Scan"
        notificationBuider =
            NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setContentTitle(title)
                .setStyle(NotificationCompat.BigTextStyle().bigText(content))
                .setContentText(content)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                Constants.NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_MIN
            )

            notificationManager = context.getSystemService(
                NotificationManager::class.java
            )
            notificationManager?.createNotificationChannel(serviceChannel)
        }

        notification = notificationBuider?.build()
        notificationManager?.notify(
            Constants.NOTIFICATION_SERVICE_ID,
            notification
        )
    }

    private fun getMainActivityClass(context: Context): Class<*>? {
        val packageName = context.packageName
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        val className = launchIntent?.component?.className
        return try {
            if (className != null)
                Class.forName(className)
            else null
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            null
        }
    }

}

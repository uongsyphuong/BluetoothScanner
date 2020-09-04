package com.usphuong.bluetoothscanner.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

object AppScheduler {
    fun schedule(
        context: Context,
        intentAlarm: Intent,
        requestCode: Int,
        intervalMillis: Long
    ) {
        val pendingIntent = PendingIntent.getService(
            context,
            requestCode,
            intentAlarm,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val time = System.currentTimeMillis() + intervalMillis
        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
        } else
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent)
    }

    fun cancel(
        context: Context,
        intentAlarm: Intent,
        requestCode: Int
    ) {
        val pendingIntent = PendingIntent.getService(
            context,
            requestCode,
            intentAlarm,
            0
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}

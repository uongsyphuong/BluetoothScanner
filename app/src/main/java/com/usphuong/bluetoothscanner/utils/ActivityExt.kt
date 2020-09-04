package com.usphuong.bluetoothscanner.utils

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.usphuong.bluetoothscanner.Constants
import com.usphuong.bluetoothscanner.R

fun Activity.requestLocationPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            Constants.REQUEST_LOCATION_PERMISSION
        )
    }
}

fun Activity.hasPermission(): Boolean {
    val permission =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    return permission == PackageManager.PERMISSION_GRANTED
}

fun Activity.isMyServiceRunning(serviceClass: Class<*>): Boolean =
    (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?)
        ?.let { am ->
            am.getRunningServices(Int.MAX_VALUE)
                .find { it.service.className == serviceClass.name } != null
        } ?: false

fun Activity.showDialog(title: String, message: String) {
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setCancelable(true)
        .setPositiveButton(
            R.string.ok
        ) { dialog, _ ->
            dialog.cancel()
        }
        .setIcon(android.R.drawable.ic_dialog_alert)
        .show()
}

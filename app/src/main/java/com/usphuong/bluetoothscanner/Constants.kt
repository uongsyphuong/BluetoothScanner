package com.usphuong.bluetoothscanner

object Constants {
    const val NOTIFICATION_CHANNEL_ID = "Channel"
    const val NOTIFICATION_CHANNEL_NAME = "ScanBluetooth channel"
    const val NOTIFICATION_CHANNEL_ID_CODE = 42020
    const val NOTIFICATION_SERVICE_ID = 2020

    const val TIME_DELAY = 260 * 1000.toLong()     //260s
    const val TIME_RUNNING = 40 * 1000.toLong()     //40s
    const val TIME_SCAN_BLE_REPORT_DELAY = 10 * 1000.toLong()

    const val EXTRA_SCHEDULER_TYPE = "extra_scheduler_type"
    const val EXTRA_URL = "extra_url"

    const val MAX_RSSI = 100

    const val REQUEST_LOCATION_PERMISSION = 0x50

    const val DEFAUT_MANUFACTOR_IOS = 0x004c
}

package com.usphuong.bluetoothscanner.service

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Intent
import android.os.IBinder
import android.os.ParcelUuid
import android.util.Log
import com.usphuong.bluetoothscanner.Constants
import com.usphuong.bluetoothscanner.Constants.EXTRA_SCHEDULER_TYPE
import com.usphuong.bluetoothscanner.Constants.TIME_DELAY
import com.usphuong.bluetoothscanner.Constants.TIME_RUNNING
import com.usphuong.bluetoothscanner.Constants.TIME_SCAN_BLE_REPORT_DELAY
import com.usphuong.bluetoothscanner.data.local.MyAppDb
import com.usphuong.bluetoothscanner.data.model.Device
import com.usphuong.bluetoothscanner.utils.AppScheduler
import com.usphuong.bluetoothscanner.utils.AppUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Date

class ScanBluetoothService : Service() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private var bluetoothLeScanner: BluetoothLeScanner? = null
    private var scanCallback: ScanCallback? = null
    private var statusScanBle = STATUS_SCAN_FINISH
    private var typeScheduler = TYPE_SCHEDULER_NONE
    private var isReport = false

    var deviceList: MutableList<Device> = mutableListOf()

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        writeLog("Start Service Scan Bluetooth")

        AppUtils.startNotification(this, applicationContext)

        initBluetooth()

        initStatus()
    }

    private fun initBluetooth() {
        bluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner
    }

    private fun initStatus() {
        statusScanBle = STATUS_SCAN_FINISH
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        initScheduler(intent)
        if (AppUtils.enableBluetooth()) {
            startScanBle()
        }
        return START_STICKY
    }

    private fun initScheduler(intent: Intent?) {
        if (intent != null) {
            when (intent.getIntExtra(EXTRA_SCHEDULER_TYPE, typeScheduler)) {
                TYPE_SCHEDULER_SCAN_BLE -> {
                    statusScanBle = STATUS_SCAN_FINISH
                    startScanBle()
                }
                TYPE_SCHEDULER_SCAN_BLE_STOP -> {
                    writeLog("startScanBle: stop")
                    stopScanBle()
                    typeScheduler = TYPE_SCHEDULER_SCAN_BLE
                    callAlarmTimer()
                }
            }
        }
    }

    private fun initAlarmTimer(
        typeScheduler: Int,
        requestCode: Int,
        intervalMillis: Long
    ) {
        val intentAlarm = Intent(applicationContext, ScanBluetoothService::class.java)
        intentAlarm.putExtra(EXTRA_SCHEDULER_TYPE, typeScheduler)

        AppScheduler.schedule(applicationContext, intentAlarm, requestCode, intervalMillis)
    }

    private fun cancelAlarmManager() {
        val intentAlarm = Intent(applicationContext, ScanBluetoothService::class.java)
        intentAlarm.putExtra(EXTRA_SCHEDULER_TYPE, typeScheduler)
        AppScheduler.cancel(applicationContext, intentAlarm, requestCode = typeScheduler)

    }

    private fun callAlarmTimer() {
        when (typeScheduler) {
            TYPE_SCHEDULER_SCAN_BLE ->
                initAlarmTimer(
                    TYPE_SCHEDULER_SCAN_BLE,
                    TYPE_SCHEDULER_SCAN_BLE,
                    TIME_DELAY
                )

            TYPE_SCHEDULER_SCAN_BLE_STOP ->
                initAlarmTimer(
                    TYPE_SCHEDULER_SCAN_BLE_STOP,
                    TYPE_SCHEDULER_SCAN_BLE_STOP,
                    TIME_RUNNING
                )
        }
    }

    private fun startScanBle() {
        try {
            if (bluetoothLeScanner != null && statusScanBle == STATUS_SCAN_FINISH) {
                statusScanBle = STATUS_SCAN_SETUP
                writeLog("startScanBle setup")

                deviceList = mutableListOf()

                val scanSettings = ScanSettings.Builder()
                scanSettings.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                if (isReport) {
                    isReport = false
                    scanSettings.setReportDelay(TIME_SCAN_BLE_REPORT_DELAY)
                } else {
                    isReport = true
                }
                val scanFilterIosManu = ScanFilter.Builder()
                scanFilterIosManu.setManufacturerData(
                    Constants.DEFAUT_MANUFACTOR_IOS,
                    byteArrayOf()
                )

                val scanFilterGatt = ScanFilter.Builder()
                val serviceUuidString = "0000feaa-0000-1000-8000-00805f9b34fb"
                val serviceUuidMaskString = "FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF"
                val parcelUuid = ParcelUuid.fromString(serviceUuidString)
                val parcelUuidMask = ParcelUuid.fromString(serviceUuidMaskString)
                scanFilterGatt.setServiceUuid(parcelUuid, parcelUuidMask)

                val listFilter = listOf(
                    scanFilterIosManu.build(),
                    scanFilterGatt.build()
                )

                scanCallback = object : ScanCallback() {
                    @SuppressLint("SimpleDateFormat")
                    override fun onScanResult(
                        callbackType: Int,
                        result: ScanResult
                    ) {
                        super.onScanResult(callbackType, result)
                        statusScanBle = STATUS_SCANNING

                        for (device in deviceList) {
                            if (device.macAddress.contains(result.device.address)) {
                                return
                            }
                        }

                        writeLog("startScanBle: has device ${result.device.address}")

                        val device =
                            Device(
                                name = result.device.name ?: "n/a",
                                macAddress = result.device.address,
                                rssi = result.rssi,
                                timeStamp = Date().time
                            )
                        scope.launch {
                            MyAppDb.getInstance(application).getDeviceDao().insert(device)
                        }
                    }

                    @SuppressLint("SimpleDateFormat")
                    override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                        super.onBatchScanResults(results)
                        statusScanBle = STATUS_SCANNING

                        results?.forEach {
                            writeLog("startScanBle: onBatchScanResults has device ${it.device.address}")

                            val device =
                                Device(
                                    name = it.device.name ?: "n/a",
                                    macAddress = it.device.address,
                                    rssi = it.rssi,
                                    timeStamp = Date().time
                                )
                            scope.launch {
                                MyAppDb.getInstance(application).getDeviceDao()
                                    .insert(device)
                            }
                        }
                    }

                    override fun onScanFailed(errorCode: Int) {
                        super.onScanFailed(errorCode)
                        statusScanBle = STATUS_SCAN_FINISH
                        writeLog("startScanBle: fail : Code: $errorCode")
                    }
                }

                bluetoothLeScanner?.startScan(listFilter, scanSettings.build(), scanCallback)

                statusScanBle = STATUS_SCANNING

                typeScheduler = TYPE_SCHEDULER_SCAN_BLE_STOP
                callAlarmTimer()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopScanBle() {
        bluetoothLeScanner?.stopScan(scanCallback)
    }

    private fun writeLog(log: String) {
        Log.e("LeooScan", log)
    }

    override fun onDestroy() {
        super.onDestroy()
        writeLog("stopService")
        cancelAlarmManager()
        job.cancel()
        stopAllScan()
    }

    private fun stopAllScan() {
        initStatus()
        stopScanBle()
    }

    companion object {
        const val TYPE_SCHEDULER_NONE = 0
        const val TYPE_SCHEDULER_SCAN_BLE = 1
        const val TYPE_SCHEDULER_SCAN_BLE_STOP = 2

        const val STATUS_SCAN_FINISH = 0
        const val STATUS_SCAN_SETUP = 1
        const val STATUS_SCANNING = 2

    }
}

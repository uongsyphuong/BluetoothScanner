package com.usphuong.bluetoothscanner.feature.scanBluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.content.ContextCompat.startForegroundService
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.usphuong.bluetoothscanner.R
import com.usphuong.bluetoothscanner.base.BaseFragment
import com.usphuong.bluetoothscanner.data.model.Device
import com.usphuong.bluetoothscanner.feature.adapter.DeviceAdapter
import com.usphuong.bluetoothscanner.service.ScanBluetoothService
import com.usphuong.bluetoothscanner.utils.AppUtils
import com.usphuong.bluetoothscanner.utils.hasPermission
import com.usphuong.bluetoothscanner.utils.isMyServiceRunning
import com.usphuong.bluetoothscanner.utils.requestLocationPermission
import com.usphuong.bluetoothscanner.viewModel.DeviceViewModel
import kotlinx.android.synthetic.main.fragment_home.rvDevice
import kotlinx.android.synthetic.main.fragment_home.switchScan
import kotlinx.android.synthetic.main.fragment_home.switchScanService
import kotlinx.android.synthetic.main.fragment_home.tvFound
import java.util.Date
import kotlin.math.abs

class HomeFragment : BaseFragment() {

    val deviceViewModel by activityViewModels<DeviceViewModel>()

    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothLeScanner: BluetoothLeScanner? = null

    private lateinit var deviceAdapter: DeviceAdapter

    override val layoutId: Int
        get() = R.layout.fragment_home

    override fun initObserver() {
        deviceViewModel.isFilterChange.observe(this, Observer {
            if (switchScan.isChecked) {
                deviceAdapter.clearData()
                stopScanBluetooth()
                scanBluetooth()
                updateTotalFoundDevice()
            }
        })
    }

    override fun initView() {
        setupView()
        setHasOptionsMenu(true)

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        mBluetoothLeScanner = mBluetoothAdapter?.bluetoothLeScanner

        setupAdapter()

        updateTotalFoundDevice()

        if (activity?.hasPermission() == false) activity?.requestLocationPermission()
    }

    private fun setupView() {
        switchScan.setOnCheckedChangeListener { _, b ->
            if (b) {
                AppUtils.enableBluetooth()
                if (activity?.hasPermission() == true) {
                    deviceAdapter.clearData()
                    scanBluetooth()
                } else activity?.requestLocationPermission()
            } else stopScanBluetooth()
        }

        switchScanService.isChecked =
            activity?.isMyServiceRunning(ScanBluetoothService::class.java) ?: false

        switchScanService.setOnCheckedChangeListener { _, b ->
            if (b) {
                AppUtils.enableBluetooth()
                if (activity?.hasPermission() == true) {
                    val intent = Intent(context, ScanBluetoothService::class.java)
                    // Start service
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context?.let { startForegroundService(it, intent) }
                    } else {
                        activity?.startService(intent)
                    }
                } else activity?.requestLocationPermission()
            } else {
                activity?.stopService(Intent(context, ScanBluetoothService::class.java))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.filter_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }

    private fun updateTotalFoundDevice() {
        val foundDeviceNum = deviceAdapter.deviceList.size
        tvFound.text =
            if (foundDeviceNum < 2) getString(R.string.found_device, foundDeviceNum)
            else getString(R.string.found_devices, foundDeviceNum)
    }

    private fun setupAdapter() {
        rvDevice.layoutManager = LinearLayoutManager(context)
        deviceAdapter = DeviceAdapter(isHistory = false)
        rvDevice.adapter = deviceAdapter
    }

    private fun scanBluetooth() {
        mBluetoothLeScanner?.startScan(scanCallback)
    }

    private fun stopScanBluetooth() {
        mBluetoothLeScanner?.stopScan(scanCallback)
    }

    private var scanCallback = object : ScanCallback() {
        @SuppressLint("SimpleDateFormat")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            if (abs(result.rssi) > deviceViewModel.endRssi || abs(result.rssi) < deviceViewModel.startRssi) return
            if (deviceViewModel.isFilterName && result.device.name.isNullOrEmpty()) return

            for (device in deviceAdapter.deviceList) {
                if (device.macAddress.contains(result.device.address)) {
                    return
                }
            }
            val device = Device(
                name = result.device.name ?: "n/a",
                macAddress = result.device.address,
                rssi = result.rssi,
                timeStamp = Date().time
            )
            deviceViewModel.insertDevice(device)
            deviceAdapter.addData(device)
            updateTotalFoundDevice()
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.i("BLE", "error")
        }
    }

    override fun onDestroyView() {
        rvDevice.adapter = null
        stopScanBluetooth()
        super.onDestroyView()
    }

}

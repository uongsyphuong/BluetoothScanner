package com.usphuong.bluetoothscanner.feature.scanBluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.usphuong.bluetoothscanner.Constants.MAX_RSSI
import com.usphuong.bluetoothscanner.R
import com.usphuong.bluetoothscanner.data.model.Device
import com.usphuong.bluetoothscanner.feature.adapter.DeviceAdapter
import com.usphuong.bluetoothscanner.feature.github.GithubActivity
import com.usphuong.bluetoothscanner.feature.history.HistoryActivity
import com.usphuong.bluetoothscanner.feature.userInfo.UserInfoActivity
import com.usphuong.bluetoothscanner.service.ScanBluetoothService
import com.usphuong.bluetoothscanner.utils.AppUtils
import com.usphuong.bluetoothscanner.utils.hasPermission
import com.usphuong.bluetoothscanner.utils.isMyServiceRunning
import com.usphuong.bluetoothscanner.utils.requestLocationPermission
import com.usphuong.bluetoothscanner.viewModel.DeviceViewModel
import com.usphuong.bluetoothscanner.viewModel.ViewModelFactory
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_home.drawerLayout
import kotlinx.android.synthetic.main.activity_home.navView
import kotlinx.android.synthetic.main.activity_home.rvDevice
import kotlinx.android.synthetic.main.activity_home.switchScan
import kotlinx.android.synthetic.main.activity_home.switchScanService
import kotlinx.android.synthetic.main.activity_home.tvFound
import java.util.Date
import javax.inject.Inject
import kotlin.math.abs

class HomeActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var deviceViewModel: DeviceViewModel

    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothLeScanner: BluetoothLeScanner? = null

    private lateinit var deviceAdapter: DeviceAdapter

    private var drawerToggle: ActionBarDrawerToggle? = null

    var startRSSI = 0
    var endRSSI = MAX_RSSI

    var isFilterName = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        AndroidInjection.inject(this)
        deviceViewModel = ViewModelProvider(this, viewModelFactory).get(DeviceViewModel::class.java)

        setupNavigationDrawer()

        setupView()

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        mBluetoothLeScanner = mBluetoothAdapter?.bluetoothLeScanner

        setupAdapter()

        updateTotalFoundDevice()

        if (!hasPermission()) requestLocationPermission()
    }

    private fun setupView() {
        switchScan.setOnCheckedChangeListener { _, b ->
            if (b) {
                AppUtils.enableBluetooth()
                if (hasPermission()) {
                    deviceAdapter.clearData()
                    scanBluetooth()
                } else requestLocationPermission()
            } else stopScanBluetooth()
        }

        switchScanService.isChecked = isMyServiceRunning(ScanBluetoothService::class.java)

        switchScanService.setOnCheckedChangeListener { _, b ->
            if (b) {
                AppUtils.enableBluetooth()
                if (hasPermission()) {
                    val intent = Intent(this, ScanBluetoothService::class.java)
                    // Start service
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent)
                    } else {
                        startService(intent)
                    }
                } else requestLocationPermission()
            } else {
                stopService(Intent(this, ScanBluetoothService::class.java))
            }
        }
    }

    private fun updateTotalFoundDevice() {
        val foundDeviceNum = deviceAdapter.deviceList.size
        tvFound.text =
            if (foundDeviceNum < 2) getString(R.string.found_device, foundDeviceNum)
            else getString(R.string.found_devices, foundDeviceNum)
    }

    private fun setupNavigationDrawer() {
        drawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerToggle?.let { drawerLayout.addDrawerListener(it) }

        navView.setNavigationItemSelectedListener {
            drawerLayout.closeDrawers()
            when (it.itemId) {
                R.id.navHistory -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                }

                R.id.navUserInfo -> {
                    startActivity(Intent(this, UserInfoActivity::class.java))
                }

                R.id.navGithub -> {
                    startActivity(Intent(this, GithubActivity::class.java))
                }
            }
            true
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

    }

    fun filterRSSI(startRSSI: Int, endRSSI: Int, isFilterName: Boolean) {
        this.startRSSI = startRSSI
        this.endRSSI = endRSSI
        this.isFilterName = isFilterName
        if (switchScan.isChecked) {
            deviceAdapter.clearData()
            stopScanBluetooth()
            scanBluetooth()
            updateTotalFoundDevice()
        }
    }

    private fun setupAdapter() {
        rvDevice.layoutManager = LinearLayoutManager(this)
        deviceAdapter = DeviceAdapter(isHistory = false)
        rvDevice.adapter = deviceAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.filter_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawers()
                } else
                    drawerLayout.openDrawer(GravityCompat.START)
                return true
            }

            R.id.filter_menu -> {
                val fm = supportFragmentManager
                val newFragment = FilterDialogFragment.newInstance()
                newFragment.show(fm, "FilterDialogFragment")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle?.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle?.onConfigurationChanged(newConfig)
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
            if (abs(result.rssi) > endRSSI || abs(result.rssi) < startRSSI) return
            if (isFilterName && result.device.name.isNullOrEmpty()) return

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

}

package com.usphuong.bluetoothscanner.feature.history

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.usphuong.bluetoothscanner.R
import com.usphuong.bluetoothscanner.data.model.Device
import com.usphuong.bluetoothscanner.feature.adapter.DeviceAdapter
import com.usphuong.bluetoothscanner.viewModel.DeviceViewModel
import com.usphuong.bluetoothscanner.viewModel.ViewModelFactory
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_history.refresh
import kotlinx.android.synthetic.main.activity_history.rvDevice
import kotlinx.android.synthetic.main.activity_history.tvEmpty
import javax.inject.Inject

class HistoryActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var deviceViewModel: DeviceViewModel

    private lateinit var deviceAdapter: DeviceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        AndroidInjection.inject(this)
        deviceViewModel = ViewModelProvider(this, viewModelFactory).get(DeviceViewModel::class.java)

        supportActionBar?.title = getString(R.string.history)

        setupAdapter()

        setupRefresh()

        initObserver()

        deviceViewModel.getListDevice()
    }

    private fun setupAdapter() {
        rvDevice.layoutManager = LinearLayoutManager(this)
        deviceAdapter = DeviceAdapter(isHistory = true)
        rvDevice.adapter = deviceAdapter
    }

    private fun setupRefresh() {
        refresh.setOnRefreshListener {
            deviceViewModel.getListDevice()
        }
    }

    private fun initObserver() {
        deviceViewModel.listDeviceLiveData.observe(this, Observer {
            refresh.isRefreshing = false
            if (it.isNullOrEmpty()) {
                tvEmpty.visibility = View.VISIBLE
                rvDevice.visibility = View.GONE
            } else {
                tvEmpty.visibility = View.GONE
                rvDevice.visibility = View.VISIBLE
                deviceAdapter.setData(it as MutableList<Device>)
            }
        })
    }
}

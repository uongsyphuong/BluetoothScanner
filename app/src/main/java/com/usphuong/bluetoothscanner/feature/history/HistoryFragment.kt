package com.usphuong.bluetoothscanner.feature.history

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.usphuong.bluetoothscanner.R
import com.usphuong.bluetoothscanner.base.BaseFragment
import com.usphuong.bluetoothscanner.data.model.Device
import com.usphuong.bluetoothscanner.feature.adapter.DeviceAdapter
import com.usphuong.bluetoothscanner.viewModel.DeviceViewModel
import kotlinx.android.synthetic.main.fragment_history.refresh
import kotlinx.android.synthetic.main.fragment_history.rvDevice
import kotlinx.android.synthetic.main.fragment_history.tvEmpty

class HistoryFragment : BaseFragment() {

    private val deviceViewModel by viewModels<DeviceViewModel>{viewModelFactory}

    private lateinit var deviceAdapter: DeviceAdapter


    override val layoutId: Int
        get() = R.layout.fragment_history

    override fun initObserver() {
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

    override fun initView() {
        setupAdapter()

        setupRefresh()

        initObserver()

        deviceViewModel.getListDevice()
    }

    private fun setupAdapter() {
        rvDevice.layoutManager = LinearLayoutManager(context)
        deviceAdapter = DeviceAdapter(isHistory = true)
        rvDevice.adapter = deviceAdapter
    }

    private fun setupRefresh() {
        refresh.setOnRefreshListener {
            deviceViewModel.getListDevice()
        }
    }

    override fun onDestroyView() {
        rvDevice.adapter = null
        super.onDestroyView()
    }
}

package com.usphuong.bluetoothscanner.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usphuong.bluetoothscanner.Constants
import com.usphuong.bluetoothscanner.data.model.Device
import com.usphuong.bluetoothscanner.data.repository.DeviceRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeviceViewModel @Inject constructor(private val deviceRepository: DeviceRepository) :
    ViewModel() {

    val isFilterChange = MutableLiveData(false)
    var startRssi = 0
    var endRssi = Constants.MAX_RSSI
    var isFilterName = false

    val insertLiveData = MutableLiveData<Boolean>()
    val listDeviceLiveData = MutableLiveData<List<Device>?>()

    fun insertDevice(device: Device) {
        viewModelScope.launch {
            insertLiveData.value = deviceRepository.insertDevice(device)
        }
    }

    fun getListDevice() {
        viewModelScope.launch {
            listDeviceLiveData.value = deviceRepository.getListDevice()
        }
    }

    fun filterRSSI(startRssi: Int, endRssi: Int, isFilterName: Boolean) {
        isFilterChange.value = !(isFilterChange.value ?: false)
        this.startRssi = startRssi
        this.endRssi = endRssi
        this.isFilterName = isFilterName
    }
}

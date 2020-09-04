package com.usphuong.bluetoothscanner.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usphuong.bluetoothscanner.data.model.Device
import com.usphuong.bluetoothscanner.data.repository.DeviceRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeviceViewModel @Inject constructor(private val deviceRepository: DeviceRepository) :
    ViewModel() {

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
}

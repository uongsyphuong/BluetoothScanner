package com.usphuong.bluetoothscanner.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.usphuong.bluetoothscanner.data.model.Device
import com.usphuong.bluetoothscanner.data.repository.DeviceRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.Date

@RunWith(JUnit4::class)
class DeviceViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    lateinit var deviceViewModel: DeviceViewModel

    @MockK
    lateinit var deviceRepository: DeviceRepository

    @Before
    fun start() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `test device view model insert success`() = runBlocking {
        deviceViewModel = DeviceViewModel(deviceRepository)

        val deviceTest = Device("Test", "Test", 100, Date().time)

        coEvery { deviceRepository.insertDevice(deviceTest) } returns true

        deviceViewModel.insertDevice(deviceTest)

        assert(deviceViewModel.insertLiveData.value == true)
    }

    @Test
    fun `test device view model get list success`() = runBlocking {

        deviceViewModel = DeviceViewModel(deviceRepository)

        val listDeviceTest = arrayListOf(
            Device("Test", "Test", 100, Date().time),
            Device("Test1", "Test1", 99, Date().time - 100),
            Device("Test2", "Test2", 98, Date().time - 200)
        )

        coEvery { deviceRepository.getListDevice() } returns listDeviceTest

        deviceViewModel.getListDevice()

        assert(deviceViewModel.listDeviceLiveData.value != null)
        assert(deviceViewModel.listDeviceLiveData.value?.size == listDeviceTest.size)
    }
}

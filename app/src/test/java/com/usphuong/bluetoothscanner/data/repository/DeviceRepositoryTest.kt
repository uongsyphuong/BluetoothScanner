package com.usphuong.bluetoothscanner.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.usphuong.bluetoothscanner.data.local.DeviceDao
import com.usphuong.bluetoothscanner.data.model.Device
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.Date

@RunWith(JUnit4::class)
class DeviceRepositoryTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    lateinit var deviceRepository: DeviceRepository

    @MockK
    lateinit var deviceDao: DeviceDao

    @Before
    fun start() {
        MockKAnnotations.init(this)
        deviceRepository = DeviceRepository(deviceDao)
    }

    @Test
    fun `test insert data success`() = runBlocking {
        val deviceTest = Device("Test", "Test", 100, Date().time)

        coEvery { deviceDao.insert(deviceTest) } returns 1

        val dataReceived = deviceRepository.insertDevice(deviceTest)

        verify { deviceDao.insert(any() as Device) }
        assertTrue(dataReceived)
    }

    @Test
    fun `test get data success`() = runBlocking {
        val listDeviceTest = arrayListOf(
            Device("Test", "Test", 100, Date().time),
            Device("Test1", "Test1", 99, Date().time - 100),
            Device("Test2", "Test2", 98, Date().time - 200)
        )

        coEvery { deviceDao.getListDevice() } returns listDeviceTest

        val dataReceived = deviceRepository.getListDevice()

        verify { deviceDao.getListDevice() }
        assert(dataReceived?.size == listDeviceTest.size)
    }
}

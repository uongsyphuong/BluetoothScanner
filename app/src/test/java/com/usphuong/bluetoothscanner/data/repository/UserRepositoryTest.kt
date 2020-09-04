package com.usphuong.bluetoothscanner.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.GsonBuilder
import com.usphuong.bluetoothscanner.data.model.Resource
import com.usphuong.bluetoothscanner.data.remote.ApiService
import io.mockk.MockKAnnotations
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertNull
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@RunWith(JUnit4::class)
class UserRepositoryTest {

    private lateinit var userRepository: UserRepository

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Inject
    private lateinit var mMockServerInstance: MockWebServer

    private lateinit var apiService: ApiService

    private fun mockNetworkResponseWithFileContent(fileName: String?, responseCode: Int) {
        mMockServerInstance.enqueue(
            MockResponse().setResponseCode(responseCode)
                .setBody(fileName?.let { getJson(fileName) } ?: "{}")
        )
    }

    private fun mockErrorNetwork() {
        mMockServerInstance.enqueue(
            MockResponse().throttleBody(bytesPerPeriod = 1048, period = 2, unit = TimeUnit.SECONDS)
        )
    }

    private fun startMockServer() {
        mMockServerInstance = MockWebServer()
        mMockServerInstance.start()
    }

    private fun getJson(path: String): String {
        val uri = javaClass.classLoader?.getResource(path) ?: return ""
        val file = File(uri.path)
        return String(file.readBytes())
    }

    private fun getMockWebServerUrl() = mMockServerInstance.url("/").toString()

    @Before
    fun start() {
        startMockServer()
        MockKAnnotations.init(this)
        val retrofit = Retrofit.Builder()
            .baseUrl(getMockWebServerUrl())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
        apiService = retrofit.create(ApiService::class.java)

        userRepository = UserRepository(apiService)
    }

    @After
    fun tearDown() {
        mMockServerInstance.shutdown()
    }


    @Test
    fun `test call api success`() = runBlocking {

        val username = "Bret"
        val mStatus = Resource.Status.SUCCESS

        mockNetworkResponseWithFileContent("success_resp.json", HttpURLConnection.HTTP_OK)

        val dataReceived = userRepository.getUserInfo()

        assertNotNull(dataReceived.data)
        assertEquals(dataReceived.status, mStatus)
        assertEquals(dataReceived.data?.username, username)
    }

    @Test
    fun `test call api success empty data`() = runBlocking {
        val message = "No data"

        val mStatus = Resource.Status.ERROR

        mockNetworkResponseWithFileContent(null, HttpURLConnection.HTTP_OK)

        val dataReceived = userRepository.getUserInfo()

        assertEquals(dataReceived.status, mStatus)
        assertEquals(dataReceived.message, message)
    }

    @Test
    fun `test call api fail`() = runBlocking {
        val mStatus = Resource.Status.ERROR

        mockNetworkResponseWithFileContent("success_resp.json", HttpURLConnection.HTTP_BAD_REQUEST)

        val dataReceived = userRepository.getUserInfo()

        assertNull(dataReceived.data)
        assertEquals(dataReceived.status, mStatus)
    }

    @Test
    fun `test call api exception`() = runBlocking {
        val mStatus = Resource.Status.ERROR

        mockErrorNetwork()

        val dataReceived = userRepository.getUserInfo()

        assertNull(dataReceived.data)
        assertEquals(dataReceived.status, mStatus)
    }

}

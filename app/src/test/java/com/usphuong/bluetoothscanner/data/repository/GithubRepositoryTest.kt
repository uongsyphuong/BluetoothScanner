package com.usphuong.bluetoothscanner.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.apollographql.apollo.ApolloClient
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
class GithubRepositoryTest {

    private lateinit var githubRepository: GithubRepository

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Inject
    private lateinit var mMockServerInstance: MockWebServer

    private lateinit var apolloClient: ApolloClient

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

        apolloClient = ApolloClient.builder()
            .serverUrl(getMockWebServerUrl())
            .build()

        githubRepository = GithubRepository(apolloClient)
    }

    @After
    fun tearDown() {
        mMockServerInstance.shutdown()
    }


    @Test
    fun `test call api success`() = runBlocking {

        val owner = "uongsyphuong"
        val name = "BluetoothScanner"
        val mStatus = Resource.Status.SUCCESS

        mockNetworkResponseWithFileContent("success_response_github.json", HttpURLConnection.HTTP_OK)

        val dataReceived = githubRepository.getRepo(owner, name)

        assertNotNull(dataReceived.data)
        assertEquals(dataReceived.status, mStatus)
        assertEquals(dataReceived.data?.repository?.name, name)
    }

    @Test
    fun `test call api success empty data`() = runBlocking {
        val message = "No data"
        val owner = "uongsyphuong"
        val name = "BluetoothScanner"

        val mStatus = Resource.Status.ERROR

        mockNetworkResponseWithFileContent(null, HttpURLConnection.HTTP_OK)

        val dataReceived = githubRepository.getRepo(owner, name)

        assertEquals(dataReceived.status, mStatus)
        assertEquals(dataReceived.message, message)
    }

    @Test
    fun `test call api fail`() = runBlocking {
        val mStatus = Resource.Status.ERROR
        val owner = "uongsyphuong"
        val name = "BluetoothScanner"

        mockNetworkResponseWithFileContent("success_resp.json", HttpURLConnection.HTTP_BAD_REQUEST)

        val dataReceived = githubRepository.getRepo(owner, name)

        assertNull(dataReceived.data)
        assertEquals(dataReceived.status, mStatus)
    }

    @Test
    fun `test call api exception`() = runBlocking {
        val mStatus = Resource.Status.ERROR
        val owner = "uongsyphuong"
        val name = "BluetoothScanner"

        mockErrorNetwork()

        val dataReceived = githubRepository.getRepo(owner, name)

        assertNull(dataReceived.data)
        assertEquals(dataReceived.status, mStatus)
    }

}

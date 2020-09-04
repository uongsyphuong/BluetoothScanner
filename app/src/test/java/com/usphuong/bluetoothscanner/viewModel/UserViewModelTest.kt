package com.usphuong.bluetoothscanner.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import com.usphuong.bluetoothscanner.data.model.Resource
import com.usphuong.bluetoothscanner.data.model.User
import com.usphuong.bluetoothscanner.data.repository.UserRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

@RunWith(JUnit4::class)
class UserViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    lateinit var userViewModel: UserViewModel

    @MockK
    lateinit var userRepository: UserRepository

    private fun getJson(path: String): String {
        val uri = javaClass.classLoader?.getResource(path) ?: return ""
        val file = File(uri.path)
        return String(file.readBytes())
    }

    @Before
    fun start() {
        MockKAnnotations.init(this)

        userViewModel = UserViewModel(userRepository)
    }

    @Test
    fun `test user view model success`() = runBlocking {
        val username = "Bret"

        val sampleResponse = getJson("success_resp.json")
        val jsonObj = Gson().fromJson(sampleResponse, User::class.java)

        coEvery { userRepository.getUserInfo() } returns Resource.success(jsonObj)

        userViewModel.getUserInfo()

        assertEquals(userViewModel.userInfoLiveData.value?.username, username)
    }

    @Test
    fun `test user view model fail`() = runBlocking {
        val message = "error"

        coEvery { userRepository.getUserInfo() } returns Resource.error(message)

        userViewModel.getUserInfo()

        assert(userViewModel.errorLiveData.value == message)
    }
}

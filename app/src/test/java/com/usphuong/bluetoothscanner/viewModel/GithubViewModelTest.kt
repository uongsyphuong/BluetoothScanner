package com.usphuong.bluetoothscanner.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import com.usphuong.bluetoothscanner.FindQuery
import com.usphuong.bluetoothscanner.data.model.Resource
import com.usphuong.bluetoothscanner.data.model.User
import com.usphuong.bluetoothscanner.data.repository.GithubRepository
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
class GithubViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var githubViewModel: GithubViewModel

    @MockK
    lateinit var gitHubRepository: GithubRepository

    private fun getJson(path: String): String {
        val uri = javaClass.classLoader?.getResource(path) ?: return ""
        val file = File(uri.path)
        return String(file.readBytes())
    }

    @Before
    fun start() {
        MockKAnnotations.init(this)

        githubViewModel = GithubViewModel(gitHubRepository)
    }

    @Test
    fun `test user view model success`() = runBlocking {
        val owner = "uongsyphuong"
        val name = "BluetoothScanner"

        val sampleResponse = getJson("success_response_github_vm.json")
        val jsonObj = Gson().fromJson(sampleResponse, FindQuery.Data::class.java)

        coEvery { gitHubRepository.getRepo(owner, name) } returns Resource.success(jsonObj)

        githubViewModel.getRepo(owner, name)

        assertEquals(githubViewModel.repoLiveData.value?.repository?.name, name)
    }

    @Test
    fun `test user view model fail`() = runBlocking {
        val message = "error"
        val owner = "uongsyphuong"
        val name = "BluetoothScanner"

        coEvery { gitHubRepository.getRepo(owner, name) } returns Resource.error(message)

        githubViewModel.getRepo(owner, name)

        assert(githubViewModel.errorLiveData.value == message)
    }
}

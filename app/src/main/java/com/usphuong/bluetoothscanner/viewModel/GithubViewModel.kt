package com.usphuong.bluetoothscanner.viewModel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usphuong.bluetoothscanner.FindQuery
import com.usphuong.bluetoothscanner.data.model.Resource
import com.usphuong.bluetoothscanner.data.repository.GithubRepository
import kotlinx.coroutines.launch

class GithubViewModel @ViewModelInject constructor(private val githubRepository: GithubRepository) :
    ViewModel() {

    val errorLiveData = MutableLiveData<String>()
    val repoLiveData = MutableLiveData<FindQuery.Data?>()

    fun getRepo(owner: String, name: String) {
        viewModelScope.launch {
            val response = githubRepository.getRepo(owner, name)
            if (response.status == Resource.Status.SUCCESS) {
                repoLiveData.postValue(response.data)
            } else {
                errorLiveData.postValue(response.message)
            }
        }
    }
}
